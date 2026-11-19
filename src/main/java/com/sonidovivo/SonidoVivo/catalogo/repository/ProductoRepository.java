package com.sonidovivo.SonidoVivo.catalogo.repository;

import com.sonidovivo.SonidoVivo.catalogo.model.Producto;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    List<Producto> findByActivoTrue();

    List<Producto> findByActivoTrueAndCategoriaSlugIgnoreCase(String slug);

    List<Producto> findByActivoTrueAndTituloContainingIgnoreCase(String texto);

    Optional<Producto> findByIdProductoAndActivoTrue(Long idProducto);

    List<Producto> findByIdProductoInAndActivoTrue(List<Long> ids);
}
