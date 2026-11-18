package com.ajedrez.dominio.movimiento;

import com.ajedrez.dominio.modelo.Color;
import com.ajedrez.dominio.modelo.Pieza;
import com.ajedrez.dominio.modelo.Tablero;
import com.ajedrez.dominio.modelo.TipoPieza;
import com.ajedrez.interfaces.IReglaDeMovimiento;
import org.junit.jupiter.api.Test;

import static com.ajedrez.dominio.modelo.Posicion.de;
import static org.assertj.core.api.Assertions.assertThat;

/** Caballo (L) y rey (un paso): saltan directo a la casilla, sin importar qué hay en el camino. */
class MovimientoDeSaltoTest {

    private final IReglaDeMovimiento caballo = new MovimientoDeSalto(Direcciones.SALTOS_DE_CABALLO);
    private final IReglaDeMovimiento rey = new MovimientoDeSalto(Direcciones.TODAS);

    @Test
    void caballo_enElCentro_tieneLosOchoSaltosPosibles() {
        var destinos = caballo.destinos(de("d4"), Color.BLANCO, Tablero.vacio());

        assertThat(destinos).hasSize(8)
                .contains(de("b3"), de("b5"), de("c2"), de("c6"), de("e2"), de("e6"), de("f3"), de("f5"));
    }

    @Test
    void caballo_enUnaEsquina_descartaLosSaltosQueCaenFueraDelTablero() {
        var destinos = caballo.destinos(de("a1"), Color.BLANCO, Tablero.vacio());

        assertThat(destinos).containsExactlyInAnyOrder(de("b3"), de("c2"));
    }

    @Test
    void caballo_noPuedeQuedarseEnUnaCasillaConPiezaPropia() {
        Tablero tablero = Tablero.vacio().colocar(de("b3"), new Pieza(TipoPieza.PEON, Color.BLANCO));

        assertThat(caballo.destinos(de("a1"), Color.BLANCO, tablero)).doesNotContain(de("b3"));
    }

    @Test
    void caballo_puedeSaltarYCapturarUnaPiezaRival() {
        Tablero tablero = Tablero.vacio().colocar(de("b3"), new Pieza(TipoPieza.PEON, Color.NEGRO));

        assertThat(caballo.destinos(de("a1"), Color.BLANCO, tablero)).contains(de("b3"));
    }

    @Test
    void caballo_saltaIndependientementeDeLoQueHayaEnElCamino() {
        // b3 no está "en el camino" geométrico de un salto en L, pero probamos que una
        // pieza interpuesta en una casilla cualquiera no afecta el resultado.
        Tablero tablero = Tablero.vacio().colocar(de("b2"), new Pieza(TipoPieza.PEON, Color.NEGRO));

        assertThat(caballo.destinos(de("a1"), Color.BLANCO, tablero)).containsExactlyInAnyOrder(de("b3"), de("c2"));
    }

    @Test
    void rey_enElCentro_puedeMoverseUnaCasillaEnCadaUnaDeLasOchoDirecciones() {
        var destinos = rey.destinos(de("d4"), Color.BLANCO, Tablero.vacio());

        assertThat(destinos).hasSize(8);
    }

    @Test
    void rey_enUnaEsquina_soloTieneTresDestinosPosibles() {
        var destinos = rey.destinos(de("a1"), Color.BLANCO, Tablero.vacio());

        assertThat(destinos).containsExactlyInAnyOrder(de("a2"), de("b1"), de("b2"));
    }
}
