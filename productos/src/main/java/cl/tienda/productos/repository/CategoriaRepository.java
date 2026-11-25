package cl.tienda.productos.repository;

import cl.tienda.productos.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    Optional<Categoria> findBySlugIgnoreCase(String slug);
    Optional<Categoria> findByNombreIgnoreCase(String nombre);
}
