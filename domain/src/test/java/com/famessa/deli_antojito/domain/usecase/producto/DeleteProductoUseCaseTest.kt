package com.famessa.deli_antojito.domain.usecase.producto

import com.famessa.deli_antojito.domain.model.ProductoDeleteResult
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class DeleteProductoUseCaseTest {
    @Test
    fun delegatesPhysicalDelete() = runTest {
        val useCase = DeleteProductoUseCase(FakeProductoRepository())

        assertEquals(ProductoDeleteResult.Success, useCase(1L))
    }
}
