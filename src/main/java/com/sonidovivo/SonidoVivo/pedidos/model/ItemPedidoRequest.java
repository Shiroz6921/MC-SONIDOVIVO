package com.sonidovivo.SonidoVivo.pedidos.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * Linea del carrito que llega desde React al confirmar la compra.
 */
public record ItemPedidoRequest(
        @NotNull Long idProducto,
        @NotNull @Min(1) @Max(100) Integer cantidad
) {
}
