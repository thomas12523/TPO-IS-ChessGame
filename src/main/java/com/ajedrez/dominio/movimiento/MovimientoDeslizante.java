package com.ajedrez.dominio.movimiento;

import com.ajedrez.dominio.modelo.Color;
import com.ajedrez.dominio.modelo.Desplazamiento;
import com.ajedrez.dominio.modelo.Posicion;
import com.ajedrez.dominio.modelo.Tablero;
import com.ajedrez.interfaces.IReglaDeMovimiento;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Avanza en línea recta tantas casillas como quiera en cada dirección dada,
 * hasta chocar con una pieza (si es rival, puede capturarla). Torre y alfil.
 */
public final class MovimientoDeslizante implements IReglaDeMovimiento {

    private final List<Desplazamiento> direcciones;

    public MovimientoDeslizante(List<Desplazamiento> direcciones) {
        this.direcciones = List.copyOf(direcciones);
    }

    @Override
    public Set<Posicion> destinos(Posicion origen, Color color, Tablero tablero) {
        Set<Posicion> destinos = new HashSet<>();
        for (Desplazamiento direccion : direcciones) {
            Optional<Posicion> actual = origen.desplazar(direccion);
            while (actual.isPresent()) {
                Posicion casilla = actual.get();
                if (tablero.estaVacia(casilla)) {
                    destinos.add(casilla);
                    actual = casilla.desplazar(direccion);
                } else {
                    if (tablero.hayRivalEn(casilla, color)) {
                        destinos.add(casilla);
                    }
                    break;
                }
            }
        }
        return destinos;
    }
}
