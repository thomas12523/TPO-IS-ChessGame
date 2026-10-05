package com.ajedrez.interfaces;

import com.ajedrez.dominio.modelo.Tablero;
/** Cómo se arma el tablero al empezar. Permite variantes (Chess960, problemas, finales) sin tocar la partida. */
public interface IDisposicionInicial {

    Tablero preparar();
}
