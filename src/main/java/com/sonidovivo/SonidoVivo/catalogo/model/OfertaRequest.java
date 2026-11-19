package com.sonidovivo.SonidoVivo.catalogo.model;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Datos para publicar una oferta sobre un producto existente.
 */
public record OfertaRequest(
        @NotNull Long idProducto,
        @NotNull @DecimalMin(value = "0", inclusive = false) BigDecimal precioOferta,
        @NotNull @DecimalMin(value = "0", inclusive = false) BigDecimal precioAnterior,
        @NotNull LocalDate fechaInicio,
        LocalDate fechaFin
) {
}
