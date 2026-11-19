-- ============================================================
-- Base de datos SonidoVivo (una base, tablas por microservicio)
-- ============================================================

-- ---------- ms-usuarios: roles y usuarios ----------
CREATE TABLE roles (
    id_rol     BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre_rol VARCHAR(30) NOT NULL UNIQUE
) ENGINE = InnoDB;

INSERT INTO roles (nombre_rol) VALUES
    ('ADMINISTRADOR'),
    ('VENDEDOR'),
    ('CLIENTE');

CREATE TABLE usuarios (
    id_usuario     BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre         VARCHAR(60)  NOT NULL,
    apellido       VARCHAR(60)  NOT NULL,
    email          VARCHAR(150) NOT NULL UNIQUE,
    password_hash  VARCHAR(100) NOT NULL,
    telefono       VARCHAR(20)  NULL,
    direccion      VARCHAR(200) NULL,
    id_rol         BIGINT       NOT NULL,
    activo         TINYINT(1)   NOT NULL DEFAULT 1,
    fecha_registro DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_usuarios_rol FOREIGN KEY (id_rol) REFERENCES roles (id_rol)
) ENGINE = InnoDB;

-- ---------- ms-catalogo: categorias, productos y ofertas ----------
CREATE TABLE categorias (
    id_categoria BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre       VARCHAR(60) NOT NULL UNIQUE,
    slug         VARCHAR(60) NOT NULL UNIQUE
) ENGINE = InnoDB;

INSERT INTO categorias (id_categoria, nombre, slug) VALUES
    (1, 'Guitarras',  'guitarras'),
    (2, 'Bajos',      'bajos'),
    (3, 'Baterías',   'baterias'),
    (4, 'Teclados',   'teclados'),
    (5, 'Accesorios', 'accesorios');

CREATE TABLE productos (
    id_producto  BIGINT AUTO_INCREMENT PRIMARY KEY,
    titulo       VARCHAR(120)  NOT NULL,
    descripcion  VARCHAR(500)  NOT NULL,
    precio       DECIMAL(10,0) NOT NULL,
    stock        INT           NOT NULL DEFAULT 0,
    imagen       VARCHAR(300)  NOT NULL,
    id_categoria BIGINT        NOT NULL,
    activo       TINYINT(1)    NOT NULL DEFAULT 1,
    CONSTRAINT fk_productos_categoria FOREIGN KEY (id_categoria) REFERENCES categorias (id_categoria)
) ENGINE = InnoDB;

INSERT INTO productos (id_producto, titulo, descripcion, precio, stock, imagen, id_categoria) VALUES
    (101, 'Affinity Stratocaster',         'Guitarra eléctrica estilo Stratocaster',               399990, 5,  '/img/Instrumentos/Affinity Stratocaster.png',              1),
    (102, 'Guitarra Acústica Folk',        'Guitarra acústica folk',                               199990, 3,  '/img/Instrumentos/Guitarra Acústica Folk.jpg',             1),
    (110, 'Guitarra Acústica Folk II',     'Guitarra acústica folk (modelo 2)',                    209990, 2,  '/img/Instrumentos/Guitarra Acústica Folk_2.jpg',           1),
    (111, 'Guitarra Acústica Folk III',    'Guitarra acústica folk (modelo 3)',                    219990, 2,  '/img/Instrumentos/Guitarra Acústica Folk_3.jpg',           1),
    (112, 'Guitarra Acústica Fender',      'Guitarra acústica tipo dreadnought con acabado negro', 239990, 3,  '/img/Instrumentos/s-l500.webp',                            1),
    (103, 'Guitarra Dreadnought',          'Guitarra acústica dreadnought',                        229990, 4,  '/img/Instrumentos/Guitarra_Dreadnought.png',               1),
    (104, 'Guitarra Eléctrica SG',         'Guitarra eléctrica de cuerpo sólido',                  299990, 6,  '/img/Instrumentos/Guitarra_Eléctrica_SG.webp',             1),
    (105, 'Guitarra Eléctrica Telecaster', 'Guitarra eléctrica estilo Telecaster',                 329990, 2,  '/img/Instrumentos/Guitarra_Eléctrica_Telecaster.webp',     1),
    (106, 'Les Paul',                      'Guitarra eléctrica Les Paul',                          459990, 1,  '/img/Instrumentos/Les Paul_1.webp',                        1),
    (107, 'Takamine',                      'Guitarra acústica Takamine',                           379990, 3,  '/img/Instrumentos/Takamine .png',                          1),
    (108, 'Yamaha C40',                    'Guitarra clásica Yamaha',                              129990, 8,  '/img/Instrumentos/yamaha-c40-guitarra-clasica.jpg',        1),
    (109, 'Yamaha JR1 3/4',                'Guitarra de tamaño 3/4',                                99990, 5,  '/img/Instrumentos/Yamaha_JR1_3_4.png',                     1),
    (201, 'Bajo Eléctrico Jazz Bass',      'Bajo de 4 cuerdas estilo jazz',                        349990, 4,  '/img/Instrumentos/BajoEléctricoJazzBass.webp',             2),
    (202, 'Bajo Eléctrico 4 Cuerdas',      'Bajo eléctrico de 4 cuerdas',                          299990, 3,  '/img/Instrumentos/Bajo_Eléctrico_4_Cuerdas.webp',          2),
    (301, 'Batería Acústica 5 piezas',     'Set completo para bateristas',                         599990, 2,  '/img/Instrumentos/BateríaAcústica5piezas.webp',            3),
    (302, 'Caja Snare 14"',                'Caja snare de 14 pulgadas',                            129990, 6,  '/img/Instrumentos/CajaSnare14.webp',                       3),
    (401, 'Sintetizador 49 teclas',        'Sintetizador Arturia',                                 279990, 3,  '/img/Instrumentos/Sintetizador49teclasArturia.webp',       4),
    (402, 'Teclado Digital 61 teclas',     'Teclado con sonidos variados',                         249990, 5,  '/img/Instrumentos/TecladoDigital61teclas.webp',            4),
    (501, 'Afinador de Clip',              'Afinador cromático de clip',                            14990, 10, '/img/Instrumentos/AfinadordeClip.webp',                    5),
    (502, 'Cable de Instrumento',          'Cable para instrumento',                                9990, 15, '/img/Instrumentos/CableInstrumento.webp',                  5),
    (503, 'Capotraste',                    'Capotraste para guitarra',                              7990, 12, '/img/Instrumentos/CapotrasteGuitarra.webp',                5),
    (504, 'Cuerdas de Bajo',               'Set de cuerdas para bajo',                              12990, 20, '/img/Instrumentos/CuerdasBajo.webp',                       5),
    (505, 'Cuerdas Guitarra Acústica',     'Set de cuerdas acústicas',                              9990, 20, '/img/Instrumentos/CuerdasGuitarraAcústica.webp',           5),
    (506, 'Cuerdas Guitarra Eléctrica',    'Set de cuerdas eléctricas',                             9990, 20, '/img/Instrumentos/CuerdasGuitarraEléctrica.webp',          5),
    (507, 'Púas de Guitarra',              'Set de púas',                                           3990, 30, '/img/Instrumentos/PúasdeGuitarra.webp',                    5),
    (508, 'Soporte de Guitarra',           'Soporte para guitarra',                                 19990, 7,  '/img/Instrumentos/SoporteGuitarra.webp',                   5);

