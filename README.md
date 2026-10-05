# Ajedrez — Ingenio y Diseño

Ajedrez para dos jugadores en el navegador. Núcleo de dominio en Java puro, expuesto vía una API REST con Spring Boot y una interfaz web sin frameworks.

## Cómo correrlo

Requiere Java 21 o superior (no hace falta tener Maven: viene el wrapper).

```bash
./mvnw spring-boot:run
```

Abrir <http://localhost:8080>.

## Alcance

| Requisito | Dónde se resuelve |
|---|---|
| Tablero 8x8 | `dominio/modelo/Tablero` (`DIMENSION`), `Posicion` no admite casillas fuera de rango |
| 6 piezas con su movimiento | `dominio/movimiento/*` + `CatalogoEstandar` |
| Alternancia de turnos | `Partida.jugar` + regla `PiezaDelJugadorEnTurno` |
| Captura | `Tablero.mover` (la pieza que llega pisa a la rival); las reglas impiden capturar piezas propias |
| Validación de movimientos inválidos | `ValidadorDeMovimientos` con una cadena de `IReglaDeValidacion` |
| Detección de jaque | `DetectorDeJaque` |
| Extra: no dejar al propio rey en jaque | regla `NoDejaAlReyEnJaque` |
| Extra: promoción del peón | `Promocion`, `PromocionCorrecta`, `EjecutorDeMovimientos` |

Fuera de alcance: enroque, captura al paso, jaque mate, ahogado y tablas. Cada uno se agregaría como una regla nueva (ver más abajo).

## Arquitectura

```
com.ajedrez
├── dominio            ← núcleo: Java puro, sin Spring ni HTTP
│   ├── modelo         Posicion, Tablero (inmutable), Pieza, Color, Movimiento
│   ├── movimiento     cómo se mueve cada pieza (estrategias)
│   ├── reglas         qué jugadas son válidas, cómo se ejecutan, jaque
│   └── partida        Partida (turnos, historial), disposición inicial
├── aplicacion         casos de uso (ServicioDePartidas) + puerto IRepositorioDePartidas
└── infraestructura    adaptadores
    ├── config         raíz de composición: conecta todo (único lugar con `new` de implementaciones)
    ├── persistencia   repositorio en memoria
    └── web            controlador REST, DTOs, mapeo de errores
static/                interfaz web (HTML/CSS/JS): solo dibuja y envía jugadas
```

Las dependencias apuntan siempre hacia adentro: `infraestructura → aplicacion → dominio`. Dentro del dominio: `modelo ← movimiento ← reglas ← partida`. El dominio no sabe que existe la web; la interfaz web no sabe ninguna regla de ajedrez (le pregunta al servidor qué jugadas son legales).

### API

| Método | Ruta | Descripción |
|---|---|---|
| `POST` | `/api/partidas` | Crea una partida |
| `GET` | `/api/partidas/{id}` | Estado de la partida |
| `GET` | `/api/partidas/{id}/movimientos?desde=e2` | Destinos legales de una pieza |
| `POST` | `/api/partidas/{id}/jugadas` | `{"origen":"e7","destino":"e8","promocion":"CABALLO"}` |

Una jugada ilegal responde `422` con `{"mensaje": "..."}` explicando el motivo.

## Decisiones de diseño: qué, por qué y cuándo romperlo

### 1. Movimiento por composición, no por herencia
**Qué:** no hay `class Torre extends Pieza`. `Pieza` es un valor (tipo + color) y cada tipo tiene asociada una `IReglaDeMovimiento` (Strategy). Hay tres reglas genéricas: `MovimientoDeslizante`, `MovimientoDeSalto`, `MovimientoDePeon`, y un `MovimientoCompuesto`. La reina es literalmente `new MovimientoCompuesto(torre, alfil)`.
**Por qué:** con herencia, la reina duplicaría código o heredaría de forma rara. Con composición, una pieza nueva de una variante (p. ej. el *arzobispo* = alfil + caballo) es una línea en un catálogo.
**Cuándo romperlo:** si las piezas tuvieran mucho comportamiento propio además de moverse (habilidades, estados), una jerarquía chica podría ser más legible.

### 2. Reglas de validación como cadena abierta (SRP + Open/Closed)
**Qué:** cada condición de una jugada válida es una clase (`HayPiezaEnOrigen`, `PiezaDelJugadorEnTurno`, `RespetaElMovimientoDeLaPieza`, `PromocionCorrecta`, `NoDejaAlReyEnJaque`). `ValidadorDeMovimientos` las aplica en orden.
**Por qué:** cada regla tiene un único motivo para cambiar y da su propio mensaje de error. Agregar enroque o reloj es sumar una clase a la lista en la configuración, sin modificar las existentes.
**Cuándo romperlo:** si las reglas empezaran a depender fuertemente unas de otras (orden frágil, estado compartido), conviene un motor de reglas más explícito.

### 3. Tablero inmutable
**Qué:** cada jugada produce un `Tablero` nuevo.
**Por qué:** detectar "¿esta jugada deja a mi rey en jaque?" es simplemente simular sobre una copia; no hay que deshacer nada ni hay riesgo de dejar el tablero corrupto. Además es seguro entre hilos.
**Cuándo romperlo:** un motor que analiza millones de posiciones (IA) necesitaría un tablero mutable con *make/unmake* por rendimiento.

### 4. Un solo concepto de "ataque"
**Qué:** `DetectorDeJaque` usa las mismas `IReglaDeMovimiento` que validan las jugadas (`casillasAtacadas`).
**Por qué:** evita tener dos definiciones de cómo ataca cada pieza que puedan divergir. El peón es la única excepción (avanza recto, ataca en diagonal), y se resuelve sobrescribiendo un método por defecto.

### 5. Inyección de dependencias e inversión de dependencias
**Qué:** todo recibe sus colaboradores por constructor. El dominio define abstracciones (`ICatalogoDeMovimientos`, `IDisposicionInicial`, `IRepositorioDePartidas`) y `ConfiguracionDelAjedrez` es la raíz de composición que elige implementaciones.
**Por qué:** cambiar la persistencia (memoria → base de datos), el armado inicial (clásico → Chess960) o el catálogo de piezas no toca el núcleo. El dominio no tiene anotaciones de Spring: se puede usar desde una consola o desde tests sin levantar nada.
**Cuándo romperlo:** clases como `Promocion` o `Direcciones` son estáticas a propósito: son conocimiento fijo del ajedrez y abstraerlas no aportaría flexibilidad real (YAGNI).

### 6. Núcleo y adaptadores (arquitectura hexagonal)
**Qué:** la web REST y el repositorio en memoria son adaptadores; `ServicioDePartidas` es el puerto de entrada.
**Por qué:** se puede sumar una interfaz de consola, un bot o un modo en red agregando un adaptador, sin tocar las reglas.
**Cuándo romperlo:** en un prototipo de una tarde, tanta separación es sobrecosto; acá se justifica porque la consigna pide modularidad y escalabilidad.

### 7. Algunos compromisos conscientes
- `ReglasDelAjedrez` agrupa catálogo, validador, ejecutor y detector para que `Partida` no tenga cuatro parámetros de reglas; es un *parameter object*, no una clase con lógica.
- La interfaz web repite una sola regla: saber cuándo un peón corona, para abrir el selector de pieza. Si no, haría falta un viaje extra al servidor. El servidor igual valida.
