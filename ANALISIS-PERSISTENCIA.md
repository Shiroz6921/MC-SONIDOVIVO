# Análisis de persistencia de datos — SonidoVivo

Actividad: planificación de la persistencia de informacion (curso DSY1104, proyecto SonidoVivo).
Analisis sobre la aplicacion web React existente y diseño de los microservicios Spring Boot
que la soportan. Implementados en este mismo proyecto (`com.sonidovivo.SonidoVivo`).

---

## 1. Identificar los datos que necesitan guardar

Se reviso cada seccion de la aplicacion React actual y se identifico que informacion debe
conservarse de manera permanente (hoy vive en `localStorage`, en arreglos dentro de los
archivos `.tsx`, o simplemente no existe):

### a) Catalogo de productos (paginas Productos, Detalle, Inicio)
La web muestra 26 productos fijos escritos en el codigo. Deben pasar a la base de datos porque:

| Dato | Para que se conserva |
|------|----------------------|
| titulo, descripcion, imagen, precio | Contenido que la tienda publica; cambia en el meson, no en el codigo |
| categoria | Agrupa el catalogo por secciones (Guitarras, Bajos, Baterias, Teclados, Accesorios) y alimenta el menu/filtros |
| stock | Disponibilidad real en bodega; debe actualizarse automaticamente al vender (hoy se desincroniza el Excel) |
| activo | Permite retirar un producto sin perder el historial de ventas |

### b) Ofertas (seccion "Ofertas" y precios tachados)
La web tiene 5 ofertas con precio anterior y precio de oferta. Se conservan porque el precio
de oferta debe verse en todo el sitio (tarjetas, detalle, carrito, admin) y caducar sin
recompilar la aplicacion:

| Dato | Para que se conserva |
|------|----------------------|
| producto, precio_oferta, precio_anterior | Mostrar el precio rebajado con el precio tachado |
| fecha_inicio, fecha_fin, activo | Vigencia de la campana; si expira, el producto vuelve a su precio normal |

### c) Usuarios y sesion (Login, Registro, Perfil, Admin)
Hoy los usuarios se guardan en `localStorage`, por lo que cada dispositivo ve cuentas
distintas y la clave queda en texto plano:

| Dato | Para que se conserva |
|------|----------------------|
| nombre, apellido, email | Identifican al cliente y a los empleados |
| password (hasheado) | Autenticacion segura; jamas en texto plano |
| rol (ADMINISTRADOR / VENDEDOR / CLIENTE) | RBAC: define a que vistas y operaciones puede acceder |
| telefono, direccion | Datos de despacho y contacto del cliente |
| activo | El administrador desactiva cuentas sin borrar su historial de compras |
| fecha_registro | Auditoria basica de alta |

### d) Pedidos y carrito (Carrito, Checkout, seguimiento)
Hoy el carrito vive en `localStorage` y la compra solo vacia el carro. Se conservan porque
el negocio necesita historial y seguimiento:

| Dato | Para que se conserva |
|------|----------------------|
| cliente (id_usuario) | A quien pertenece el pedido; permite "historial de compras por cliente" (hoy no existe) |
| fecha | Orden cronologico y resumen de ventas por periodo |
| tipo de entrega (despacho / retiro en tienda) + direccion | Starken, Chilexpress o retiro en Vina del Mar |
| estado (EN_PREPARACION, DESPACHADO, ENTREGADO, CANCELADO) | El cliente y el vendedor hacen seguimiento del pedido |
| total | Resumen de ventas del dueño |
| lineas del detalle: producto, cantidad, precio unitario, subtotal | Que se compro y a que precio. Se guarda una **copia** del nombre y precio para que el historial no cambie si el catalogo cambia |

### e) Mensajes de contacto (pagina Contacto)
Hoy el formulario solo muestra "mensaje enviado" y no guarda nada:

| Dato | Para que se conserva |
|------|----------------------|
| nombre, email, telefono | Saber a quien responder |
| asunto, mensaje | Contenido de la consulta |
| estado (PENDIENTE / ATENDIDO) | Bandeja de trabajo del vendedor: evita que queden consultas sin respuesta (3 de cada 10 hoy) |
| fecha de envio | Priorizar y auditar |

### f) Stock (control de inventario)
No es una tabla aparte: el stock es un campo de cada producto. Se conserva **actualizado**
porque los problemas del negocio son exactamente: ventas de productos sin existencia y
diferencias entre Excel y bodega. Los pedidos lo descuentan al confirmarse y el
administrador lo corrige manualmente cuando llega mercaderia.

---

## 2. Proponer los microservicios necesarios

