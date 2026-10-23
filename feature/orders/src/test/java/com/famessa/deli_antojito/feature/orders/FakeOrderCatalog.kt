package com.famessa.deli_antojito.feature.orders

import com.famessa.deli_antojito.domain.model.*
import com.famessa.deli_antojito.domain.repository.ProductoRepository
import kotlinx.coroutines.flow.MutableStateFlow

class FakeOrderCatalog : ProductoRepository {
    var waitForGet: kotlinx.coroutines.CompletableDeferred<Unit>? = null
    val products = MutableStateFlow(listOf(Producto(1, "Fixture", 12.50), Producto(2, "Otro", 7.10), Producto(3, "Inactivo", 10.0, activo = false)))
    override fun observeProductos() = products
    override suspend fun getProductoById(id: Long): Producto? {
        waitForGet?.await()
        return products.value.find { it.id == id }
    }
    override suspend fun isNombreDisponible(nombre: String, excludeId: Long?) = true
    override suspend fun saveProducto(producto: Producto): ProductoSaveResult = error("Not used by order form")
    override suspend fun deleteProducto(id: Long): ProductoDeleteResult = error("Not used by order form")
}
