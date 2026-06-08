package com.famessa.deli_antojito.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.famessa.deli_antojito.data.entities.*
import com.famessa.deli_antojito.data.dao.*

@Database(entities = [Producto::class, Configuracion::class], version = 3, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productoDao(): ProductoDao
    abstract fun configuracionDao(): ConfiguracionDao

    companion object {
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
