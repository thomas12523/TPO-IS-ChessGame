package com.ajedrez.dominio.partida;

import com.ajedrez.dominio.modelo.Color;
import com.ajedrez.dominio.modelo.Movimiento;
import com.ajedrez.dominio.modelo.Pieza;
import com.ajedrez.dominio.modelo.Posicion;
import com.ajedrez.dominio.modelo.Tablero;
import com.ajedrez.dominio.reglas.IntentoDeMovimiento;
import com.ajedrez.dominio.reglas.ReglasDelAjedrez;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Una partida en curso: el único objeto con estado mutable del dominio.
 * Coordina turnos, historial y capturas, y delega en las reglas todo lo demás.
 */
public final class Partida {

    private final String id;
    private final ReglasDelAjedrez reglas;
    private final List<JugadaRealizada> historial = new ArrayList<>();
    private Tablero tablero;
    private Color turno;

    public Partida(String id, Tablero tableroInicial, Color quienEmpieza, ReglasDelAjedrez reglas) {
        this.id = Objects.requireNonNull(id, "id");
        this.tablero = Objects.requireNonNull(tableroInicial, "tableroInicial");
        this.turno = Objects.requireNonNull(quienEmpieza, "quienEmpieza");
        this.reglas = Objects.requireNonNull(reglas, "reglas");
    }

    /**
     * Juega un movimiento del jugador en turno.
     *
     * @throws com.ajedrez.errores.MovimientoInvalidoException si las reglas no lo permiten
     */
    public synchronized JugadaRealizada jugar(Movimiento movimiento) {
        reglas.validador().validar(new IntentoDeMovimiento(tablero, turno, movimiento));

        Pieza movida = tablero.piezaEn(movimiento.origen()).orElseThrow();
        Pieza capturada = tablero.piezaEn(movimiento.destino()).orElse(null);
        Pieza queLlega = reglas.ejecutor().piezaQueLlega(movida, movimiento);

        tablero = reglas.ejecutor().ejecutar(tablero, movimiento);
        turno = turno.opuesto();

        JugadaRealizada jugada = new JugadaRealizada(movimiento, movida, queLlega, capturada, enJaque());
        historial.add(jugada);
        return jugada;
    }

    /** Casillas a las que la pieza en {@code origen} puede moverse legalmente ahora mismo. */
    public synchronized Set<Posicion> destinosLegales(Posicion origen) {
        Optional<Pieza> pieza = tablero.piezaEn(origen);
        if (pieza.isEmpty()) {
            return Set.of();
        }
        return reglas.catalogo().reglaPara(pieza.get().tipo())
                .destinos(origen, pieza.get().color(), tablero).stream()
                .filter(destino -> reglas.validador().esValido(
                        new IntentoDeMovimiento(tablero, turno, Movimiento.de(origen, destino))))
                .collect(Collectors.toUnmodifiableSet());
    }

    /** ¿El rey del jugador en turno está en jaque? */
    public synchronized boolean enJaque() {
        return reglas.detector().estaEnJaque(tablero, turno);
    }

    public synchronized Optional<Posicion> reyEnJaque() {
        return enJaque() ? reglas.detector().ubicacionDelRey(tablero, turno) : Optional.empty();
    }

    /** Piezas que {@code captor} le sacó al rival, en orden. */
    public synchronized List<Pieza> capturadasPor(Color captor) {
        return historial.stream()
                .filter(jugada -> jugada.piezaMovida().esDe(captor))
                .flatMap(jugada -> jugada.captura().stream())
                .toList();
    }

    public String id() {
        return id;
    }

    public synchronized Tablero tablero() {
        return tablero;
    }

    public synchronized Color turno() {
        return turno;
    }

    public synchronized List<JugadaRealizada> historial() {
        return List.copyOf(historial);
    }
}
