package cl.tienda.productos.controller;

import cl.tienda.productos.model.Oferta;
import cl.tienda.productos.service.CatalogoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Ofertas", description = "Ofertas del catálogo")
@RestController
@RequestMapping("/api/ofertas")
@CrossOrigin(origins = "http://localhost:5173")
public class OfertaController {

    private final CatalogoService service;

    public OfertaController(CatalogoService service) {
        this.service = service;
    }

    @Operation(summary = "Listar ofertas")
    @GetMapping
    public List<Oferta> listar() {
        return service.listarOfertas();
    }
}
