package com.famessa.deli_antojito.feature.product

import com.famessa.deli_antojito.domain.model.Producto
import com.famessa.deli_antojito.domain.model.ProductoSaveResult
import com.famessa.deli_antojito.domain.usecase.producto.GetProductoByIdUseCase
import com.famessa.deli_antojito.domain.usecase.producto.SaveProductoUseCase
import com.famessa.deli_antojito.domain.usecase.producto.ValidateProductoUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ProductEditViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private fun viewModel(repository: FakeProductoRepository): ProductEditViewModel {
        val validator = ValidateProductoUseCase()
        return ProductEditViewModel(
            getProductoByIdUseCase = GetProductoByIdUseCase(repository),
            saveProductoUseCase = SaveProductoUseCase(repository, validator),
            validateProductoUseCase = validator
        )
    }

    @Test
    fun createModeDefaultsActive() {
        val viewModel = viewModel(FakeProductoRepository())

        viewModel.start(null)

        assertTrue(viewModel.uiState.value.activo)
    }

    @Test
    fun imageSourceChooserCanBeOpenedAndClosed() {
        val viewModel = viewModel(FakeProductoRepository())

        viewModel.requestImageSourceChooser()
        assertTrue(viewModel.uiState.value.showImageSourceChooser)

        viewModel.dismissImageSourceChooser()
        assertTrue(!viewModel.uiState.value.showImageSourceChooser)
    }

    @Test
    fun selectingImageClosesChooserAndStoresImage() {
        val viewModel = viewModel(FakeProductoRepository())

        viewModel.requestImageSourceChooser()
        viewModel.onImageSelected("abc123", "image/png", 128)

        assertTrue(!viewModel.uiState.value.showImageSourceChooser)
        assertEquals("abc123", viewModel.uiState.value.img)
    }

    @Test
    fun blankNameShowsValidation() = runTest {
        val viewModel = viewModel(FakeProductoRepository())
        viewModel.start(null)

        viewModel.save()
        advanceUntilIdle()

        assertEquals("El nombre del producto es obligatorio.", viewModel.uiState.value.validationMessage)
    }

    @Test
    fun validCreateMarksSaved() = runTest {
        val viewModel = viewModel(FakeProductoRepository())
        viewModel.start(null)
        viewModel.onNameChange("Alitas")
        viewModel.onPriceChange("120")

        viewModel.save()
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.isSaved)
    }

    @Test
    fun saveFailurePreservesInput() = runTest {
        val repository = FakeProductoRepository()
        repository.saveResult = ProductoSaveResult.PersistenceError()
        val viewModel = viewModel(repository)
        viewModel.start(null)
        viewModel.onNameChange("Alitas")
        viewModel.onPriceChange("120")

        viewModel.save()
        advanceUntilIdle()

        assertEquals("Alitas", viewModel.uiState.value.nombre)
        assertEquals("No se pudo guardar el producto.", viewModel.uiState.value.errorMessage)
    }

    @Test
    fun editModeLoadsExistingProduct() = runTest {
        val viewModel = viewModel(
            FakeProductoRepository(listOf(Producto(id = 1, nombre = "Alitas", precioBase = 120.0)))
        )

        viewModel.start(1)
        advanceUntilIdle()

        assertEquals("Alitas", viewModel.uiState.value.nombre)
    }

    @Test
    fun missingEditProductShowsError() = runTest {
        val viewModel = viewModel(FakeProductoRepository())

        viewModel.start(99)
        advanceUntilIdle()

        assertEquals("El producto no existe.", viewModel.uiState.value.errorMessage)
    }
}
