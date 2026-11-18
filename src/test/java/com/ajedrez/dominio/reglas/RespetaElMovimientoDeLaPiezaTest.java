package com.ajedrez.dominio.reglas;

import com.ajedrez.dominio.modelo.Color;
import com.ajedrez.dominio.modelo.Movimiento;
import com.ajedrez.dominio.modelo.Pieza;
import com.ajedrez.dominio.modelo.Tablero;
import com.ajedrez.dominio.modelo.TipoPieza;
import com.ajedrez.dominio.movimiento.CatalogoEstandar;
import org.junit.jupiter.api.Test;

import static com.ajedrez.dominio.modelo.Posicion.de;
import static org.assertj.core.api.Assertions.assertThat;

class RespetaElMovimientoDeLaPiezaTest {

    private final RespetaElMovimientoDeLaPieza regla = new RespetaElMovimientoDeLaPieza(new CatalogoEstandar());

    @Test
    void destinoAlcanzableSegunLaGeometriaDeLaPieza_esValido() {
        Tablero tablero = Tablero.vacio().colocar(de("a1"), new Pieza(TipoPieza.TORRE, Color.BLANCO));
        var intento = new IntentoDeMovimiento(tablero, Color.BLANCO, Movimiento.de(de("a1"), de("a4")));

        assertThat(regla.verificar(intento)).isEmpty();
    }

    @Test
    void destinoNoAlcanzable_esInvalido() {
        Tablero tablero = Tablero.vacio().colocar(de("a1"), new Pieza(TipoPieza.TORRE, Color.BLANCO));
        var intento = new IntentoDeMovimiento(tablero, Color.BLANCO, Movimiento.de(de("a1"), de("b2")));

        assertThat(regla.verificar(intento)).contains("La torre no puede moverse de a1 a b2.");
    }

    @Test
    void destinoConUnaPiezaPropia_noEsAlcanzable_yEsInvalido() {
        Tablero tablero = Tablero.vacio()
                .colocar(de("a1"), new Pieza(TipoPieza.TORRE, Color.BLANCO))
                .colocar(de("a4"), new Pieza(TipoPieza.PEON, Color.BLANCO));
        var intento = new IntentoDeMovimiento(tablero, Color.BLANCO, Movimiento.de(de("a1"), de("a4")));

        assertThat(regla.verificar(intento)).isPresent();
    }

    @Test
    void capturarUnaPiezaRivalEnUnDestinoAlcanzable_esValido() {
        Tablero tablero = Tablero.vacio()
                .colocar(de("a1"), new Pieza(TipoPieza.TORRE, Color.BLANCO))
                .colocar(de("a4"), new Pieza(TipoPieza.PEON, Color.NEGRO));
        var intento = new IntentoDeMovimiento(tablero, Color.BLANCO, Movimiento.de(de("a1"), de("a4")));

        assertThat(regla.verificar(intento)).isEmpty();
    }
}
