# SonidoVivo — Backend (Microservicios Spring Boot + MySQL + Eureka)

Backend de la tienda **SonidoVivo** (instrumentos musicales) organizado como
**microservicios independientes**, siguiendo la arquitectura del curso:
servidor Eureka, API Gateway y un microservicio por dominio de negocio.

## Arquitectura

```
Eureka Server (8761)  <--  todos los microservicios se registran aquí
API Gateway (8080)    <--  puerta de entrada única para React
├── usuarios        (8081)  registro, login (BCrypt), roles
├── mensajeria      (8082)  formulario de contacto
├── productos       (8083)  catálogo, categorías, ofertas, stock
├── pedidos         (8084)  compras (consulta precios/stock a productos vía Eureka)
└── carrito         (8085)  carrito por usuario (consulta productos vía Eureka)
```

Cada microservicio tiene su propia base de datos MySQL (`sonidovivo_<servicio>`,
creada automáticamente con `createDatabaseIfNotExist=true`) y sus tablas se crean
con **migraciones Flyway** (no `ddl-auto`). La comunicación entre microservicios
se hace por **REST a través de Eureka** (ej. `pedidos` y `carrito` usan
`ProductoClient` con `@LoadBalanced`).

## Requisitos

1. Java 17
2. MySQL 8 (Laragon recomendado)
3. Maven (se incluye wrapper `mvnw` en cada proyecto)

## Cómo levantar todo (en este orden)

1. **MySQL** corriendo (Laragon → Start All, o `mysqld` manual).
2. **Eureka Server**: `cd eureka-server && .\mvnw.cmd spring-boot:run` → panel en http://localhost:8761
3. **Microservicios** (cada uno en su carpeta, en terminales separadas):
   - `usuarios` (8081)
   - `mensajeria` (8082)
   - `productos` (8083)
   - `pedidos` (8084)
   - `carrito` (8085)
4. **API Gateway**: `cd api-gateway && .\mvnw.cmd spring-boot:run` → http://localhost:8080

React se conecta al Gateway en `http://localhost:8080`. CORS habilitado para `http://localhost:5173`.

## Endpoints (a través del Gateway, puerto 8080)

- `POST /api/usuarios/registro`, `POST /api/usuarios/login`
- `GET /api/productos`, `GET /api/productos/{id}`, `GET /api/categorias`, `GET /api/ofertas`
- `PUT /api/productos/{id}/descontar?cantidad=N`, `PUT /api/productos/{id}/reponer?cantidad=N`
- `POST /api/pedidos`, `GET /api/pedidos`, `GET /api/pedidos?usuarioId=`, `PUT /api/pedidos/{id}/estado?estado=`, `PUT /api/pedidos/{id}/cancelar`
- `GET/POST/PUT/DELETE /api/carrito/{usuarioId}...`
- `POST /api/contactos`, `GET /api/contactos`, `PUT /api/contactos/{id}/estado`

## Documentación (Swagger por microservicio)

- usuarios: http://localhost:8081/swagger-ui.html
- productos: http://localhost:8083/swagger-ui.html
- pedidos: http://localhost:8084/swagger-ui.html
- carrito: http://localhost:8085/swagger-ui.html
- mensajeria: http://localhost:8082/swagger-ui.html

## Credenciales de demo

- admin: `admin@sonidovivo.com` / `1234` (rol ADMINISTRADOR)
- vendedor: `vendedor@sonidovivo.com` / `1234` (rol VENDEDOR)
- El registro crea usuarios con rol CLIENTE.

## Notas

- Contraseñas cifradas con **BCrypt** (bean `PasswordEncoder` en `CifradoConfig` del microservicio de usuarios).
- Cada microservicio se registra en Eureka con su `spring.application.name`.
- La actividad de persistencia está documentada en `ANALISIS-PERSISTENCIA.md`.
