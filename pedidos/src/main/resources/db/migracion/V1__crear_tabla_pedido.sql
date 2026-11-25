CREATE TABLE pedido (
    id BIGINT NOT NULL AUTO_INCREMENT,
    usuario_id BIGINT NOT NULL,
    total DECIMAL(12,0) NOT NULL,
    tipo_entrega VARCHAR(20) NOT NULL,
    direccion_entrega VARCHAR(200),
    estado VARCHAR(20) NOT NULL,
    fecha_pedido DATETIME(6) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE pedido_item (
    pedido_id BIGINT NOT NULL,
    producto_id BIGINT NOT NULL,
    nombre_producto VARCHAR(120) NOT NULL,
    precio_unitario DECIMAL(10,0) NOT NULL,
    cantidad INT NOT NULL,
    subtotal DECIMAL(12,0) NOT NULL,
    CONSTRAINT fk_pedido_item_pedido FOREIGN KEY (pedido_id) REFERENCES pedido (id)
);
