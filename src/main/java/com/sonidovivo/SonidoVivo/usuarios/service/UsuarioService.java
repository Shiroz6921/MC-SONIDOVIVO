package com.sonidovivo.SonidoVivo.usuarios.service;

import com.sonidovivo.SonidoVivo.excepciones.RecursoNoEncontradoException;
import com.sonidovivo.SonidoVivo.usuarios.model.ActualizarUsuarioRequest;
import com.sonidovivo.SonidoVivo.usuarios.model.CambiarEstadoRequest;
import com.sonidovivo.SonidoVivo.usuarios.model.CambiarRolRequest;
import com.sonidovivo.SonidoVivo.usuarios.model.LoginRequest;
import com.sonidovivo.SonidoVivo.usuarios.model.RegistroRequest;
import com.sonidovivo.SonidoVivo.usuarios.model.Usuario;
import com.sonidovivo.SonidoVivo.usuarios.model.UsuarioResponse;
import com.sonidovivo.SonidoVivo.usuarios.model.Rol;
import com.sonidovivo.SonidoVivo.usuarios.repository.RolRepository;
import com.sonidovivo.SonidoVivo.usuarios.repository.UsuarioRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioService {

    private static final String ROL_POR_DEFECTO = "CLIENTE";

    private final UsuarioRepository usuarios;
    private final RolRepository roles;
    private final BCryptPasswordEncoder encriptador = new BCryptPasswordEncoder();

    public UsuarioService(UsuarioRepository usuarios, RolRepository roles) {
        this.usuarios = usuarios;
        this.roles = roles;
    }

    @Transactional
    public UsuarioResponse registrar(RegistroRequest datos) {
        String email = datos.email().trim().toLowerCase();
        if (usuarios.existsByEmailIgnoreCase(email)) {
            throw new IllegalArgumentException("El correo ya esta registrado");
        }
        Usuario usuario = new Usuario();
        usuario.setNombre(datos.nombre().trim());
        usuario.setApellido(datos.apellido().trim());
        usuario.setEmail(email);
        usuario.setPasswordHash(encriptador.encode(datos.password()));
        usuario.setTelefono(vacioANull(datos.telefono()));
        usuario.setDireccion(vacioANull(datos.direccion()));
        usuario.setRol(rolPorNombre(ROL_POR_DEFECTO));
        usuario.setActivo(Boolean.TRUE);
        usuario.setFechaRegistro(LocalDateTime.now());
        return aRespuesta(usuarios.save(usuario));
    }

    @Transactional(readOnly = true)
    public UsuarioResponse login(LoginRequest credenciales) {
        Usuario usuario = usuarios.findByEmailIgnoreCase(credenciales.email().trim())
                .orElseThrow(() -> new RecursoNoEncontradoException("Credenciales incorrectas"));

        if (!Boolean.TRUE.equals(usuario.getActivo())) {
            throw new IllegalStateException("El usuario esta desactivado");
        }
        if (!encriptador.matches(credenciales.password(), usuario.getPasswordHash())) {
            throw new RecursoNoEncontradoException("Credenciales incorrectas");
        }
        return aRespuesta(usuario);
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponse> listar(Boolean soloActivos) {
        List<Usuario> lista = Boolean.TRUE.equals(soloActivos)
                ? usuarios.findAll().stream().filter(u -> Boolean.TRUE.equals(u.getActivo())).toList()
                : usuarios.findAll();
        return lista.stream().map(UsuarioService::aRespuesta).toList();
    }

    @Transactional(readOnly = true)
    public UsuarioResponse obtener(Long id) {
        return aRespuesta(buscar(id));
    }

    @Transactional
    public UsuarioResponse actualizar(Long id, ActualizarUsuarioRequest cambios) {
        Usuario usuario = buscar(id);
        if (datosValidos(cambios.nombre())) {
            usuario.setNombre(cambios.nombre().trim());
        }
        if (datosValidos(cambios.apellido())) {
            usuario.setApellido(cambios.apellido().trim());
        }
        if (cambios.telefono() != null) {
            usuario.setTelefono(vacioANull(cambios.telefono()));
        }
        if (cambios.direccion() != null) {
            usuario.setDireccion(vacioANull(cambios.direccion()));
        }
        return aRespuesta(usuarios.save(usuario));
    }

    @Transactional
    public UsuarioResponse cambiarRol(Long id, CambiarRolRequest peticion) {
        Usuario usuario = buscar(id);
        usuario.setRol(rolPorNombre(peticion.nombreRol().trim()));
        return aRespuesta(usuarios.save(usuario));
    }

    @Transactional
    public UsuarioResponse cambiarEstado(Long id, CambiarEstadoRequest peticion) {
        Usuario usuario = buscar(id);
        usuario.setActivo(peticion.activo());
        return aRespuesta(usuarios.save(usuario));
    }

    /** Borrado logico: el usuario queda desactivado, no se pierde el historial. */
    @Transactional
    public void desactivar(Long id) {
        Usuario usuario = buscar(id);
        usuario.setActivo(Boolean.FALSE);
        usuarios.save(usuario);
    }

    private Usuario buscar(Long id) {
        return usuarios.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario " + id + " no existe"));
    }

    private Rol rolPorNombre(String nombre) {
        return roles.findByNombreRolIgnoreCase(nombre)
                .orElseThrow(() -> new RecursoNoEncontradoException("Rol " + nombre + " no existe"));
    }

    private static boolean datosValidos(String valor) {
        return valor != null && !valor.isBlank();
    }

    private static String vacioANull(String valor) {
        return (valor == null || valor.isBlank()) ? null : valor.trim();
    }

    private static UsuarioResponse aRespuesta(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getIdUsuario(),
                usuario.getNombre(),
                usuario.getApellido(),
                usuario.getEmail(),
                usuario.getTelefono(),
                usuario.getDireccion(),
                usuario.getRol().getNombreRol(),
                Boolean.TRUE.equals(usuario.getActivo()),
                usuario.getFechaRegistro()
        );
    }
}
