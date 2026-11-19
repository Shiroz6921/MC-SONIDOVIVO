package com.sonidovivo.SonidoVivo.pedidos.repository;

import com.sonidovivo.SonidoVivo.pedidos.model.Pedido;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    /** Historial de compras de un cliente, del mas reciente al mas antiguo. */
    List<Pedido> findByIdUsuarioOrderByIdPedidoDesc(Long idUsuario);
}
