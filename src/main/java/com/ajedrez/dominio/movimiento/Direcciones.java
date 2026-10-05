package com.ajedrez.dominio.movimiento;

import com.ajedrez.dominio.modelo.Desplazamiento;

import java.util.List;
import java.util.stream.Stream;

/** Conjuntos de desplazamientos reutilizables para armar reglas de movimiento. */
public final class Direcciones {

    public static final List<Desplazamiento> ORTOGONALES = List.of(
            new Desplazamiento(1, 0), new Desplazamiento(-1, 0),
            new Desplazamiento(0, 1), new Desplazamiento(0, -1));

    public static final List<Desplazamiento> DIAGONALES = List.of(
            new Desplazamiento(1, 1), new Desplazamiento(1, -1),
            new Desplazamiento(-1, 1), new Desplazamiento(-1, -1));

    public static final List<Desplazamiento> TODAS =
            Stream.concat(ORTOGONALES.stream(), DIAGONALES.stream()).toList();

    public static final List<Desplazamiento> SALTOS_DE_CABALLO = List.of(
            new Desplazamiento(1, 2), new Desplazamiento(2, 1),
            new Desplazamiento(2, -1), new Desplazamiento(1, -2),
            new Desplazamiento(-1, -2), new Desplazamiento(-2, -1),
            new Desplazamiento(-2, 1), new Desplazamiento(-1, 2));

    private Direcciones() {
    }
}
