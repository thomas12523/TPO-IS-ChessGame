package com.ajedrez.dominio.movimiento;

import com.ajedrez.dominio.modelo.Color;
import com.ajedrez.dominio.modelo.Posicion;
import com.ajedrez.dominio.modelo.Tablero;
import com.ajedrez.interfaces.IReglaDeMovimiento;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Une varias reglas en una (patrón Composite). La reina no es una clase nueva
 * ni hereda de nadie: es "lo que hace la torre + lo que hace el alfil".
 */
public final class MovimientoCompuesto implements IReglaDeMovimiento {

    private final List<IReglaDeMovimiento> reglas;

    public MovimientoCompuesto(IReglaDeMovimiento... reglas) {
        this.reglas = List.of(reglas);
    }

    @Override
    public Set<Posicion> destinos(Posicion origen, Color color, Tablero tablero) {
        Set<Posicion> union = new HashSet<>();
        reglas.forEach(regla -> union.addAll(regla.destinos(origen, color, tablero)));
        return union;
    }

    @Override
    public Set<Posicion> casillasAtacadas(Posicion origen, Color color, Tablero tablero) {
        Set<Posicion> union = new HashSet<>();
        reglas.forEach(regla -> union.addAll(regla.casillasAtacadas(origen, color, tablero)));
        return union;
    }
}
