CREATE TABLE contacto (
    id BIGINT NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL,
    telefono VARCHAR(20),
    asunto VARCHAR(120) NOT NULL,
    mensaje TEXT NOT NULL,
    fecha_envio DATETIME(6) NOT NULL,
    PRIMARY KEY (id)
);
