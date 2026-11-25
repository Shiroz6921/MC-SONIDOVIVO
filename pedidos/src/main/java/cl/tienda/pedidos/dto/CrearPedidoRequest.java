package cl.tienda.pedidos.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CrearPedidoRequest(
        @NotNull(message = "El usuario es obligatorio") Long usuarioId,
        @NotBlank(message = "El tipo de entrega es obligatorio")
        @Pattern(regexp = "DESPACHO|RETIRO_TIENDA") String tipoEntrega,
        @Size(max = 200) String direccionEntrega,
        @Valid @NotEmpty(message = "El pedido debe tener al menos un producto") List<Item> items
) {
    public record Item(
            @NotNull(message = "El producto es obligatorio") Long productoId,
            @NotNull(message = "La cantidad es obligatoria")
            @Min(value = 1, message = "La cantidad debe ser mayor a 0")
            Integer cantidad
    ) {
    }
}
