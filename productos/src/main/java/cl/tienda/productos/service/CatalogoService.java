package cl.tienda.productos.service;

import cl.tienda.productos.model.Categoria;
import cl.tienda.productos.model.Oferta;
import cl.tienda.productos.model.Producto;
import cl.tienda.productos.repository.CategoriaRepository;
import cl.tienda.productos.repository.OfertaRepository;
import cl.tienda.productos.repository.ProductoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class CatalogoService {

    private final ProductoRepository productos;
    private final CategoriaRepository categorias;
    private final OfertaRepository ofertas;

    public CatalogoService(ProductoRepository productos, CategoriaRepository categorias, OfertaRepository ofertas) {
        this.productos = productos;
        this.categorias = categorias;
        this.ofertas = ofertas;
    }

    public List<Producto> listarProductos(String categoria, String texto) {
        List<Producto> lista;
        if (categoria != null && !categoria.isBlank()) {
            lista = productos.findByActivoTrueAndCategoriaSlugIgnoreCase(categoria.trim());
        } else if (texto != null && !texto.isBlank()) {
            lista = productos.findByActivoTrueAndTituloContainingIgnoreCase(texto.trim());
        } else {
            lista = productos.findByActivoTrue();
        }
        return lista;
    }

    public Producto obtenerProducto(Long id) {
        return productos.findByIdAndActivoTrue(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no existe o fue retirado"));
    }

    @Transactional
    public Producto crearProducto(Producto peticion) {
        peticion.setId(null);
        peticion.setActivo(true);
        if (peticion.getCategoria() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La categoría es obligatoria");
        }
        Categoria cat = categorias.findById(peticion.getCategoria().getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoría no existe"));
        peticion.setCategoria(cat);
        return productos.save(peticion);
    }

    @Transactional
    public Producto actualizarProducto(Long id, Producto peticion) {
        Producto producto = productos.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no existe"));
        producto.setTitulo(peticion.getTitulo());
        producto.setDescripcion(peticion.getDescripcion());
        producto.setPrecio(peticion.getPrecio());
        producto.setStock(peticion.getStock());
        producto.setImagen(peticion.getImagen());
        if (peticion.getCategoria() != null && peticion.getCategoria().getId() != null) {
            Categoria cat = categorias.findById(peticion.getCategoria().getId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoría no existe"));
            producto.setCategoria(cat);
        }
        if (peticion.getActivo() != null) {
            producto.setActivo(peticion.getActivo());
        }
        return productos.save(producto);
    }

    @Transactional
    public void eliminarProducto(Long id) {
        Producto producto = productos.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no existe"));
        producto.setActivo(false);
        productos.save(producto);
    }

    @Transactional
    public Producto cambiarStock(Long id, Integer delta) {
        Producto producto = obtenerProducto(id);
        int nuevo = producto.getStock() + delta;
        if (nuevo < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El stock no puede quedar negativo");
        }
        producto.setStock(nuevo);
        return productos.save(producto);
    }

    @Transactional
    public Producto descontarStock(Long id, Integer cantidad) {
        Producto producto = obtenerProducto(id);
        if (producto.getStock() < cantidad) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Stock insuficiente para " + producto.getTitulo() + ": quedan " + producto.getStock());
        }
        producto.setStock(producto.getStock() - cantidad);
        return productos.save(producto);
    }

    @Transactional
    public Producto reponerStock(Long id, Integer cantidad) {
        Producto producto = obtenerProducto(id);
        producto.setStock(producto.getStock() + cantidad);
        return productos.save(producto);
    }

    public List<Categoria> listarCategorias() {
        return categorias.findAll();
    }

    public List<Oferta> listarOfertas() {
        return ofertas.findByActivoTrue().stream()
                .filter(o -> Boolean.TRUE.equals(o.getProducto().getActivo()))
                .toList();
    }

    public Oferta obtenerOferta(Long idProducto) {
        return ofertas.findByProductoIdAndActivoTrue(idProducto).orElse(null);
    }
}
