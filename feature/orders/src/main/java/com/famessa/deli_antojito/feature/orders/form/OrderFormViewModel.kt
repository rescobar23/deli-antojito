package com.famessa.deli_antojito.feature.orders.form


import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.famessa.deli_antojito.domain.model.*
import com.famessa.deli_antojito.domain.usecase.pedido.*
import com.famessa.deli_antojito.domain.usecase.producto.ObserveProductosUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class OrderFormViewModel @Inject constructor(
    private val capture: CapturePedidoProductUseCase,
    private val create: CreatePedidoUseCase,
    private val update: UpdatePedidoUseCase,
    private val observe: ObservePedidoUseCase,
    private val products: ObserveProductosUseCase,
    private val savedState: SavedStateHandle
) : ViewModel() {
    private var token: String = savedState.get<String>("token") ?: UUID.randomUUID().toString().also { savedState["token"] = it }
    private var orderId: Long? = null
    private var revision: Long = 0
    private var initialized = false
    private var currentEntry: String? = null
    private var catalogJob: Job? = null
    private var loadJob: Job? = null
    private val state = MutableStateFlow(OrderFormState())
    val uiState = state.asStateFlow()

    init { loadProducts() }
    fun loadProducts() {
        catalogJob?.cancel()
        catalogJob = viewModelScope.launch {
            products().catch { failure ->
                if (failure is CancellationException) throw failure
                state.update { it.copy(catalogError = "No se pudo leer el catálogo. Reintenta.") }
            }.collect { values -> state.update { current -> current.copy(
                catalogError = null,
                products = values.filter { it.activo }.mapNotNull { product ->
                    try { ProductChoice(product.id, product.nombre, Money.fromCatalogPrice(product.precioBase)) }
                    catch (_: IllegalArgumentException) { null }
                }) }
            }
        }
    }
    fun start(id: Long?, entry: String = id?.toString() ?: "new") {
        if (initialized && currentEntry == entry) return
        loadJob?.cancel()
        if (savedState.get<String>("entry") != null && savedState.get<String>("entry") != entry) {
            savedState.remove<Long>("draftId")
            token = UUID.randomUUID().toString()
            savedState["token"] = token
        }
        savedState["entry"] = entry
        currentEntry = entry
        initialized = true; orderId = id; revision = 0
        state.update { OrderFormState(entryKey = entry, products = it.products, catalogError = it.catalogError) }
        if (savedState.get<Long>("draftId") == (id ?: 0L)) {
            revision = savedState.get<Long>("draftRevision") ?: 0
            val keys = savedState.get<ArrayList<String>>("lineKeys") ?: arrayListOf()
            val names = savedState.get<ArrayList<String>>("lineNames") ?: arrayListOf()
            val quantities = savedState.get<ArrayList<String>>("lineQuantities") ?: arrayListOf()
            val ids = savedState.get<LongArray>("lineIds") ?: longArrayOf()
            val products = savedState.get<LongArray>("lineProducts") ?: longArrayOf()
            val prices = savedState.get<LongArray>("linePrices") ?: longArrayOf()
            val lines = keys.indices.mapNotNull { index ->
                val name = names.getOrNull(index) ?: return@mapNotNull null
                val product = products.getOrNull(index) ?: return@mapNotNull null
                val price = prices.getOrNull(index) ?: return@mapNotNull null
                val quantity = quantities.getOrNull(index) ?: return@mapNotNull null
                if (price < 0) return@mapNotNull null
                OrderFormLine(keys[index], ids.getOrNull(index)?.takeIf { it > 0 }, PedidoProductSnapshot(product, name, Money(price)), quantity)
            }
            state.update { it.copy(cliente = savedState.get<String>("draftClient").orEmpty(), observaciones = savedState.get<String>("draftNotes").orEmpty(), lines = lines, dirty = savedState.get<Boolean>("draftDirty") ?: false) }
            recalculate()
            if (id != null) loadJob = viewModelScope.launch {
                val result = observe(id).first()
                if (currentEntry != entry || orderId != id) return@launch
                val order = (result as? PedidoReadResult.Success)?.value
                if (order != null) state.update { it.copy(createdAt = order.fechaCreacion, status = order.estado) }
                if (order == null || order.estado != EstadoPedido.PENDIENTE || order.revision != revision) {
                    state.update { it.copy(editable = false, error = "El pedido cambió. Recarga para continuar; tu borrador se conserva.") }
                }
            }
            return
        }
        if (id == null) return
        reload()
    }
    fun reload() {
        val id = orderId ?: return
        val entry = currentEntry
        loadJob?.cancel()
        state.update { it.copy(loading = true, error = null) }
        loadJob = viewModelScope.launch {
            val result = observe(id).first()
            if (currentEntry != entry || orderId != id) return@launch
            when (result) {
                is PedidoReadResult.Success -> {
                    val order = result.value; revision = order.revision
                    val editable = order.estado == EstadoPedido.PENDIENTE
                    state.update { it.copy(loading = false, cliente = order.cliente, observaciones = order.observaciones.orEmpty(),
                        createdAt = order.fechaCreacion, status = order.estado,
                        lines = order.lineas.map { line -> OrderFormLine(line.id.toString(), line.id,
                            PedidoProductSnapshot(line.productoId, line.nombreProducto, line.precioUnitario), line.cantidad.toString()) },
                        total = order.total, dirty = false, editable = editable,
                        error = if (editable) null else "El pedido ya no permite edición.") }
                    persist()
                }
                else -> state.update { it.copy(loading = false, editable = false, error = "El pedido no está disponible para editar. Reintenta la carga o vuelve.") }
            }
        }
    }
    fun client(value: String) = edit { it.copy(cliente = value) }
    fun notes(value: String) = edit { it.copy(observaciones = value) }
    fun quantity(key: String, value: String) = edit { state -> state.copy(lines = state.lines.map { if (it.key == key) it.copy(quantity = value) else it }) }
    fun remove(key: String) = edit { state -> state.copy(lines = state.lines.filterNot { it.key == key }) }
    private fun edit(transform: (OrderFormState) -> OrderFormState) {
        if (state.value.saving || !state.value.editable) return
        state.update { transform(it.copy(error = null)).copy(dirty = true) }; recalculate(); persist()
    }
    fun add(productId: Long) {
        if (state.value.saving || state.value.saved || state.value.capturing || !state.value.editable) return
        val entry = currentEntry
        state.update { it.copy(capturing = true) }
        viewModelScope.launch {
            try {
                val snapshot = capture(productId)
                if (currentEntry != entry || state.value.saving || state.value.saved) return@launch
                if (snapshot == null) { state.update { it.copy(error = "El producto no está disponible o su precio no es válido.") }; return@launch }
                edit { current ->
                    val same = current.lines.firstOrNull { it.snapshot.productoId == snapshot.productoId && it.snapshot.precio == snapshot.precio }
                    if (same == null) current.copy(lines = current.lines + OrderFormLine(UUID.randomUUID().toString(), snapshot = snapshot, quantity = "1"))
                    else {
                        val next = same.quantity.toIntOrNull()?.toLong()?.plus(1)
                        if (next == null || next > Int.MAX_VALUE) current.copy(error = "Corrige la cantidad antes de añadir más unidades.")
                        else current.copy(lines = current.lines.map { if (it.key == same.key) it.copy(quantity = next.toString()) else it })
                    }
                }
            } catch (failure: CancellationException) { throw failure
            } catch (_: Exception) {
                if (currentEntry == entry) state.update { it.copy(error = "No se pudo leer el producto. Reintenta.") }
            } finally {
                if (currentEntry == entry) state.update { it.copy(capturing = false) }
            }
        }
    }
    private fun recalculate() {
        val total = try { state.value.lines.fold(Money(0)) { sum, line -> sum + line.snapshot.precio * (line.quantity.toIntOrNull() ?: 0) } }
        catch (_: IllegalArgumentException) { null }
        state.update { it.copy(total = total) }
    }
    fun save() {
        val current = state.value
        if (current.saving || current.capturing || !current.editable || current.loading) return
        if (current.cliente.trim().isEmpty() || current.lines.isEmpty() || current.total == null || current.lines.any { (it.quantity.toIntOrNull() ?: 0) <= 0 }) {
            state.update { it.copy(error = "Captura cliente, al menos un producto y cantidades enteras positivas dentro del límite permitido.") }; return
        }
        val draft = PedidoDraft(current.cliente, current.lines.map { line ->
            val quantity = line.quantity.toInt()
            line.existingId?.let { PedidoDraftLinea.Existente(it, quantity) } ?: PedidoDraftLinea.Nueva(line.snapshot, quantity)
        }, current.observaciones)
        state.update { it.copy(saving = true, error = null) }
        viewModelScope.launch {
            val id = orderId
            val result = if (id == null) create(CrearPedidoCommand(token, draft)) else update(ActualizarPedidoCommand(id, revision, draft))
            when (result) {
                is PedidoWriteResult.Success -> {
                    state.update { it.copy(saving = false, dirty = false, saved = true) }
                    savedState.remove<Long>("draftId")
                }
                else -> state.update { it.copy(saving = false, error = when (result) {
                    PedidoWriteResult.Conflict -> "El pedido cambió. Recarga antes de continuar; tu borrador se conserva."
                    is PedidoWriteResult.ProductUnavailable -> "Un producto nuevo ya no está disponible. Retíralo o sustitúyelo."
                    is PedidoWriteResult.ValidationError -> "Revisa cliente, productos, cantidades e importes."
                    else -> "No se pudo guardar el pedido. Tu borrador se conserva; puedes reintentar."
                }) }
            }
        }
    }
    private fun persist() {
        val current = state.value
        savedState["draftId"] = orderId ?: 0L
        savedState["draftRevision"] = revision
        savedState["draftClient"] = current.cliente
        savedState["draftNotes"] = current.observaciones
        savedState["draftDirty"] = current.dirty
        savedState["lineKeys"] = ArrayList(current.lines.map { it.key })
        savedState["lineNames"] = ArrayList(current.lines.map { it.snapshot.nombre })
        savedState["lineQuantities"] = ArrayList(current.lines.map { it.quantity })
        savedState["lineIds"] = current.lines.map { it.existingId ?: 0L }.toLongArray()
        savedState["lineProducts"] = current.lines.map { it.snapshot.productoId }.toLongArray()
        savedState["linePrices"] = current.lines.map { it.snapshot.precio.centavos }.toLongArray()
    }
}
