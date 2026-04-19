package com.famessa.deli_antojito.domain.repository

import com.famessa.deli_antojito.domain.model.Producto
import kotlinx.coroutines.flow.Flow

interface ProductoRepository {
    suspend fun insertProducto(producto: Producto)
    fun getAllProductos(): Flow<List<Producto>>
    suspend fun getProductoById(id: Long): Producto?
    suspend fun deleteProducto(id: Long)
}
