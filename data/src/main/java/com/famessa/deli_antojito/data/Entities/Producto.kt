package com.famessa.deli_antojito.data.entities


import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "productos",
    indices = [Index(value = ["nombre"])]
)
data class Producto(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nombre: String,
    @ColumnInfo(name = "precio_base")
    val precioBase: Double,
    val img: String? = null,
    val activo: Boolean = true
)
