-- Fixture sintético reconstruido desde entidades y migraciones del repositorio.
-- No es un esquema exportado por Room. El test establece user_version=3.
CREATE TABLE productos (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, nombre TEXT NOT NULL, precio_base REAL NOT NULL, img TEXT, activo INTEGER NOT NULL);
CREATE INDEX index_productos_nombre ON productos(nombre);
CREATE TABLE configuracion (id INTEGER NOT NULL PRIMARY KEY, nombre_negocio TEXT NOT NULL, created_at INTEGER NOT NULL, updated_at INTEGER NOT NULL);
INSERT INTO productos (id, nombre, precio_base, img, activo) VALUES (7, 'Producto fixture', 12.5, NULL, 1);
INSERT INTO configuracion (id, nombre_negocio, created_at, updated_at) VALUES (1, 'Negocio fixture', 10, 20);

