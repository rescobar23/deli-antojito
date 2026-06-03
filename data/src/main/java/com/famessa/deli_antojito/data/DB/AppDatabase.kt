package com.famessa.deli_antojito.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.famessa.deli_antojito.data.entities.*
import com.famessa.deli_antojito.data.dao.*

@Database(entities = [Producto::class, Configuracion::class], version = 2, exportSchema = false)
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
    }
}
