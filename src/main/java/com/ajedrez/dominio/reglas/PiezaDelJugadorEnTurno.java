package com.ajedrez.dominio.reglas;

import com.ajedrez.interfaces.IReglaDeValidacion;

import java.util.Optional;

/** Cada jugador solo puede mover sus propias piezas, y solo en su turno. */
public final class PiezaDelJugadorEnTurno implements IReglaDeValidacion {

    @Override
    public Optional<String> verificar(IntentoDeMovimiento intento) {
        boolean esPropia = intento.piezaMovida()
                .map(pieza -> pieza.esDe(intento.turno()))
                .orElse(false);
        if (esPropia) {
            return Optional.empty();
        }
        return Optional.of("Es el turno de las " + intento.turno().nombre() + ".");
    }
}
