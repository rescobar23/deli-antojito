package com.famessa.deli_antojito.domain.usecase.cierre
import com.famessa.deli_antojito.domain.repository.CierreConsultaRepository
import com.famessa.deli_antojito.domain.model.cierre.*
import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import org.junit.Test
import org.junit.Assert.*
@OptIn(ExperimentalCoroutinesApi::class)
class GetConsultaCierreUseCaseTest {
    @Test fun invalidNeverReads() = runTest {
        var calls = 0
        val repository = object : CierreConsultaRepository { override suspend fun readCompleted(periodo: CierrePeriodo): ConsultaSnapshotsResult { calls++; return ConsultaSnapshotsResult.Success(fixture()) } }
        val get = GetConsultaCierreUseCase(repository, { 3000 }, ValidateCierrePeriodoUseCase { true }, CalculateConsultaCierreUseCase(), StandardTestDispatcher(testScheduler))
        assertTrue(get(period.copy(inicioMillis = 2000)) is ConsultaCierreResult.InvalidPeriod)
        assertEquals(0, calls)
        assertTrue(get(period) is ConsultaCierreResult.Success)
        assertEquals(1, calls)
    }
    @Test fun readFailureIsExplicit() = runTest {
        val repository = object : CierreConsultaRepository { override suspend fun readCompleted(periodo: CierrePeriodo) = ConsultaSnapshotsResult.ReadFailure }
        assertEquals(ConsultaCierreResult.ReadFailure, GetConsultaCierreUseCase(repository, {3000}, ValidateCierrePeriodoUseCase {true}, CalculateConsultaCierreUseCase(), StandardTestDispatcher(testScheduler))(period))
    }
    @Test fun cancellationPropagates() = runTest {
        val repository = object : CierreConsultaRepository { override suspend fun readCompleted(periodo: CierrePeriodo): ConsultaSnapshotsResult = throw CancellationException() }
        try { GetConsultaCierreUseCase(repository, {3000}, ValidateCierrePeriodoUseCase {true}, CalculateConsultaCierreUseCase(), StandardTestDispatcher(testScheduler))(period); fail() } catch (_: CancellationException) { }
    }
}
