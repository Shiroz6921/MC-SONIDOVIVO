package com.sonidovivo.SonidoVivo.pedidos.model;

/**
 * Estados del pedido, tal como los describe el negocio:
 * se prepara, se despacha y finalmente se entrega.
 */
public enum EstadoPedido {
    EN_PREPARACION,
    DESPACHADO,
    ENTREGADO,
    CANCELADO
}
