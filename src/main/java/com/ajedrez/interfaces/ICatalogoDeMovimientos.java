package com.ajedrez.interfaces;

import com.ajedrez.dominio.modelo.TipoPieza;
/**
 * Decide qué regla de movimiento corresponde a cada tipo de pieza.
 * Es una abstracción: una variante de ajedrez (p. ej. piezas de fantasía)
 * solo necesita otro catálogo, sin tocar el resto del sistema.
 */
public interface ICatalogoDeMovimientos {

    IReglaDeMovimiento reglaPara(TipoPieza tipo);
}
