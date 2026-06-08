package com.famessa.deli_antojito.domain.repository

import com.famessa.deli_antojito.domain.model.Producto
import kotlinx.coroutines.flow.Flow

interface ProductoRepository {
    fun observeProductos(): Flow<List<Producto>>
    suspend fun getProductoById(id: Long): Producto?
    suspend fun isNombreDisponible(nombre: String, excludeId: Long? = null): Boolean
    suspend fun saveProducto(producto: Producto): com.famessa.deli_antojito.domain.model.ProductoSaveResult
    suspend fun deleteProducto(id: Long): com.famessa.deli_antojito.domain.model.ProductoDeleteResult
}
