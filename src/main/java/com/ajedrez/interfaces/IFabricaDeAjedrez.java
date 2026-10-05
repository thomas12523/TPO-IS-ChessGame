package com.ajedrez.interfaces;

import com.ajedrez.dominio.reglas.ReglasDelAjedrez;
/**
 * Abstract Factory: crea la familia de objetos que definen una variante de ajedrez
 * (movimientos, posición inicial y reglas). Cada variante implementa su propia fábrica
 * y garantiza que sus productos son compatibles entre sí; el resto del sistema solo
 * conoce esta interfaz.
 */
public interface IFabricaDeAjedrez {

    ICatalogoDeMovimientos crearCatalogo();

    IDisposicionInicial crearDisposicion();

    /** Reglas armadas sobre el catálogo de esta misma variante. */
    ReglasDelAjedrez crearReglas();
}
