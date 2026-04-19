package com.famessa.deli_antojito.data.entities


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "productos")
data class Producto(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nombre: String,
    val precioBase: Double,
    val categoria: String
)