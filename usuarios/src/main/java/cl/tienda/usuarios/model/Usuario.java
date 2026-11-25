package cl.tienda.usuarios.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, length = 100)
    private String apellido;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    // Guardada cifrada con BCrypt. Nunca se devuelve a React (ver UsuarioRespuesta).
    @Column(nullable = false, length = 100)
    private String password;

    // ADMINISTRADOR, VENDEDOR o CLIENTE
    @Column(nullable = false, length = 20)
    private String rol = "CLIENTE";

    @Column(length = 20)
    private String telefono;

    @Column(length = 200)
    private String direccion;

    @Column(nullable = false)
    private Boolean activo = true;

    @Column(name = "fecha_registro", nullable = false, updatable = false)
    private LocalDateTime fechaRegistro;

    @PrePersist
    protected void asignarFechaRegistro() {
        this.fechaRegistro = LocalDateTime.now();
    }
}
