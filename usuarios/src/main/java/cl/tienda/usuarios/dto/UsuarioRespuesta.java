package cl.tienda.usuarios.dto;

import cl.tienda.usuarios.model.Usuario;

import java.time.LocalDateTime;

public record UsuarioRespuesta(
        Long id,
        String nombre,
        String apellido,
        String email,
        String telefono,
        String direccion,
        String rol,
        boolean activo,
        LocalDateTime fechaRegistro
) {
    public static UsuarioRespuesta desde(Usuario usuario) {
        return new UsuarioRespuesta(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getApellido() == null ? "" : usuario.getApellido(),
                usuario.getEmail(),
                usuario.getTelefono(),
                usuario.getDireccion(),
                usuario.getRol(),
                Boolean.TRUE.equals(usuario.getActivo()),
                usuario.getFechaRegistro()
        );
    }
}
