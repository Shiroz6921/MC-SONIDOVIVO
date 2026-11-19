package com.sonidovivo.SonidoVivo.usuarios.model;

import jakarta.validation.constraints.Size;

/**
 * Edicion del perfil (propio) o de un usuario (administrador).
 */
public record ActualizarUsuarioRequest(
        @Size(max = 60) String nombre,
        @Size(max = 60) String apellido,
        @Size(max = 20) String telefono,
        @Size(max = 200) String direccion
) {
}
