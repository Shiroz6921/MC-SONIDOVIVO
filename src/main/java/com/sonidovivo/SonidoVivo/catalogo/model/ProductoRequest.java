package com.sonidovivo.SonidoVivo.catalogo.model;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

/**
 * Datos para crear o editar un producto (panel de administracion).
 */
public record ProductoRequest(
        @NotBlank @Size(max = 120) String titulo,
        @NotBlank @Size(max = 500) String descripcion,
        @NotNull @DecimalMin(value = "0", inclusive = false) BigDecimal precio,
        @NotNull @Min(0) @Max(100000) Integer stock,
        @NotBlank @Size(max = 300) String imagen,
        @NotNull Long idCategoria,
        Boolean activo
) {
}
