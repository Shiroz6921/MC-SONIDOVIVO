-- Cada fila es un producto dentro del carrito de un usuario.
CREATE TABLE carrito_item (
    id BIGINT NOT NULL AUTO_INCREMENT,
    usuario_id BIGINT NOT NULL,
    producto_id BIGINT NOT NULL,
    nombre VARCHAR(150) NOT NULL,
    precio INT NOT NULL,
    imagen VARCHAR(300),
    cantidad INT NOT NULL,
    fecha_agregado DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_carrito_usuario_producto (usuario_id, producto_id)
);
