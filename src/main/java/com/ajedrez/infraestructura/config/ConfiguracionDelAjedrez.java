package com.ajedrez.infraestructura.config;

import com.ajedrez.aplicacion.ServicioDePartidas;
import com.ajedrez.dominio.fabrica.FabricaEstandar;
import com.ajedrez.dominio.reglas.ReglasDelAjedrez;
import com.ajedrez.interfaces.IDisposicionInicial;
import com.ajedrez.interfaces.IFabricaDeAjedrez;
import com.ajedrez.interfaces.IRepositorioDePartidas;
import com.ajedrez.interfaces.IServicioDePartidas;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Raíz de composición: el único lugar que elige la variante de ajedrez.
 * El dominio no tiene ni una anotación de Spring; para jugar otra variante
 * alcanza con devolver otra {@link IFabricaDeAjedrez} acá.
 */
@Configuration
public class ConfiguracionDelAjedrez {

    @Bean
    IFabricaDeAjedrez fabricaDeAjedrez() {
        return new FabricaEstandar();
    }

    @Bean
    IDisposicionInicial disposicionInicial(IFabricaDeAjedrez fabrica) {
        return fabrica.crearDisposicion();
    }

    @Bean
    ReglasDelAjedrez reglasDelAjedrez(IFabricaDeAjedrez fabrica) {
        return fabrica.crearReglas();
    }

    @Bean
    IServicioDePartidas servicioDePartidas(IRepositorioDePartidas repositorio,
                                          IDisposicionInicial disposicion,
                                          ReglasDelAjedrez reglas) {
        return new ServicioDePartidas(repositorio, disposicion, reglas);
    }
}
