package com.ajedrez.dominio.movimiento;

import com.ajedrez.dominio.modelo.Color;
import com.ajedrez.dominio.modelo.Pieza;
import com.ajedrez.dominio.modelo.Tablero;
import com.ajedrez.dominio.modelo.TipoPieza;
import org.junit.jupiter.api.Test;

import static com.ajedrez.dominio.modelo.Posicion.de;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * El peón: el caso especial del catálogo. Avanza recto (una o dos casillas desde su fila
 * inicial) solo si está libre, y ataca/captura únicamente en diagonal.
 */
class MovimientoDePeonTest {

    private final MovimientoDePeon peon = new MovimientoDePeon();

    @Test
    void blanco_desdeSuFilaInicial_puedeAvanzarUnaODosCasillas() {
        var destinos = peon.destinos(de("e2"), Color.BLANCO, Tablero.vacio());

        assertThat(destinos).containsExactlyInAnyOrder(de("e3"), de("e4"));
    }

    @Test
    void negro_avanzaHaciaFilasMasBajas() {
        var destinos = peon.destinos(de("e7"), Color.NEGRO, Tablero.vacio());

        assertThat(destinos).containsExactlyInAnyOrder(de("e6"), de("e5"));
    }

    @Test
    void fueraDeSuFilaInicial_soloPuedeAvanzarUnaCasilla() {
        var destinos = peon.destinos(de("e3"), Color.BLANCO, Tablero.vacio());

        assertThat(destinos).containsExactly(de("e4"));
    }

    @Test
    void avanceSimple_bloqueadoPorCualquierPieza_noEsPosible() {
        Tablero conPiezaPropia = Tablero.vacio().colocar(de("e3"), new Pieza(TipoPieza.PEON, Color.BLANCO));
        Tablero conPiezaRival = Tablero.vacio().colocar(de("e3"), new Pieza(TipoPieza.PEON, Color.NEGRO));

        assertThat(peon.destinos(de("e2"), Color.BLANCO, conPiezaPropia)).doesNotContain(de("e3"), de("e4"));
        assertThat(peon.destinos(de("e2"), Color.BLANCO, conPiezaRival)).doesNotContain(de("e3"), de("e4"));
    }

    @Test
    void avanceDoble_bloqueadoSoloEnLaSegundaCasilla_permiteElPrimerPaso() {
        Tablero tablero = Tablero.vacio().colocar(de("e4"), new Pieza(TipoPieza.PEON, Color.NEGRO));

        var destinos = peon.destinos(de("e2"), Color.BLANCO, tablero);

        assertThat(destinos).containsExactly(de("e3"));
    }

    @Test
    void noPuedeCapturarHaciaAdelanteEnLineaRecta() {
        Tablero tablero = Tablero.vacio().colocar(de("e3"), new Pieza(TipoPieza.PEON, Color.NEGRO));

        assertThat(peon.destinos(de("e2"), Color.BLANCO, tablero)).doesNotContain(de("e3"));
    }

    @Test
    void capturaEnDiagonal_soloSiHayUnaPiezaRivalAhi() {
        Tablero sinNadaEnDiagonal = Tablero.vacio();
        Tablero conRivalEnDiagonal = Tablero.vacio().colocar(de("d3"), new Pieza(TipoPieza.PEON, Color.NEGRO));
        Tablero conPiezaPropiaEnDiagonal = Tablero.vacio().colocar(de("d3"), new Pieza(TipoPieza.PEON, Color.BLANCO));

        assertThat(peon.destinos(de("e2"), Color.BLANCO, sinNadaEnDiagonal)).doesNotContain(de("d3"));
        assertThat(peon.destinos(de("e2"), Color.BLANCO, conRivalEnDiagonal)).contains(de("d3"));
        assertThat(peon.destinos(de("e2"), Color.BLANCO, conPiezaPropiaEnDiagonal)).doesNotContain(de("d3"));
    }

    @Test
    void casillasAtacadas_sonLasDosDiagonales_hayaOnoPiezaAhi() {
        // Clave para la detección de jaque: el peón amenaza su diagonal aunque esté vacía.
        var atacadas = peon.casillasAtacadas(de("e2"), Color.BLANCO, Tablero.vacio());

        assertThat(atacadas).containsExactlyInAnyOrder(de("d3"), de("f3"));
    }
}
