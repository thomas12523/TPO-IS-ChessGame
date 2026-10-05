package com.ajedrez.infraestructura.web;

import com.ajedrez.errores.JugadaInvalidaException;
import com.ajedrez.errores.PartidaNoEncontradaException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/** Traduce las excepciones del núcleo a códigos HTTP. */
@RestControllerAdvice
public class ManejadorDeErrores {

    public record ErrorDto(String mensaje) {
    }

    @ExceptionHandler(JugadaInvalidaException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ErrorDto movimientoInvalido(JugadaInvalidaException e) {
        return new ErrorDto(e.getMessage());
    }

    @ExceptionHandler(PartidaNoEncontradaException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorDto partidaNoEncontrada(PartidaNoEncontradaException e) {
        return new ErrorDto(e.getMessage());
    }

    @ExceptionHandler({IllegalArgumentException.class, NullPointerException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDto pedidoInvalido(RuntimeException e) {
        return new ErrorDto(e.getMessage() != null ? e.getMessage() : "Pedido inválido.");
    }
}
