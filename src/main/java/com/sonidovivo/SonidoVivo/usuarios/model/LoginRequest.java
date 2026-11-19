package com.sonidovivo.SonidoVivo.usuarios.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Credenciales que envia el formulario de inicio de sesion de React.
 */
public record LoginRequest(
        @NotBlank @Email String email,
        @NotBlank String password
) {
}
