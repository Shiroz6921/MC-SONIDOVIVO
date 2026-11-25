package cl.tienda.pedidos.dto;

import java.math.BigDecimal;

public record ProductoInfo(Long id, String titulo, BigDecimal precio, Integer stock) {
}
