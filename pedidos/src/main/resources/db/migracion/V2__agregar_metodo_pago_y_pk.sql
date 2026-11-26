ALTER TABLE pedido ADD COLUMN metodo_pago VARCHAR(20) NOT NULL DEFAULT 'TARJETA';
ALTER TABLE pedido_item ADD PRIMARY KEY (pedido_id, producto_id);
