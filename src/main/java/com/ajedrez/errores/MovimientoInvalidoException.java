package com.ajedrez.errores;

/** Se lanza cuando se intenta una jugada que las reglas no permiten. */
public class MovimientoInvalidoException extends RuntimeException {

    public MovimientoInvalidoException(String motivo) {
        super(motivo);
    }
}
