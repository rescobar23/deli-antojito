package com.famessa.deli_antojito.domain.model

data class Producto(
    val id: Long = 0,
    val nombre: String,
    val precioBase: Double,
    val categoria: String
)
