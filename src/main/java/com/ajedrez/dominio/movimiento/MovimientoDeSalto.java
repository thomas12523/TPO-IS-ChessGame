package com.ajedrez.dominio.movimiento;

import com.ajedrez.dominio.modelo.Color;
import com.ajedrez.dominio.modelo.Desplazamiento;
import com.ajedrez.dominio.modelo.Posicion;
import com.ajedrez.dominio.modelo.Tablero;
import com.ajedrez.interfaces.IReglaDeMovimiento;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Salta directamente a cada casilla indicada por los desplazamientos, sin importar
 * lo que haya en el camino. Caballo (en L) y rey (un paso en cualquier dirección).
 */
public final class MovimientoDeSalto implements IReglaDeMovimiento {

    private final List<Desplazamiento> saltos;

    public MovimientoDeSalto(List<Desplazamiento> saltos) {
        this.saltos = List.copyOf(saltos);
    }

    @Override
    public Set<Posicion> destinos(Posicion origen, Color color, Tablero tablero) {
        return saltos.stream()
                .flatMap(salto -> origen.desplazar(salto).stream())
                .filter(casilla -> tablero.estaVacia(casilla) || tablero.hayRivalEn(casilla, color))
                .collect(Collectors.toSet());
    }
}
