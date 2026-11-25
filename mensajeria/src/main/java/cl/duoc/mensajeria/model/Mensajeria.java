package cl.duoc.mensajeria.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "contacto")
public class Mensajeria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String nombre;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "El correo no es válido")
    @Size(max = 150)
    @Column(nullable = false, length = 150)
    private String email;

    @Size(max = 20)
    @Column(length = 20)
    private String telefono;

    @NotBlank(message = "El asunto es obligatorio")
    @Size(max = 120)
    @Column(nullable = false, length = 120)
    private String asunto;

    @NotBlank(message = "El mensaje es obligatorio")
    @Size(min = 10, max = 1000)
    @Column(nullable = false, columnDefinition = "TEXT")
    private String mensaje;

    @Column(nullable = false, length = 20)
    private String estado = "PENDIENTE";

    @Column(name = "fecha_envio", nullable = false, updatable = false)
    private LocalDateTime fechaEnvio;

    @PrePersist
    protected void asignarFecha() {
        this.fechaEnvio = LocalDateTime.now();
    }
}
