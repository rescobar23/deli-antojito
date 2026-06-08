package com.famessa.deli_antojito.domain.usecase.producto

import com.famessa.deli_antojito.domain.model.Producto
import com.famessa.deli_antojito.domain.model.ProductoDeleteResult
import com.famessa.deli_antojito.domain.model.ProductoSaveResult
import com.famessa.deli_antojito.domain.repository.ProductoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeProductoRepository(
    initialProducts: List<Producto> = emptyList()
) : ProductoRepository {
    private val products = MutableStateFlow(initialProducts)
    var saveResult: ProductoSaveResult? = null
    var deleteResult: ProductoDeleteResult? = null

    override fun observeProductos(): Flow<List<Producto>> = products

    override suspend fun getProductoById(id: Long): Producto? = products.value.firstOrNull { it.id == id }

    override suspend fun isNombreDisponible(nombre: String, excludeId: Long?): Boolean {
        val normalized = nombre.trim().lowercase()
        return products.value.none { it.nombre.trim().lowercase() == normalized && it.id != excludeId }
    }

    override suspend fun saveProducto(producto: Producto): ProductoSaveResult {
        saveResult?.let { return it }
        val saved = if (producto.id == 0L) producto.copy(id = 1L) else producto
        products.value = products.value.filterNot { it.id == saved.id } + saved
        return ProductoSaveResult.Success(saved)
    }

    override suspend fun deleteProducto(id: Long): ProductoDeleteResult {
        deleteResult?.let { return it }
        products.value = products.value.filterNot { it.id == id }
        return ProductoDeleteResult.Success
    }
}
