package com.ajedrez.dominio.modelo;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Tablero inmutable de 8x8: sabe qué pieza hay en cada casilla y nada más.
 * No conoce las reglas del ajedrez. Cada cambio devuelve un tablero nuevo, lo que
 * permite simular jugadas ("¿y si muevo acá, quedo en jaque?") sin deshacer nada.
 */
public final class Tablero {

    public static final int DIMENSION = 8;

    private final Map<Posicion, Pieza> casillas;

    private Tablero(Map<Posicion, Pieza> casillas) {
        this.casillas = Map.copyOf(casillas);
    }

    public static Tablero vacio() {
        return new Tablero(Map.of());
    }

    public static boolean estaDentro(int columna, int fila) {
        return columna >= 0 && columna < DIMENSION && fila >= 0 && fila < DIMENSION;
    }

    public Optional<Pieza> piezaEn(Posicion posicion) {
        return Optional.ofNullable(casillas.get(posicion));
    }

    public boolean estaVacia(Posicion posicion) {
        return !casillas.containsKey(posicion);
    }

    /** ¿Hay en la casilla una pieza del bando contrario a {@code color}? */
    public boolean hayRivalEn(Posicion posicion, Color color) {
        return piezaEn(posicion).map(pieza -> !pieza.esDe(color)).orElse(false);
    }

    public Tablero colocar(Posicion posicion, Pieza pieza) {
        Map<Posicion, Pieza> nuevas = new HashMap<>(casillas);
        nuevas.put(posicion, pieza);
        return new Tablero(nuevas);
    }

    /**
     * Saca la pieza de {@code origen} y deja {@code piezaQueLlega} en {@code destino}
     * (pisando lo que hubiera: así se resuelve la captura).
     */
    public Tablero mover(Posicion origen, Posicion destino, Pieza piezaQueLlega) {
        Map<Posicion, Pieza> nuevas = new HashMap<>(casillas);
        nuevas.remove(origen);
        nuevas.put(destino, piezaQueLlega);
        return new Tablero(nuevas);
    }

    public Map<Posicion, Pieza> piezasDe(Color color) {
        return casillas.entrySet().stream()
                .filter(entrada -> entrada.getValue().esDe(color))
                .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public Map<Posicion, Pieza> todasLasPiezas() {
        return casillas;
    }

    public Optional<Posicion> ubicacionDe(Pieza pieza) {
        return casillas.entrySet().stream()
                .filter(entrada -> entrada.getValue().equals(pieza))
                .map(Map.Entry::getKey)
                .findFirst();
    }
}
