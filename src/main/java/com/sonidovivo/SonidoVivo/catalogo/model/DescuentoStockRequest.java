package com.sonidovivo.SonidoVivo.catalogo.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * Descuento de stock que solicita el microservicio de pedidos al confirmar una venta.
 */
public record DescuentoStockRequest(
        @NotNull @Min(1) @Max(1000) Integer cantidad
) {
}
