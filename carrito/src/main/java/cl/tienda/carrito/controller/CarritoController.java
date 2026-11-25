package cl.tienda.carrito.controller;

import cl.tienda.carrito.dto.AgregarItemRequest;
import cl.tienda.carrito.dto.CarritoRespuesta;
import cl.tienda.carrito.service.CarritoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Carrito", description = "Carrito de compras guardado en MySQL por usuario")
@RestController
@RequestMapping("/api/carrito")
@CrossOrigin(origins = "http://localhost:5173")
public class CarritoController {

    private final CarritoService service;

    public CarritoController(CarritoService service) {
        this.service = service;
    }

    @Operation(summary = "Ver carrito")
    @GetMapping("/{usuarioId}")
    public CarritoRespuesta obtener(@PathVariable("usuarioId") Long usuarioId) {
        return service.obtener(usuarioId);
    }

    @Operation(summary = "Agregar producto")
    @PostMapping("/{usuarioId}/items")
    public CarritoRespuesta agregar(@PathVariable("usuarioId") Long usuarioId,
                                    @Valid @RequestBody AgregarItemRequest datos) {
        return service.agregar(usuarioId, datos);
    }

    @Operation(summary = "Cambiar cantidad")
    @PutMapping("/{usuarioId}/items/{productoId}")
    public CarritoRespuesta cambiarCantidad(@PathVariable("usuarioId") Long usuarioId,
                                            @PathVariable("productoId") Long productoId,
                                            @RequestParam("cantidad") int cantidad) {
        return service.cambiarCantidad(usuarioId, productoId, cantidad);
    }

    @Operation(summary = "Quitar producto")
    @DeleteMapping("/{usuarioId}/items/{productoId}")
    public CarritoRespuesta quitar(@PathVariable("usuarioId") Long usuarioId,
                                   @PathVariable("productoId") Long productoId) {
        return service.quitar(usuarioId, productoId);
    }

    @Operation(summary = "Vaciar carrito")
    @DeleteMapping("/{usuarioId}")
    public ResponseEntity<Void> vaciar(@PathVariable("usuarioId") Long usuarioId) {
        service.vaciar(usuarioId);
        return ResponseEntity.noContent().build();
    }
}
