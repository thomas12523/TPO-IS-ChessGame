package com.ajedrez.dominio.modelo;

/**
 * Tipos de pieza. Deliberadamente NO contiene la lógica de movimiento:
 * cómo se mueve cada tipo lo decide un {@code ICatalogoDeMovimientos}, que se inyecta.
 */
public enum TipoPieza {
    PEON("el", "peón", ""),
    TORRE("la", "torre", "T"),
    CABALLO("el", "caballo", "C"),
    ALFIL("el", "alfil", "A"),
    REINA("la", "reina", "D"),
    REY("el", "rey", "R");

    private final String articulo;
    private final String nombre;
    private final String simbolo;

    TipoPieza(String articulo, String nombre, String simbolo) {
        this.articulo = articulo;
        this.nombre = nombre;
        this.simbolo = simbolo;
    }

    public String nombre() {
        return nombre;
    }

    /** Nombre con su artículo, para mensajes: "la torre", "el alfil". */
    public String nombreConArticulo() {
        return articulo + " " + nombre;
    }

    /** Símbolo en notación algebraica española (el peón no lleva). */
    public String simbolo() {
        return simbolo;
    }

    /** Piezas en las que puede convertirse un peón al coronar. */
    public boolean esPromocionable() {
        return this == TORRE || this == CABALLO || this == ALFIL || this == REINA;
    }
}
