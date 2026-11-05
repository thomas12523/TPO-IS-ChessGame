package com.ajedrez.dominio.reglas;

import com.ajedrez.dominio.modelo.Color;
import com.ajedrez.dominio.modelo.Movimiento;
import com.ajedrez.dominio.modelo.Tablero;
import com.ajedrez.errores.MovimientoInvalidoException;
import com.ajedrez.interfaces.IReglaDeValidacion;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;

import static com.ajedrez.dominio.modelo.Posicion.de;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * El validador no conoce ninguna regla concreta: solo aplica la lista que le inyectan,
 * en orden, y se detiene en la primera que falla (short-circuit).
 */
class ValidadorDeMovimientosTest {

    private final IntentoDeMovimiento cualquierIntento =
            new IntentoDeMovimiento(Tablero.vacio(), Color.BLANCO, Movimiento.de(de("a1"), de("a2")));

    @Test
    void siNingunaReglaFalla_elIntentoEsValido() {
        var validador = new ValidadorDeMovimientos(List.of(siempreAprueba(), siempreAprueba()));

        assertThat(validador.esValido(cualquierIntento)).isTrue();
        assertThat(validador.primeraViolacion(cualquierIntento)).isEmpty();
    }

    @Test
    void seDetieneEnLaPrimeraReglaQueFalla_yDevuelveSuMensaje() {
        var primera = siempreRechaza("primer motivo");
        var segunda = mock(IReglaDeValidacion.class);

        var validador = new ValidadorDeMovimientos(List.of(primera, segunda));

        assertThat(validador.primeraViolacion(cualquierIntento)).contains("primer motivo");
        verify(segunda, never()).verificar(Mockito.any());
    }

    @Test
    void siUnaReglaPosteriorFallaria_peroLaAnteriorYaAprobo_seEvaluaLaSegunda() {
        var validador = new ValidadorDeMovimientos(List.of(siempreAprueba(), siempreRechaza("segundo motivo")));

        assertThat(validador.primeraViolacion(cualquierIntento)).contains("segundo motivo");
    }

    @Test
    void validar_siEsValido_noLanzaNingunaExcepcion() {
        var validador = new ValidadorDeMovimientos(List.of(siempreAprueba()));

        validador.validar(cualquierIntento); // no debe lanzar
    }

    @Test
    void validar_siEsInvalido_lanzaMovimientoInvalidoException_conElMotivo() {
        var validador = new ValidadorDeMovimientos(List.of(siempreRechaza("no se puede")));

        assertThatThrownBy(() -> validador.validar(cualquierIntento))
                .isInstanceOf(MovimientoInvalidoException.class)
                .hasMessage("no se puede");
    }

    private static IReglaDeValidacion siempreAprueba() {
        return intento -> Optional.empty();
    }

    private static IReglaDeValidacion siempreRechaza(String motivo) {
        return intento -> Optional.of(motivo);
    }
}
