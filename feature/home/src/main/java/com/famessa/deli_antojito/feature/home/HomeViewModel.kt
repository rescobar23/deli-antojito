package com.famessa.deli_antojito.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.famessa.deli_antojito.domain.usecase.configuracion.ObserveConfiguracionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.famessa.deli_antojito.domain.model.PedidoReadResult
import com.famessa.deli_antojito.domain.usecase.pedido.ObservePedidosUseCase
import com.famessa.deli_antojito.domain.usecase.pedido.ObserveDailyOrdersSummaryUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val observeConfiguracionUseCase: ObserveConfiguracionUseCase,
    private val observeOrders: ObservePedidosUseCase,
    private val observeSummary: ObserveDailyOrdersSummaryUseCase
) : ViewModel() {

    private val _businessName = MutableStateFlow("")
    val businessName: StateFlow<String> = _businessName
    private val dashboard = MutableStateFlow(HomeDashboardState())
    val dashboardState = dashboard.asStateFlow()
    private var ordersJob: Job? = null
    private var summaryJob: Job? = null

    init {
        observarConfiguracion()
        retryOrders()
        retrySummary()
    }

    private fun observarConfiguracion() {
        viewModelScope.launch {
            observeConfiguracionUseCase()
                .catch { if (it is CancellationException) throw it; _businessName.value = "" }
                .collect { configuracion ->
                    _businessName.value = configuracion?.nombreNegocio.orEmpty()
                }
        }
    }

    fun retryOrders() {
        ordersJob?.cancel()
        dashboard.update { it.copy(loadingOrders = true, ordersError = null) }
        ordersJob = viewModelScope.launch {
            observeOrders().catch { failure ->
                if (failure is CancellationException) throw failure
                emit(PedidoReadResult.PersistenceError)
            }.collect { result -> dashboard.update { current ->
                when (result) {
                    is PedidoReadResult.Success -> current.copy(recentOrders = result.value.take(4), loadingOrders = false, ordersError = null)
                    else -> current.copy(loadingOrders = false, ordersError = "No se pudieron cargar los pedidos recientes.")
                }
            } }
        }
    }

    fun retrySummary() {
        summaryJob?.cancel()
        dashboard.update { it.copy(summaryError = null) }
        summaryJob = viewModelScope.launch {
            observeSummary().catch { failure ->
                if (failure is CancellationException) throw failure
                emit(PedidoReadResult.PersistenceError)
            }.collect { result -> dashboard.update { current ->
                when (result) {
                    is PedidoReadResult.Success -> current.copy(summary = result.value, summaryError = null)
                    else -> current.copy(summaryError = "No se pudo cargar el resumen del día.")
                }
            } }
        }
    }

}
