CREATE TABLE articulos
(
    id        INT AUTO_INCREMENT NOT NULL,
    nombre    VARCHAR(100) NOT NULL,
    precio DOUBLE NULL,
    marca     VARCHAR(255) NULL,
    categoria VARCHAR(255) NULL,
    stock     INT NULL,
    create_at datetime     NOT NULL,
    update_at datetime NULL,
    CONSTRAINT pk_articulos PRIMARY KEY (id)
);

CREATE TABLE usuarios
(
    id       INT AUTO_INCREMENT NOT NULL,
    email    VARCHAR(255) NULL,
    password VARCHAR(255) NULL,
    activo   BIT(1)      NOT NULL,
    rol      VARCHAR(20) NOT NULL,
    CONSTRAINT pk_usuarios PRIMARY KEY (id)
);

ALTER TABLE articulos
    ADD CONSTRAINT uc_articulos_nombre UNIQUE (nombre);