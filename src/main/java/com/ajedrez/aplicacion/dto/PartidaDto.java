package com.ajedrez.aplicacion.dto;

import java.util.List;

/**
 * Vista de una partida para quien consume el núcleo. Son solo datos simples:
 * el formato puede cambiar sin tocar el dominio (y viceversa).
 */
public record PartidaDto(
        String id,
        String turno,
        boolean enJaque,
        String reyEnJaque,
        List<PiezaEnCasillaDto> piezas,
        List<String> historial,
        UltimaJugadaDto ultimaJugada,
        List<String> capturadasPorBlancas,
        List<String> capturadasPorNegras) {

    public record PiezaEnCasillaDto(String casilla, String tipo, String color) {
    }

    public record UltimaJugadaDto(String origen, String destino) {
    }
}
