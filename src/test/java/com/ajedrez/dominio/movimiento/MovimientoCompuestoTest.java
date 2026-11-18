package com.ajedrez.dominio.movimiento;

import com.ajedrez.dominio.modelo.Color;
import com.ajedrez.dominio.modelo.Posicion;
import com.ajedrez.dominio.modelo.Tablero;
import com.ajedrez.interfaces.IReglaDeMovimiento;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static com.ajedrez.dominio.modelo.Posicion.de;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * La reina no es una clase nueva: es "lo que hace la torre" + "lo que hace el alfil"
 * unidas por composición (Composite).
 */
class MovimientoCompuestoTest {

    private final IReglaDeMovimiento torre = new MovimientoDeslizante(Direcciones.ORTOGONALES);
    private final IReglaDeMovimiento alfil = new MovimientoDeslizante(Direcciones.DIAGONALES);
    private final IReglaDeMovimiento reina = new MovimientoCompuesto(torre, alfil);

    @Test
    void destinos_esLaUnionDeLosDestinosDeTorreYAlfil() {
        Tablero tablero = Tablero.vacio();

        Set<Posicion> esperado = new HashSet<>(torre.destinos(de("d4"), Color.BLANCO, tablero));
        esperado.addAll(alfil.destinos(de("d4"), Color.BLANCO, tablero));

        assertThat(reina.destinos(de("d4"), Color.BLANCO, tablero)).isEqualTo(esperado);
    }

    @Test
    void destinos_incluyeMovimientosRectosYDiagonales() {
        var destinos = reina.destinos(de("d4"), Color.BLANCO, Tablero.vacio());

        assertThat(destinos).contains(de("d8"), de("a4")) // torre
                .contains(de("a1"), de("g7"));            // alfil
    }
}
