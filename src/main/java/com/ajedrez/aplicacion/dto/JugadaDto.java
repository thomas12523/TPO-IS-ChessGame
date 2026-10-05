package com.ajedrez.aplicacion.dto;

/** Una jugada pedida por el cliente, en notación de casillas ("e2", "e4"). {@code promocion} es opcional. */
public record JugadaDto(String origen, String destino, String promocion) {
}
