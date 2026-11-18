package com.ajedrez.dominio.modelo;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/** Posicion: la invariante "no existe una casilla fuera del tablero" vive acá. */
class PosicionTest {

    @Test
    void de_notacionValida_calculaColumnaYFilaCorrectas() {
        Posicion e4 = Posicion.de("e4");

        assertThat(e4.columna()).isEqualTo(4); // e -> índice 4
        assertThat(e4.fila()).isEqualTo(3);    // fila 4 -> índice 3
    }

    @Test
    void de_esInsensibleAMayusculas() {
        assertThat(Posicion.de("E4")).isEqualTo(Posicion.de("e4"));
    }

    @Test
    void de_esquinas_sonValidas() {
        assertThat(Posicion.de("a1")).isEqualTo(new Posicion(0, 0));
        assertThat(Posicion.de("h8")).isEqualTo(new Posicion(7, 7));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"e", "e44", "z1", "a9", "a0", "i1", "e-"})
    void de_notacionInvalida_lanzaIllegalArgumentException(String notacion) {
        assertThatThrownBy(() -> Posicion.de(notacion))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void constructor_fueraDelTablero_lanzaIllegalArgumentException() {
        assertThatThrownBy(() -> new Posicion(8, 0)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new Posicion(0, -1)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void desplazar_dentroDelTablero_devuelveLaNuevaPosicion() {
        Posicion e4 = Posicion.de("e4");

        assertThat(e4.desplazar(new Desplazamiento(1, 1))).contains(Posicion.de("f5"));
    }

    @Test
    void desplazar_fueraDelTablero_devuelveOptionalVacio() {
        Posicion a1 = Posicion.de("a1");

        assertThat(a1.desplazar(new Desplazamiento(-1, 0))).isEmpty();
        assertThat(a1.desplazar(new Desplazamiento(0, -1))).isEmpty();
    }

    @Test
    void notacion_esInversaDeDe() {
        for (String casilla : new String[] {"a1", "e4", "h8", "d7"}) {
            assertThat(Posicion.de(casilla).notacion()).isEqualTo(casilla);
        }
    }
}
