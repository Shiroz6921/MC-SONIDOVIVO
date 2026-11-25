-- Crea la tabla de usuarios
CREATE TABLE usuario (
    id BIGINT NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL,
    password VARCHAR(100) NOT NULL,
    rol VARCHAR(20) NOT NULL,
    telefono VARCHAR(20),
    direccion VARCHAR(200),
    activo TINYINT(1) NOT NULL DEFAULT 1,
    fecha_registro DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_usuario_email (email)
);
