package com.ajedrez.interfaces;

import com.ajedrez.dominio.partida.Partida;

import java.util.Optional;

/** Puerto de salida: dónde se guardan las partidas. La aplicación no sabe si es memoria, una base de datos o un archivo. */
public interface IRepositorioDePartidas {

    void guardar(Partida partida);

    Optional<Partida> buscar(String id);
}
