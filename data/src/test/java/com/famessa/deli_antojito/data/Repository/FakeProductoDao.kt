package com.famessa.deli_antojito.data.Repository

import com.famessa.deli_antojito.data.dao.ProductoDao
import com.famessa.deli_antojito.data.entities.Producto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeProductoDao(
    initialProducts: List<Producto> = emptyList(),
    private val failWrites: Boolean = false
) : ProductoDao {
    private val products = MutableStateFlow(initialProducts)
    private var nextId = (initialProducts.maxOfOrNull { it.id } ?: 0L) + 1L

    override suspend fun insertProducto(producto: Producto): Long {
        if (failWrites) throw IllegalStateException("write failed")
        val id = nextId++
        products.value = products.value + producto.copy(id = id)
        return id
    }

    override fun getAllProductos(): Flow<List<Producto>> = products

    override suspend fun getProductoById(id: Long): Producto? = products.value.firstOrNull { it.id == id }

    override suspend fun updateProducto(producto: Producto): Int {
        if (failWrites) throw IllegalStateException("write failed")
        val old = products.value
        if (old.none { it.id == producto.id }) return 0
        products.value = old.map { if (it.id == producto.id) producto else it }
        return 1
    }

    override suspend fun getProductoByNormalizedName(nombre: String, excludeId: Long?): Producto? {
        val normalized = nombre.trim().lowercase()
        return products.value.firstOrNull {
            it.nombre.trim().lowercase() == normalized && (excludeId == null || it.id != excludeId)
        }
    }

    override suspend fun deleteProducto(id: Long): Int {
        if (failWrites) throw IllegalStateException("write failed")
        val old = products.value
        products.value = old.filterNot { it.id == id }
        return old.size - products.value.size
    }
}
