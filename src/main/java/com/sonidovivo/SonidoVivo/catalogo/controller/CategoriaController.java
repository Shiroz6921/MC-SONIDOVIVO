package com.sonidovivo.SonidoVivo.catalogo.controller;

import com.sonidovivo.SonidoVivo.catalogo.model.Categoria;
import com.sonidovivo.SonidoVivo.catalogo.service.CatalogoService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Categorias para armar el menu y los filtros de la web React.
 */
@RestController
@RequestMapping("/api/categorias")
public class CategoriaController {

    private final CatalogoService catalogo;

    public CategoriaController(CatalogoService catalogo) {
        this.catalogo = catalogo;
    }

    @GetMapping
    public ResponseEntity<List<Categoria>> listar() {
        return ResponseEntity.ok(catalogo.listarCategorias());
    }
}
