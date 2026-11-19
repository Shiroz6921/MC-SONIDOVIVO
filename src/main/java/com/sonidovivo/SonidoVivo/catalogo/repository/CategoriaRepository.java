package com.sonidovivo.SonidoVivo.catalogo.repository;

import com.sonidovivo.SonidoVivo.catalogo.model.Categoria;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    Optional<Categoria> findBySlugIgnoreCase(String slug);

    Optional<Categoria> findByNombreIgnoreCase(String nombre);
}
