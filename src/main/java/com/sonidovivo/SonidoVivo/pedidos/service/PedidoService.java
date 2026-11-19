package com.sonidovivo.SonidoVivo.pedidos.service;

import com.sonidovivo.SonidoVivo.catalogo.model.ProductoResponse;
import com.sonidovivo.SonidoVivo.catalogo.service.CatalogoService;
import com.sonidovivo.SonidoVivo.excepciones.OperacionInvalidaException;
import com.sonidovivo.SonidoVivo.excepciones.RecursoNoEncontradoException;
import com.sonidovivo.SonidoVivo.pedidos.model.CrearPedidoRequest;
import com.sonidovivo.SonidoVivo.pedidos.model.DetallePedido;
import com.sonidovivo.SonidoVivo.pedidos.model.DetallePedidoResponse;
import com.sonidovivo.SonidoVivo.pedidos.model.EstadoPedido;
import com.sonidovivo.SonidoVivo.pedidos.model.ItemPedidoRequest;
import com.sonidovivo.SonidoVivo.pedidos.model.Pedido;
import com.sonidovivo.SonidoVivo.pedidos.model.PedidoResponse;
import com.sonidovivo.SonidoVivo.pedidos.model.TipoEntrega;
import com.sonidovivo.SonidoVivo.pedidos.repository.PedidoRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Reglas de negocio de los pedidos:
 * - valida el stock contra el microservicio de catalogo antes de aceptar el pedido;
 * - guarda una copia de nombre y precio de cada producto (historial estable);
 * - descuenta el stock automaticamente al confirmar;
 * - reintegra el stock si el pedido se cancela antes de entregarse.
 */
@Service
public class PedidoService {

    private final PedidoRepository pedidos;
    private final CatalogoService catalogo;

    public PedidoService(PedidoRepository pedidos, CatalogoService catalogo) {
        this.pedidos = pedidos;
        this.catalogo = catalogo;
    }

    @Transactional
    public PedidoResponse crear(CrearPedidoRequest peticion) {
        TipoEntrega tipoEntrega = TipoEntrega.valueOf(peticion.tipoEntrega());
        if (tipoEntrega == TipoEntrega.DESPACHO
                && (peticion.direccionEntrega() == null || peticion.direccionEntrega().isBlank())) {
            throw new OperacionInvalidaException("El despacho requiere una direccion de entrega");
        }

        // 1) Valida y calcula todo ANTES de guardar (usa el precio vigente del catalogo,
        //    considerando la oferta si el producto la tiene).
        BigDecimal total = BigDecimal.ZERO;
        Pedido pedido = new Pedido();
        pedido.setIdUsuario(peticion.idUsuario());
        pedido.setFechaPedido(LocalDateTime.now());
        pedido.setEstado(EstadoPedido.EN_PREPARACION);
        pedido.setTipoEntrega(tipoEntrega);
        pedido.setDireccionEntrega(
                tipoEntrega == TipoEntrega.DESPACHO ? peticion.direccionEntrega().trim() : null);

        for (ItemPedidoRequest item : peticion.items()) {
            ProductoResponse producto = catalogo.obtenerProducto(item.idProducto());
            if (producto.stock() < item.cantidad()) {
                throw new OperacionInvalidaException(
                        "Stock insuficiente para " + producto.titulo() + ": quedan " + producto.stock());
            }
            BigDecimal precioVigente = producto.precioOferta() != null
                    ? producto.precioOferta()
                    : producto.precio();
            BigDecimal subtotal = precioVigente.multiply(BigDecimal.valueOf(item.cantidad()));

            DetallePedido detalle = new DetallePedido();
            detalle.setPedido(pedido);
            detalle.setIdProducto(producto.idProducto());
            detalle.setNombreProducto(producto.titulo());
            detalle.setPrecioUnitario(precioVigente);
            detalle.setCantidad(item.cantidad());
            detalle.setSubtotal(subtotal);
            pedido.getDetalles().add(detalle);

            total = total.add(subtotal);
        }
        pedido.setTotal(total);

        // 2) Guarda el pedido con su detalle.
        Pedido guardado = pedidos.save(pedido);

        // 3) Descuenta el stock en el catalogo.
        for (DetallePedido detalle : guardado.getDetalles()) {
            catalogo.descontarStock(detalle.getIdProducto(), detalle.getCantidad());
        }

        return aRespuesta(guardado);
    }

