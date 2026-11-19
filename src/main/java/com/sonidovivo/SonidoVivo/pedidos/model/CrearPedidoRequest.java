package com.sonidovivo.SonidoVivo.pedidos.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.List;

/**
 * Datos que envia React al confirmar el carrito: cliente, forma de entrega
 * (con direccion si es despacho) y las lineas del carrito.
 */
public record CrearPedidoRequest(
        @NotNull Long idUsuario,
        @NotBlank @Pattern(regexp = "DESPACHO|RETIRO_TIENDA") String tipoEntrega,
        @Size(max = 200) String direccionEntrega,
        @Valid @NotEmpty List<ItemPedidoRequest> items
) {
}
