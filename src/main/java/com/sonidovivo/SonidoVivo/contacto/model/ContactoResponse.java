package com.sonidovivo.SonidoVivo.contacto.model;

import java.time.LocalDateTime;

/**
 * Mensaje de contacto que la API devuelve al panel de gestion.
 */
public record ContactoResponse(
        Long idContacto,
        String nombre,
        String email,
        String telefono,
        String asunto,
        String mensaje,
        String estado,
        LocalDateTime fechaEnvio
) {
}