    @Transactional(readOnly = true)
    public List<PedidoResponse> listarTodos() {
        return pedidos.findAll().stream()
                .sorted((a, b) -> Long.compare(b.getIdPedido(), a.getIdPedido()))
                .map(PedidoService::aRespuesta)
                .toList();
    }

    /** Historial de compras del cliente (solo sus propios pedidos). */
    @Transactional(readOnly = true)
    public List<PedidoResponse> listarPorUsuario(Long idUsuario) {
        return pedidos.findByIdUsuarioOrderByIdPedidoDesc(idUsuario).stream()
                .map(PedidoService::aRespuesta)
                .toList();
    }

    @Transactional(readOnly = true)
    public PedidoResponse obtener(Long id) {
        return aRespuesta(buscar(id));
    }

    /** El vendedor o el dueño avanzan el pedido: EN_PREPARACION -> DESPACHADO -> ENTREGADO. */
    @Transactional
    public PedidoResponse cambiarEstado(Long id, String nuevoEstado) {
        Pedido pedido = buscar(id);
        EstadoPedido estado = EstadoPedido.valueOf(nuevoEstado);

        if (pedido.getEstado() == EstadoPedido.CANCELADO) {
            throw new OperacionInvalidaException("Un pedido cancelado no cambia de estado");
        }
        if (pedido.getEstado() == EstadoPedido.ENTREGADO) {
            throw new OperacionInvalidaException("Un pedido entregado no cambia de estado");
        }
        if (estado == EstadoPedido.DESPACHADO && pedido.getEstado() != EstadoPedido.EN_PREPARACION) {
            throw new OperacionInvalidaException("Solo un pedido en preparacion puede despacharse");
        }
        if (estado == EstadoPedido.ENTREGADO && pedido.getEstado() != EstadoPedido.DESPACHADO) {
            throw new OperacionInvalidaException("Solo un pedido despachado puede entregarse");
        }
        pedido.setEstado(estado);
        return aRespuesta(pedidos.save(pedido));
    }

    /** Cancela el pedido y reintegra el stock (solo si aun no se entrega). */
    @Transactional
    public PedidoResponse cancelar(Long id) {
        Pedido pedido = buscar(id);
        if (pedido.getEstado() == EstadoPedido.ENTREGADO) {
            throw new OperacionInvalidaException("Un pedido entregado no puede cancelarse");
        }
        if (pedido.getEstado() == EstadoPedido.CANCELADO) {
            throw new OperacionInvalidaException("El pedido ya esta cancelado");
        }
        for (DetallePedido detalle : pedido.getDetalles()) {
            try {
                // Cantidad negativa: el catalogo reintegra el stock vendido.
                catalogo.descontarStock(detalle.getIdProducto(), -detalle.getCantidad());
            } catch (OperacionInvalidaException ignorado) {
                // Si el producto ya no existe en el catalogo, no se reintegra: se cancela igualmente.
            }
        }
        pedido.setEstado(EstadoPedido.CANCELADO);
        return aRespuesta(pedidos.save(pedido));
    }

    /** Elimina un pedido cancelado (administrador, limpieza de datos). */
    @Transactional
    public void eliminar(Long id) {
        Pedido pedido = buscar(id);
        if (pedido.getEstado() != EstadoPedido.CANCELADO) {
            throw new OperacionInvalidaException("Solo se puede eliminar un pedido cancelado");
        }
        pedidos.delete(pedido);
    }

    private Pedido buscar(Long id) {
        return pedidos.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Pedido " + id + " no existe"));
    }

    private static PedidoResponse aRespuesta(Pedido pedido) {
        List<DetallePedidoResponse> detalles = pedido.getDetalles().stream()
                .map(detalle -> new DetallePedidoResponse(
                        detalle.getIdProducto(),
                        detalle.getNombreProducto(),
                        detalle.getPrecioUnitario(),
                        detalle.getCantidad(),
                        detalle.getSubtotal()))
                .toList();
        return new PedidoResponse(
                pedido.getIdPedido(),
                pedido.getIdUsuario(),
                pedido.getFechaPedido(),
                pedido.getEstado().name(),
                pedido.getTipoEntrega().name(),
                pedido.getDireccionEntrega(),
                pedido.getTotal(),
                detalles
        );
    }
}
