# Análisis de persistencia de datos — SonidoVivo

Proyecto: tienda de instrumentos musicales SonidoVivo (Viña del Mar).
Stack: React (frontend) + Spring Boot (7 microservicios) + MySQL (una base por servicio) + API Gateway + Eureka.

---

## 1. Microservicios y sus bases de datos

| Servicio | Puerto | Base de datos | Responsabilidad |
|---|---|---|---|
| api-gateway | 8080 | — | Enrutamiento y CORS |
| eureka-server | 8761 | — | Descubrimiento de servicios |
| usuarios | 8081 | sonidovivo_usuarios | Autenticación, roles, perfiles |
| mensajeria | 8082 | sonidovivo_mensajeria | Mensajes de contacto |
| productos | 8083 | sonidovivo_productos | Catálogo, categorías, ofertas, stock |
| pedidos | 8084 | sonidovivo_pedidos | Pedidos y detalle |
| carrito | 8085 | sonidovivo_carrito | Carrito por usuario |

Cada microservicio tiene su propia base de datos MySQL (Flyway para migraciones).

## 2. Entidades principales

- **usuarios**: id, nombre, apellido, email, password (BCrypt), rol (ADMINISTRADOR/VENDEDOR/CLIENTE), activo, fecha_registro.
- **producto**: id, titulo, descripcion, precio, stock, imagen, categoria (FK), activo.
- **categoria**: id, nombre, slug.
- **oferta**: id, producto (FK), precio_oferta, precio_anterior, fecha_inicio, fecha_fin, activo.
- **pedido**: id, usuario_id, total, tipo_entrega, direccion_entrega, estado, fecha_pedido, metodo_pago.
- **pedido_item**: pedido_id, producto_id, nombre_producto, precio_unitario, cantidad, subtotal (PK compuesta).
- **carrito_item**: id, usuario_id, producto_id, nombre, precio, imagen, cantidad.
- **contacto**: id, nombre, email, telefono, asunto, mensaje, estado, fecha_envio.

## 3. Notas

- Las contraseñas se almacenan cifradas con BCrypt.
- El stock se descuenta al registrar un pedido y se repone al cancelar.
- Las ofertas se aplican al precio en carrito y pedidos.