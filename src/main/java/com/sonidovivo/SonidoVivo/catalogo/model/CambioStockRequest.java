package com.sonidovivo.SonidoVivo.catalogo.model;

import jakarta.validation.constraints.NotNull;

/**
 * Cambio manual de stock (dueño o vendedor). "delta" positivo repone y
 * negativo corrige mermas; el descuento automatico lo hace el microservicio
 * de pedidos.
 */
public record CambioStockRequest(
        @NotNull Integer delta
) {
}
