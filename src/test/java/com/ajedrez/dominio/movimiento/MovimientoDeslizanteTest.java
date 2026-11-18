package com.ajedrez.dominio.movimiento;

import com.ajedrez.dominio.modelo.Color;
import com.ajedrez.dominio.modelo.Pieza;
import com.ajedrez.dominio.modelo.Posicion;
import com.ajedrez.dominio.modelo.Tablero;
import com.ajedrez.dominio.modelo.TipoPieza;
import com.ajedrez.interfaces.IReglaDeMovimiento;
import org.junit.jupiter.api.Test;

import static com.ajedrez.dominio.modelo.Posicion.de;
import static org.assertj.core.api.Assertions.assertThat;

/** Torre y alfil: avanzan en línea recta hasta chocar con una pieza. */
class MovimientoDeslizanteTest {

    private final IReglaDeMovimiento torre = new MovimientoDeslizante(Direcciones.ORTOGONALES);
    private final IReglaDeMovimiento alfil = new MovimientoDeslizante(Direcciones.DIAGONALES);

    @Test
    void torre_enTableroVacio_llegaHastaElBordeEnLasCuatroDirecciones() {
        Tablero tablero = Tablero.vacio();

        var destinos = torre.destinos(de("d4"), Color.BLANCO, tablero);

        // 7 columnas + 7 filas (descontando d4) = 14
        assertThat(destinos).hasSize(14)
                .contains(de("d1"), de("d8"), de("a4"), de("h4"));
    }

    @Test
    void alfil_enTableroVacio_llegaHastaElBordeEnLasCuatroDiagonales() {
        Tablero tablero = Tablero.vacio();

        var destinos = alfil.destinos(de("d4"), Color.BLANCO, tablero);

        assertThat(destinos).contains(de("a1"), de("g7"), de("a7"), de("g1"));
    }

    @Test
    void destinos_seDetieneAntesDeUnaPiezaPropia_sinIncluirla() {
        Tablero tablero = Tablero.vacio()
                .colocar(de("d6"), new Pieza(TipoPieza.PEON, Color.BLANCO));

        var destinos = torre.destinos(de("d4"), Color.BLANCO, tablero);

        assertThat(destinos).contains(de("d5")).doesNotContain(de("d6"), de("d7"), de("d8"));
    }

    @Test
    void destinos_capturaUnaPiezaRival_yNoSigueMasAlla() {
        Tablero tablero = Tablero.vacio()
                .colocar(de("d6"), new Pieza(TipoPieza.PEON, Color.NEGRO));

        var destinos = torre.destinos(de("d4"), Color.BLANCO, tablero);

        assertThat(destinos).contains(de("d5"), de("d6")).doesNotContain(de("d7"), de("d8"));
    }

    @Test
    void destinos_noSaltaSobreUnaPiezaIntermedia_niPropiaNiRival() {
        Tablero conPiezaPropia = Tablero.vacio().colocar(de("d5"), new Pieza(TipoPieza.PEON, Color.BLANCO));
        Tablero conPiezaRival = Tablero.vacio().colocar(de("d5"), new Pieza(TipoPieza.PEON, Color.NEGRO));

        assertThat(torre.destinos(de("d4"), Color.BLANCO, conPiezaPropia)).doesNotContain(de("d6"), de("d7"));
        assertThat(torre.destinos(de("d4"), Color.BLANCO, conPiezaRival)).doesNotContain(de("d7"), de("d8"));
    }
}
