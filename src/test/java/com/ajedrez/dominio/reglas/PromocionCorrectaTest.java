package com.ajedrez.dominio.reglas;

import com.ajedrez.dominio.modelo.Color;
import com.ajedrez.dominio.modelo.Movimiento;
import com.ajedrez.dominio.modelo.Pieza;
import com.ajedrez.dominio.modelo.Tablero;
import com.ajedrez.dominio.modelo.TipoPieza;
import org.junit.jupiter.api.Test;

import static com.ajedrez.dominio.modelo.Posicion.de;
import static org.assertj.core.api.Assertions.assertThat;

class PromocionCorrectaTest {

    private final PromocionCorrecta regla = new PromocionCorrecta();

    @Test
    void sinPromocionElegida_esSiempreValido() {
        Tablero tablero = Tablero.vacio().colocar(de("e2"), new Pieza(TipoPieza.PEON, Color.BLANCO));
        var intento = new IntentoDeMovimiento(tablero, Color.BLANCO,
                Movimiento.de(de("e2"), de("e4")));

        assertThat(regla.verificar(intento)).isEmpty();
    }

    @Test
    void elegirPromocion_enUnMovimientoQueNoCorona_esInvalido() {
        Tablero tablero = Tablero.vacio().colocar(de("e2"), new Pieza(TipoPieza.PEON, Color.BLANCO));
        var intento = new IntentoDeMovimiento(tablero, Color.BLANCO,
                new Movimiento(de("e2"), de("e3"), TipoPieza.REINA));

        assertThat(regla.verificar(intento))
                .contains("Solo se puede elegir promoción cuando un peón llega a la última fila.");
    }

    @Test
    void elegirUnaPiezaNoPromocionable_esInvalido() {
        Tablero tablero = Tablero.vacio().colocar(de("e7"), new Pieza(TipoPieza.PEON, Color.BLANCO));
        var intento = new IntentoDeMovimiento(tablero, Color.BLANCO,
                new Movimiento(de("e7"), de("e8"), TipoPieza.REY));

        assertThat(regla.verificar(intento)).contains("Un peón no puede convertirse en rey.");
    }

    @Test
    void coronarEligiendoUnaPiezaValida_esValido() {
        Tablero tablero = Tablero.vacio().colocar(de("e7"), new Pieza(TipoPieza.PEON, Color.BLANCO));
        var intento = new IntentoDeMovimiento(tablero, Color.BLANCO,
                new Movimiento(de("e7"), de("e8"), TipoPieza.CABALLO));

        assertThat(regla.verificar(intento)).isEmpty();
    }
}
