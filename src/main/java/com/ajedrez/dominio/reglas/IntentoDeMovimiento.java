package com.ajedrez.dominio.reglas;

import com.ajedrez.dominio.modelo.Color;
import com.ajedrez.dominio.modelo.Movimiento;
import com.ajedrez.dominio.modelo.Pieza;
import com.ajedrez.dominio.modelo.Tablero;

import java.util.Optional;

/** Todo lo que una regla necesita saber para juzgar una jugada: el tablero, de quién es el turno y qué se quiere mover. */
public record IntentoDeMovimiento(Tablero tablero, Color turno, Movimiento movimiento) {

    public Optional<Pieza> piezaMovida() {
        return tablero.piezaEn(movimiento.origen());
    }
}
