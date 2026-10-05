package com.ajedrez.dominio.reglas;

import com.ajedrez.interfaces.IReglaDeValidacion;

import java.util.Optional;

/** No se puede mover desde una casilla vacía. */
public final class HayPiezaEnOrigen implements IReglaDeValidacion {

    @Override
    public Optional<String> verificar(IntentoDeMovimiento intento) {
        if (intento.piezaMovida().isPresent()) {
            return Optional.empty();
        }
        return Optional.of("No hay ninguna pieza en " + intento.movimiento().origen() + ".");
    }
}
