package cl.tienda.productos.controller;

import cl.tienda.productos.model.Categoria;
import cl.tienda.productos.model.Oferta;
import cl.tienda.productos.model.Producto;
import cl.tienda.productos.service.CatalogoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Productos", description = "Catálogo de productos y control de stock")
@RestController
@RequestMapping("/api/productos")
@CrossOrigin(origins = "http://localhost:5173")
public class ProductoController {

    private final CatalogoService service;

    public ProductoController(CatalogoService service) {
        this.service = service;
    }

    @Operation(summary = "Listar productos")
    @GetMapping
    public List<Producto> listar(@RequestParam(name = "categoria", required = false) String categoria,
                                 @RequestParam(name = "texto", required = false) String texto) {
        return service.listarProductos(categoria, texto);
    }

    @Operation(summary = "Obtener producto")
    @GetMapping("/{id}")
    public Producto obtener(@PathVariable("id") Long id) {
        return service.obtenerProducto(id);
    }

    @Operation(summary = "Crear producto")
    @PostMapping
    public ResponseEntity<Producto> guardar(@Valid @RequestBody Producto producto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crearProducto(producto));
    }

    @Operation(summary = "Modificar producto")
    @PutMapping("/{id}")
    public Producto actualizar(@PathVariable("id") Long id, @Valid @RequestBody Producto producto) {
        return service.actualizarProducto(id, producto);
    }

    @Operation(summary = "Eliminar producto")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable("id") Long id) {
        service.eliminarProducto(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Descontar stock")
    @PutMapping("/{id}/descontar")
    public Producto descontar(@PathVariable("id") Long id, @RequestParam("cantidad") int cantidad) {
        return service.descontarStock(id, cantidad);
    }

    @Operation(summary = "Reponer stock")
    @PutMapping("/{id}/reponer")
    public Producto reponer(@PathVariable("id") Long id, @RequestParam("cantidad") int cantidad) {
        return service.reponerStock(id, cantidad);
    }

    @Operation(summary = "Cambiar stock manualmente")
    @PutMapping("/{id}/stock")
    public Producto cambiarStock(@PathVariable("id") Long id, @RequestParam("delta") int delta) {
        return service.cambiarStock(id, delta);
    }
}
