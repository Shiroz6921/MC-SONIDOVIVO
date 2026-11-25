package cl.tienda.productos.controller;

import cl.tienda.productos.model.Categoria;
import cl.tienda.productos.service.CatalogoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Categorias", description = "Categorías del catálogo")
@RestController
@RequestMapping("/api/categorias")
@CrossOrigin(origins = "http://localhost:5173")
public class CategoriaController {

    private final CatalogoService service;

    public CategoriaController(CatalogoService service) {
        this.service = service;
    }

    @Operation(summary = "Listar categorías")
    @GetMapping
    public List<Categoria> listar() {
        return service.listarCategorias();
    }
}
