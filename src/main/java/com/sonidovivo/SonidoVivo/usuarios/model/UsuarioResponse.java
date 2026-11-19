package com.sonidovivo.SonidoVivo.usuarios.model;

import java.time.LocalDateTime;

/**
 * Usuario que la API devuelve al frontend. Nunca incluye el password.
 */
public record UsuarioResponse(
        Long idUsuario,
        String nombre,
        String apellido,
        String email,
        String telefono,
        String direccion,
        String rol,
        boolean activo,
        LocalDateTime fechaRegistro
) {
}
