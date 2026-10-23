package com.famessa.deli_antojito.domain.usecase.pedido
import com.famessa.deli_antojito.domain.model.*
import com.famessa.deli_antojito.domain.repository.ProductoRepository
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test

internal class OrderTestCatalog : ProductoRepository {
    var product: Producto? = Producto(1, "Fixture", 1.005)
    override fun observeProductos() = flowOf(listOfNotNull(product))
    override suspend fun getProductoById(id: Long) = product?.takeIf { it.id == id }
    override suspend fun isNombreDisponible(nombre: String, excludeId: Long?) = true
    override suspend fun saveProducto(producto: Producto): ProductoSaveResult = error("Unused")
    override suspend fun deleteProducto(id: Long): ProductoDeleteResult = error("Unused")
}
class CapturePedidoProductUseCaseTest {
    @Test fun capturesOnlyValidActiveProductsWithoutBinaryArithmetic() = runTest {
        val catalog = OrderTestCatalog()
        val capture = CapturePedidoProductUseCase(catalog)
        assertEquals(101L, capture(1)?.precio?.centavos)
        assertNull(capture(99))
        catalog.product = catalog.product?.copy(activo = false)
        assertNull(capture(1))
        catalog.product = Producto(1, "Fixture", Double.NaN)
        assertNull(capture(1))
        catalog.product = Producto(1, " ", 1.0)
        assertNull(capture(1))
        catalog.product = null
        assertNull(capture(1))
    }
}

