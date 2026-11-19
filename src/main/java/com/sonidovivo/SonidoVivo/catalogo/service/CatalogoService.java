package com.sonidovivo.SonidoVivo.catalogo.service;

import com.sonidovivo.SonidoVivo.catalogo.model.Categoria;
import com.sonidovivo.SonidoVivo.catalogo.model.Oferta;
import com.sonidovivo.SonidoVivo.catalogo.model.OfertaRequest;
import com.sonidovivo.SonidoVivo.catalogo.model.OfertaResponse;
import com.sonidovivo.SonidoVivo.catalogo.model.Producto;
import com.sonidovivo.SonidoVivo.catalogo.model.ProductoRequest;
import com.sonidovivo.SonidoVivo.catalogo.model.ProductoResponse;
import com.sonidovivo.SonidoVivo.catalogo.repository.CategoriaRepository;
import com.sonidovivo.SonidoVivo.catalogo.repository.OfertaRepository;
import com.sonidovivo.SonidoVivo.catalogo.repository.ProductoRepository;
import com.sonidovivo.SonidoVivo.excepciones.OperacionInvalidaException;
import com.sonidovivo.SonidoVivo.excepciones.RecursoNoEncontradoException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    /* ---------- Productos ---------- */

    @Transactional(readOnly = true)
    public List<ProductoResponse> listarProductos(String categoria, String texto) {
        List<Producto> lista;
        if (categoria != null && !categoria.isBlank()) {
            lista = productos.findByActivoTrueAndCategoriaSlugIgnoreCase(categoria.trim());
        } else if (texto != null && !texto.isBlank()) {
            lista = productos.findByActivoTrueAndTituloContainingIgnoreCase(texto.trim());
        } else {
            lista = productos.findByActivoTrue();
        }
        return completarConOfertas(lista);
    }

    @Transactional(readOnly = true)
    public ProductoResponse obtenerProducto(Long id) {
        Producto producto = buscarProductoActivo(id);
        return aRespuesta(producto, ofertas.findByProductoIdProductoAndActivoTrue(id).orElse(null));
    }

    @Transactional
    public ProductoResponse crearProducto(ProductoRequest peticion) {
        Categoria categoria = buscarCategoria(peticion.idCategoria());
        Producto producto = new Producto();
        producto.setTitulo(peticion.titulo().trim());
        producto.setDescripcion(peticion.descripcion().trim());
        producto.setPrecio(peticion.precio());
        producto.setStock(peticion.stock());
        producto.setImagen(peticion.imagen().trim());
        producto.setCategoria(categoria);
        producto.setActivo(peticion.activo() == null || peticion.activo());
        return aRespuesta(productos.save(producto), null);
    }

    @Transactional
    public ProductoResponse actualizarProducto(Long id, ProductoRequest peticion) {
        Producto producto = productos.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto " + id + " no existe"));
        producto.setTitulo(peticion.titulo().trim());
        producto.setDescripcion(peticion.descripcion().trim());
        producto.setPrecio(peticion.precio());
        producto.setStock(peticion.stock());
        producto.setImagen(peticion.imagen().trim());
        producto.setCategoria(buscarCategoria(peticion.idCategoria()));
        if (peticion.activo() != null) {
            producto.setActivo(peticion.activo());
        }
        return aRespuesta(productos.save(producto), null);
    }

    /** Cambio manual de stock por parte del dueño o los vendedores. */
    @Transactional
    public ProductoResponse cambiarStock(Long id, Integer delta) {
        Producto producto = buscarProductoActivo(id);
        int nuevoStock = producto.getStock() + delta;
        if (nuevoStock < 0) {
            throw new OperacionInvalidaException("El stock no puede quedar negativo");
        }
        producto.setStock(nuevoStock);
        return aRespuesta(productos.save(producto), null);
    }

    /** Descuento automatico de stock cuando el microservicio de pedidos registra una venta.
     *  Con cantidad negativa reintegra el stock (pedido cancelado). */
    @Transactional
    public ProductoResponse descontarStock(Long id, Integer cantidad) {
        Producto producto = buscarProductoActivo(id);
        if (producto.getStock() < cantidad) {
            throw new OperacionInvalidaException(
                    "Stock insuficiente para " + producto.getTitulo() + ": quedan " + producto.getStock());
        }
        producto.setStock(producto.getStock() - cantidad);
        return aRespuesta(productos.save(producto), null);
    }

    /** Borrado logico para no romper los pedidos antiguos. */
    @Transactional
    public void eliminarProducto(Long id) {
        Producto producto = productos.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto " + id + " no existe"));
        producto.setActivo(Boolean.FALSE);
        productos.save(producto);
    }

    /* ---------- Ofertas ---------- */

    @Transactional(readOnly = true)
    public List<OfertaResponse> listarOfertas() {
        return ofertas.findByActivoTrue().stream()
                .filter(oferta -> Boolean.TRUE.equals(oferta.getProducto().getActivo()))
                .map(CatalogoService::aRespuestaOferta)
                .toList();
    }

    @Transactional
    public OfertaResponse crearOferta(OfertaRequest peticion) {
        if (peticion.precioOferta().compareTo(peticion.precioAnterior()) >= 0) {
            throw new OperacionInvalidaException("El precio de oferta debe ser menor al precio anterior");
        }
        Producto producto = buscarProductoActivo(peticion.idProducto());
        ofertas.findByProductoIdProductoAndActivoTrue(producto.getIdProducto())
                .ifPresent(oferta -> {
                    throw new OperacionInvalidaException(
                            "El producto " + producto.getTitulo() + " ya tiene una oferta activa");
                });
        Oferta oferta = new Oferta();
        oferta.setProducto(producto);
        oferta.setPrecioOferta(peticion.precioOferta());
        oferta.setPrecioAnterior(peticion.precioAnterior());
        oferta.setFechaInicio(peticion.fechaInicio());
        oferta.setFechaFin(peticion.fechaFin());
        return aRespuestaOferta(ofertas.save(oferta));
    }

    @Transactional
    public void eliminarOferta(Long idOferta) {
        Oferta oferta = ofertas.findById(idOferta)
                .orElseThrow(() -> new RecursoNoEncontradoException("Oferta " + idOferta + " no existe"));
        oferta.setActivo(Boolean.FALSE);
        ofertas.save(oferta);
    }

    /* ---------- Categorias ---------- */

    @Transactional(readOnly = true)
    public List<Categoria> listarCategorias() {
        return categorias.findAll();
    }

    /* ---------- Metodos privados ---------- */

    private Producto buscarProductoActivo(Long id) {
        return productos.findByIdProductoAndActivoTrue(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto " + id + " no existe o fue retirado"));
    }

    private Categoria buscarCategoria(Long id) {
        return categorias.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Categoria " + id + " no existe"));
    }

    /** Completa el precio de oferta de cada producto en una sola consulta. */
    private List<ProductoResponse> completarConOfertas(List<Producto> lista) {
        if (lista.isEmpty()) {
            return List.of();
        }
        Map<Long, Oferta> ofertasPorProducto = ofertas
                .findByProductoIdProductoInAndActivoTrue(
                        lista.stream().map(Producto::getIdProducto).toList())
                .stream()
                .collect(Collectors.toMap(oferta -> oferta.getProducto().getIdProducto(), Function.identity()));

        return lista.stream()
                .map(producto -> aRespuesta(producto, ofertasPorProducto.get(producto.getIdProducto())))
                .toList();
    }

    private static ProductoResponse aRespuesta(Producto producto, Oferta oferta) {
        boolean conOferta = oferta != null && Boolean.TRUE.equals(oferta.getActivo());
        return new ProductoResponse(
                producto.getIdProducto(),
                producto.getTitulo(),
                producto.getDescripcion(),
                producto.getPrecio(),
                producto.getStock(),
                producto.getImagen(),
                producto.getCategoria().getNombre(),
                Boolean.TRUE.equals(producto.getActivo()),
                conOferta ? oferta.getPrecioOferta() : null,
                conOferta ? oferta.getPrecioAnterior() : null
        );
    }

    private static OfertaResponse aRespuestaOferta(Oferta oferta) {
        Producto producto = oferta.getProducto();
        return new OfertaResponse(
                oferta.getIdOferta(),
                producto.getIdProducto(),
                producto.getTitulo(),
                producto.getImagen(),
                producto.getPrecio(),
                oferta.getPrecioOferta(),
                oferta.getPrecioAnterior(),
                producto.getStock()
        );
    }
}
