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
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class BusinessConfigEditViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private fun viewModel(repository: FakeConfiguracionRepository): BusinessConfigViewModel {
        val validator = ValidateBusinessNameUseCase()
        return BusinessConfigViewModel(
            saveConfiguracionUseCase = SaveConfiguracionUseCase(repository, validator),
            observeConfiguracionUseCase = ObserveConfiguracionUseCase(repository),
            validateBusinessNameUseCase = validator
        )
    }

    @Test
    fun editModePrefillsCurrentName() = runTest {
        val viewModel = viewModel(
            FakeConfiguracionRepository(Configuracion(nombreNegocio = "Actual", createdAt = 1L, updatedAt = 1L))
        )

        viewModel.start(isInitialSetup = false)
        advanceUntilIdle()

        assertEquals("Actual", viewModel.uiState.value.businessName)
    }

    @Test
    fun editModeShowsValidationFeedback() = runTest {
        val viewModel = viewModel(
            FakeConfiguracionRepository(Configuracion(nombreNegocio = "Actual", createdAt = 1L, updatedAt = 1L))
        )
        viewModel.start(isInitialSetup = false)
        advanceUntilIdle()
        viewModel.onBusinessNameChange("")

        viewModel.save()
        advanceUntilIdle()

        assertEquals("El nombre del negocio es obligatorio.", viewModel.uiState.value.validationMessage)
    }

    @Test
    fun editModeSuccessfulUpdateMarksSaved() = runTest {
        val repository = FakeConfiguracionRepository(Configuracion(nombreNegocio = "Actual", createdAt = 1L, updatedAt = 1L))
        repository.updateResult = ConfiguracionSaveResult.Success(
            Configuracion(nombreNegocio = "Nuevo", createdAt = 1L, updatedAt = 2L)
        )
        val viewModel = viewModel(repository)
        viewModel.start(isInitialSetup = false)
        advanceUntilIdle()
        viewModel.onBusinessNameChange("Nuevo")

        viewModel.save()
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.isSaved)
    }

    @Test
    fun editModeSaveFailurePreservesInput() = runTest {
        val repository = FakeConfiguracionRepository(Configuracion(nombreNegocio = "Actual", createdAt = 1L, updatedAt = 1L))
        repository.updateResult = ConfiguracionSaveResult.PersistenceError()
        val viewModel = viewModel(repository)
        viewModel.start(isInitialSetup = false)
        advanceUntilIdle()
        viewModel.onBusinessNameChange("Nombre escrito")

        viewModel.save()
        advanceUntilIdle()

        assertEquals("Nombre escrito", viewModel.uiState.value.businessName)
        assertEquals("No se pudo guardar. Intenta de nuevo.", viewModel.uiState.value.errorMessage)
    }
}
