package com.ajedrez.aplicacion.dto;

import java.util.List;

/** Casillas a las que puede moverse legalmente la pieza que está en {@code desde}. */
public record DestinosDto(String desde, List<String> destinos) {
}
