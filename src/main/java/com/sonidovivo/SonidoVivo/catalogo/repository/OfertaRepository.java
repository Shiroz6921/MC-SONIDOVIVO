package com.sonidovivo.SonidoVivo.catalogo.repository;

import com.sonidovivo.SonidoVivo.catalogo.model.Oferta;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OfertaRepository extends JpaRepository<Oferta, Long> {

    List<Oferta> findByActivoTrue();

    List<Oferta> findByProductoIdProductoInAndActivoTrue(List<Long> idsProductos);

    Optional<Oferta> findByProductoIdProductoAndActivoTrue(Long idProducto);
}
