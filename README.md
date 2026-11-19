# SonidoVivo — Backend (Spring Boot + MySQL)

Backend de la tienda **SonidoVivo** para la web React (`React-SonidoVivo`).
Implementa **4 microservicios por dominio de negocio**, cada uno con sus capas
`controller`, `model`, `repository` y `service`, dentro de este mismo proyecto
Spring Boot (paquete base `com.sonidovivo.SonidoVivo`):

| Paquete | Rutas base | Responsabilidad | Tablas |
|---------|-----------|-----------------|--------|
| `usuarios` | `/api/auth`, `/api/usuarios` | Registro, login (BCrypt), perfil, roles y gestión de usuarios | `roles`, `usuarios` |
| `catalogo` | `/api/productos`, `/api/categorias`, `/api/ofertas` | Catálogo público, ofertas y stock | `categorias`, `productos`, `ofertas` |
| `pedidos` | `/api/pedidos` | Confirmación del carrito, estados y historial de compras | `pedidos`, `detalle_pedido` |
| `contacto` | `/api/contactos` | Formulario de contacto y bandeja de atención | `contactos` |

Compartidos: `config` (CORS y manejo de errores JSON) y `excepciones`.

- **Puerto:** `8080` (una sola aplicación, rutas REST separadas por recurso).
- **Base de datos:** MySQL `sonidovivo` (una base con las 8 tablas, FKs reales).
- **Migraciones:** Flyway crea las tablas y datos semilla al arrancar
  (`src/main/resources/db/migration/V1__crear_tablas_sonidovivo.sql`).
  No se usa `ddl-auto`.

---

## Requisitos

1. **Java 17** (JDK).
2. **MySQL 8** corriendo localmente.
3. No se requiere Maven global: el proyecto usa el wrapper (`mvnw`).

---

## Puesta en marcha

### 1. Crear la base de datos (una vez)

Con MySQL corriendo, ejecuta (o desde MySQL Workbench):

```sql
CREATE DATABASE sonidovivo CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

Las **tablas y los datos semilla los crea Flyway automáticamente** al iniciar la
aplicación (roles, 5 categorías, 26 productos y 5 ofertas).

### 2. Configurar credenciales

Edita `src/main/resources/application.properties` con tu usuario y contraseña de MySQL:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/sonidovivo
spring.datasource.username=root
spring.datasource.password=TU_CONTRASENA
```

### 3. Ejecutar

Desde la carpeta del proyecto (`SonidoVivo\SonidoVivo`):

```powershell
.\mvnw.cmd spring-boot:run
```

Verifica en el log que Flyway ejecutó la migración `V1` y que la app subió en el puerto 8080.
Prueba rápida en el navegador o con PowerShell:

```powershell
Invoke-RestMethod http://localhost:8080/api/productos | Select-Object -First 3
```

---

## Endpoints principales (base: `http://localhost:8080`)

### Autenticación y usuarios
| Método | Ruta | Uso |
|--------|------|-----|
| `POST` | `/api/auth/registro` | Registro público (rol CLIENTE por defecto) |
| `POST` | `/api/auth/login` | Inicio de sesión |
| `GET` | `/api/usuarios` | Listado (admin) — opcional `?soloActivos=true` |
| `GET` | `/api/usuarios/{id}` | Ver un usuario |
| `PUT` | `/api/usuarios/{id}` | Actualizar datos del perfil |
| `PUT` | `/api/usuarios/{id}/rol` | Cambiar rol (admin) |
| `PUT` | `/api/usuarios/{id}/estado` | Activar/desactivar (admin) |
| `DELETE` | `/api/usuarios/{id}` | Borrado lógico (admin) |

