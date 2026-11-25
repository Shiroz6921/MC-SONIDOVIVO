package cl.tienda.carrito.dto;

import java.util.List;

public record CarritoRespuesta(Long usuarioId, List<Item> items, Integer total) {
    public record Item(Long id, Long productoId, String nombre, Integer precio, String imagen, Integer cantidad, Integer subtotal) {
    }

    public static CarritoRespuesta desde(Long usuarioId, List<cl.tienda.carrito.model.CarritoItem> items) {
        List<Item> lista = items.stream().map(i -> new Item(
                i.getId(), i.getProductoId(), i.getNombre(), i.getPrecio(), i.getImagen(),
                i.getCantidad(), i.getPrecio() * i.getCantidad())).toList();
        Integer total = lista.stream().mapToInt(Item::subtotal).sum();
        return new CarritoRespuesta(usuarioId, lista, total);
    }
}
