package com.ajedrez.interfaces;

import com.ajedrez.dominio.reglas.IntentoDeMovimiento;

import java.util.Optional;

/** Decide si un intento de movimiento es legal. */
public interface IValidadorDeMovimientos {

    Optional<String> primeraViolacion(IntentoDeMovimiento intento);

    boolean esValido(IntentoDeMovimiento intento);

    /** @throws MovimientoInvalidoException si el movimiento no es legal */
    void validar(IntentoDeMovimiento intento);
}
