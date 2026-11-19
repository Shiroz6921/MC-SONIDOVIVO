package com.sonidovivo.SonidoVivo.catalogo.model;

import java.math.BigDecimal;

/**
 * Oferta lista para mostrarse en la seccion de ofertas de la web.
 */
public record OfertaResponse(
        Long idOferta,
        Long idProducto,
        String tituloProducto,
        String imagen,
        BigDecimal precio,
        BigDecimal precioOferta,
        BigDecimal precioAnterior,
        Integer stock
) {
}
