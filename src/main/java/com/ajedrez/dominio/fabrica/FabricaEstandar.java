package com.ajedrez.dominio.fabrica;

import com.ajedrez.dominio.movimiento.CatalogoEstandar;
import com.ajedrez.dominio.partida.DisposicionEstandar;
import com.ajedrez.dominio.reglas.DetectorDeJaque;
import com.ajedrez.dominio.reglas.EjecutorDeMovimientos;
import com.ajedrez.dominio.reglas.HayPiezaEnOrigen;
import com.ajedrez.dominio.reglas.NoDejaAlReyEnJaque;
import com.ajedrez.dominio.reglas.PiezaDelJugadorEnTurno;
import com.ajedrez.dominio.reglas.PromocionCorrecta;
import com.ajedrez.dominio.reglas.ReglasDelAjedrez;
import com.ajedrez.dominio.reglas.RespetaElMovimientoDeLaPieza;
import com.ajedrez.dominio.reglas.ValidadorDeMovimientos;
import com.ajedrez.interfaces.ICatalogoDeMovimientos;
import com.ajedrez.interfaces.IDetectorDeJaque;
import com.ajedrez.interfaces.IDisposicionInicial;
import com.ajedrez.interfaces.IEjecutorDeMovimientos;
import com.ajedrez.interfaces.IFabricaDeAjedrez;
import com.ajedrez.interfaces.IValidadorDeMovimientos;

import java.util.List;

/** Fábrica concreta del ajedrez clásico. */
public final class FabricaEstandar implements IFabricaDeAjedrez {

    @Override
    public ICatalogoDeMovimientos crearCatalogo() {
        return new CatalogoEstandar();
    }

    @Override
    public IDisposicionInicial crearDisposicion() {
        return new DisposicionEstandar();
    }

    /** El orden importa: las reglas baratas primero, la simulación de jaque al final. */
    @Override
    public ReglasDelAjedrez crearReglas() {
        ICatalogoDeMovimientos catalogo = crearCatalogo();
        IEjecutorDeMovimientos ejecutor = new EjecutorDeMovimientos();
        IDetectorDeJaque detector = new DetectorDeJaque(catalogo);
        IValidadorDeMovimientos validador = new ValidadorDeMovimientos(List.of(
                new HayPiezaEnOrigen(),
                new PiezaDelJugadorEnTurno(),
                new RespetaElMovimientoDeLaPieza(catalogo),
                new PromocionCorrecta(),
                new NoDejaAlReyEnJaque(ejecutor, detector)));
        return new ReglasDelAjedrez(catalogo, validador, ejecutor, detector);
    }
}
