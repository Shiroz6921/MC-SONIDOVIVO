package com.sonidovivo.SonidoVivo.catalogo.controller;

import com.sonidovivo.SonidoVivo.catalogo.model.OfertaRequest;
import com.sonidovivo.SonidoVivo.catalogo.model.OfertaResponse;
import com.sonidovivo.SonidoVivo.catalogo.service.CatalogoService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Ofertas que la web muestra con el precio tachado.
 */
@RestController
@RequestMapping("/api/ofertas")
public class OfertaController {

    private final CatalogoService catalogo;

    public OfertaController(CatalogoService catalogo) {
        this.catalogo = catalogo;
    }

    @GetMapping
    public ResponseEntity<List<OfertaResponse>> listar() {
        return ResponseEntity.ok(catalogo.listarOfertas());
    }

    @PostMapping
    public ResponseEntity<OfertaResponse> crear(@Valid @RequestBody OfertaRequest peticion) {
        return ResponseEntity.status(HttpStatus.CREATED).body(catalogo.crearOferta(peticion));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        catalogo.eliminarOferta(id);
        return ResponseEntity.noContent().build();
    }
}
