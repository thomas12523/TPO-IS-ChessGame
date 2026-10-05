package com.ajedrez.dominio.modelo;

/**
 * Bando de un jugador. Conoce el sentido en el que avanzan sus peones
 * para que ninguna regla tenga que preguntar "¿soy blanco o negro?".
 */
public enum Color {
    BLANCO(1, "blancas"),
    NEGRO(-1, "negras");

    private final int sentidoDeAvance;
    private final String nombre;

    Color(int sentidoDeAvance, String nombre) {
        this.sentidoDeAvance = sentidoDeAvance;
        this.nombre = nombre;
    }

    public int sentidoDeAvance() {
        return sentidoDeAvance;
    }

    public String nombre() {
        return nombre;
    }

    public Color opuesto() {
        return this == BLANCO ? NEGRO : BLANCO;
    }
}
