package com.famessa.deli_antojito.feature.orders.list

import com.famessa.deli_antojito.feature.orders.*
import com.famessa.deli_antojito.domain.model.*
import com.famessa.deli_antojito.domain.usecase.pedido.ObserveDailyOrdersSummaryUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.Assert.*
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class OrdersSummaryViewModelTest {
    @Test fun summaryFailureHasItsOwnRetryAndRecovers() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            val repo = FakePedidoRepository()
            repo.summary.value = PedidoReadResult.PersistenceError
            val model = OrdersSummaryViewModel(ObserveDailyOrdersSummaryUseCase(repo, FakeBusinessDayProvider()))
            runCurrent(); assertNotNull(model.uiState.value.error)
            repo.summary.value = PedidoReadResult.Success(ResumenConteos(5, 2, 1, 1))
            model.retry(); runCurrent()
            assertNull(model.uiState.value.error)
            assertEquals(5, model.uiState.value.value?.conteos?.totalPedidos)
        } finally { Dispatchers.resetMain() }
    }
}
