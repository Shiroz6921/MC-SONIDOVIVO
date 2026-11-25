package cl.tienda.pedidos.controller;

import cl.tienda.pedidos.dto.CrearPedidoRequest;
import cl.tienda.pedidos.model.Pedido;
import cl.tienda.pedidos.service.PedidoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Pedidos", description = "Compras de los clientes")
@RestController
@RequestMapping("/api/pedidos")
@CrossOrigin(origins = "http://localhost:5173")
public class PedidoController {

    private final PedidoService service;

    public PedidoController(PedidoService service) {
        this.service = service;
    }

    @Operation(summary = "Crear pedido")
    @PostMapping
    public ResponseEntity<Pedido> crear(@Valid @RequestBody CrearPedidoRequest datos) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(datos));
    }

    @Operation(summary = "Listar pedidos")
    @GetMapping
    public List<Pedido> listar(@RequestParam(name = "usuarioId", required = false) Long usuarioId) {
        return service.listar(usuarioId);
    }

    @Operation(summary = "Obtener pedido")
    @GetMapping("/{id}")
    public Pedido obtener(@PathVariable("id") Long id) {
        return service.obtener(id);
    }

    @Operation(summary = "Cambiar estado")
    @PutMapping("/{id}/estado")
    public Pedido cambiarEstado(@PathVariable("id") Long id, @RequestParam("estado") String estado) {
        return service.cambiarEstado(id, estado);
    }

    @Operation(summary = "Cancelar pedido")
    @PutMapping("/{id}/cancelar")
    public Pedido cancelar(@PathVariable("id") Long id) {
        return service.cancelar(id);
    }
}