### Catálogo, ofertas y stock
| Método | Ruta | Uso |
|--------|------|-----|
| `GET` | `/api/productos` | Catálogo — filtros `?categoria=guitarras&texto=yamaha` |
| `GET` | `/api/productos/{id}` | Detalle (página producto de React) |
| `POST` | `/api/productos` | Crear producto (admin) |
| `PUT` | `/api/productos/{id}` | Editar producto (admin) |
| `PUT` | `/api/productos/{id}/stock` | Reposición/corrección manual (vendedor/admin) |
| `PUT` | `/api/productos/{id}/descontar-stock` | Descuento automático al vender (usado por pedidos) |
| `DELETE` | `/api/productos/{id}` | Borrado lógico (admin) |
| `GET` | `/api/ofertas` | Ofertas vigentes (precio tachado) |
| `POST` | `/api/ofertas` | Crear oferta (admin) |
| `DELETE` | `/api/ofertas/{id}` | Eliminar oferta (admin) |
| `GET` | `/api/categorias` | Menú y filtros de la web |

### Pedidos
| Método | Ruta | Uso |
|--------|------|-----|
| `POST` | `/api/pedidos` | Confirmar carrito (valida stock, cobra precio de oferta si hay) |
| `GET` | `/api/pedidos` | Todos los pedidos (vendedor/admin) |
| `GET` | `/api/pedidos/usuario/{idUsuario}` | Historial de un cliente |
| `GET` | `/api/pedidos/{id}` | Ver un pedido |
| `PUT` | `/api/pedidos/{id}/estado` | Avanzar estado: EN_PREPARACION → DESPACHADO → ENTREGADO |
| `PUT` | `/api/pedidos/{id}/cancelar` | Cancelar (reintegra el stock) |
| `DELETE` | `/api/pedidos/{id}` | Eliminar pedido ya cancelado (admin) |

### Contacto
| Método | Ruta | Uso |
|--------|------|-----|
| `POST` | `/api/contactos` | Formulario de contacto (público) |
| `GET` | `/api/contactos` | Bandeja — filtro `?estado=PENDIENTE` |
| `PUT` | `/api/contactos/{id}/estado` | Marcar PENDIENTE/ATENDIDO |
| `DELETE` | `/api/contactos/{id}` | Eliminar mensaje (admin) |

---

## Ejemplos de uso (JSON)

**Registro:**
```http
POST http://localhost:8080/api/auth/registro
Content-Type: application/json

{
  "nombre": "Camila",
  "apellido": "Rojas",
  "email": "camila@example.com",
  "password": "clave123",
  "telefono": "+56912345678",
  "direccion": "Av. Valparaíso 123, Viña del Mar"
}
```

**Login:** `POST /api/auth/login` con `{"email": "...", "password": "..."}`.
La respuesta incluye el rol pero **nunca** la contraseña.

**Confirmar un pedido (con stock y precio reales del catálogo):**
```http
POST http://localhost:8080/api/pedidos
Content-Type: application/json

{
  "idUsuario": 1,
  "tipoEntrega": "DESPACHO",
  "direccionEntrega": "Calle Falsa 456, Viña del Mar",
  "items": [
    { "idProducto": 104, "cantidad": 1 },
    { "idProducto": 507, "cantidad": 3 }
  ]
}
```

**Mensaje de contacto:** `POST /api/contactos` con `nombre`, `email`, `telefono`,
`asunto` y `mensaje` (mínimo 10 caracteres).

---

## Notas

- **Primera cuenta** queda con rol CLIENTE. Para convertirla en administrador:
  `PUT /api/usuarios/{id}/rol` con `{"nombreRol": "ADMINISTRADOR"}`.
- **Errores** en formato JSON uniforme (`{"fecha": "...", "mensaje": "..."}`) con
  código HTTP correcto: 400 operación inválida, 404 recurso no encontrado, 409 estado conflictivo.
- **CORS** habilitado para `http://localhost:5173` (servidor de desarrollo de Vite),
  en `config/CorsConfig.java`.
- La documentación de la actividad está en `ANALISIS-PERSISTENCIA.md`
  (datos a guardar, microservicios, tablas/campos/tipos/relaciones y operaciones).
