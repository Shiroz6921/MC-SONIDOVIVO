package cl.duoc.mensajeria.controller;

import cl.duoc.mensajeria.model.Mensajeria;
import cl.duoc.mensajeria.service.MensajeriaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Mensajería", description = "Mensajes del formulario de contacto")
@RestController
@RequestMapping("/api/contactos")
@CrossOrigin(origins = "http://localhost:5173")
public class MensajeriaController {

    private final MensajeriaService service;

    public MensajeriaController(MensajeriaService service) {
        this.service = service;
    }

    @Operation(summary = "Enviar mensaje")
    @PostMapping
    public ResponseEntity<Mensajeria> guardar(@Valid @RequestBody Mensajeria contacto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.guardar(contacto));
    }

    @Operation(summary = "Listar mensajes")
    @GetMapping
    public List<Mensajeria> listar() {
        return service.listar();
    }

    @Operation(summary = "Cambiar estado")
    @PutMapping("/{id}/estado")
    public Mensajeria cambiarEstado(@PathVariable("id") Long id, @RequestParam("estado") String estado) {
        return service.cambiarEstado(id, estado);
    }
}
