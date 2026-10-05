package com.ajedrez.dominio.reglas;

import com.ajedrez.interfaces.ICatalogoDeMovimientos;
import com.ajedrez.interfaces.IDetectorDeJaque;
import com.ajedrez.interfaces.IEjecutorDeMovimientos;
import com.ajedrez.interfaces.IValidadorDeMovimientos;

import java.util.Objects;

/**
 * Agrupa las piezas que definen "cómo se juega": qué mueve cada pieza, qué jugadas
 * son válidas, cómo se aplican y cuándo hay jaque. Una partida recibe esto ya armado.
 */
public record ReglasDelAjedrez(
        ICatalogoDeMovimientos catalogo,
        IValidadorDeMovimientos validador,
        IEjecutorDeMovimientos ejecutor,
        IDetectorDeJaque detector) {

    public ReglasDelAjedrez {
        Objects.requireNonNull(catalogo, "catalogo");
        Objects.requireNonNull(validador, "validador");
        Objects.requireNonNull(ejecutor, "ejecutor");
        Objects.requireNonNull(detector, "detector");
    }
}
