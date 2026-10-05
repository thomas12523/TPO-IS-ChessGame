package com.ajedrez.errores;

/** La jugada pedida no es legal. Es la forma en que la aplicación le avisa a sus clientes, sin exponer el dominio. */
public class JugadaInvalidaException extends RuntimeException {

    public JugadaInvalidaException(String motivo, Throwable causa) {
        super(motivo, causa);
    }
}
