package com.famessa.deli_antojito.feature.orders.list

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.famessa.deli_antojito.domain.model.*
import com.famessa.deli_antojito.feature.orders.components.*
import com.famessa.deli_antojito.core.ui.theme.OrdersAquaHeading
import com.famessa.deli_antojito.core.ui.theme.OrdersAquaSelected
import com.famessa.deli_antojito.core.ui.theme.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.RoundedCornerShape

@OptIn(ExperimentalMaterial3Api::class)
@Composable fun OrdersScreen(
    state: OrdersUiState,
    onAdd: () -> Unit = {}, onOpen: (Long) -> Unit = {}, onSearch: (String) -> Unit = {},
    onTab: (EstadoPedido?) -> Unit = {}, onFilters: (PedidoConsulta) -> Unit = {}, onRetry: () -> Unit = {},
    summary: OrdersSummaryState? = null, onSummaryRetry: () -> Unit = {}, onBack: () -> Unit = {}
) {
    var filters by rememberSaveable { mutableStateOf(false) }
    Scaffold(modifier = Modifier.aquaScreenBackground().testTag("orders_screen"), containerColor = Color.Transparent, topBar = {
        AquaTopBar("Pedidos", onBack, navigationIcon = OrderIcon.Home, navigationDescription = "Ir al inicio")
    }, floatingActionButton = {
        Column(horizontalAlignment = Alignment.End, modifier = Modifier.padding(end = 6.dp, bottom = 10.dp)) {
            FloatingActionButton(onClick = onAdd, modifier = Modifier.size(74.dp).testTag("orders_add"),
                shape = androidx.compose.foundation.shape.CircleShape, containerColor = AquaLv3, contentColor = Color.White) {
                OrderUiIcon(OrderIcon.Add, "Agregar pedido", modifier = Modifier.size(38.dp), tint = Color.White)
            }
            Text("Agregar pedido", modifier = Modifier.padding(top = 10.dp), color = AquaLv5,
                style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
        }
    }) { padding ->
        Column(Modifier.padding(padding).fillMaxSize()) {
            Row(Modifier.padding(horizontal = 20.dp, vertical = 24.dp).fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.weight(1f)) { OrderSearchBar(state.query.texto, onSearch) }
                OutlinedIconButton(onClick = { filters = true }, modifier = Modifier.size(56.dp).testTag("orders_filters"),
                    shape = RoundedCornerShape(8.dp), border = BorderStroke(1.dp, Color(0xFFE1E5E7))) {
                    OrderUiIcon(OrderIcon.Filter, "Filtros avanzados", tint = Color(0xFF5C6871))
                }
            }
            val statuses = listOf<EstadoPedido?>(null) + EstadoPedido.entries
            PrimaryScrollableTabRow(selectedTabIndex = statuses.indexOf(state.query.estadoPestana).coerceAtLeast(0), edgePadding = 8.dp,
                contentColor = AquaLv4, containerColor = Color.Transparent) {
                statuses.forEach { status -> Tab(selected = status == state.query.estadoPestana, onClick = { onTab(status) },
                    modifier = Modifier.testTag("orders_tab_${status?.name ?: "TODOS"}"), text = {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                            OrderUiIcon(status?.icon() ?: OrderIcon.Orders, modifier = Modifier.size(20.dp),
                                tint = if (status == state.query.estadoPestana) AquaLv4 else Color(0xFF58646E))
                            Text(when(status) {
                                null -> "Todos"
                                EstadoPedido.PENDIENTE -> "Pendientes"
                                EstadoPedido.COMPLETADO -> "Completados"
                                EstadoPedido.CANCELADO -> "Cancelados"
                                else -> status.label()
                            })
                        }
                    }) }
            }
            LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(start = 20.dp, top = 24.dp, end = 20.dp, bottom = 130.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                if (state.loading) item { LinearProgressIndicator(Modifier.fillMaxWidth()) }
                if (state.error != null) item { Column {
                    Text(state.error, color = MaterialTheme.colorScheme.error)
                    if (state.pedidos.isNotEmpty()) Text("Los resultados visibles pueden estar desactualizados.")
                    TextButton(onClick = onRetry, modifier = Modifier.testTag("orders_retry")) { Text("Reintentar") }
                } }
                if (!state.loading && state.error == null && state.pedidos.isEmpty()) item {
                    Text(if (state.total == 0 && state.query == PedidoConsulta()) "Aún no hay pedidos. Agrega el primero." else "No se encontraron pedidos",
                        modifier = Modifier.testTag(if (state.total == 0 && state.query == PedidoConsulta()) "orders_empty" else "orders_no_results"))
                }
                items(state.pedidos, key = { it.id }) { order -> OrderCard(order) { onOpen(order.id) } }
                if (summary != null) item { OrdersDailySummary(summary, onSummaryRetry) }
            }
        }
    }
    if (filters) OrderFilters(state.query, onApply = { onFilters(it); filters = false }, onCancel = { filters = false })
}
