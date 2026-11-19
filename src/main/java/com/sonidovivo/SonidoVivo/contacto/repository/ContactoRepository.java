package com.sonidovivo.SonidoVivo.contacto.repository;

import com.sonidovivo.SonidoVivo.contacto.model.Contacto;
import com.sonidovivo.SonidoVivo.contacto.model.EstadoContacto;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContactoRepository extends JpaRepository<Contacto, Long> {

    List<Contacto> findByEstadoOrderByFechaEnvioDesc(EstadoContacto estado);

    List<Contacto> findAllByOrderByFechaEnvioDesc();
}
