package com.sonidovivo.SonidoVivo.pedidos.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Pedido completo para la API.
 */
public record PedidoResponse(
        Long idPedido,
        Long idUsuario,
        LocalDateTime fechaPedido,
        String estado,
        String tipoEntrega,
        String direccionEntrega,
        BigDecimal total,
        List<DetallePedidoResponse> detalles
) {
}
