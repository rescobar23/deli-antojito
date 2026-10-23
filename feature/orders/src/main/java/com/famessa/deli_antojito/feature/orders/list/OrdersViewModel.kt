package com.famessa.deli_antojito.feature.orders.list

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.famessa.deli_antojito.domain.model.*
import com.famessa.deli_antojito.domain.time.LocalDateParts
import com.famessa.deli_antojito.domain.usecase.pedido.ObservePedidosUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
@HiltViewModel
class OrdersViewModel @Inject constructor(
    private val observe: ObservePedidosUseCase,
    private val saved: SavedStateHandle = SavedStateHandle()
) : ViewModel() {
    private fun savedDate(key: String): LocalDateParts? = saved.get<IntArray>(key)?.takeIf { it.size == 3 }?.let { LocalDateParts(it[0], it[1], it[2]) }
    private val criteria = MutableStateFlow(PedidoConsulta(
        estadoPestana = saved.get<String>("tab")?.let { name -> EstadoPedido.entries.find { it.name == name } },
        estadosAvanzados = saved.get<ArrayList<String>>("states")?.mapNotNull { name -> EstadoPedido.entries.find { it.name == name } }?.toSet() ?: emptySet(),
        desde = savedDate("from"), hasta = savedDate("through"),
        minimo = saved.get<Long>("min")?.let(::Money), maximo = saved.get<Long>("max")?.let(::Money)
    ))
    private val text = MutableStateFlow(saved.get<String>("text") ?: "")
    private val retryKey = MutableStateFlow(0)
    private val state = MutableStateFlow(OrdersUiState(query = criteria.value.copy(texto = text.value)))
    val uiState: StateFlow<OrdersUiState> = state.asStateFlow()
    init {
        viewModelScope.launch { observe.observeTotal().collect { result ->
            if (result is PedidoReadResult.Success) state.update { it.copy(total = result.value) }
        } }
        viewModelScope.launch {
            combine(text.debounce { if (it.isEmpty()) 0L else 300L }, criteria, retryKey) { input, query, _ -> query.copy(texto = input) }
                .flatMapLatest { query ->
                    state.update { it.copy(loading = true, error = null) }
                    observe(query)
                }.collect { result -> state.update { current -> when (result) {
                    is PedidoReadResult.Success -> current.copy(loading = false, pedidos = result.value, error = null)
                    else -> current.copy(loading = false, error = "No se pudieron leer los pedidos. Reintenta.")
                } } }
        }
    }
    fun search(value: String) {
        text.value = value; saved["text"] = value
        state.update { it.copy(query = it.query.copy(texto = value)) }
    }
    fun selectTab(value: EstadoPedido?) = applyQuery(criteria.value.copy(estadoPestana = value))
    fun applyFilters(query: PedidoConsulta) = applyQuery(query.copy(estadoPestana = criteria.value.estadoPestana))
    private fun applyQuery(query: PedidoConsulta) {
        criteria.value = query.copy(texto = "")
        saved["tab"] = query.estadoPestana?.name
        saved["states"] = ArrayList(query.estadosAvanzados.map { it.name })
        saved["from"] = query.desde?.let { intArrayOf(it.year, it.month, it.day) }
        saved["through"] = query.hasta?.let { intArrayOf(it.year, it.month, it.day) }
        saved["min"] = query.minimo?.centavos; saved["max"] = query.maximo?.centavos
        state.update { it.copy(query = query.copy(texto = text.value)) }
    }
    fun retry() { retryKey.value++ }
}
