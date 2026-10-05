// Adaptador de presentación: solo dibuja lo que devuelve la API y le envía jugadas.
// Ninguna regla del ajedrez vive acá; las jugadas legales se le preguntan al servidor.

const API = "/api/partidas";
const CLAVE_PARTIDA = "ajedrez.partidaId";
const COLUMNAS = "abcdefgh";

// Se usan las figuras rellenas para ambos colores (el color lo da el CSS);
// ︎ fuerza la versión de texto y evita que el peón se dibuje como emoji.
const FIGURAS = {
    REY: "♚", REINA: "♛", TORRE: "♜",
    ALFIL: "♝", CABALLO: "♞", PEON: "♟︎",
};
const PROMOCIONES = ["REINA", "TORRE", "ALFIL", "CABALLO"];

const estado = {
    partida: null,
    seleccion: null,
    destinos: [],
};

const $ = (id) => document.getElementById(id);

// ---------- Comunicación con la API ----------

async function pedir(url, opciones = {}) {
    const respuesta = await fetch(url, {
        headers: { "Content-Type": "application/json" },
        ...opciones,
    });
    const cuerpo = await respuesta.json().catch(() => ({}));
    if (!respuesta.ok) {
        const error = new Error(cuerpo.mensaje || "Error inesperado del servidor.");
        error.status = respuesta.status;
        throw error;
    }
    return cuerpo;
}

const api = {
    crear: () => pedir(API, { method: "POST" }),
    obtener: (id) => pedir(`${API}/${id}`),
    destinos: (id, desde) => pedir(`${API}/${id}/movimientos?desde=${desde}`),
    jugar: (id, jugada) => pedir(`${API}/${id}/jugadas`, { method: "POST", body: JSON.stringify(jugada) }),
};

function recordarPartida(id) {
    try { localStorage.setItem(CLAVE_PARTIDA, id); } catch { /* sin almacenamiento: no pasa nada */ }
}

function partidaRecordada() {
    try { return localStorage.getItem(CLAVE_PARTIDA); } catch { return null; }
}

// ---------- Utilidades ----------

function piezaEn(casilla) {
    return estado.partida.piezas.find((p) => p.casilla === casilla) || null;
}

function crearFigura(tipo, color) {
    const span = document.createElement("span");
    span.className = `pieza ${color.toLowerCase()}`;
    span.textContent = FIGURAS[tipo];
    return span;
}

function esCoronacion(origen, destino) {
    const pieza = piezaEn(origen);
    if (!pieza || pieza.tipo !== "PEON") return false;
    const filaFinal = pieza.color === "BLANCO" ? "8" : "1";
    return destino[1] === filaFinal;
}

function mostrarError(mensaje) {
    const aviso = $("aviso-error");
    aviso.textContent = mensaje;
    aviso.hidden = !mensaje;
}

// ---------- Dibujo ----------

function dibujarTablero() {
    const tablero = $("tablero");
    tablero.replaceChildren();
    const { partida, seleccion, destinos } = estado;

    for (let fila = 8; fila >= 1; fila--) {
        for (let c = 0; c < 8; c++) {
            const casilla = `${COLUMNAS[c]}${fila}`;
            const boton = document.createElement("button");
            boton.type = "button";
            boton.dataset.casilla = casilla;
            boton.className = `casilla ${(c + fila) % 2 === 0 ? "clara" : "oscura"}`;
            boton.setAttribute("aria-label", casilla);

            if (partida.ultimaJugada && [partida.ultimaJugada.origen, partida.ultimaJugada.destino].includes(casilla)) {
                boton.classList.add("ultima");
            }
            if (casilla === seleccion) boton.classList.add("seleccionada");
            if (casilla === partida.reyEnJaque) boton.classList.add("en-jaque");

            const pieza = piezaEn(casilla);
            if (destinos.includes(casilla)) {
                boton.classList.add("destino");
                if (pieza) boton.classList.add("captura");
            }
            if (pieza) {
                boton.append(crearFigura(pieza.tipo, pieza.color));
                boton.setAttribute("aria-label", `${casilla}, ${pieza.tipo.toLowerCase()} ${pieza.color.toLowerCase()}`);
            }

            if (c === 0) boton.append(coordenada("fila", fila));
            if (fila === 1) boton.append(coordenada("columna", COLUMNAS[c]));

            tablero.append(boton);
        }
    }
}

function coordenada(tipo, texto) {
    const span = document.createElement("span");
    span.className = `coordenada ${tipo}`;
    span.textContent = texto;
    return span;
}

