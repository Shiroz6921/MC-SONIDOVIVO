package com.sonidovivo.SonidoVivo.usuarios.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Datos que envia el formulario de registro de React.
 */
public record RegistroRequest(
        @NotBlank @Size(max = 60) String nombre,
        @NotBlank @Size(max = 60) String apellido,
        @NotBlank @Email @Size(max = 150) String email,
        @NotBlank @Size(min = 6, max = 100) String password,
        @Pattern(regexp = "^[0-9+\\-\\s]{0,20}$", message = "El telefono solo acepta numeros") String telefono,
        @Size(max = 200) String direccion
) {
}
