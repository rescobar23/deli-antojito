-- Fixture sintético reconstruido desde entidades y migraciones del repositorio.
-- No es un esquema exportado por Room. El test establece user_version=2.
CREATE TABLE productos (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, nombre TEXT NOT NULL, precioBase REAL NOT NULL, categoria TEXT NOT NULL);
CREATE TABLE configuracion (id INTEGER NOT NULL PRIMARY KEY, nombre_negocio TEXT NOT NULL, created_at INTEGER NOT NULL, updated_at INTEGER NOT NULL);
INSERT INTO productos (id, nombre, precioBase, categoria) VALUES (7, 'Producto fixture', 12.5, 'Prueba');
INSERT INTO configuracion (id, nombre_negocio, created_at, updated_at) VALUES (1, 'Negocio fixture', 10, 20);

