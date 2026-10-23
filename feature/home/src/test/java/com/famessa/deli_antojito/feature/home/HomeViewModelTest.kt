package com.famessa.deli_antojito.feature.home

import androidx.lifecycle.ViewModelStore
import com.famessa.deli_antojito.domain.model.*
import com.famessa.deli_antojito.domain.repository.*
import com.famessa.deli_antojito.domain.time.*
import com.famessa.deli_antojito.domain.usecase.configuracion.ObserveConfiguracionUseCase
import com.famessa.deli_antojito.domain.usecase.pedido.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.*
import org.junit.Assert.*
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {
    private fun TestScope.withHome(block: (HomeViewModel, OrdersRepository, DayClock) -> Unit) {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val repo = OrdersRepository()
        val clock = DayClock()
        val store = ViewModelStore()
        try {
            val model = HomeViewModel(ObserveConfiguracionUseCase(ConfigRepository()), ObservePedidosUseCase(repo),
                ObserveDailyOrdersSummaryUseCase(repo, clock))
            store.put("home", model)
            runCurrent()
            block(model, repo, clock)
        } finally { store.clear(); Dispatchers.resetMain() }
    }

    @Test fun displaysOnlyFourRecentOrdersAndIndependentDailyCounts() = runTest {
        withHome { model, repo, _ ->
            repo.orders.value = PedidoReadResult.Success((7L downTo 1L).map {
                PedidoResumen(it, it, "Cliente $it", it, EstadoPedido.PENDIENTE, Money(100))
            })
            repo.summary.value = PedidoReadResult.Success(ResumenConteos(20, 7, 8, 5))
            runCurrent()
            assertEquals(listOf(7L, 6L, 5L, 4L), model.dashboardState.value.recentOrders.map { it.id })
            assertEquals(20, model.dashboardState.value.summary?.conteos?.totalPedidos)
            assertEquals("Mi negocio", model.businessName.value)
        }
    }

    @Test fun ordersErrorDoesNotReplaceSummaryAndCanRecover() = runTest {
        withHome { model, repo, _ ->
            repo.orders.value = PedidoReadResult.PersistenceError
            runCurrent()
            assertNotNull(model.dashboardState.value.ordersError)
            assertNotNull(model.dashboardState.value.summary)
            repo.orders.value = PedidoReadResult.Success(emptyList())
            model.retryOrders()
            runCurrent()
            assertNull(model.dashboardState.value.ordersError)
            assertFalse(model.dashboardState.value.loadingOrders)
        }
    }

    @Test fun summaryErrorRecoversAndDateChangesAtMidnight() = runTest {
        withHome { model, repo, clock ->
            repo.summary.value = PedidoReadResult.PersistenceError
            runCurrent()
            assertNotNull(model.dashboardState.value.summaryError)
            repo.summary.value = PedidoReadResult.Success(ResumenConteos(3, 1, 1, 1))
            clock.day.value = BusinessDay(LocalDateParts(2026, 10, 22), 100, 200)
            model.retrySummary()
            runCurrent()
            assertNull(model.dashboardState.value.summaryError)
            assertEquals(LocalDateParts(2026, 10, 22), model.dashboardState.value.summary?.fecha)
            assertEquals(3, model.dashboardState.value.summary?.conteos?.totalPedidos)
        }
    }

    private class ConfigRepository : ConfiguracionRepository {
        override fun observeConfiguracion() = flowOf(Configuracion(nombreNegocio = "Mi negocio", createdAt = 1, updatedAt = 1))
        override fun observeConfiguracionState() = flowOf(ConfiguracionState.MissingConfiguration)
        override suspend fun saveConfiguracion(nombreNegocio: String): ConfiguracionSaveResult = error("Unused")
        override suspend fun updateConfiguracion(nombreNegocio: String): ConfiguracionSaveResult = error("Unused")
    }

    private class DayClock : BusinessDayProvider {
        val day = MutableStateFlow(BusinessDay(LocalDateParts(2026, 10, 21), 0, 100))
        override fun nowMillis() = 0L
        override fun observeDay(): Flow<BusinessDay> = day
        override fun range(from: LocalDateParts?, through: LocalDateParts?) = EpochRange(null, null)
    }

    private class OrdersRepository : PedidoRepository {
        val orders = MutableStateFlow<PedidoReadResult<List<PedidoResumen>>>(PedidoReadResult.Success(emptyList()))
        val summary = MutableStateFlow<PedidoReadResult<ResumenConteos>>(PedidoReadResult.Success(ResumenConteos(0, 0, 0, 0)))
        override fun observePedidos(consulta: PedidoConsulta): Flow<PedidoReadResult<List<PedidoResumen>>> = orders
        override fun observeResumen(inicio: Long, finExclusivo: Long): Flow<PedidoReadResult<ResumenConteos>> = summary
        override fun observePedido(id: Long): Flow<PedidoReadResult<Pedido>> = flowOf(PedidoReadResult.NotFound)
        override fun observeTotalPedidos(): Flow<PedidoReadResult<Int>> = flowOf(PedidoReadResult.Success(0))
        override suspend fun crear(command: CrearPedidoCommand): PedidoWriteResult = error("Unused")
        override suspend fun actualizar(command: ActualizarPedidoCommand): PedidoWriteResult = error("Unused")
        override suspend fun cambiarEstado(command: CambiarEstadoPedidoCommand): PedidoWriteResult = error("Unused")
    }
}
