package cl.tienda.usuarios.controller;

import cl.tienda.usuarios.dto.LoginRequest;
import cl.tienda.usuarios.dto.RegistroRequest;
import cl.tienda.usuarios.dto.UsuarioRespuesta;
import cl.tienda.usuarios.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Usuarios", description = "Registro, login y administración de usuarios")
@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "http://localhost:5173")
public class UsuarioController {

    private final UsuarioService service;

    public UsuarioController(UsuarioService service) {
        this.service = service;
    }

    @Operation(summary = "Registrar cliente")
    @PostMapping("/registro")
    public ResponseEntity<UsuarioRespuesta> registrar(@Valid @RequestBody RegistroRequest datos) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.registrar(datos));
    }

    @Operation(summary = "Iniciar sesión")
    @PostMapping("/login")
    public UsuarioRespuesta login(@Valid @RequestBody LoginRequest datos) {
        return service.login(datos);
    }

    @Operation(summary = "Listar usuarios")
    @GetMapping
    public List<UsuarioRespuesta> listar() {
        return service.listar();
    }

    @Operation(summary = "Obtener usuario")
    @GetMapping("/{id}")
    public UsuarioRespuesta obtener(@PathVariable("id") Long id) {
        return service.obtener(id);
    }

    @Operation(summary = "Cambiar rol")
    @PutMapping("/{id}/rol")
    public UsuarioRespuesta cambiarRol(@PathVariable("id") Long id, @RequestParam("rol") String rol) {
        return service.cambiarRol(id, rol);
    }

    @Operation(summary = "Eliminar usuario")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable("id") Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
