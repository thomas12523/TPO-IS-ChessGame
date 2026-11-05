package com.ajedrez.dominio.partida;

import com.ajedrez.dominio.fabrica.FabricaEstandar;
import com.ajedrez.dominio.modelo.Color;
import com.ajedrez.dominio.modelo.Movimiento;
import com.ajedrez.dominio.modelo.Pieza;
import com.ajedrez.dominio.modelo.Tablero;
import com.ajedrez.dominio.modelo.TipoPieza;
import com.ajedrez.dominio.reglas.ReglasDelAjedrez;
import com.ajedrez.errores.MovimientoInvalidoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.ajedrez.dominio.modelo.Posicion.de;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests de integración del dominio: Partida coordinando turnos e historial sobre
 * las reglas reales del ajedrez clásico (FabricaEstandar).
 */
class PartidaTest {

    private ReglasDelAjedrez reglas;

    @BeforeEach
    void armarReglas() {
        reglas = new FabricaEstandar().crearReglas();
    }

    private Partida partidaEstandar() {
        return new Partida("test", new DisposicionEstandar().preparar(), Color.BLANCO, reglas);
    }

    @Test
    void alEmpezar_juegaBlancoYNoHayHistorial() {
        Partida partida = partidaEstandar();

        assertThat(partida.turno()).isEqualTo(Color.BLANCO);
        assertThat(partida.historial()).isEmpty();
    }

    @Test
    void jugar_unMovimientoValido_alternaElTurno() {
        Partida partida = partidaEstandar();

        partida.jugar(Movimiento.de(de("e2"), de("e4")));

        assertThat(partida.turno()).isEqualTo(Color.NEGRO);
    }

    @Test
    void jugar_unMovimientoValido_loAgregaAlHistorial() {
        Partida partida = partidaEstandar();

        partida.jugar(Movimiento.de(de("e2"), de("e4")));

        assertThat(partida.historial()).hasSize(1);
        assertThat(partida.historial().get(0).movimiento()).isEqualTo(Movimiento.de(de("e2"), de("e4")));
    }

    @Test
    void jugar_unMovimientoInvalido_lanzaExcepcionYNoCambiaElEstado() {
        Partida partida = partidaEstandar();

        assertThatThrownBy(() -> partida.jugar(Movimiento.de(de("e7"), de("e5")))) // pieza negra, turno blanco
                .isInstanceOf(MovimientoInvalidoException.class);

        assertThat(partida.turno()).isEqualTo(Color.BLANCO);
        assertThat(partida.historial()).isEmpty();
    }

    @Test
    void destinosLegales_unPeonEnFilaInicial_puedeAvanzarUnaODosCasillas() {
        Partida partida = partidaEstandar();

        assertThat(partida.destinosLegales(de("e2"))).containsExactlyInAnyOrder(de("e3"), de("e4"));
    }

    @Test
    void destinosLegales_unaCasillaVacia_esConjuntoVacio() {
        Partida partida = partidaEstandar();

        assertThat(partida.destinosLegales(de("e4"))).isEmpty();
    }

    @Test
    void destinosLegales_excluyeMovimientosQueDejarianAlPropioReyEnJaque() {
        // Rey blanco en e1, torre blanca en e2 (clavada), torre negra en e8.
        Tablero tablero = Tablero.vacio()
                .colocar(de("e1"), new Pieza(TipoPieza.REY, Color.BLANCO))
                .colocar(de("e2"), new Pieza(TipoPieza.TORRE, Color.BLANCO))
                .colocar(de("e8"), new Pieza(TipoPieza.TORRE, Color.NEGRO))
                .colocar(de("a8"), new Pieza(TipoPieza.REY, Color.NEGRO));
        Partida partida = new Partida("test", tablero, Color.BLANCO, reglas);

        // La torre clavada solo puede moverse dentro de la columna "e": o sigue tapando
        // al rey (e3..e7), o captura directamente a la torre que amenaza (e8).
        assertThat(partida.destinosLegales(de("e2")))
                .containsExactlyInAnyOrder(de("e3"), de("e4"), de("e5"), de("e6"), de("e7"), de("e8"))
                .doesNotContain(de("d2"), de("f2"));
    }

    @Test
    void capturadasPor_registraLasPiezasQueCadaBandoLeSacoAlRival() {
        Partida partida = partidaEstandar();

        partida.jugar(Movimiento.de(de("e2"), de("e4")));
        partida.jugar(Movimiento.de(de("d7"), de("d5")));
        partida.jugar(Movimiento.de(de("e4"), de("d5"))); // blancas capturan el peón negro

        assertThat(partida.capturadasPor(Color.BLANCO)).containsExactly(new Pieza(TipoPieza.PEON, Color.NEGRO));
        assertThat(partida.capturadasPor(Color.NEGRO)).isEmpty();
    }

    @Test
    void enJaque_detectaCuandoElReyEnTurnoEstaAmenazado() {
        Tablero tablero = Tablero.vacio()
                .colocar(de("e1"), new Pieza(TipoPieza.REY, Color.BLANCO))
                .colocar(de("e8"), new Pieza(TipoPieza.TORRE, Color.NEGRO))
                .colocar(de("a8"), new Pieza(TipoPieza.REY, Color.NEGRO));
        Partida partida = new Partida("test", tablero, Color.BLANCO, reglas);

        assertThat(partida.enJaque()).isTrue();
        assertThat(partida.reyEnJaque()).contains(de("e1"));
    }
}
