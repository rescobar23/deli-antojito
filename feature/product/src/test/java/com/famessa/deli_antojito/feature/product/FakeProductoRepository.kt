package com.famessa.deli_antojito.feature.product

import com.famessa.deli_antojito.domain.model.Producto
import com.famessa.deli_antojito.domain.model.ProductoDeleteResult
import com.famessa.deli_antojito.domain.model.ProductoSaveResult
import com.famessa.deli_antojito.domain.repository.ProductoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow

class FakeProductoRepository(
    initialProducts: List<Producto> = emptyList(),
    private val failObserve: Boolean = false
) : ProductoRepository {
    private val products = MutableStateFlow(initialProducts)
    var saveResult: ProductoSaveResult? = null
    var deleteResult: ProductoDeleteResult? = null

    override fun observeProductos(): Flow<List<Producto>> {
        return if (failObserve) flow { throw IllegalStateException("read failed") } else products
    }

    override suspend fun getProductoById(id: Long): Producto? = products.value.firstOrNull { it.id == id }

    override suspend fun isNombreDisponible(nombre: String, excludeId: Long?): Boolean {
        val normalized = nombre.trim().lowercase()
        return products.value.none { it.nombre.trim().lowercase() == normalized && it.id != excludeId }
    }

    override suspend fun saveProducto(producto: Producto): ProductoSaveResult {
        saveResult?.let { return it }
        if (!isNombreDisponible(producto.nombre, producto.id.takeIf { it > 0L })) {
            return ProductoSaveResult.DuplicateName
        }
        val saved = if (producto.id == 0L) producto.copy(id = 99L) else producto
        products.value = products.value.filterNot { it.id == saved.id } + saved
        return ProductoSaveResult.Success(saved)
    }

    override suspend fun deleteProducto(id: Long): ProductoDeleteResult {
        deleteResult?.let { return it }
        products.value = products.value.filterNot { it.id == id }
        return ProductoDeleteResult.Success
    }
}
