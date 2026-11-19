package com.sonidovivo.SonidoVivo.catalogo.controller;

import com.sonidovivo.SonidoVivo.catalogo.model.CambioStockRequest;
import com.sonidovivo.SonidoVivo.catalogo.model.DescuentoStockRequest;
import com.sonidovivo.SonidoVivo.catalogo.model.ProductoRequest;
import com.sonidovivo.SonidoVivo.catalogo.model.ProductoResponse;
import com.sonidovivo.SonidoVivo.catalogo.service.CatalogoService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * API del catalogo: catalogo publico para la web y operaciones de stock
 * para vendedores, administrador y el microservicio de pedidos.
 */
@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final CatalogoService catalogo;

    public ProductoController(CatalogoService catalogo) {
        this.catalogo = catalogo;
    }

    /** Catalogo con precios y stock real. Filtros: ?categoria=guitarras&texto=yamaha */
    @GetMapping
    public ResponseEntity<List<ProductoResponse>> listar(
            @RequestParam(name = "categoria", required = false) String categoria,
            @RequestParam(name = "texto", required = false) String texto) {
        return ResponseEntity.ok(catalogo.listarProductos(categoria, texto));
    }

    /** Detalle de producto (pagina /producto/:id de React). */
    @GetMapping("/{id}")
    public ResponseEntity<ProductoResponse> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(catalogo.obtenerProducto(id));
    }

    @PostMapping
    public ResponseEntity<ProductoResponse> crear(@Valid @RequestBody ProductoRequest peticion) {
        return ResponseEntity.status(HttpStatus.CREATED).body(catalogo.crearProducto(peticion));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductoResponse> actualizar(
            @PathVariable Long id, @Valid @RequestBody ProductoRequest peticion) {
        return ResponseEntity.ok(catalogo.actualizarProducto(id, peticion));
    }

    /** Reposicion o correccion manual de stock (vendedor/administrador). */
    @PutMapping("/{id}/stock")
    public ResponseEntity<ProductoResponse> cambiarStock(
            @PathVariable Long id, @Valid @RequestBody CambioStockRequest peticion) {
        return ResponseEntity.ok(catalogo.cambiarStock(id, peticion.delta()));
    }

    /** Descuento automatico de stock al confirmar un pedido (lo usa el microservicio de pedidos). */
    @PutMapping("/{id}/descontar-stock")
    public ResponseEntity<ProductoResponse> descontarStock(
            @PathVariable Long id, @Valid @RequestBody DescuentoStockRequest peticion) {
        return ResponseEntity.ok(catalogo.descontarStock(id, peticion.cantidad()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        catalogo.eliminarProducto(id);
        return ResponseEntity.noContent().build();
    }
}