Se definieron **4 microservicios** organizados por responsabilidad de negocio. En esta
etapa del proyecto se implementan **dentro de un mismo proyecto Spring Boot** (`SonidoVivo`),
cada uno como modulo independiente con sus propias capas `controller`, `model`,
`repository` y `service`, y con sus rutas REST separadas por recurso. Esta organizacion
permite extraerlos despues a aplicaciones independientes (cada una con su BD y su puerto)
sin cambiar el codigo de negocio.

| # | Microservicio (paquete) | Rutas base | Responsabilidad |
|---|-------------------------|------------|-----------------|
| 1 | **usuarios** (`com.sonidovivo.SonidoVivo.usuarios`) | `/api/auth`, `/api/usuarios` | Registro, inicio de sesion, perfil y gestion de usuarios con roles (panel de administracion). Tablas: `roles`, `usuarios` |
| 2 | **catalogo** (`com.sonidovivo.SonidoVivo.catalogo`) | `/api/productos`, `/api/categorias`, `/api/ofertas` | Catalogo publico, categorias, ofertas y control de stock. Tablas: `categorias`, `productos`, `ofertas` |
| 3 | **pedidos** (`com.sonidovivo.SonidoVivo.pedidos`) | `/api/pedidos` | Flujo completo de compra: confirma el carrito, valida y descuenta stock (llamando al servicio de catalogo), cambia estados y guarda historial. Tablas: `pedidos`, `detalle_pedido` |
| 4 | **contacto** (`com.sonidovivo.SonidoVivo.contacto`) | `/api/contactos` | Mensajes del formulario de contacto y su bandeja de atencion. Tabla: `contactos` |

**Responsabilidades y funcionalidades que atiende cada uno:**

- **usuarios**: autenticacion (registro/login con contrasena hasheada en BCrypt),
  perfil del cliente, y operaciones de administracion: listar usuarios, cambiar rol,
  activar/desactivar (borrado logico). Atiende las paginas Login, Registro, Perfil y Admin.
- **catalogo**: unico dueño de los datos de producto. Atiende la pagina Productos,
  Inicio (destacados y ofertas), Detalle de producto y la parte de inventario del Admin.
  Tambien expone el descuento de stock para que el microservicio de pedidos registre la
  venta (regla del negocio: "el stock se actualiza automaticamente al registrar una venta").
- **pedidos**: convierte el carrito de React en un pedido real. Valida stock y precio
  contra el catalogo, guarda el pedido con su detalle, y permite que el vendedor avance el
  estado (EN_PREPARACION -> DESPACHADO -> ENTREGADO) o se cancele (reintegra stock).
- **contacto**: recibe el formulario publico y entrega a vendedores/admin una bandeja
  con estado PENDIENTE/ATENDIDO, marcado y eliminacion.

**Comunicacion:** React <-> backend exclusivamente por HTTP con JSON
(GET/POST/PUT/DELETE); el frontend nunca toca la base de datos directamente.
Entre microservicios, `pedidos` consume la logica de `catalogo` para precios y stock.

**Base de datos:** una sola base MySQL (`sonidovivo`) con las 8 tablas; cada
microservicio administra solo las tablas de su responsabilidad. Las referencias cruzadas
(`pedidos.id_usuario` -> `usuarios`, `detalle_pedido.id_producto` -> `productos`) quedan
como claves foraneas fisicas. Las tablas se crean con **migraciones Flyway** (no con
`ddl-auto`), versionadas en `src/main/resources/db/migration`.

---

## 3. Definir las tablas y sus campos principales

Modelo normalizado (3FN): cada tabla guarda solo datos que dependen de su propia clave;
los datos repetitivos (nombre y precio del producto en la venta) viven en el detalle como
snapshot de auditoria. Todas las tablas son InnoDB con claves `BIGINT AUTO_INCREMENT`.

**`roles`** (ms-usuarios)
| Campo | Tipo | Descripcion |
|-------|------|-------------|
| `id_rol` **(PK)** | BIGINT AI | Identificador |
| `nombre_rol` | VARCHAR(30) UNIQUE | ADMINISTRADOR, VENDEDOR, CLIENTE (semilla) |

**`usuarios`** (ms-usuarios)
| Campo | Tipo | Descripcion |
|-------|------|-------------|
| `id_usuario` **(PK)** | BIGINT AI | Identificador |
| `nombre` | VARCHAR(60) NOT NULL | — |
| `apellido` | VARCHAR(60) NOT NULL | — |
| `email` | VARCHAR(150) NOT NULL UNIQUE | Credencial de acceso |
| `password_hash` | VARCHAR(100) NOT NULL | Contrasena hasheada (BCrypt) |
| `telefono` | VARCHAR(20) NULL | — |
| `direccion` | VARCHAR(200) NULL | Para despachos |
| `id_rol` | BIGINT NOT NULL | **FK -> roles.id_rol** |
| `activo` | TINYINT(1) DEFAULT 1 | Desactivacion logica |
| `fecha_registro` | DATETIME DEFAULT NOW | Auditoria |