CREATE TABLE ofertas (
    id_oferta       BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_producto     BIGINT        NOT NULL UNIQUE,
    precio_oferta   DECIMAL(10,0) NOT NULL,
    precio_anterior DECIMAL(10,0) NOT NULL,
    fecha_inicio    DATE          NOT NULL,
    fecha_fin       DATE          NULL,
    activo          TINYINT(1)    NOT NULL DEFAULT 1,
    CONSTRAINT fk_ofertas_producto FOREIGN KEY (id_producto) REFERENCES productos (id_producto),
    CONSTRAINT chk_oferta_menor CHECK (precio_oferta < precio_anterior)
) ENGINE = InnoDB;

INSERT INTO ofertas (id_producto, precio_oferta, precio_anterior, fecha_inicio) VALUES
    (104, 249990, 299990, '2026-01-01'),
    (201, 299990, 349990, '2026-01-01'),
    (402, 199990, 249990, '2026-01-01'),
    (301, 499990, 599990, '2026-01-01'),
    (501,  9990,  14990, '2026-01-01');

-- ---------- ms-pedidos: pedidos y detalle ----------
CREATE TABLE pedidos (
    id_pedido         BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_usuario        BIGINT       NOT NULL,
    fecha_pedido      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    estado            ENUM('EN_PREPARACION','DESPACHADO','ENTREGADO','CANCELADO') NOT NULL DEFAULT 'EN_PREPARACION',
    tipo_entrega      ENUM('DESPACHO','RETIRO_TIENDA') NOT NULL,
    direccion_entrega VARCHAR(200) NULL,
    total             DECIMAL(12,0) NOT NULL DEFAULT 0,
    CONSTRAINT fk_pedidos_usuario FOREIGN KEY (id_usuario) REFERENCES usuarios (id_usuario),
    INDEX idx_pedidos_usuario (id_usuario)
) ENGINE = InnoDB;

CREATE TABLE detalle_pedido (
    id_detalle      BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_pedido       BIGINT        NOT NULL,
    id_producto     BIGINT        NOT NULL,
    nombre_producto VARCHAR(120)  NOT NULL,
    precio_unitario DECIMAL(10,0) NOT NULL,
    cantidad        INT           NOT NULL,
    subtotal        DECIMAL(12,0) NOT NULL,
    CONSTRAINT fk_detalle_pedido FOREIGN KEY (id_pedido) REFERENCES pedidos (id_pedido) ON DELETE CASCADE,
    CONSTRAINT fk_detalle_producto FOREIGN KEY (id_producto) REFERENCES productos (id_producto),
    INDEX idx_detalle_producto (id_producto)
) ENGINE = InnoDB;

-- ---------- ms-contacto: mensajes del formulario ----------
CREATE TABLE contactos (
    id_contacto BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre      VARCHAR(100) NOT NULL,
    email       VARCHAR(150) NOT NULL,
    telefono    VARCHAR(20)  NULL,
    asunto      VARCHAR(120) NOT NULL,
    mensaje     VARCHAR(1000) NOT NULL,
    estado      ENUM('PENDIENTE','ATENDIDO') NOT NULL DEFAULT 'PENDIENTE',
    fecha_envio DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_contactos_estado (estado)
) ENGINE = InnoDB;
