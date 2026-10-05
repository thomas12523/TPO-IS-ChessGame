package com.ajedrez.dominio.modelo;

import java.util.Optional;

/**
 * Casilla del tablero (objeto de valor inmutable). Columna 0 = "a", fila 0 = "1".
 * Es imposible construir una posición fuera del tablero: la invariante vive acá
 * y no se repite en cada regla.
 */
public record Posicion(int columna, int fila) {

    private static final String LETRAS_DE_COLUMNA = "abcdefghijklmnopqrstuvwxyz";

    public Posicion {
        if (!Tablero.estaDentro(columna, fila)) {
            throw new IllegalArgumentException("Posición fuera del tablero: (" + columna + ", " + fila + ")");
        }
    }

    /** Crea una posición a partir de notación algebraica, p. ej. "e4". */
    public static Posicion de(String notacion) {
        if (notacion == null || notacion.length() != 2) {
            throw new IllegalArgumentException("Notación de casilla inválida: " + notacion);
        }
        int columna = LETRAS_DE_COLUMNA.indexOf(Character.toLowerCase(notacion.charAt(0)));
        int fila = notacion.charAt(1) - '1';
        if (columna < 0 || !Tablero.estaDentro(columna, fila)) {
            throw new IllegalArgumentException("Notación de casilla inválida: " + notacion);
        }
        return new Posicion(columna, fila);
    }

    /** La casilla resultante de aplicar el desplazamiento, o vacío si cae fuera del tablero. */
    public Optional<Posicion> desplazar(Desplazamiento desplazamiento) {
        int nuevaColumna = columna + desplazamiento.columnas();
        int nuevaFila = fila + desplazamiento.filas();
        return Tablero.estaDentro(nuevaColumna, nuevaFila)
                ? Optional.of(new Posicion(nuevaColumna, nuevaFila))
                : Optional.empty();
    }

    public String notacion() {
        return "" + LETRAS_DE_COLUMNA.charAt(columna) + (fila + 1);
    }

    @Override
    public String toString() {
        return notacion();
    }
}