function dibujarPanel() {
    const { partida } = estado;
    const colorTurno = partida.turno.toLowerCase();
    $("ficha-turno").className = `ficha-turno ${colorTurno}`;
    $("texto-turno").textContent = `Juegan las ${colorTurno === "blanco" ? "blancas" : "negras"}`;
    $("aviso-jaque").hidden = !partida.enJaque;

    const historial = $("historial");
    historial.replaceChildren();
    if (partida.historial.length === 0) {
        const vacio = document.createElement("li");
        vacio.className = "vacio";
        vacio.textContent = "Todavía no hay jugadas.";
        historial.append(vacio);
    }
    partida.historial.forEach((jugada, i) => {
        if (i % 2 === 0) {
            const numero = document.createElement("li");
            numero.className = "numero";
            numero.textContent = `${i / 2 + 1}.`;
            historial.append(numero);
        }
        const item = document.createElement("li");
        item.textContent = jugada;
        historial.append(item);
    });
    if (partida.historial.length % 2 === 1) historial.append(document.createElement("li"));
    historial.scrollTop = historial.scrollHeight;

    dibujarCapturas("capturas-blancas", partida.capturadasPorBlancas, "NEGRO");
    dibujarCapturas("capturas-negras", partida.capturadasPorNegras, "BLANCO");
}

function dibujarCapturas(id, tipos, colorDeLasPiezas) {
    const contenedor = $(id);
    contenedor.replaceChildren(...tipos.map((tipo) => crearFigura(tipo, colorDeLasPiezas)));
    contenedor.querySelectorAll(".pieza").forEach((p) => (p.style.fontSize = "22px"));
}

function dibujar() {
    dibujarTablero();
    dibujarPanel();
}

// ---------- Interacción ----------

async function alHacerClic(casilla) {
    const { partida, seleccion, destinos } = estado;
    mostrarError("");

    if (seleccion && destinos.includes(casilla)) {
        await mover(seleccion, casilla);
        return;
    }

    const pieza = piezaEn(casilla);
    if (pieza && pieza.color === partida.turno && casilla !== seleccion) {
        estado.seleccion = casilla;
        estado.destinos = (await api.destinos(partida.id, casilla)).destinos;
        if (estado.destinos.length === 0) mostrarError("Esa pieza no tiene jugadas legales.");
    } else {
        if (seleccion && casilla !== seleccion) {
            // Intento de jugada no listada: se la enviamos al servidor para mostrar su motivo.
            await mover(seleccion, casilla);
            return;
        }
        if (pieza && pieza.color !== partida.turno) {
            mostrarError(`Es el turno de las ${partida.turno === "BLANCO" ? "blancas" : "negras"}.`);
        }
        estado.seleccion = null;
        estado.destinos = [];
    }
    dibujar();
}

async function mover(origen, destino) {
    let promocion = null;
    if (estado.destinos.includes(destino) && esCoronacion(origen, destino)) {
        promocion = await elegirPromocion(piezaEn(origen).color);
    }
    try {
        estado.partida = await api.jugar(estado.partida.id, { origen, destino, promocion });
    } catch (error) {
        mostrarError(error.message);
    }
    estado.seleccion = null;
    estado.destinos = [];
    dibujar();
}

function elegirPromocion(color) {
    const dialogo = $("dialogo-promocion");
    const opciones = $("opciones-promocion");
    opciones.replaceChildren(...PROMOCIONES.map((tipo) => {
        const boton = document.createElement("button");
        boton.value = tipo;
        boton.setAttribute("aria-label", tipo.toLowerCase());
        boton.append(crearFigura(tipo, color));
        return boton;
    }));
    return new Promise((resolver) => {
        dialogo.addEventListener("close", () => resolver(dialogo.returnValue || "REINA"), { once: true });
        dialogo.returnValue = "";
        dialogo.showModal();
    });
}

async function nuevaPartida() {
    estado.partida = await api.crear();
    estado.seleccion = null;
    estado.destinos = [];
    recordarPartida(estado.partida.id);
    mostrarError("");
    dibujar();
}

async function iniciar() {
    $("tablero").addEventListener("click", (evento) => {
        const casilla = evento.target.closest(".casilla");
        if (casilla) alHacerClic(casilla.dataset.casilla).catch((e) => mostrarError(e.message));
    });
    $("nueva-partida").addEventListener("click", () => nuevaPartida().catch((e) => mostrarError(e.message)));

    const id = partidaRecordada();
    if (id) {
        try {
            estado.partida = await api.obtener(id);
            dibujar();
            return;
        } catch {
            // La partida ya no existe (p. ej. se reinició el servidor): se crea otra.
        }
    }
    await nuevaPartida();
}

iniciar().catch((e) => mostrarError(e.message));
