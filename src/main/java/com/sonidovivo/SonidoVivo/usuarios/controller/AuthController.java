package com.sonidovivo.SonidoVivo.usuarios.controller;

import com.sonidovivo.SonidoVivo.usuarios.model.LoginRequest;
import com.sonidovivo.SonidoVivo.usuarios.model.RegistroRequest;
import com.sonidovivo.SonidoVivo.usuarios.model.UsuarioResponse;
import com.sonidovivo.SonidoVivo.usuarios.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoints publicos de autenticacion (sin sesion iniciada).
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UsuarioService usuarios;

    public AuthController(UsuarioService usuarios) {
        this.usuarios = usuarios;
    }

    @PostMapping("/registro")
    public ResponseEntity<UsuarioResponse> registro(@Valid @RequestBody RegistroRequest peticion) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarios.registrar(peticion));
    }

    @PostMapping("/login")
    public ResponseEntity<UsuarioResponse> login(@Valid @RequestBody LoginRequest peticion) {
        return ResponseEntity.ok(usuarios.login(peticion));
    }
}
