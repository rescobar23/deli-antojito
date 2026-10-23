package com.famessa.deli_antojito.feature.orders.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.famessa.deli_antojito.domain.model.*
import com.famessa.deli_antojito.domain.usecase.pedido.ObserveDailyOrdersSummaryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class OrdersSummaryState(val value: ResumenPedidos? = null, val error: String? = null)

@HiltViewModel
class OrdersSummaryViewModel @Inject constructor(private val observe: ObserveDailyOrdersSummaryUseCase) : ViewModel() {
    private val state = MutableStateFlow(OrdersSummaryState())
    val uiState = state.asStateFlow()
    private var job: Job? = null
    init { retry() }
    fun retry() {
        job?.cancel()
        job = viewModelScope.launch { observe().collect { result -> state.value = when (result) {
            is PedidoReadResult.Success -> OrdersSummaryState(result.value)
            else -> OrdersSummaryState(error = "No se pudo cargar el resumen.")
        } } }
    }
}
