package com.ajedrez.dominio.reglas;

import com.ajedrez.dominio.modelo.Pieza;
import com.ajedrez.dominio.modelo.TipoPieza;
import com.ajedrez.interfaces.IReglaDeValidacion;

import java.util.Optional;

/** Solo se elige pieza de promoción al coronar un peón, y solo entre torre, caballo, alfil o reina. */
public final class PromocionCorrecta implements IReglaDeValidacion {

    @Override
    public Optional<String> verificar(IntentoDeMovimiento intento) {
        Optional<TipoPieza> elegida = intento.movimiento().promocionElegida();
        if (elegida.isEmpty()) {
            return Optional.empty();
        }
        Pieza pieza = intento.piezaMovida().orElseThrow();
        if (!Promocion.corresponde(pieza, intento.movimiento().destino())) {
            return Optional.of("Solo se puede elegir promoción cuando un peón llega a la última fila.");
        }
        if (!elegida.get().esPromocionable()) {
            return Optional.of("Un peón no puede convertirse en " + elegida.get().nombre() + ".");
        }
        return Optional.empty();
    }
}
