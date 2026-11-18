package com.ajedrez.dominio.reglas;

import com.ajedrez.dominio.modelo.Color;
import com.ajedrez.dominio.modelo.Movimiento;
import com.ajedrez.dominio.modelo.Pieza;
import com.ajedrez.dominio.modelo.Tablero;
import com.ajedrez.dominio.modelo.TipoPieza;
import org.junit.jupiter.api.Test;

import static com.ajedrez.dominio.modelo.Posicion.de;
import static org.assertj.core.api.Assertions.assertThat;

class PiezaDelJugadorEnTurnoTest {

    private final PiezaDelJugadorEnTurno regla = new PiezaDelJugadorEnTurno();

    @Test
    void moverLaPiezaDelColorEnTurno_esValido() {
        Tablero tablero = Tablero.vacio().colocar(de("e2"), new Pieza(TipoPieza.PEON, Color.BLANCO));
        var intento = new IntentoDeMovimiento(tablero, Color.BLANCO, Movimiento.de(de("e2"), de("e4")));

        assertThat(regla.verificar(intento)).isEmpty();
    }

    @Test
    void moverUnaPiezaRival_esInvalido() {
        Tablero tablero = Tablero.vacio().colocar(de("e7"), new Pieza(TipoPieza.PEON, Color.NEGRO));
        var intento = new IntentoDeMovimiento(tablero, Color.BLANCO, Movimiento.de(de("e7"), de("e5")));

        assertThat(regla.verificar(intento)).contains("Es el turno de las blancas.");
    }
}
