package com.ajedrez.dominio.reglas;

import com.ajedrez.dominio.modelo.Color;
import com.ajedrez.dominio.modelo.Movimiento;
import com.ajedrez.dominio.modelo.Pieza;
import com.ajedrez.dominio.modelo.Tablero;
import com.ajedrez.dominio.modelo.TipoPieza;
import org.junit.jupiter.api.Test;

import static com.ajedrez.dominio.modelo.Posicion.de;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EjecutorDeMovimientosTest {

    private final EjecutorDeMovimientos ejecutor = new EjecutorDeMovimientos();

    @Test
    void ejecutar_unMovimientoSinCaptura_quitaDelOrigenYPoneEnElDestino() {
        Tablero tablero = Tablero.vacio().colocar(de("e2"), new Pieza(TipoPieza.PEON, Color.BLANCO));

        Tablero resultado = ejecutor.ejecutar(tablero, Movimiento.de(de("e2"), de("e4")));

        assertThat(resultado.estaVacia(de("e2"))).isTrue();
        assertThat(resultado.piezaEn(de("e4"))).contains(new Pieza(TipoPieza.PEON, Color.BLANCO));
    }

    @Test
    void ejecutar_unaCaptura_laPiezaQueLlegaPisaALaRival() {
        Tablero tablero = Tablero.vacio()
                .colocar(de("e4"), new Pieza(TipoPieza.PEON, Color.BLANCO))
                .colocar(de("d5"), new Pieza(TipoPieza.PEON, Color.NEGRO));

        Tablero resultado = ejecutor.ejecutar(tablero, Movimiento.de(de("e4"), de("d5")));

        assertThat(resultado.piezaEn(de("d5"))).contains(new Pieza(TipoPieza.PEON, Color.BLANCO));
        assertThat(resultado.todasLasPiezas()).hasSize(1);
    }

    @Test
    void ejecutar_siNoHayPiezaEnElOrigen_lanzaExcepcion() {
        assertThatThrownBy(() -> ejecutor.ejecutar(Tablero.vacio(), Movimiento.de(de("e2"), de("e4"))))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void piezaQueLlega_unPeonQueNoLlegaALaUltimaFila_siguePeon() {
        Pieza peon = new Pieza(TipoPieza.PEON, Color.BLANCO);

        Pieza queLlega = ejecutor.piezaQueLlega(peon, Movimiento.de(de("e2"), de("e4")));

        assertThat(queLlega).isEqualTo(peon);
    }

    @Test
    void piezaQueLlega_peonQueCoronaSinElegirPromocion_seConvierteEnReina() {
        Pieza peon = new Pieza(TipoPieza.PEON, Color.BLANCO);

        Pieza queLlega = ejecutor.piezaQueLlega(peon, Movimiento.de(de("e7"), de("e8")));

        assertThat(queLlega).isEqualTo(new Pieza(TipoPieza.REINA, Color.BLANCO));
    }

    @Test
    void piezaQueLlega_peonQueCoronaConPromocionElegida_seConvierteEnEsaPieza() {
        Pieza peon = new Pieza(TipoPieza.PEON, Color.BLANCO);

        Pieza queLlega = ejecutor.piezaQueLlega(peon, new Movimiento(de("e7"), de("e8"), TipoPieza.CABALLO));

        assertThat(queLlega).isEqualTo(new Pieza(TipoPieza.CABALLO, Color.BLANCO));
    }

    @Test
    void piezaQueLlega_peonNegroQueCorona_esEnLaFila1() {
        Pieza peon = new Pieza(TipoPieza.PEON, Color.NEGRO);

        Pieza queLlega = ejecutor.piezaQueLlega(peon, Movimiento.de(de("e2"), de("e1")));

        assertThat(queLlega).isEqualTo(new Pieza(TipoPieza.REINA, Color.NEGRO));
    }
}
