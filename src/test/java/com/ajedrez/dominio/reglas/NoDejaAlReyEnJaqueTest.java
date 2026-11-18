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

/**
 * Se simula la jugada sobre una copia del tablero (gracias a que es inmutable) y se
 * verifica si el propio rey queda en jaque. No valida geometría: eso es otra regla.
 */
class NoDejaAlReyEnJaqueTest {

    private final NoDejaAlReyEnJaque regla =
            new NoDejaAlReyEnJaque(new EjecutorDeMovimientos(), new DetectorDeJaque(new CatalogoEstandar()));

    @Test
    void moverUnaPiezaClavada_quedaExpuestoElRey_esInvalido() {
        // Rey blanco en e1, torre blanca en e2 (tapa), torre negra en e8: mover la torre
        // blanca fuera de la columna "e" expone al rey.
        Tablero tablero = Tablero.vacio()
                .colocar(de("e1"), new Pieza(TipoPieza.REY, Color.BLANCO))
                .colocar(de("e2"), new Pieza(TipoPieza.TORRE, Color.BLANCO))
                .colocar(de("e8"), new Pieza(TipoPieza.TORRE, Color.NEGRO));
        var intento = new IntentoDeMovimiento(tablero, Color.BLANCO, Movimiento.de(de("e2"), de("d2")));

        assertThat(regla.verificar(intento)).contains("Esa jugada dejaría a tu rey en jaque.");
    }

    @Test
    void moverLaPiezaClavadaDentroDeLaMismaLinea_siSigueTapando_esValido() {
        Tablero tablero = Tablero.vacio()
                .colocar(de("e1"), new Pieza(TipoPieza.REY, Color.BLANCO))
                .colocar(de("e2"), new Pieza(TipoPieza.TORRE, Color.BLANCO))
                .colocar(de("e8"), new Pieza(TipoPieza.TORRE, Color.NEGRO));
        var intento = new IntentoDeMovimiento(tablero, Color.BLANCO, Movimiento.de(de("e2"), de("e5")));

        assertThat(regla.verificar(intento)).isEmpty();
    }

    @Test
    void siYaEstaEnJaqueYLaJugadaNoLoResuelve_avisaQueSigueEnJaque() {
        // Rey blanco en e1 ya en jaque por la torre negra en e8 (nada lo tapa);
        // mover una pieza que no afecta esa amenaza no resuelve nada.
        Tablero tablero = Tablero.vacio()
                .colocar(de("e1"), new Pieza(TipoPieza.REY, Color.BLANCO))
                .colocar(de("e8"), new Pieza(TipoPieza.TORRE, Color.NEGRO))
                .colocar(de("a1"), new Pieza(TipoPieza.TORRE, Color.BLANCO));
        var intento = new IntentoDeMovimiento(tablero, Color.BLANCO, Movimiento.de(de("a1"), de("a4")));

        assertThat(regla.verificar(intento)).contains("Tu rey está en jaque: esa jugada no lo resuelve.");
    }

    @Test
    void moverElReyFueraDeLaLineaDeAtaque_resuelveElJaque_esValido() {
        Tablero tablero = Tablero.vacio()
                .colocar(de("e1"), new Pieza(TipoPieza.REY, Color.BLANCO))
                .colocar(de("e8"), new Pieza(TipoPieza.TORRE, Color.NEGRO));
        var intento = new IntentoDeMovimiento(tablero, Color.BLANCO, Movimiento.de(de("e1"), de("d1")));

        assertThat(regla.verificar(intento)).isEmpty();
    }
}
