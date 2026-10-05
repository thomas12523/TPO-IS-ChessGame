package com.ajedrez.dominio.reglas;

import com.ajedrez.dominio.modelo.Movimiento;
import com.ajedrez.dominio.modelo.Pieza;
import com.ajedrez.interfaces.ICatalogoDeMovimientos;
import com.ajedrez.interfaces.IReglaDeValidacion;

import java.util.Optional;

/** El destino tiene que ser alcanzable según la forma de moverse de la pieza (incluye no capturar piezas propias). */
public final class RespetaElMovimientoDeLaPieza implements IReglaDeValidacion {

    private final ICatalogoDeMovimientos catalogo;

    public RespetaElMovimientoDeLaPieza(ICatalogoDeMovimientos catalogo) {
        this.catalogo = catalogo;
    }

    @Override
    public Optional<String> verificar(IntentoDeMovimiento intento) {
        Pieza pieza = intento.piezaMovida().orElseThrow();
        Movimiento movimiento = intento.movimiento();
        boolean alcanzable = catalogo.reglaPara(pieza.tipo())
                .destinos(movimiento.origen(), pieza.color(), intento.tablero())
                .contains(movimiento.destino());
        if (alcanzable) {
            return Optional.empty();
        }
        return Optional.of(capitalizar(pieza.tipo().nombreConArticulo()) + " no puede moverse de "
                + movimiento.origen() + " a " + movimiento.destino() + ".");
    }

    private static String capitalizar(String texto) {
        return Character.toUpperCase(texto.charAt(0)) + texto.substring(1);
    }
}
