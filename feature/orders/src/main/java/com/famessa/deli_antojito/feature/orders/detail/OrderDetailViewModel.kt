package com.famessa.deli_antojito.feature.orders.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.famessa.deli_antojito.domain.model.*
import com.famessa.deli_antojito.domain.usecase.pedido.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class OrderDetailState(val loading: Boolean = true, val pedido: Pedido? = null, val busy: Boolean = false, val error: String? = null)

@HiltViewModel
class OrderDetailViewModel @Inject constructor(
    private val observe: ObservePedidoUseCase,
    private val change: ChangePedidoStateUseCase
) : ViewModel() {
    private val state = MutableStateFlow(OrderDetailState())
    val uiState = state.asStateFlow()
    private var orderId: Long? = null
    private var job: Job? = null
    fun start(id: Long, force: Boolean = false) {
        if (!force && orderId == id) return
        orderId = id; job?.cancel()
        state.value = OrderDetailState()
        job = viewModelScope.launch { observe(id).collect { result -> state.update { current -> when (result) {
            is PedidoReadResult.Success -> current.copy(loading = false, pedido = result.value, error = null)
            PedidoReadResult.NotFound -> current.copy(loading = false, pedido = null, error = "El pedido no está disponible.")
            else -> current.copy(loading = false, error = "No se pudo leer el pedido. Reintenta.")
        } } } }
    }
    fun retry() { orderId?.let { start(it, true) } }
    fun transition(target: EstadoPedido) {
        val order = state.value.pedido ?: return
        if (state.value.busy) return
        state.update { it.copy(busy = true, error = null) }
        viewModelScope.launch {
            val result = change(CambiarEstadoPedidoCommand(order.id, order.revision, target))
            if (orderId != order.id) return@launch
            state.update { it.copy(busy = false, error = when (result) {
                is PedidoWriteResult.Success -> null
                PedidoWriteResult.Conflict -> "El pedido cambió. Recarga antes de continuar."
                else -> "No se pudo cambiar el estado. Reintenta o recarga."
            }) }
        }
    }
}
