package com.ajedrez.dominio.reglas;

import com.ajedrez.dominio.modelo.Color;
import com.ajedrez.dominio.modelo.Pieza;
import com.ajedrez.dominio.modelo.Tablero;
import com.ajedrez.dominio.modelo.TipoPieza;
import com.ajedrez.dominio.movimiento.CatalogoEstandar;
import org.junit.jupiter.api.Test;

import static com.ajedrez.dominio.modelo.Posicion.de;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * El detector reutiliza las mismas reglas de movimiento que validan jugadas
 * (no hay una segunda definición de "cómo ataca cada pieza").
 */
class DetectorDeJaqueTest {

    private final DetectorDeJaque detector = new DetectorDeJaque(new CatalogoEstandar());

    @Test
    void reyAtacadoPorUnaTorre_enSuMismaColumna_estaEnJaque() {
        Tablero tablero = Tablero.vacio()
                .colocar(de("e1"), new Pieza(TipoPieza.REY, Color.BLANCO))
                .colocar(de("e8"), new Pieza(TipoPieza.TORRE, Color.NEGRO));

        assertThat(detector.estaEnJaque(tablero, Color.BLANCO)).isTrue();
    }

    @Test
    void reyAtacadoPorUnAlfil_enDiagonal_estaEnJaque() {
        Tablero tablero = Tablero.vacio()
                .colocar(de("e1"), new Pieza(TipoPieza.REY, Color.BLANCO))
                .colocar(de("a5"), new Pieza(TipoPieza.ALFIL, Color.NEGRO));

        assertThat(detector.estaEnJaque(tablero, Color.BLANCO)).isTrue();
    }

    @Test
    void reyAtacadoPorLaReina_estaEnJaque() {
        Tablero tablero = Tablero.vacio()
                .colocar(de("e1"), new Pieza(TipoPieza.REY, Color.BLANCO))
                .colocar(de("e4"), new Pieza(TipoPieza.REINA, Color.NEGRO));

        assertThat(detector.estaEnJaque(tablero, Color.BLANCO)).isTrue();
    }

    @Test
    void reyAtacadoPorUnCaballo_estaEnJaque() {
        Tablero tablero = Tablero.vacio()
                .colocar(de("e1"), new Pieza(TipoPieza.REY, Color.BLANCO))
                .colocar(de("d3"), new Pieza(TipoPieza.CABALLO, Color.NEGRO));

        assertThat(detector.estaEnJaque(tablero, Color.BLANCO)).isTrue();
    }

    @Test
    void reyAtacadoPorUnPeon_enDiagonalHaciaElRey_estaEnJaque() {
        // Peón negro en d2 avanza hacia filas menores y ataca c1/e1.
        Tablero tablero = Tablero.vacio()
                .colocar(de("e1"), new Pieza(TipoPieza.REY, Color.BLANCO))
                .colocar(de("d2"), new Pieza(TipoPieza.PEON, Color.NEGRO));

        assertThat(detector.estaEnJaque(tablero, Color.BLANCO)).isTrue();
    }

    @Test
    void unPeonQueSoloPuedeAvanzarRecto_noDaJaqueAlReyQueTieneEnfrente() {
        // Un peón amenaza en diagonal, no de frente: el rey en la misma columna está a salvo.
        Tablero tablero = Tablero.vacio()
                .colocar(de("e1"), new Pieza(TipoPieza.REY, Color.BLANCO))
                .colocar(de("e2"), new Pieza(TipoPieza.PEON, Color.NEGRO));

        assertThat(detector.estaEnJaque(tablero, Color.BLANCO)).isFalse();
    }

    @Test
    void reyNoAtacadoPorNinguna_noEstaEnJaque() {
        Tablero tablero = Tablero.vacio()
                .colocar(de("e1"), new Pieza(TipoPieza.REY, Color.BLANCO))
                .colocar(de("a8"), new Pieza(TipoPieza.TORRE, Color.NEGRO));

        assertThat(detector.estaEnJaque(tablero, Color.BLANCO)).isFalse();
    }

    @Test
    void unaPiezaBloqueandoLaLineaDeAtaque_quitaElJaque() {
        Tablero tablero = Tablero.vacio()
                .colocar(de("e1"), new Pieza(TipoPieza.REY, Color.BLANCO))
                .colocar(de("e4"), new Pieza(TipoPieza.PEON, Color.BLANCO))
                .colocar(de("e8"), new Pieza(TipoPieza.TORRE, Color.NEGRO));

        assertThat(detector.estaEnJaque(tablero, Color.BLANCO)).isFalse();
    }

    @Test
    void siElReyNoEstaEnElTablero_noEstaEnJaque() {
        assertThat(detector.estaEnJaque(Tablero.vacio(), Color.BLANCO)).isFalse();
    }

    @Test
    void ubicacionDelRey_devuelveSuCasillaActual() {
        Tablero tablero = Tablero.vacio().colocar(de("g1"), new Pieza(TipoPieza.REY, Color.BLANCO));

        assertThat(detector.ubicacionDelRey(tablero, Color.BLANCO)).contains(de("g1"));
    }
}
