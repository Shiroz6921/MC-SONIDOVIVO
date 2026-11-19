package com.sonidovivo.SonidoVivo.contacto.controller;

import com.sonidovivo.SonidoVivo.contacto.model.ContactoRequest;
import com.sonidovivo.SonidoVivo.contacto.model.ContactoResponse;
import com.sonidovivo.SonidoVivo.contacto.model.EstadoContacto;
import com.sonidovivo.SonidoVivo.contacto.model.EstadoContactoRequest;
import com.sonidovivo.SonidoVivo.contacto.service.ContactoService;
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
 * API de contactos: el formulario publico registra mensajes y el personal
 * de la tienda los consulta y marca como atendidos.
 */
@RestController
@RequestMapping("/api/contactos")
public class ContactoController {

    private final ContactoService contactos;

    public ContactoController(ContactoService contactos) {
        this.contactos = contactos;
    }

    /** Publico: lo llama el formulario de contacto de React. */
    @PostMapping
    public ResponseEntity<ContactoResponse> registrar(@Valid @RequestBody ContactoRequest peticion) {
        return ResponseEntity.status(HttpStatus.CREATED).body(contactos.registrar(peticion));
    }

    /** Bandeja de mensajes. Filtro opcional: ?estado=PENDIENTE */
    @GetMapping
    public ResponseEntity<List<ContactoResponse>> listar(
            @RequestParam(name = "estado", required = false) String estado) {
        EstadoContacto filtro = null;
        if (estado != null && !estado.isBlank()) {
            filtro = EstadoContacto.valueOf(estado.trim().toUpperCase());
        }
        return ResponseEntity.ok(contactos.listar(filtro));
    }

    /** Marca un mensaje como ATENDIDO (o lo devuelve a PENDIENTE). */
    @PutMapping("/{id}/estado")
    public ResponseEntity<ContactoResponse> cambiarEstado(
            @PathVariable Long id, @Valid @RequestBody EstadoContactoRequest peticion) {
        return ResponseEntity.ok(contactos.cambiarEstado(id, peticion.estado()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        contactos.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
