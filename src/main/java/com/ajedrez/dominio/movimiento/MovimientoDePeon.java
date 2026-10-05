package com.ajedrez.dominio.movimiento;

import com.ajedrez.dominio.modelo.Color;
import com.ajedrez.dominio.modelo.Desplazamiento;
import com.ajedrez.dominio.modelo.Posicion;
import com.ajedrez.dominio.modelo.Tablero;
import com.ajedrez.interfaces.IReglaDeMovimiento;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * El peón: avanza una casilla (o dos desde su fila inicial) solo si está libre,
 * y captura únicamente en diagonal hacia adelante.
 */
public final class MovimientoDePeon implements IReglaDeMovimiento {

    @Override
    public Set<Posicion> destinos(Posicion origen, Color color, Tablero tablero) {
        Set<Posicion> destinos = new HashSet<>();
        int sentido = color.sentidoDeAvance();

        Optional<Posicion> unPaso = origen.desplazar(new Desplazamiento(0, sentido))
                .filter(tablero::estaVacia);
        unPaso.ifPresent(destinos::add);

        if (unPaso.isPresent() && origen.fila() == filaInicial(color)) {
            origen.desplazar(new Desplazamiento(0, 2 * sentido))
                    .filter(tablero::estaVacia)
                    .ifPresent(destinos::add);
        }

        casillasAtacadas(origen, color, tablero).stream()
                .filter(casilla -> tablero.hayRivalEn(casilla, color))
                .forEach(destinos::add);

        return destinos;
    }

    @Override
    public Set<Posicion> casillasAtacadas(Posicion origen, Color color, Tablero tablero) {
        int sentido = color.sentidoDeAvance();
        Set<Posicion> atacadas = new HashSet<>();
        origen.desplazar(new Desplazamiento(-1, sentido)).ifPresent(atacadas::add);
        origen.desplazar(new Desplazamiento(1, sentido)).ifPresent(atacadas::add);
        return atacadas;
    }

    private static int filaInicial(Color color) {
        return color == Color.BLANCO ? 1 : Tablero.DIMENSION - 2;
    }
}
