package com.ajedrez.dominio.reglas;

import com.ajedrez.interfaces.IReglaDeValidacion;
import com.ajedrez.interfaces.IValidadorDeMovimientos;
import com.ajedrez.errores.MovimientoInvalidoException;

import java.util.List;
import java.util.Optional;

/**
 * Aplica, en orden, una lista de reglas de validación y se detiene en la primera
 * que falla. No sabe cuáles son las reglas: se las inyectan.
 */
public final class ValidadorDeMovimientos implements IValidadorDeMovimientos {

    private final List<IReglaDeValidacion> reglas;

    public ValidadorDeMovimientos(List<IReglaDeValidacion> reglas) {
        this.reglas = List.copyOf(reglas);
    }

    @Override
    public Optional<String> primeraViolacion(IntentoDeMovimiento intento) {
        for (IReglaDeValidacion regla : reglas) {
            Optional<String> violacion = regla.verificar(intento);
            if (violacion.isPresent()) {
                return violacion;
            }
        }
        return Optional.empty();
    }

    @Override
    public boolean esValido(IntentoDeMovimiento intento) {
        return primeraViolacion(intento).isEmpty();
    }

    @Override
    public void validar(IntentoDeMovimiento intento) {
        primeraViolacion(intento).ifPresent(motivo -> {
            throw new MovimientoInvalidoException(motivo);
        });
    }
}
