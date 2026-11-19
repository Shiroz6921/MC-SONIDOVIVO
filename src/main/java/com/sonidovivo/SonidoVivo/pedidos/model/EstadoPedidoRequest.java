package com.sonidovivo.SonidoVivo.pedidos.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * Cambio de estado del pedido (vendedor/administrador).
 */
public record EstadoPedidoRequest(
        @NotBlank @Pattern(regexp = "EN_PREPARACION|DESPACHADO|ENTREGADO|CANCELADO") String estado
) {
}
