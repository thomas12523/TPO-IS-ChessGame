package com.ajedrez.interfaces;

import com.ajedrez.dominio.modelo.Color;
import com.ajedrez.dominio.modelo.Posicion;
import com.ajedrez.dominio.modelo.Tablero;

import java.util.Optional;

/** Responde si un rey está amenazado. */
public interface IDetectorDeJaque {

    boolean estaEnJaque(Tablero tablero, Color color);

    Optional<Posicion> ubicacionDelRey(Tablero tablero, Color color);

    boolean estaAtacada(Posicion casilla, Color atacante, Tablero tablero);
}
