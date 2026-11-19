package com.sonidovivo.SonidoVivo.usuarios.controller;

import com.sonidovivo.SonidoVivo.usuarios.model.ActualizarUsuarioRequest;
import com.sonidovivo.SonidoVivo.usuarios.model.CambiarEstadoRequest;
import com.sonidovivo.SonidoVivo.usuarios.model.CambiarRolRequest;
import com.sonidovivo.SonidoVivo.usuarios.model.UsuarioResponse;
import com.sonidovivo.SonidoVivo.usuarios.service.UsuarioService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Gestion de usuarios (panel de administracion y perfil propio).
 * La restriccion por rol se aplicara en el modulo de seguridad.
 */
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarios;

    public UsuarioController(UsuarioService usuarios) {
        this.usuarios = usuarios;
    }

    /** Listado para el panel de administracion. ?soloActivos=true limita el listado. */
    @GetMapping
    public ResponseEntity<List<UsuarioResponse>> listar(
            @RequestParam(name = "soloActivos", required = false) Boolean soloActivos) {
        return ResponseEntity.ok(usuarios.listar(soloActivos));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponse> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(usuarios.obtener(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponse> actualizar(
            @PathVariable Long id, @Valid @RequestBody ActualizarUsuarioRequest peticion) {
        return ResponseEntity.ok(usuarios.actualizar(id, peticion));
    }

    @PutMapping("/{id}/rol")
    public ResponseEntity<UsuarioResponse> cambiarRol(
            @PathVariable Long id, @Valid @RequestBody CambiarRolRequest peticion) {
        return ResponseEntity.ok(usuarios.cambiarRol(id, peticion));
    }

    @PutMapping("/{id}/estado")
    public ResponseEntity<UsuarioResponse> cambiarEstado(
            @PathVariable Long id, @Valid @RequestBody CambiarEstadoRequest peticion) {
        return ResponseEntity.ok(usuarios.cambiarEstado(id, peticion));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desactivar(@PathVariable Long id) {
        usuarios.desactivar(id);
        return ResponseEntity.noContent().build();
    }
}
