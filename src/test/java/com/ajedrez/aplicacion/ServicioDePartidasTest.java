package com.ajedrez.aplicacion;

import com.ajedrez.aplicacion.dto.JugadaDto;
import com.ajedrez.aplicacion.dto.PartidaDto;
import com.ajedrez.dominio.fabrica.FabricaEstandar;
import com.ajedrez.dominio.modelo.Color;
import com.ajedrez.dominio.modelo.Pieza;
import com.ajedrez.dominio.modelo.Tablero;
import com.ajedrez.dominio.modelo.TipoPieza;
import com.ajedrez.dominio.partida.DisposicionEstandar;
import com.ajedrez.dominio.partida.Partida;
import com.ajedrez.errores.JugadaInvalidaException;
import com.ajedrez.errores.PartidaNoEncontradaException;
import com.ajedrez.infraestructura.persistencia.RepositorioDePartidasEnMemoria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.ajedrez.dominio.modelo.Posicion.de;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests "sociables" del caso de uso: repositorio real en memoria + reglas reales,
 * para verificar la traducción DTO <-> dominio y el manejo de errores de la aplicación.
 */
class ServicioDePartidasTest {

    private RepositorioDePartidasEnMemoria repositorio;
    private ServicioDePartidas servicio;

    @BeforeEach
    void armarServicio() {
        repositorio = new RepositorioDePartidasEnMemoria();
        servicio = new ServicioDePartidas(repositorio, new DisposicionEstandar(), new FabricaEstandar().crearReglas());
    }

    @Test
    void crearPartida_devuelveUnTableroInicialConTurnoBlancoYSinHistorial() {
        PartidaDto dto = servicio.crearPartida();

        assertThat(dto.turno()).isEqualTo("BLANCO");
        assertThat(dto.piezas()).hasSize(32);
        assertThat(dto.historial()).isEmpty();
        assertThat(dto.enJaque()).isFalse();
    }

    @Test
    void obtener_unaPartidaExistente_devuelveSuEstadoActual() {
        PartidaDto creada = servicio.crearPartida();

        PartidaDto obtenida = servicio.obtener(creada.id());

        assertThat(obtenida.id()).isEqualTo(creada.id());
    }

    @Test
    void obtener_unaPartidaInexistente_lanzaPartidaNoEncontradaException() {
        assertThatThrownBy(() -> servicio.obtener("no-existe"))
                .isInstanceOf(PartidaNoEncontradaException.class);
    }

    @Test
    void jugar_unaJugadaValida_actualizaElTurnoYElHistorial() {
        PartidaDto creada = servicio.crearPartida();

        PartidaDto actualizada = servicio.jugar(creada.id(), new JugadaDto("e2", "e4", null));

        assertThat(actualizada.turno()).isEqualTo("NEGRO");
        assertThat(actualizada.historial()).containsExactly("e2-e4");
        assertThat(actualizada.ultimaJugada().origen()).isEqualTo("e2");
        assertThat(actualizada.ultimaJugada().destino()).isEqualTo("e4");
    }

    @Test
    void jugar_unaJugadaInvalida_traduceLaExcepcionDeDominioAJugadaInvalidaException() {
        PartidaDto creada = servicio.crearPartida();

        // Pieza negra, pero es el turno de las blancas.
        assertThatThrownBy(() -> servicio.jugar(creada.id(), new JugadaDto("e7", "e5", null)))
                .isInstanceOf(JugadaInvalidaException.class)
                .hasMessage("Es el turno de las blancas.");
    }

    @Test
    void jugar_sobreUnaPartidaInexistente_lanzaPartidaNoEncontradaException() {
        assertThatThrownBy(() -> servicio.jugar("no-existe", new JugadaDto("e2", "e4", null)))
                .isInstanceOf(PartidaNoEncontradaException.class);
    }

    @Test
    void jugar_conPromocionEnTextoLibre_laTraduceAlTipoDePiezaCorrecto() {
        Tablero tablero = Tablero.vacio()
                .colocar(de("a7"), new Pieza(TipoPieza.PEON, Color.BLANCO))
                .colocar(de("e1"), new Pieza(TipoPieza.REY, Color.BLANCO))
                .colocar(de("e8"), new Pieza(TipoPieza.REY, Color.NEGRO));
        Partida partida = new Partida("promocion", tablero, Color.BLANCO, new FabricaEstandar().crearReglas());
        repositorio.guardar(partida);

        PartidaDto resultado = servicio.jugar("promocion", new JugadaDto("a7", "a8", "caballo"));

        assertThat(resultado.piezas())
                .anySatisfy(pieza -> {
                    assertThat(pieza.casilla()).isEqualTo("a8");
                    assertThat(pieza.tipo()).isEqualTo("CABALLO");
                    assertThat(pieza.color()).isEqualTo("BLANCO");
                });
    }

    @Test
    void destinosLegales_devuelveLasCasillasEnNotacionYOrdenadas() {
        PartidaDto creada = servicio.crearPartida();

        var destinos = servicio.destinosLegales(creada.id(), "b1"); // caballo

        assertThat(destinos.desde()).isEqualTo("b1");
        assertThat(destinos.destinos()).isSortedAccordingTo(String::compareTo);
        assertThat(destinos.destinos()).containsExactly("a3", "c3");
    }
}
