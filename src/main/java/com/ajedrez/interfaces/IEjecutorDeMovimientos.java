package com.ajedrez.interfaces;

import com.ajedrez.dominio.modelo.Movimiento;
import com.ajedrez.dominio.modelo.Pieza;
import com.ajedrez.dominio.modelo.Tablero;
/** Aplica un movimiento ya validado sobre un tablero y devuelve el tablero resultante. */
public interface IEjecutorDeMovimientos {

    Tablero ejecutar(Tablero tablero, Movimiento movimiento);

    Pieza piezaQueLlega(Pieza pieza, Movimiento movimiento);
}
