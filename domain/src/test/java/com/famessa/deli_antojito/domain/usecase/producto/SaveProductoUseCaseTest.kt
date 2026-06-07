package com.famessa.deli_antojito.domain.usecase.producto

import com.famessa.deli_antojito.domain.model.Producto
import com.famessa.deli_antojito.domain.model.ProductoSaveResult
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Test

class SaveProductoUseCaseTest {
    @Test
    fun savesValidProduct() = runTest {
        val useCase = SaveProductoUseCase(FakeProductoRepository(), ValidateProductoUseCase())

        val result = useCase(Producto(nombre = "Alitas", precioBase = 120.0))

        assertTrue(result is ProductoSaveResult.Success)
    }

    @Test
    fun rejectsInvalidProduct() = runTest {
        val useCase = SaveProductoUseCase(FakeProductoRepository(), ValidateProductoUseCase())

        val result = useCase(Producto(nombre = "", precioBase = 0.0))

        assertTrue(result is ProductoSaveResult.InvalidProduct)
    }
}
