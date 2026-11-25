package cl.tienda.usuarios.service;

import cl.tienda.usuarios.dto.LoginRequest;
import cl.tienda.usuarios.dto.RegistroRequest;
import cl.tienda.usuarios.dto.UsuarioRespuesta;
import cl.tienda.usuarios.model.Usuario;
import cl.tienda.usuarios.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository repository;
    private final PasswordEncoder encoder;

    public UsuarioService(UsuarioRepository repository, PasswordEncoder encoder) {
        this.repository = repository;
        this.encoder = encoder;
    }

    public UsuarioRespuesta registrar(RegistroRequest datos) {
        String email = datos.email().trim().toLowerCase();
        if (repository.existsByEmailIgnoreCase(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El correo ya está registrado");
        }
        Usuario usuario = new Usuario();
        usuario.setNombre(datos.nombre().trim());
        usuario.setApellido(datos.apellido().trim());
        usuario.setEmail(email);
        usuario.setPassword(encoder.encode(datos.password()));
        usuario.setTelefono(datos.telefono() == null ? null : datos.telefono().trim());
        usuario.setDireccion(datos.direccion() == null ? null : datos.direccion().trim());
        usuario.setRol("CLIENTE");
        usuario.setActivo(true);
        return UsuarioRespuesta.desde(repository.save(usuario));
    }

    public UsuarioRespuesta login(LoginRequest datos) {
        Usuario usuario = repository.findByEmailIgnoreCase(datos.email().trim())
                .filter(u -> encoder.matches(datos.password(), u.getPassword()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Correo o contraseña incorrectos"));
        if (!Boolean.TRUE.equals(usuario.getActivo())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "El usuario está desactivado");
        }
        return UsuarioRespuesta.desde(usuario);
    }

    public List<UsuarioRespuesta> listar() {
        return repository.findAll().stream().map(UsuarioRespuesta::desde).toList();
    }

    public UsuarioRespuesta obtener(Long id) {
        return UsuarioRespuesta.desde(buscar(id));
    }

    public UsuarioRespuesta cambiarRol(Long id, String rol) {
        if (rol == null || (!rol.equals("ADMINISTRADOR") && !rol.equals("VENDEDOR") && !rol.equals("CLIENTE"))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Rol no válido");
        }
        Usuario usuario = buscar(id);
        usuario.setRol(rol);
        return UsuarioRespuesta.desde(repository.save(usuario));
    }

    public void eliminar(Long id) {
        repository.delete(buscar(id));
    }

    private Usuario buscar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
    }
}
