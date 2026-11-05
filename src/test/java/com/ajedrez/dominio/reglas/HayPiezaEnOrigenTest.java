package com.ajedrez.dominio.reglas;

import com.ajedrez.dominio.modelo.Color;
import com.ajedrez.dominio.modelo.Movimiento;
import com.ajedrez.dominio.modelo.Pieza;
import com.ajedrez.dominio.modelo.Tablero;
import com.ajedrez.dominio.modelo.TipoPieza;
import org.junit.jupiter.api.Test;

import static com.ajedrez.dominio.modelo.Posicion.de;
import static org.assertj.core.api.Assertions.assertThat;

class HayPiezaEnOrigenTest {

    private final HayPiezaEnOrigen regla = new HayPiezaEnOrigen();

    @Test
    void siHayPiezaEnElOrigen_noHayViolacion() {
        Tablero tablero = Tablero.vacio().colocar(de("e2"), new Pieza(TipoPieza.PEON, Color.BLANCO));
        var intento = new IntentoDeMovimiento(tablero, Color.BLANCO, Movimiento.de(de("e2"), de("e4")));

        assertThat(regla.verificar(intento)).isEmpty();
    }

    @Test
    void siElOrigenEstaVacio_devuelveUnMensajeDeError() {
        var intento = new IntentoDeMovimiento(Tablero.vacio(), Color.BLANCO, Movimiento.de(de("e2"), de("e4")));

        assertThat(regla.verificar(intento)).contains("No hay ninguna pieza en e2.");
    }
}
