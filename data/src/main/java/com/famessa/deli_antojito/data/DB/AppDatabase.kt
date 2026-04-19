package com.famessa.deli_antojito.data.DB

import androidx.room.Database
import androidx.room.RoomDatabase
import com.famessa.deli_antojito.data.Entities

@Database(entities = [Producto::class, Configuracion::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productoDao(): ProductoDao
    abstract fun configuracionDao(): ConfiguracionDao
}