package com.ajedrez.interfaces;

import com.ajedrez.dominio.modelo.Color;
import com.ajedrez.dominio.modelo.Posicion;
import com.ajedrez.dominio.modelo.Tablero;

import java.util.Set;

/**
 * Estrategia que describe cómo se desplaza un tipo de pieza (patrón Strategy).
 * Devuelve los destinos que la geometría de la pieza permite, sin considerar
 * reglas globales como el turno o dejar al propio rey en jaque.
 */
public interface IReglaDeMovimiento {

    Set<Posicion> destinos(Posicion origen, Color color, Tablero tablero);

    /**
     * Casillas que la pieza amenaza. Para casi todas coincide con sus destinos;
     * el peón es la excepción (avanza recto pero captura en diagonal).
     */
    default Set<Posicion> casillasAtacadas(Posicion origen, Color color, Tablero tablero) {
        return destinos(origen, color, tablero);
    }
}
