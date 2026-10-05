package com.ajedrez.dominio.partida;

import com.ajedrez.dominio.modelo.Movimiento;
import com.ajedrez.dominio.modelo.Pieza;

import java.util.Optional;

/** Registro inmutable de una jugada ya hecha, para el historial. */
public record JugadaRealizada(
        Movimiento movimiento,
        Pieza piezaMovida,
        Pieza piezaFinal,
        Pieza piezaCapturada,
        boolean dioJaque) {

    public Optional<Pieza> captura() {
        return Optional.ofNullable(piezaCapturada);
    }

    public boolean fuePromocion() {
        return !piezaMovida.equals(piezaFinal);
    }

    /** Notación algebraica larga en español, p. ej. "Cg1-f3", "e7xd8=D+". */
    public String notacion() {
        StringBuilder texto = new StringBuilder()
                .append(piezaMovida.tipo().simbolo())
                .append(movimiento.origen())
                .append(piezaCapturada != null ? "x" : "-")
                .append(movimiento.destino());
        if (fuePromocion()) {
            texto.append('=').append(piezaFinal.tipo().simbolo());
        }
        if (dioJaque) {
            texto.append('+');
        }
        return texto.toString();
    }
}
