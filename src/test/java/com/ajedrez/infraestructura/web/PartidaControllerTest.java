package com.ajedrez.infraestructura.web;

import com.ajedrez.aplicacion.dto.DestinosDto;
import com.ajedrez.aplicacion.dto.JugadaDto;
import com.ajedrez.aplicacion.dto.PartidaDto;
import com.ajedrez.errores.JugadaInvalidaException;
import com.ajedrez.errores.PartidaNoEncontradaException;
import com.ajedrez.interfaces.IServicioDePartidas;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * El controlador no conoce reglas de ajedrez: solo conecta rutas con el servicio.
 * Por eso se testea con el servicio mockeado, enfocándose en el contrato HTTP.
 */
@WebMvcTest(PartidaController.class)
class PartidaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private IServicioDePartidas servicio;

    private static PartidaDto partidaDeEjemplo() {
        return new PartidaDto("abc-123", "BLANCO", false, null, List.of(), List.of(), null, List.of(), List.of());
    }

    @Test
    void crear_devuelve201ConLaPartidaCreada() throws Exception {
        when(servicio.crearPartida()).thenReturn(partidaDeEjemplo());

        mockMvc.perform(post("/api/partidas"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("abc-123"))
                .andExpect(jsonPath("$.turno").value("BLANCO"));
    }

    @Test
    void obtener_unaPartidaExistente_devuelve200ConSuEstado() throws Exception {
        when(servicio.obtener("abc-123")).thenReturn(partidaDeEjemplo());

        mockMvc.perform(get("/api/partidas/abc-123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("abc-123"));
    }

    @Test
    void obtener_unaPartidaInexistente_devuelve404ConMensaje() throws Exception {
        when(servicio.obtener("no-existe")).thenThrow(new PartidaNoEncontradaException("no-existe"));

        mockMvc.perform(get("/api/partidas/no-existe"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value("No existe la partida no-existe."));
    }

    @Test
    void movimientos_devuelve200ConLosDestinosLegales() throws Exception {
        when(servicio.destinosLegales("abc-123", "e2")).thenReturn(new DestinosDto("e2", List.of("e3", "e4")));

        mockMvc.perform(get("/api/partidas/abc-123/movimientos").param("desde", "e2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.desde").value("e2"))
                .andExpect(jsonPath("$.destinos[0]").value("e3"))
                .andExpect(jsonPath("$.destinos[1]").value("e4"));
    }

    @Test
    void jugar_unaJugadaValida_devuelve200ConLaPartidaActualizada() throws Exception {
        when(servicio.jugar(eq("abc-123"), any(JugadaDto.class)))
                .thenReturn(new PartidaDto("abc-123", "NEGRO", false, null, List.of(),
                        List.of("e2-e4"), new PartidaDto.UltimaJugadaDto("e2", "e4"), List.of(), List.of()));

        mockMvc.perform(post("/api/partidas/abc-123/jugadas")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(new JugadaDto("e2", "e4", null))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.turno").value("NEGRO"))
                .andExpect(jsonPath("$.historial[0]").value("e2-e4"));
    }

    @Test
    void jugar_unaJugadaInvalida_devuelve422ConElMotivo() throws Exception {
        when(servicio.jugar(anyString(), any(JugadaDto.class)))
                .thenThrow(new JugadaInvalidaException("Es el turno de las negras.", null));

        mockMvc.perform(post("/api/partidas/abc-123/jugadas")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(new JugadaDto("e2", "e4", null))))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.mensaje").value("Es el turno de las negras."));
    }
}
