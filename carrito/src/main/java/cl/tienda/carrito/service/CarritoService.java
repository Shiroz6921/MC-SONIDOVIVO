package cl.tienda.carrito.service;

import cl.tienda.carrito.client.ProductoClient;
import cl.tienda.carrito.dto.AgregarItemRequest;
import cl.tienda.carrito.dto.CarritoRespuesta;
import cl.tienda.carrito.dto.ProductoInfo;
import cl.tienda.carrito.model.CarritoItem;
import cl.tienda.carrito.repository.CarritoItemRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class CarritoService {

    private final CarritoItemRepository repository;
    private final ProductoClient productoClient;

    public CarritoService(CarritoItemRepository repository, ProductoClient productoClient) {
        this.repository = repository;
        this.productoClient = productoClient;
    }

    public CarritoRespuesta obtener(Long usuarioId) {
        return CarritoRespuesta.desde(usuarioId, repository.findByUsuarioIdOrderByIdAsc(usuarioId));
    }

    @Transactional
    public CarritoRespuesta agregar(Long usuarioId, AgregarItemRequest datos) {
        ProductoInfo producto = productoClient.obtener(datos.productoId());

        CarritoItem item = repository.findByUsuarioIdAndProductoId(usuarioId, datos.productoId())
                .orElseGet(() -> {
                    CarritoItem nuevo = new CarritoItem();
                    nuevo.setUsuarioId(usuarioId);
                    nuevo.setProductoId(datos.productoId());
                    nuevo.setCantidad(0);
                    return nuevo;
                });

        int nuevaCantidad = item.getCantidad() + datos.cantidad();
        if (nuevaCantidad > producto.stock()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Stock insuficiente para " + producto.titulo() + " (disponible: " + producto.stock() + ")");
        }

        item.setNombre(producto.titulo());
        item.setPrecio(producto.precio());
        item.setImagen(producto.imagen());
        item.setCantidad(nuevaCantidad);
        repository.save(item);
        return obtener(usuarioId);
    }

    @Transactional
    public CarritoRespuesta cambiarCantidad(Long usuarioId, Long productoId, int cantidad) {
        if (cantidad <= 0) {
            return quitar(usuarioId, productoId);
        }
        CarritoItem item = repository.findByUsuarioIdAndProductoId(usuarioId, productoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "El producto no está en el carrito"));
        ProductoInfo producto = productoClient.obtener(productoId);
        if (cantidad > producto.stock()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Stock insuficiente para " + producto.titulo() + " (disponible: " + producto.stock() + ")");
        }
        item.setNombre(producto.titulo());
        item.setPrecio(producto.precio());
        item.setImagen(producto.imagen());
        item.setCantidad(cantidad);
        repository.save(item);
        return obtener(usuarioId);
    }

    @Transactional
    public CarritoRespuesta quitar(Long usuarioId, Long productoId) {
        CarritoItem item = repository.findByUsuarioIdAndProductoId(usuarioId, productoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "El producto no está en el carrito"));
        repository.delete(item);
        return obtener(usuarioId);
    }

    @Transactional
    public void vaciar(Long usuarioId) {
        repository.deleteByUsuarioId(usuarioId);
    }
}
