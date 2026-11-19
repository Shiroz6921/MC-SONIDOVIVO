package com.sonidovivo.SonidoVivo.pedidos.controller;

import com.sonidovivo.SonidoVivo.pedidos.model.CrearPedidoRequest;
import com.sonidovivo.SonidoVivo.pedidos.model.EstadoPedidoRequest;
import com.sonidovivo.SonidoVivo.pedidos.model.PedidoResponse;
import com.sonidovivo.SonidoVivo.pedidos.service.PedidoService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * API de pedidos: confirmacion del carrito (React), historial del cliente
 * y gestion de estados para vendedor y administrador.
 */
@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final PedidoService pedidos;

    public PedidoController(PedidoService pedidos) {
        this.pedidos = pedidos;
    }

    /** Confirma el carrito de compras y registra el pedido. */
    @PostMapping
    public ResponseEntity<PedidoResponse> crear(@Valid @RequestBody CrearPedidoRequest peticion) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pedidos.crear(peticion));
    }

    /** Todos los pedidos, para la vista de gestion (vendedor/administrador). */
    @GetMapping
    public ResponseEntity<List<PedidoResponse>> listar() {
        return ResponseEntity.ok(pedidos.listarTodos());
    }

    /** Historial de un cliente (ve solo sus propios pedidos). */
    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<PedidoResponse>> listarPorUsuario(@PathVariable Long idUsuario) {
        return ResponseEntity.ok(pedidos.listarPorUsuario(idUsuario));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PedidoResponse> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(pedidos.obtener(id));
    }

    /** Avanza el estado: EN_PREPARACION, DESPACHADO o ENTREGADO. */
    @PutMapping("/{id}/estado")
    public ResponseEntity<PedidoResponse> cambiarEstado(
            @PathVariable Long id, @Valid @RequestBody EstadoPedidoRequest peticion) {
        return ResponseEntity.ok(pedidos.cambiarEstado(id, peticion.estado()));
    }

    /** Cancela el pedido (si no fue entregado) y reintegra el stock. */
    @PutMapping("/{id}/cancelar")
    public ResponseEntity<PedidoResponse> cancelar(@PathVariable Long id) {
        return ResponseEntity.ok(pedidos.cancelar(id));
    }

    /** Elimina un pedido ya cancelado (administrador). */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        pedidos.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
