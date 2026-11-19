package com.sonidovivo.SonidoVivo.pedidos.model;

import java.math.BigDecimal;

/**
 * Linea de un pedido ya guardado (historial del cliente o gestion del vendedor).
 */
public record DetallePedidoResponse(
        Long idProducto,
        String nombreProducto,
        BigDecimal precioUnitario,
        Integer cantidad,
        BigDecimal subtotal
) {
}