Relacion: `usuarios.id_rol` -> `roles.id_rol` (N usuarios : 1 rol).

**`categorias`** (ms-catalogo)
| Campo | Tipo | Descripcion |
|-------|------|-------------|
| `id_categoria` **(PK)** | BIGINT AI | — |
| `nombre` | VARCHAR(60) UNIQUE | Nombre visible |
| `slug` | VARCHAR(60) UNIQUE | Para URLs/filtros (`guitarras`, `bajos`...) |

**`productos`** (ms-catalogo)
| Campo | Tipo | Descripcion |
|-------|------|-------------|
| `id_producto` **(PK)** | BIGINT AI | Mismos ids que el catalogo de la web (101..508) |
| `titulo` | VARCHAR(120) NOT NULL | — |
| `descripcion` | VARCHAR(500) NOT NULL | — |
| `precio` | DECIMAL(10,0) NOT NULL | Precio normal en CLP (sin decimales) |
| `stock` | INT NOT NULL DEFAULT 0 | Disponibilidad real |
| `imagen` | VARCHAR(300) NOT NULL | Ruta publica (`/img/Instrumentos/...`) |
| `id_categoria` | BIGINT NOT NULL | **FK -> categorias.id_categoria** |
| `activo` | TINYINT(1) DEFAULT 1 | Borrado logico |

**`ofertas`** (ms-catalogo)
| Campo | Tipo | Descripcion |
|-------|------|-------------|
| `id_oferta` **(PK)** | BIGINT AI | — |
| `id_producto` | BIGINT NOT NULL UNIQUE | **FK -> productos.id_producto** (1 oferta activa por producto) |
| `precio_oferta` | DECIMAL(10,0) NOT NULL | Precio rebajado |
| `precio_anterior` | DECIMAL(10,0) NOT NULL | Precio tachado en la web |
| `fecha_inicio` | DATE NOT NULL | Vigencia desde |
| `fecha_fin` | DATE NULL | Vigencia hasta |
| `activo` | TINYINT(1) DEFAULT 1 | — |

Restriccion `CHECK (precio_oferta < precio_anterior)`.

Relaciones: `productos.id_categoria` -> `categorias` (N:1); `ofertas.id_producto` -> `productos` (1:1 con oferta activa).

**`pedidos`** (ms-pedidos)
| Campo | Tipo | Descripcion |
|-------|------|-------------|
| `id_pedido` **(PK)** | BIGINT AI | — |
| `id_usuario` | BIGINT NOT NULL | **FK -> usuarios.id_usuario** |
| `fecha_pedido` | DATETIME DEFAULT NOW | — |
| `estado` | ENUM(EN_PREPARACION, DESPACHADO, ENTREGADO, CANCELADO) | Seguimiento |
| `tipo_entrega` | ENUM(DESPACHO, RETIRO_TIENDA) | — |
| `direccion_entrega` | VARCHAR(200) NULL | Solo si es despacho |
| `total` | DECIMAL(12,0) DEFAULT 0 | Suma de subtotales |

**`detalle_pedido`** (ms-pedidos)
| Campo | Tipo | Descripcion |
|-------|------|-------------|
| `id_detalle` **(PK)** | BIGINT AI | — |
| `id_pedido` | BIGINT NOT NULL | **FK -> pedidos.id_pedido** (ON DELETE CASCADE) |
| `id_producto` | BIGINT NOT NULL | **FK -> productos.id_producto** |
| `nombre_producto` | VARCHAR(120) NOT NULL | Snapshot del nombre |
| `precio_unitario` | DECIMAL(10,0) NOT NULL | Snapshot del precio cobrado (con oferta si existia) |
| `cantidad` | INT NOT NULL | — |
| `subtotal` | DECIMAL(12,0) NOT NULL | cantidad x precio unitario |

Relacion: `detalle_pedido.id_pedido` -> `pedidos` (N:1, un pedido tiene 1..n lineas).

