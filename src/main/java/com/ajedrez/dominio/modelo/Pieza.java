package com.ajedrez.dominio.modelo;

import java.util.Objects;

/** Una pieza es solo su tipo y su color: un objeto de valor, sin comportamiento de movimiento. */
public record Pieza(TipoPieza tipo, Color color) {

    public Pieza {
        Objects.requireNonNull(tipo, "tipo");
        Objects.requireNonNull(color, "color");
    }

    public boolean es(TipoPieza otroTipo) {
        return tipo == otroTipo;
    }

    public boolean esDe(Color otroColor) {
        return color == otroColor;
    }
}
