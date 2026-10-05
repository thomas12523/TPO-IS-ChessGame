package com.ajedrez.infraestructura.persistencia;

import com.ajedrez.dominio.partida.Partida;
import com.ajedrez.interfaces.IRepositorioDePartidas;

import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/** Adaptador de persistencia más simple posible. Las partidas se pierden al reiniciar el servidor. */
@Repository
public class RepositorioDePartidasEnMemoria implements IRepositorioDePartidas {

    private final Map<String, Partida> partidas = new ConcurrentHashMap<>();

    @Override
    public void guardar(Partida partida) {
        partidas.put(partida.id(), partida);
    }

    @Override
    public Optional<Partida> buscar(String id) {
        return Optional.ofNullable(partidas.get(id));
    }
}
