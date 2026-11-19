package com.sonidovivo.SonidoVivo.usuarios.repository;

import com.sonidovivo.SonidoVivo.usuarios.model.Rol;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RolRepository extends JpaRepository<Rol, Long> {

    Optional<Rol> findByNombreRolIgnoreCase(String nombreRol);
}
