package com.sonidovivo.SonidoVivo.usuarios.model;

import jakarta.validation.constraints.NotBlank;

/**
 * Cambio de rol de un usuario (solo administrador).
 */
public record CambiarRolRequest(
        @NotBlank String nombreRol
) {
}
