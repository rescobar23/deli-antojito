package com.famessa.deli_antojito.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.famessa.deli_antojito.data.entities.*
import com.famessa.deli_antojito.data.dao.*

@Database(entities = [Producto::class, Configuracion::class, Pedido::class,
    PedidoDetalle::class, PedidoTransicion::class, PedidoSecuencia::class], version = 4, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productoDao(): ProductoDao
    abstract fun configuracionDao(): ConfiguracionDao
    abstract fun pedidoDao(): PedidoDao

    companion object {
        /** Initializes the same singleton in a fresh database as in the additive migration. */
        val CREATE_CALLBACK = object : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                db.execSQL("INSERT INTO pedido_secuencia (id, siguiente_numero) VALUES (1, 1)")
            }
        }

        /** Adds orders without rewriting existing catalog or configuration data. */
        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""CREATE TABLE IF NOT EXISTS pedidos (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    numero INTEGER NOT NULL, create_token TEXT NOT NULL,
                    cliente TEXT NOT NULL, cliente_busqueda TEXT NOT NULL, numero_busqueda TEXT NOT NULL,
                    fecha_creacion INTEGER NOT NULL, fecha_actualizacion INTEGER NOT NULL,
                    estado TEXT NOT NULL, total_centavos INTEGER NOT NULL, observaciones TEXT, revision INTEGER NOT NULL
                )""".trimIndent())
                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_pedidos_numero ON pedidos(numero)")
                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_pedidos_create_token ON pedidos(create_token)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_pedidos_fecha_creacion ON pedidos(fecha_creacion)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_pedidos_estado_fecha_creacion ON pedidos(estado, fecha_creacion)")
                db.execSQL("""CREATE TABLE IF NOT EXISTS pedido_detalle (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, pedido_id INTEGER NOT NULL,
                    producto_id INTEGER NOT NULL, nombre_producto TEXT NOT NULL, cantidad INTEGER NOT NULL,
                    precio_unitario_centavos INTEGER NOT NULL, subtotal_centavos INTEGER NOT NULL, posicion INTEGER NOT NULL,
                    FOREIGN KEY(pedido_id) REFERENCES pedidos(id) ON UPDATE NO ACTION ON DELETE RESTRICT
                )""".trimIndent())
                db.execSQL("CREATE INDEX IF NOT EXISTS index_pedido_detalle_pedido_id ON pedido_detalle(pedido_id)")
                db.execSQL("""CREATE TABLE IF NOT EXISTS pedido_transiciones (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, pedido_id INTEGER NOT NULL,
                    estado_anterior TEXT NOT NULL, estado_nuevo TEXT NOT NULL, fecha_hora INTEGER NOT NULL,
                    FOREIGN KEY(pedido_id) REFERENCES pedidos(id) ON UPDATE NO ACTION ON DELETE RESTRICT
                )""".trimIndent())
                db.execSQL("CREATE INDEX IF NOT EXISTS index_pedido_transiciones_pedido_id ON pedido_transiciones(pedido_id)")
                db.execSQL("CREATE TABLE IF NOT EXISTS pedido_secuencia (id INTEGER NOT NULL PRIMARY KEY, siguiente_numero INTEGER NOT NULL)")
                db.execSQL("INSERT INTO pedido_secuencia (id, siguiente_numero) VALUES (1, 1)")
            }
        }

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE configuracion ADD COLUMN created_at INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE configuracion ADD COLUMN updated_at INTEGER NOT NULL DEFAULT 0")
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE productos_new (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        nombre TEXT NOT NULL,
                        precio_base REAL NOT NULL,
                        img TEXT,
                        activo INTEGER NOT NULL DEFAULT 1
                    )
                    """.trimIndent()
                )
                db.execSQL(
                    """
                    INSERT INTO productos_new (id, nombre, precio_base, img, activo)
                    SELECT id, nombre, precioBase, NULL, 1 FROM productos
                    """.trimIndent()
                )
                db.execSQL("DROP TABLE productos")
                db.execSQL("ALTER TABLE productos_new RENAME TO productos")
                db.execSQL("CREATE INDEX index_productos_nombre ON productos(nombre)")
            }
        }
    }
}
