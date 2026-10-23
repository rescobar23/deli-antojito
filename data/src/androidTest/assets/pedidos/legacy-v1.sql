-- Fixture sintético reconstruido desde entidades y migraciones del repositorio.
-- No es un esquema exportado por Room. El test establece user_version=1.
CREATE TABLE productos (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, nombre TEXT NOT NULL, precioBase REAL NOT NULL, categoria TEXT NOT NULL);
CREATE TABLE configuracion (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, nombre_negocio TEXT NOT NULL);
INSERT INTO productos (id, nombre, precioBase, categoria) VALUES (7, 'Producto fixture', 12.5, 'Prueba');
INSERT INTO configuracion (id, nombre_negocio) VALUES (1, 'Negocio fixture');

