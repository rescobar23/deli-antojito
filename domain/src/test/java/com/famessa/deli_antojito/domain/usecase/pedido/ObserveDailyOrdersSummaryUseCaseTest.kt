package com.famessa.deli_antojito.domain.usecase.pedido
import com.famessa.deli_antojito.domain.model.*
import com.famessa.deli_antojito.domain.time.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.*
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ObserveDailyOrdersSummaryUseCaseTest {
    @Test fun switchesCivilDayAndPreservesExplicitErrors() = runTest {
        val repo = FakePedidoRepository()
        val day = MutableStateFlow(BusinessDay(LocalDateParts(2026,10,21), 0, 86400000))
        val clock = object : BusinessDayProvider {
            override fun nowMillis() = 0L
            override fun observeDay() = day
            override fun range(from: LocalDateParts?, through: LocalDateParts?) = EpochRange(null,null)
        }
        val values = mutableListOf<PedidoReadResult<ResumenPedidos>>()
        val job = backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            ObserveDailyOrdersSummaryUseCase(repo, clock)().toList(values)
        }
        runCurrent()
        assertEquals(day.value.fecha, (values.last() as PedidoReadResult.Success).value.fecha)
        day.value = BusinessDay(LocalDateParts(2026,10,22),86400000,172800000)
        runCurrent()
        assertEquals(day.value.fecha, (values.last() as PedidoReadResult.Success).value.fecha)
        repo.summary.value = PedidoReadResult.PersistenceError
        runCurrent()
        assertEquals(PedidoReadResult.PersistenceError, values.last())
        repo.summary.value = PedidoReadResult.InvalidLocalData
        runCurrent()
        assertEquals(PedidoReadResult.InvalidLocalData, values.last())
        job.cancel()
    }
}

