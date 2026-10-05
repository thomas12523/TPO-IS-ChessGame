package com.ajedrez.dominio.reglas;

import com.ajedrez.dominio.modelo.Color;
import com.ajedrez.dominio.modelo.Pieza;
import com.ajedrez.dominio.modelo.Posicion;
import com.ajedrez.dominio.modelo.Tablero;
import com.ajedrez.dominio.modelo.TipoPieza;
import com.ajedrez.interfaces.ICatalogoDeMovimientos;
import com.ajedrez.interfaces.IDetectorDeJaque;

import java.util.Optional;

/**
 * Un rey está en jaque si alguna pieza rival ataca su casilla. Reutiliza las mismas
 * reglas de movimiento de las piezas: no hay una segunda definición de "cómo ataca un alfil".
 */
public final class DetectorDeJaque implements IDetectorDeJaque {

    private final ICatalogoDeMovimientos catalogo;

    public DetectorDeJaque(ICatalogoDeMovimientos catalogo) {
        this.catalogo = catalogo;
    }

    @Override
    public boolean estaEnJaque(Tablero tablero, Color color) {
        return ubicacionDelRey(tablero, color)
                .map(rey -> estaAtacada(rey, color.opuesto(), tablero))
                .orElse(false);
    }

    @Override
    public Optional<Posicion> ubicacionDelRey(Tablero tablero, Color color) {
        return tablero.ubicacionDe(new Pieza(TipoPieza.REY, color));
    }

    @Override
    public boolean estaAtacada(Posicion casilla, Color atacante, Tablero tablero) {
        return tablero.piezasDe(atacante).entrySet().stream()
                .anyMatch(entrada -> catalogo.reglaPara(entrada.getValue().tipo())
                        .casillasAtacadas(entrada.getKey(), atacante, tablero)
                        .contains(casilla));
    }
}
