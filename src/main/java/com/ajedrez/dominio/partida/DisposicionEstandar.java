package com.ajedrez.dominio.partida;

import com.ajedrez.dominio.modelo.Color;
import com.ajedrez.dominio.modelo.Pieza;
import com.ajedrez.dominio.modelo.Posicion;
import com.ajedrez.dominio.modelo.Tablero;
import com.ajedrez.dominio.modelo.TipoPieza;
import com.ajedrez.interfaces.IDisposicionInicial;

import java.util.List;

/** La posición inicial clásica: blancas en las filas 1 y 2, negras en las 7 y 8. */
public final class DisposicionEstandar implements IDisposicionInicial {

    private static final List<TipoPieza> FILA_TRASERA = List.of(
            TipoPieza.TORRE, TipoPieza.CABALLO, TipoPieza.ALFIL, TipoPieza.REINA,
            TipoPieza.REY, TipoPieza.ALFIL, TipoPieza.CABALLO, TipoPieza.TORRE);

    @Override
    public Tablero preparar() {
        Tablero tablero = Tablero.vacio();
        tablero = ubicarBando(tablero, Color.BLANCO, 0, 1);
        tablero = ubicarBando(tablero, Color.NEGRO, Tablero.DIMENSION - 1, Tablero.DIMENSION - 2);
        return tablero;
    }

    private Tablero ubicarBando(Tablero tablero, Color color, int filaTrasera, int filaDePeones) {
        for (int columna = 0; columna < Tablero.DIMENSION; columna++) {
            tablero = tablero
                    .colocar(new Posicion(columna, filaTrasera), new Pieza(FILA_TRASERA.get(columna), color))
                    .colocar(new Posicion(columna, filaDePeones), new Pieza(TipoPieza.PEON, color));
        }
        return tablero;
    }
}
