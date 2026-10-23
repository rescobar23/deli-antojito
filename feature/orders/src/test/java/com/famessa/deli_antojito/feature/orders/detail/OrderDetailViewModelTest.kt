package com.famessa.deli_antojito.feature.orders.detail

import com.famessa.deli_antojito.feature.orders.FakePedidoRepository
import com.famessa.deli_antojito.domain.usecase.pedido.ObservePedidoUseCase
import com.famessa.deli_antojito.domain.usecase.pedido.ChangePedidoStateUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.Assert.*
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class OrderDetailViewModelTest {
    @Test fun notFoundIsNotAnEmptySuccessfulOrder() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            val repository = FakePedidoRepository()
            val viewModel = OrderDetailViewModel(ObservePedidoUseCase(repository), ChangePedidoStateUseCase(repository))
            viewModel.start(99)
            runCurrent()
            assertFalse(viewModel.uiState.value.loading)
            assertNull(viewModel.uiState.value.pedido)
            assertNotNull(viewModel.uiState.value.error)
        } finally { Dispatchers.resetMain() }
    }
}
