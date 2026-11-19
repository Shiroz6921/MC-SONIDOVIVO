package com.sonidovivo.SonidoVivo.usuarios.model;

import jakarta.validation.constraints.NotNull;

/**
 * Activar o desactivar un usuario (solo administrador).
 */
public record CambiarEstadoRequest(
        @NotNull Boolean activo
) {
}
