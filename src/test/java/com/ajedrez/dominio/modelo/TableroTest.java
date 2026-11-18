package com.ajedrez.dominio.modelo;

import org.junit.jupiter.api.Test;

import static com.ajedrez.dominio.modelo.Posicion.de;
import static org.assertj.core.api.Assertions.assertThat;

/** Tablero: inmutable, no conoce reglas de ajedrez, solo "qué pieza hay en cada casilla". */
class TableroTest {

    private static final Pieza TORRE_BLANCA = new Pieza(TipoPieza.TORRE, Color.BLANCO);
    private static final Pieza PEON_NEGRO = new Pieza(TipoPieza.PEON, Color.NEGRO);

    @Test
    void vacio_noTieneNingunaPieza() {
        assertThat(Tablero.vacio().todasLasPiezas()).isEmpty();
    }

    @Test
    void colocar_agregaLaPiezaSinMutarElTableroOriginal() {
        Tablero original = Tablero.vacio();
        Tablero conTorre = original.colocar(de("a1"), TORRE_BLANCA);

        assertThat(original.estaVacia(de("a1"))).isTrue();
        assertThat(conTorre.piezaEn(de("a1"))).contains(TORRE_BLANCA);
    }

    @Test
    void mover_sacaLaPiezaDelOrigenYLaDejaEnElDestino() {
        Tablero tablero = Tablero.vacio().colocar(de("a1"), TORRE_BLANCA);

        Tablero resultado = tablero.mover(de("a1"), de("a4"), TORRE_BLANCA);

        assertThat(resultado.estaVacia(de("a1"))).isTrue();
        assertThat(resultado.piezaEn(de("a4"))).contains(TORRE_BLANCA);
    }

    @Test
    void mover_elTableroOriginalNoCambia() {
        Tablero tablero = Tablero.vacio().colocar(de("a1"), TORRE_BLANCA);

        tablero.mover(de("a1"), de("a4"), TORRE_BLANCA);

        assertThat(tablero.piezaEn(de("a1"))).contains(TORRE_BLANCA);
        assertThat(tablero.estaVacia(de("a4"))).isTrue();
    }

    @Test
    void mover_haciaUnaCasillaOcupada_resuelveLaCaptura() {
        Tablero tablero = Tablero.vacio()
                .colocar(de("a1"), TORRE_BLANCA)
                .colocar(de("a8"), PEON_NEGRO);

        Tablero resultado = tablero.mover(de("a1"), de("a8"), TORRE_BLANCA);

        assertThat(resultado.piezaEn(de("a8"))).contains(TORRE_BLANCA);
        assertThat(resultado.todasLasPiezas()).hasSize(1);
    }

    @Test
    void hayRivalEn_true_soloSiHayUnaPiezaDeOtroColor() {
        Tablero tablero = Tablero.vacio().colocar(de("a8"), PEON_NEGRO);

        assertThat(tablero.hayRivalEn(de("a8"), Color.BLANCO)).isTrue();
        assertThat(tablero.hayRivalEn(de("a8"), Color.NEGRO)).isFalse();
        assertThat(tablero.hayRivalEn(de("a1"), Color.BLANCO)).isFalse();
    }

    @Test
    void piezasDe_filtraSoloLasDelColorPedido() {
        Tablero tablero = Tablero.vacio()
                .colocar(de("a1"), TORRE_BLANCA)
                .colocar(de("a8"), PEON_NEGRO);

        assertThat(tablero.piezasDe(Color.BLANCO)).containsOnlyKeys(de("a1"));
        assertThat(tablero.piezasDe(Color.NEGRO)).containsOnlyKeys(de("a8"));
    }

    @Test
    void ubicacionDe_encuentraLaUnicaPiezaDeEseTipoYColor() {
        Tablero tablero = Tablero.vacio().colocar(de("e1"), new Pieza(TipoPieza.REY, Color.BLANCO));

        assertThat(tablero.ubicacionDe(new Pieza(TipoPieza.REY, Color.BLANCO))).contains(de("e1"));
    }

    @Test
    void ubicacionDe_siNoEstaEnElTablero_devuelveVacio() {
        assertThat(Tablero.vacio().ubicacionDe(new Pieza(TipoPieza.REY, Color.BLANCO))).isEmpty();
    }

    @Test
    void estaDentro_respetaLosLimitesDelTableroDe8x8() {
        assertThat(Tablero.estaDentro(0, 0)).isTrue();
        assertThat(Tablero.estaDentro(7, 7)).isTrue();
        assertThat(Tablero.estaDentro(8, 0)).isFalse();
        assertThat(Tablero.estaDentro(0, -1)).isFalse();
    }
}
