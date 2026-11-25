package cl.tienda.productos.repository;

import cl.tienda.productos.model.Oferta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OfertaRepository extends JpaRepository<Oferta, Long> {
    List<Oferta> findByActivoTrue();
    Optional<Oferta> findByProductoIdAndActivoTrue(Long idProducto);
}
