package com.ajedrez.aplicacion;

import com.ajedrez.aplicacion.dto.PartidaDto;
import com.ajedrez.aplicacion.dto.PartidaDto.PiezaEnCasillaDto;
import com.ajedrez.aplicacion.dto.PartidaDto.UltimaJugadaDto;
import com.ajedrez.dominio.modelo.Color;
import com.ajedrez.dominio.modelo.Pieza;
import com.ajedrez.dominio.partida.JugadaRealizada;
import com.ajedrez.dominio.partida.Partida;

import java.util.Comparator;
import java.util.List;

/** Traduce el dominio a DTOs. Es el único punto donde la aplicación convierte una {@link Partida} en datos. */
final class MapeadorDePartidas {

    private MapeadorDePartidas() {
    }

    static PartidaDto aDto(Partida partida) {
        List<PiezaEnCasillaDto> piezas = partida.tablero().todasLasPiezas().entrySet().stream()
                .map(entrada -> new PiezaEnCasillaDto(
                        entrada.getKey().notacion(),
                        entrada.getValue().tipo().name(),
                        entrada.getValue().color().name()))
                .sorted(Comparator.comparing(PiezaEnCasillaDto::casilla))
                .toList();

        List<JugadaRealizada> historial = partida.historial();
        UltimaJugadaDto ultima = historial.isEmpty() ? null : new UltimaJugadaDto(
                historial.getLast().movimiento().origen().notacion(),
                historial.getLast().movimiento().destino().notacion());

        return new PartidaDto(
                partida.id(),
                partida.turno().name(),
                partida.enJaque(),
                partida.reyEnJaque().map(Object::toString).orElse(null),
                piezas,
                historial.stream().map(JugadaRealizada::notacion).toList(),
                ultima,
                tipos(partida.capturadasPor(Color.BLANCO)),
                tipos(partida.capturadasPor(Color.NEGRO)));
    }

    private static List<String> tipos(List<Pieza> piezas) {
        return piezas.stream().map(pieza -> pieza.tipo().name()).toList();
    }
}
