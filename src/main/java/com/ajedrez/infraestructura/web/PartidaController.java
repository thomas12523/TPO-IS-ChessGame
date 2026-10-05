package com.ajedrez.infraestructura.web;

import com.ajedrez.aplicacion.dto.DestinosDto;
import com.ajedrez.aplicacion.dto.JugadaDto;
import com.ajedrez.aplicacion.dto.PartidaDto;
import com.ajedrez.interfaces.IServicioDePartidas;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Adaptador de entrada HTTP. Solo conecta rutas con casos de uso: recibe y devuelve DTOs
 * y no conoce el dominio ni contiene ninguna regla del ajedrez.
 */
@RestController
@RequestMapping("/api/partidas")
public class PartidaController {

    private final IServicioDePartidas servicio;

    public PartidaController(IServicioDePartidas servicio) {
        this.servicio = servicio;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PartidaDto crear() {
        return servicio.crearPartida();
    }

    @GetMapping("/{id}")
    public PartidaDto obtener(@PathVariable String id) {
        return servicio.obtener(id);
    }

    @GetMapping("/{id}/movimientos")
    public DestinosDto movimientos(@PathVariable String id, @RequestParam String desde) {
        return servicio.destinosLegales(id, desde);
    }

    @PostMapping("/{id}/jugadas")
    public PartidaDto jugar(@PathVariable String id, @RequestBody JugadaDto jugada) {
        return servicio.jugar(id, jugada);
    }
}
