package com.famessa.deli_antojito.data.Repository

import com.famessa.deli_antojito.data.repository.ProductoRepositoryImpl
import com.famessa.deli_antojito.domain.model.Producto
import com.famessa.deli_antojito.domain.model.ProductoDeleteResult
import com.famessa.deli_antojito.domain.model.ProductoSaveResult
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ProductoRepositoryImplTest {
    @Test
    fun observesProducts() = runTest {
        val repository = ProductoRepositoryImpl(
            FakeProductoDao(listOf(com.famessa.deli_antojito.data.entities.Producto(id = 1, nombre = "Alitas", precioBase = 120.0)))
        )

        assertEquals("Alitas", repository.observeProductos().first().single().nombre)
    }

    @Test
    fun createsProduct() = runTest {
        val repository = ProductoRepositoryImpl(FakeProductoDao())

        val result = repository.saveProducto(Producto(nombre = "Alitas", precioBase = 120.0))

        assertTrue(result is ProductoSaveResult.Success)
    }

    @Test
    fun rejectsDuplicateNameIgnoringCaseAndSpaces() = runTest {
        val repository = ProductoRepositoryImpl(
            FakeProductoDao(listOf(com.famessa.deli_antojito.data.entities.Producto(id = 1, nombre = "Alitas", precioBase = 120.0)))
        )

        val result = repository.saveProducto(Producto(nombre = " alitas ", precioBase = 100.0))

        assertEquals(ProductoSaveResult.DuplicateName, result)
    }

    @Test
    fun updatesExistingProduct() = runTest {
        val repository = ProductoRepositoryImpl(
            FakeProductoDao(listOf(com.famessa.deli_antojito.data.entities.Producto(id = 1, nombre = "Alitas", precioBase = 120.0)))
        )

        val result = repository.saveProducto(Producto(id = 1, nombre = "Alitas BBQ", precioBase = 150.0))

        assertTrue(result is ProductoSaveResult.Success)
    }

    @Test
    fun deletesExistingProduct() = runTest {
        val repository = ProductoRepositoryImpl(
            FakeProductoDao(listOf(com.famessa.deli_antojito.data.entities.Producto(id = 1, nombre = "Alitas", precioBase = 120.0)))
        )

        assertEquals(ProductoDeleteResult.Success, repository.deleteProducto(1))
    }
}
