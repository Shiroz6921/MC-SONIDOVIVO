package com.sonidovivo.SonidoVivo.usuarios.repository;

import com.sonidovivo.SonidoVivo.usuarios.model.Usuario;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);

    Optional<Usuario> findByIdUsuarioAndActivoTrue(Long idUsuario);
}
