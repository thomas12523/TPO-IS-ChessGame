package com.ajedrez.interfaces;

import com.ajedrez.aplicacion.dto.DestinosDto;
import com.ajedrez.aplicacion.dto.JugadaDto;
import com.ajedrez.aplicacion.dto.PartidaDto;
/**
 * Puerto de entrada: los casos de uso del juego. Quien lo use (web, consola, tests)
 * habla solo en DTOs y no conoce ninguna clase del dominio.
 */
public interface IServicioDePartidas {

    PartidaDto crearPartida();

    /** @throws PartidaNoEncontradaException si no existe */
    PartidaDto obtener(String id);

    /** @throws JugadaInvalidaException si las reglas no permiten la jugada */
    PartidaDto jugar(String id, JugadaDto jugada);

    DestinosDto destinosLegales(String id, String origen);
}
