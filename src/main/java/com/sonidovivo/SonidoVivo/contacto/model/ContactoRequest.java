package com.sonidovivo.SonidoVivo.contacto.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Datos del formulario de contacto de la web React.
 */
public record ContactoRequest(
        @NotBlank @Size(max = 100) String nombre,
        @NotBlank @Email @Size(max = 150) String email,
        @Pattern(regexp = "^[0-9+\\-\\s]{0,20}$", message = "El telefono solo acepta numeros") String telefono,
        @NotBlank @Size(max = 120) String asunto,
        @NotBlank @Size(min = 10, max = 1000) String mensaje
) {
}
