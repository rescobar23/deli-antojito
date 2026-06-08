package com.famessa.deli_antojito.feature.businessconfig

import com.famessa.deli_antojito.domain.model.Configuracion
import com.famessa.deli_antojito.domain.model.ConfiguracionSaveResult
import com.famessa.deli_antojito.domain.usecase.configuracion.ObserveConfiguracionUseCase
import com.famessa.deli_antojito.domain.usecase.configuracion.SaveConfiguracionUseCase
import com.famessa.deli_antojito.domain.usecase.configuracion.ValidateBusinessNameUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class BusinessConfigViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private fun viewModel(repository: FakeConfiguracionRepository = FakeConfiguracionRepository()): BusinessConfigViewModel {
        val validator = ValidateBusinessNameUseCase()
        return BusinessConfigViewModel(
            saveConfiguracionUseCase = SaveConfiguracionUseCase(repository, validator),
            observeConfiguracionUseCase = ObserveConfiguracionUseCase(repository),
            validateBusinessNameUseCase = validator
        )
    }

    @Test
    fun startsWithInitialInputState() {
        val viewModel = viewModel()

        assertEquals("", viewModel.uiState.value.businessName)
        assertTrue(viewModel.uiState.value.isInitialSetup)
    }

    @Test
    fun blankNameShowsValidationError() = runTest {
        val viewModel = viewModel()
        viewModel.start(isInitialSetup = true)

        viewModel.save()
        advanceUntilIdle()

        assertEquals("El nombre del negocio es obligatorio.", viewModel.uiState.value.validationMessage)
    }

    @Test
    fun successfulInitialSaveMarksStateSaved() = runTest {
        val repository = FakeConfiguracionRepository()
        repository.saveResult = ConfiguracionSaveResult.Success(
            Configuracion(nombreNegocio = "Deli", createdAt = 1L, updatedAt = 2L)
        )
        val viewModel = viewModel(repository)
        viewModel.start(isInitialSetup = true)
        viewModel.onBusinessNameChange("Deli")

        viewModel.save()
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.isSaved)
        assertFalse(viewModel.uiState.value.isSaving)
    }

    @Test
    fun saveFailurePreservesInput() = runTest {
        val repository = FakeConfiguracionRepository()
        repository.saveResult = ConfiguracionSaveResult.PersistenceError()
        val viewModel = viewModel(repository)
        viewModel.start(isInitialSetup = true)
        viewModel.onBusinessNameChange("Deli Capturado")

        viewModel.save()
        advanceUntilIdle()

        assertEquals("Deli Capturado", viewModel.uiState.value.businessName)
        assertEquals("No se pudo guardar. Intenta de nuevo.", viewModel.uiState.value.errorMessage)
    }

    @Test
    fun retryRestartsCurrentMode() {
        val viewModel = viewModel()
        viewModel.start(isInitialSetup = true)

        viewModel.retry()

        assertTrue(viewModel.uiState.value.isInitialSetup)
    }
}
