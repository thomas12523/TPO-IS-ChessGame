package com.ajedrez.dominio.reglas;

import com.ajedrez.dominio.modelo.Movimiento;
import com.ajedrez.dominio.modelo.Pieza;
import com.ajedrez.dominio.modelo.Tablero;
import com.ajedrez.interfaces.IEjecutorDeMovimientos;
/**
 * Aplica un movimiento (ya validado) sobre un tablero y devuelve el tablero resultante.
 * Resuelve la captura (la pieza que llega pisa a la que estaba) y la coronación.
 */
public final class EjecutorDeMovimientos implements IEjecutorDeMovimientos {

    @Override
    public Tablero ejecutar(Tablero tablero, Movimiento movimiento) {
        Pieza pieza = tablero.piezaEn(movimiento.origen())
                .orElseThrow(() -> new IllegalArgumentException("No hay pieza en " + movimiento.origen()));
        return tablero.mover(movimiento.origen(), movimiento.destino(), piezaQueLlega(pieza, movimiento));
    }

    @Override
    public Pieza piezaQueLlega(Pieza pieza, Movimiento movimiento) {
        if (Promocion.corresponde(pieza, movimiento.destino())) {
            return new Pieza(movimiento.promocionElegida().orElse(Promocion.POR_DEFECTO), pieza.color());
        }
        return pieza;
    }
}
