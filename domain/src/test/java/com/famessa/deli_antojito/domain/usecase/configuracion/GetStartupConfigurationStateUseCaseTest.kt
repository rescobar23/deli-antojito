package com.famessa.deli_antojito.domain.usecase.configuracion

import com.famessa.deli_antojito.domain.model.Configuracion
import com.famessa.deli_antojito.domain.model.ConfiguracionState
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Test

class GetStartupConfigurationStateUseCaseTest {
    @Test
    fun emitsMissingConfigurationAfterLoading() = runTest {
        val useCase = GetStartupConfigurationStateUseCase(FakeConfiguracionRepository())

        assertTrue(useCase().drop(1).first() is ConfiguracionState.MissingConfiguration)
    }

    @Test
    fun emitsConfiguredAfterLoading() = runTest {
        val repository = FakeConfiguracionRepository(
            ConfiguracionState.Configured(Configuracion(nombreNegocio = "Demo"))
        )
        val useCase = GetStartupConfigurationStateUseCase(repository)

        assertTrue(useCase().drop(1).first() is ConfiguracionState.Configured)
    }

    @Test
    fun emitsInvalidLocalDataAfterLoading() = runTest {
        val useCase = GetStartupConfigurationStateUseCase(
            FakeConfiguracionRepository(ConfiguracionState.InvalidLocalData)
        )

        assertTrue(useCase().drop(1).first() is ConfiguracionState.InvalidLocalData)
    }

    @Test
    fun propagatesReadFailureFromRepository() = runTest {
        val useCase = GetStartupConfigurationStateUseCase(
            FakeConfiguracionRepository(ConfiguracionState.MissingConfiguration, failOnObserve = true)
        )

        val result = runCatching { useCase().drop(1).first() }

        assertTrue(result.isFailure)
    }
}