**`contactos`** (ms-contacto)
| Campo | Tipo | Descripcion |
|-------|------|-------------|
| `id_contacto` **(PK)** | BIGINT AI | — |
| `nombre` | VARCHAR(100) NOT NULL | — |
| `email` | VARCHAR(150) NOT NULL | Donde responder |
| `telefono` | VARCHAR(20) NULL | — |
| `asunto` | VARCHAR(120) NOT NULL | — |
| `mensaje` | VARCHAR(1000) NOT NULL | — |
| `estado` | ENUM(PENDIENTE, ATENDIDO) DEFAULT PENDIENTE | Bandeja del vendedor |
| `fecha_envio` | DATETIME DEFAULT NOW | Orden de atencion |

---

## 4. Identificar las operaciones necesarias

Operaciones por microservicio, tal como quedaron implementadas (todas responden JSON
y devuelven HTTP 200/201, 400/404/409 con un cuerpo de error uniforme).

### Microservicio de usuarios
| Operacion | Metodo y ruta | Quien la usa |
|-----------|---------------|--------------|
| Registrar cuenta | `POST /api/auth/registro` | Formulario de registro (publico) |
| Iniciar sesion | `POST /api/auth/login` | Formulario de login (publico) |
| Consultar usuarios | `GET /api/usuarios` (opcional `?soloActivos=true`) | Administrador |
| Consultar un usuario | `GET /api/usuarios/{id}` | Admin / perfil propio |
| Actualizar datos | `PUT /api/usuarios/{id}` | Perfil propio / admin |
| Asignar rol | `PUT /api/usuarios/{id}/rol` | Administrador |
| Activar / desactivar | `PUT /api/usuarios/{id}/estado` | Administrador |
| Eliminar (logico) | `DELETE /api/usuarios/{id}` | Administrador |

### Microservicio de catalogo
| Operacion | Metodo y ruta | Quien la usa |
|-----------|---------------|--------------|
| Consultar catalogo | `GET /api/productos` (filtros `?categoria=`, `?texto=`) | Clientes y vendedores (publico) |
| Consultar producto | `GET /api/productos/{id}` | Pagina de detalle (publico) |
| Registrar producto | `POST /api/productos` | Administrador |
| Actualizar producto | `PUT /api/productos/{id}` | Administrador |
| Corregir stock | `PUT /api/productos/{id}/stock` | Dueño / vendedor (reposicion o merma) |
| Descontar stock | `PUT /api/productos/{id}/descontar-stock` | Microservicio de pedidos (venta confirmada) |
| Eliminar producto | `DELETE /api/productos/{id}` (logico) | Administrador |
| Consultar ofertas | `GET /api/ofertas` | Publico (seccion ofertas) |
| Crear oferta | `POST /api/ofertas` | Administrador |
| Eliminar oferta | `DELETE /api/ofertas/{id}` | Administrador |
| Consultar categorias | `GET /api/categorias` | Publico (menu y filtros) |

### Microservicio de pedidos
| Operacion | Metodo y ruta | Quien la usa |
|-----------|---------------|--------------|
| Registrar pedido (confirmar carrito) | `POST /api/pedidos` | Cliente (valida stock/precio contra el catalogo) |
| Consultar todos los pedidos | `GET /api/pedidos` | Vendedor / administrador |
| Consultar pedidos de un cliente | `GET /api/pedidos/usuario/{idUsuario}` | El propio cliente |
| Consultar un pedido | `GET /api/pedidos/{id}` | Cliente / vendedor |
| Actualizar estado | `PUT /api/pedidos/{id}/estado` | Vendedor / administrador |
| Cancelar pedido | `PUT /api/pedidos/{id}/cancelar` | Cliente o admin (reintegra stock) |
| Eliminar pedido cancelado | `DELETE /api/pedidos/{id}` | Administrador |

### Microservicio de contacto
| Operacion | Metodo y ruta | Quien la usa |
|-----------|---------------|--------------|
| Registrar mensaje | `POST /api/contactos` | Formulario de contacto (publico) |
| Consultar bandeja | `GET /api/contactos` (filtro `?estado=PENDIENTE`) | Vendedor / administrador |
| Marcar como atendido | `PUT /api/contactos/{id}/estado` | Vendedor / administrador |
| Eliminar mensaje | `DELETE /api/contactos/{id}` | Administrador |

**Reglas de negocio importantes implementadas:**
- El precio que se cobra es el de oferta cuando el producto tiene una activa; queda
  congelado en `detalle_pedido` para el historial.
- No se acepta un pedido si algun producto no tiene stock suficiente (error 400 con mensaje).
- Al confirmar un pedido se descuenta el stock automaticamente; al cancelar (antes de
  entregarse) se reintegra.
- Los productos y usuarios se desactivan logicamente: nunca se pierde el historial.
- El email de usuario es unico (comparacion sin distinguir mayusculas) y la contrasena
  se guarda hasheada con BCrypt.
