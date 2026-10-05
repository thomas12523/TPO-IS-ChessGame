package com.ajedrez.dominio.reglas;

import com.ajedrez.dominio.modelo.Tablero;
import com.ajedrez.interfaces.IDetectorDeJaque;
import com.ajedrez.interfaces.IEjecutorDeMovimientos;
import com.ajedrez.interfaces.IReglaDeValidacion;

import java.util.Optional;

/**
 * Una jugada no puede dejar (o mantener) en jaque al rey de quien la hace.
 * Se simula sobre una copia del tablero, que es inmutable.
 */
public final class NoDejaAlReyEnJaque implements IReglaDeValidacion {

    private final IEjecutorDeMovimientos ejecutor;
    private final IDetectorDeJaque detector;

    public NoDejaAlReyEnJaque(IEjecutorDeMovimientos ejecutor, IDetectorDeJaque detector) {
        this.ejecutor = ejecutor;
        this.detector = detector;
    }

    @Override
    public Optional<String> verificar(IntentoDeMovimiento intento) {
        Tablero resultante = ejecutor.ejecutar(intento.tablero(), intento.movimiento());
        if (!detector.estaEnJaque(resultante, intento.turno())) {
            return Optional.empty();
        }
        boolean yaEstabaEnJaque = detector.estaEnJaque(intento.tablero(), intento.turno());
        return Optional.of(yaEstabaEnJaque
                ? "Tu rey está en jaque: esa jugada no lo resuelve."
                : "Esa jugada dejaría a tu rey en jaque.");
    }
}
