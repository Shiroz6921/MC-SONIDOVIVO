package cl.tienda.productos.repository;

import cl.tienda.productos.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
    List<Producto> findByActivoTrue();
    List<Producto> findByActivoTrueAndCategoriaSlugIgnoreCase(String slug);
    List<Producto> findByActivoTrueAndTituloContainingIgnoreCase(String texto);
    Optional<Producto> findByIdAndActivoTrue(Long id);
}
