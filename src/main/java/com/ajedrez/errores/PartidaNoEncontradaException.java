package com.ajedrez.errores;

public class PartidaNoEncontradaException extends RuntimeException {

    public PartidaNoEncontradaException(String id) {
        super("No existe la partida " + id + ".");
    }
}
