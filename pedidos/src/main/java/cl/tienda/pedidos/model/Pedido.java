package cl.tienda.pedidos.model;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "pedido")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @Column(nullable = false, precision = 12, scale = 0)
    private BigDecimal total = BigDecimal.ZERO;

    @NotBlank
    @Column(name = "tipo_entrega", nullable = false, length = 20)
    private String tipoEntrega;

    @Column(name = "direccion_entrega", length = 200)
    private String direccionEntrega;

    @Column(name = "metodo_pago", length = 20)
    private String metodoPago = "TARJETA";

    @Column(nullable = false, length = 20)
    private String estado = "EN_PREPARACION";

    @Column(name = "fecha_pedido", nullable = false, updatable = false)
    private LocalDateTime fechaPedido;

    @NotEmpty
    @Valid
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "pedido_item", joinColumns = @JoinColumn(name = "pedido_id"))
    private List<Item> items = new ArrayList<>();

    @PrePersist
    protected void asignarFecha() {
        this.fechaPedido = LocalDateTime.now();
    }

    @Data
    @Embeddable
    public static class Item {
        @NotNull
        @Column(name = "producto_id", nullable = false)
        private Long productoId;

        @NotBlank
        @Column(nullable = false, length = 120)
        private String nombreProducto;

        @NotNull
        @Column(name = "precio_unitario", nullable = false, precision = 10, scale = 0)
        private BigDecimal precioUnitario;

        @NotNull
        @Min(1)
        @Column(nullable = false)
        private Integer cantidad;

        @Column(nullable = false, precision = 12, scale = 0)
        private BigDecimal subtotal;
    }
}
