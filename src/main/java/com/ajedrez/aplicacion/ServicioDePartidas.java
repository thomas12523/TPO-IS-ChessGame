package com.ajedrez.aplicacion;

import com.ajedrez.aplicacion.dto.DestinosDto;
import com.ajedrez.aplicacion.dto.JugadaDto;
import com.ajedrez.aplicacion.dto.PartidaDto;
import com.ajedrez.dominio.modelo.Color;
import com.ajedrez.dominio.modelo.Movimiento;
import com.ajedrez.dominio.modelo.Posicion;
import com.ajedrez.dominio.modelo.TipoPieza;
import com.ajedrez.dominio.partida.Partida;
import com.ajedrez.dominio.reglas.ReglasDelAjedrez;
import com.ajedrez.interfaces.IDisposicionInicial;
import com.ajedrez.interfaces.IRepositorioDePartidas;
import com.ajedrez.interfaces.IServicioDePartidas;
import com.ajedrez.errores.JugadaInvalidaException;
import com.ajedrez.errores.MovimientoInvalidoException;
import com.ajedrez.errores.PartidaNoEncontradaException;

import java.util.UUID;

/** Implementación de los casos de uso: traduce DTOs a dominio, ejecuta y devuelve DTOs. */
public class ServicioDePartidas implements IServicioDePartidas {

    private final IRepositorioDePartidas repositorio;
    private final IDisposicionInicial disposicionInicial;
    private final ReglasDelAjedrez reglas;

    public ServicioDePartidas(IRepositorioDePartidas repositorio,
                              IDisposicionInicial disposicionInicial,
                              ReglasDelAjedrez reglas) {
        this.repositorio = repositorio;
        this.disposicionInicial = disposicionInicial;
        this.reglas = reglas;
    }

    @Override
    public PartidaDto crearPartida() {
        Partida partida = new Partida(UUID.randomUUID().toString(), disposicionInicial.preparar(), Color.BLANCO, reglas);
        repositorio.guardar(partida);
        return MapeadorDePartidas.aDto(partida);
    }

    @Override
    public PartidaDto obtener(String id) {
        return MapeadorDePartidas.aDto(buscar(id));
    }

    @Override
    public PartidaDto jugar(String id, JugadaDto jugada) {
        Partida partida = buscar(id);
        try {
            partida.jugar(aMovimiento(jugada));
        } catch (MovimientoInvalidoException e) {
            throw new JugadaInvalidaException(e.getMessage(), e);
        }
        repositorio.guardar(partida);
        return MapeadorDePartidas.aDto(partida);
    }

    @Override
    public DestinosDto destinosLegales(String id, String origen) {
        var destinos = buscar(id).destinosLegales(Posicion.de(origen)).stream()
                .map(Posicion::notacion)
                .sorted()
                .toList();
        return new DestinosDto(origen, destinos);
    }

    private Partida buscar(String id) {
        return repositorio.buscar(id).orElseThrow(() -> new PartidaNoEncontradaException(id));
    }

    private static Movimiento aMovimiento(JugadaDto jugada) {
        return new Movimiento(
                Posicion.de(jugada.origen()),
                Posicion.de(jugada.destino()),
                jugada.promocion() == null || jugada.promocion().isBlank()
                        ? null
                        : TipoPieza.valueOf(jugada.promocion().toUpperCase()));
    }
}
