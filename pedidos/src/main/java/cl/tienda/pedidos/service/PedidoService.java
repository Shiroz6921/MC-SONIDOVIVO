package cl.tienda.pedidos.service;

import cl.tienda.pedidos.client.ProductoClient;
import cl.tienda.pedidos.dto.CrearPedidoRequest;
import cl.tienda.pedidos.dto.ProductoInfo;
import cl.tienda.pedidos.model.Pedido;
import cl.tienda.pedidos.repository.PedidoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class PedidoService {

    private static final Set<String> ESTADOS_VALIDOS = Set.of("EN_PREPARACION", "DESPACHADO", "ENTREGADO");
    private static final Set<String> TIPOS_ENTREGA = Set.of("DESPACHO", "RETIRO_TIENDA");

    private final PedidoRepository repository;
    private final ProductoClient productoClient;

    public PedidoService(PedidoRepository repository, ProductoClient productoClient) {
        this.repository = repository;
        this.productoClient = productoClient;
    }

    public Pedido crear(CrearPedidoRequest datos) {
        if (!TIPOS_ENTREGA.contains(datos.tipoEntrega())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tipo de entrega no válido");
        }
        if (datos.tipoEntrega().equals("DESPACHO")
                && (datos.direccionEntrega() == null || datos.direccionEntrega().isBlank())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El despacho requiere una dirección de entrega");
        }

        Map<Long, Integer> cantidades = new LinkedHashMap<>();
        for (CrearPedidoRequest.Item item : datos.items()) {
            cantidades.merge(item.productoId(), item.cantidad(), Integer::sum);
        }

        Pedido pedido = new Pedido();
        pedido.setUsuarioId(datos.usuarioId());
        pedido.setTipoEntrega(datos.tipoEntrega());
        pedido.setDireccionEntrega(datos.tipoEntrega().equals("DESPACHO") ? datos.direccionEntrega().trim() : null);
        pedido.setMetodoPago(datos.metodoPago() == null || datos.metodoPago().isBlank() ? "TARJETA" : datos.metodoPago().trim());

        Map<Long, Integer> descontados = new LinkedHashMap<>();
        try {
            for (Map.Entry<Long, Integer> entrada : cantidades.entrySet()) {
                ProductoInfo producto = productoClient.obtener(entrada.getKey());
                if (producto.stock() < entrada.getValue()) {
                    throw new ResponseStatusException(HttpStatus.CONFLICT,
                            "Stock insuficiente para " + producto.titulo() + ": quedan " + producto.stock());
                }
                productoClient.descontar(producto.id(), entrada.getValue());
                descontados.put(producto.id(), entrada.getValue());

                Pedido.Item item = new Pedido.Item();
                item.setProductoId(producto.id());
                item.setNombreProducto(producto.titulo());
                BigDecimal precioFinal = producto.precioOferta() != null ? producto.precioOferta() : producto.precio();
                item.setPrecioUnitario(precioFinal);
                item.setCantidad(entrada.getValue());
                item.setSubtotal(precioFinal.multiply(BigDecimal.valueOf(entrada.getValue())));
                pedido.getItems().add(item);
            }
            return guardar(pedido);
        } catch (RuntimeException e) {
            devolverStock(descontados);
            throw e;
        }
    }

    public Pedido guardar(Pedido pedido) {
        pedido.setId(null);
        pedido.setEstado("EN_PREPARACION");
        BigDecimal total = BigDecimal.ZERO;
        for (Pedido.Item item : pedido.getItems()) {
            total = total.add(item.getSubtotal());
        }
        pedido.setTotal(total);
        return repository.save(pedido);
    }

    public List<Pedido> listar(Long usuarioId) {
        if (usuarioId == null) {
            return repository.findAllByOrderByFechaPedidoDesc();
        }
        return repository.findByUsuarioIdOrderByFechaPedidoDesc(usuarioId);
    }

    public Pedido obtener(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido no encontrado"));
    }

    public Pedido cambiarEstado(Long id, String estado) {
        if (!ESTADOS_VALIDOS.contains(estado)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Estado no válido");
        }
        Pedido pedido = obtener(id);
        if (pedido.getEstado().equals("CANCELADO")) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "No se puede cambiar el estado de un pedido cancelado");
        }
        if (pedido.getEstado().equals("ENTREGADO")) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Un pedido entregado no cambia de estado");
        }
        if (estado.equals("DESPACHADO") && !pedido.getEstado().equals("EN_PREPARACION")) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Solo un pedido en preparación puede despacharse");
        }
        if (estado.equals("ENTREGADO") && !pedido.getEstado().equals("DESPACHADO")) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Solo un pedido despachado puede entregarse");
        }
        pedido.setEstado(estado);
        return repository.save(pedido);
    }

    public Pedido cancelar(Long id) {
        Pedido pedido = obtener(id);
        if (!pedido.getEstado().equals("EN_PREPARACION")) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Solo se pueden cancelar pedidos en preparación (este está " + pedido.getEstado() + ")");
        }
        List<Pedido.Item> repuestos = new ArrayList<>();
        try {
            for (Pedido.Item item : pedido.getItems()) {
                productoClient.reponer(item.getProductoId(), item.getCantidad());
                repuestos.add(item);
            }
        } catch (RuntimeException e) {
            for (Pedido.Item item : repuestos) {
                try {
                    productoClient.descontar(item.getProductoId(), item.getCantidad());
                } catch (RuntimeException ignorado) {
                }
            }
            throw e;
        }
        pedido.setEstado("CANCELADO");
        return repository.save(pedido);
    }

    private void devolverStock(Map<Long, Integer> descontados) {
        descontados.forEach((productoId, cantidad) -> {
            try {
                productoClient.reponer(productoId, cantidad);
            } catch (RuntimeException ignorado) {
            }
        });
    }
}
