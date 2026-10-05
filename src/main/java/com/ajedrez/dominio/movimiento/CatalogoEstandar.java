package com.ajedrez.dominio.movimiento;

import com.ajedrez.dominio.modelo.TipoPieza;
import com.ajedrez.interfaces.ICatalogoDeMovimientos;
import com.ajedrez.interfaces.IReglaDeMovimiento;

import java.util.EnumMap;
import java.util.Map;

/** Movimientos del ajedrez clásico, armados por composición de unas pocas reglas genéricas. */
public final class CatalogoEstandar implements ICatalogoDeMovimientos {

    private final Map<TipoPieza, IReglaDeMovimiento> reglas = new EnumMap<>(TipoPieza.class);

    public CatalogoEstandar() {
        IReglaDeMovimiento torre = new MovimientoDeslizante(Direcciones.ORTOGONALES);
        IReglaDeMovimiento alfil = new MovimientoDeslizante(Direcciones.DIAGONALES);

        reglas.put(TipoPieza.PEON, new MovimientoDePeon());
        reglas.put(TipoPieza.TORRE, torre);
        reglas.put(TipoPieza.ALFIL, alfil);
        reglas.put(TipoPieza.REINA, new MovimientoCompuesto(torre, alfil));
        reglas.put(TipoPieza.CABALLO, new MovimientoDeSalto(Direcciones.SALTOS_DE_CABALLO));
        reglas.put(TipoPieza.REY, new MovimientoDeSalto(Direcciones.TODAS));
    }

    @Override
    public IReglaDeMovimiento reglaPara(TipoPieza tipo) {
        IReglaDeMovimiento regla = reglas.get(tipo);
        if (regla == null) {
            throw new IllegalStateException("No hay regla de movimiento para " + tipo);
        }
        return regla;
    }
}
