ALTER TABLE articulos
    ADD usuario_id INT NULL;

ALTER TABLE articulos
    ADD CONSTRAINT FK_ARTICULOS_ON_USUARIO FOREIGN KEY (usuario_id) REFERENCES usuarios (id);