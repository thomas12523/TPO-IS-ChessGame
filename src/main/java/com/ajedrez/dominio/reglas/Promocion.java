package com.ajedrez.dominio.reglas;

import com.ajedrez.dominio.modelo.Color;
import com.ajedrez.dominio.modelo.Pieza;
import com.ajedrez.dominio.modelo.Posicion;
import com.ajedrez.dominio.modelo.Tablero;
import com.ajedrez.dominio.modelo.TipoPieza;
/** Conocimiento sobre la coronación del peón, en un solo lugar. */
public final class Promocion {

    public static final TipoPieza POR_DEFECTO = TipoPieza.REINA;

    private Promocion() {
    }

    /** ¿Mover esta pieza a este destino es una coronación? */
    public static boolean corresponde(Pieza pieza, Posicion destino) {
        return pieza.es(TipoPieza.PEON) && destino.fila() == filaFinal(pieza.color());
    }

    private static int filaFinal(Color color) {
        return color == Color.BLANCO ? Tablero.DIMENSION - 1 : 0;
    }
}
