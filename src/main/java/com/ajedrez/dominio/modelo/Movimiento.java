package com.ajedrez.dominio.modelo;

import java.util.Objects;
import java.util.Optional;

/**
 * Intención de mover una pieza de una casilla a otra. Si es una coronación,
 * puede indicar en qué pieza se convierte el peón (si no, se asume reina).
 */
public record Movimiento(Posicion origen, Posicion destino, TipoPieza promocion) {

    public Movimiento {
        Objects.requireNonNull(origen, "origen");
        Objects.requireNonNull(destino, "destino");
    }

    public static Movimiento de(Posicion origen, Posicion destino) {
        return new Movimiento(origen, destino, null);
    }

    public Optional<TipoPieza> promocionElegida() {
        return Optional.ofNullable(promocion);
    }
}
