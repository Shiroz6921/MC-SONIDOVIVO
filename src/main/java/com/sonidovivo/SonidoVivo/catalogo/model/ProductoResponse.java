package com.sonidovivo.SonidoVivo.catalogo.model;

import java.math.BigDecimal;

/**
 * Producto que la API devuelve a la web. Incluye la categoria, el stock y,
 * si existe, la oferta vigente (para mostrar el precio tachado).
 */
public record ProductoResponse(
        Long idProducto,
        String titulo,
        String descripcion,
        BigDecimal precio,
        Integer stock,
        String imagen,
        String categoria,
        boolean activo,
        BigDecimal precioOferta,
        BigDecimal precioAnterior
) {
}
