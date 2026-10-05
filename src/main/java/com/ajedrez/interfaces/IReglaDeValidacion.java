package com.ajedrez.interfaces;

import com.ajedrez.dominio.reglas.IntentoDeMovimiento;

import java.util.Optional;

/**
 * Una única condición que una jugada debe cumplir. Agregar una regla nueva
 * (enroque, tiempo, variantes) es agregar una clase, no modificar las existentes.
 */
public interface IReglaDeValidacion {

    /** @return el motivo por el que la jugada es inválida, o vacío si esta regla la acepta. */
    Optional<String> verificar(IntentoDeMovimiento intento);
}
