package com.sonidovivo.SonidoVivo.contacto.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * Cambio de estado del mensaje (el vendedor lo marca como atendido).
 */
public record EstadoContactoRequest(
        @NotBlank @Pattern(regexp = "PENDIENTE|ATENDIDO") String estado
) {
}
