package com.famessa.deli_antojito.feature.orders.detail

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.famessa.deli_antojito.core.ui.theme.*
import com.famessa.deli_antojito.domain.model.*
import com.famessa.deli_antojito.feature.orders.components.*

private val DetailInk = Color(0xFF273744)
private val DetailMuted = Color(0xFF687580)
private val DetailBorder = Color(0xFFE5EAED)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun OrderDetailScreen(state: OrderDetailState, onBack: () -> Unit, onEdit: () -> Unit,
    onTransition: (EstadoPedido) -> Unit, retry: () -> Unit) {
    var menu by remember { mutableStateOf(false) }
    Scaffold(Modifier.aquaScreenBackground().testTag("order_detail"), containerColor = Color.Transparent, topBar = {
        AquaTopBar("Detalle de pedido", onBack, actions = {
            Box {
                IconButton(onClick = { menu = true }) { OrderUiIcon(OrderIcon.More, "Más opciones", tint = Color.White) }
                DropdownMenu(expanded = menu, onDismissRequest = { menu = false }) {
                    DropdownMenuItem(text = { Text("Recargar") }, onClick = { menu = false; retry() }, enabled = !state.busy)
                }
            }
        })
    }) { padding ->
        if (state.loading) {
            Box(Modifier.padding(padding).fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = AquaLv4)
            }
            return@Scaffold
        }
        Column(Modifier.padding(padding).fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 24.dp).padding(top = 26.dp, bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)) {
            if (state.busy) LinearProgressIndicator(Modifier.fillMaxWidth(), color = AquaLv4)
            state.error?.let {
                Text(it, color = MaterialTheme.colorScheme.error)
                TextButton(onClick = retry) { Text("Recargar") }
            }
            state.pedido?.let { order ->
                DetailPanel {
                    Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        DetailIcon(OrderIcon.Orders, large = true)
                        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Pedido ${orderNumber(order.numero)}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = DetailInk)
                            Text(orderDate(order.fechaCreacion), style = MaterialTheme.typography.bodyMedium, color = DetailMuted)
                        }
                        OrderStatusBadge(order.estado)
                    }
                }
                DetailPanel {
                    DetailHeading("Información del cliente", OrderIcon.User)
                    DetailValue("Nombre", order.cliente)
                    HorizontalDivider(Modifier.padding(horizontal = 16.dp), color = DetailBorder)
                    DetailValue("Creado", orderDate(order.fechaCreacion))
                    HorizontalDivider(Modifier.padding(horizontal = 16.dp), color = DetailBorder)
                    DetailValue("Actualizado", orderDate(order.fechaActualizacion))
                }
                DetailPanel {
                    DetailHeading("Productos del pedido", OrderIcon.Box)
                    OrderProductsTable(order.lineas)
                }
                DetailPanel {
                    DetailHeading("Resumen del pedido", OrderIcon.Orders)
                    Column(Modifier.fillMaxWidth().background(Color(0xFFF2F8F8)).padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Subtotal", color = DetailMuted)
                            Text(order.total.display(), color = DetailMuted)
                        }
                        HorizontalDivider(color = DetailBorder)
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text("Total", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = DetailInk)
                            Text(order.total.display(), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = AquaLv4)
                        }
                    }
                }
                DetailPanel {
                    DetailHeading("Notas del pedido", OrderIcon.Notes)
                    Surface(Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                        color = Color(0xFFF0F7F7), shape = RoundedCornerShape(6.dp), border = BorderStroke(1.dp, Color(0xFFDDEEEE))) {
                        Text(order.observaciones?.takeIf { it.isNotBlank() } ?: "Sin notas",
                            modifier = Modifier.padding(12.dp), color = if (order.observaciones.isNullOrBlank()) DetailMuted else DetailInk)
                    }
                }
                DetailPanel {
                    DetailHeading("Estado del pedido", OrderIcon.Clock)
                    OrderStateProgress(order.estado)
                }
                val enabled = !state.busy && state.error == null
                FlowRow(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    if (order.estado == EstadoPedido.PENDIENTE) {
                        OutlinedButton(onClick = onEdit, enabled = enabled, shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, AquaLv4), modifier = Modifier.testTag("order_detail_edit")) {
                            OrderUiIcon(OrderIcon.Edit, modifier = Modifier.size(20.dp), tint = AquaLv4)
                            Spacer(Modifier.width(8.dp))
                            Text("Editar", color = AquaLv4)
                        }
                        Button(onClick = { onTransition(EstadoPedido.EN_PREPARACION) }, enabled = enabled,
                            shape = RoundedCornerShape(8.dp), colors = ButtonDefaults.buttonColors(containerColor = AquaLv4),
                            modifier = Modifier.testTag("order_detail_prepare")) {
                            OrderUiIcon(OrderIcon.Chef, modifier = Modifier.size(20.dp), tint = Color.White)
                            Spacer(Modifier.width(8.dp))
                            Text("Iniciar preparación")
                        }
                    }
                    if (order.estado == EstadoPedido.EN_PREPARACION) {
                        Button(onClick = { onTransition(EstadoPedido.COMPLETADO) }, enabled = enabled,
                            shape = RoundedCornerShape(8.dp), colors = ButtonDefaults.buttonColors(containerColor = AquaLv4),
                            modifier = Modifier.testTag("order_detail_complete")) {
                            OrderUiIcon(OrderIcon.Check, modifier = Modifier.size(20.dp), tint = Color.White)
                            Spacer(Modifier.width(8.dp))
                            Text("Completar")
                        }
                    }
                    if (order.estado == EstadoPedido.PENDIENTE || order.estado == EstadoPedido.EN_PREPARACION) {
                        OutlinedButton(onClick = { onTransition(EstadoPedido.CANCELADO) }, enabled = enabled,
                            shape = RoundedCornerShape(8.dp), border = BorderStroke(1.dp, Color(0xFFD51C2B)),
                            modifier = Modifier.testTag("order_detail_cancel")) {
                            OrderUiIcon(OrderIcon.Cancel, modifier = Modifier.size(20.dp), tint = Color(0xFFD51C2B))
                            Spacer(Modifier.width(8.dp))
                            Text("Cancelar pedido", color = Color(0xFFD51C2B))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailPanel(content: @Composable ColumnScope.() -> Unit) {
    Surface(Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp), color = Color.White, border = BorderStroke(1.dp, DetailBorder)) {
        Column(content = content)
    }
}

@Composable
private fun DetailIcon(icon: OrderIcon, large: Boolean = false) {
    Surface(Modifier.size(if (large) 48.dp else 36.dp), shape = CircleShape, color = Color(0xFFE5F2F2)) {
        OrderUiIcon(icon, modifier = Modifier.padding(if (large) 12.dp else 9.dp), tint = AquaLv4)
    }
}

@Composable
private fun DetailHeading(text: String, icon: OrderIcon) {
    Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        DetailIcon(icon)
        Text(text, color = AquaLv4, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun DetailValue(label: String, value: String) {
    Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(label, Modifier.weight(1f), color = DetailMuted, style = MaterialTheme.typography.bodyMedium)
        Text(value, Modifier.weight(2f), color = DetailInk, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun OrderProductsTable(lines: List<PedidoLinea>) {
    BoxWithConstraints {
        val wide = maxWidth >= 560.dp
        Column {
            if (wide) {
                Row(Modifier.fillMaxWidth().background(Color(0xFFEEF6F7)).padding(12.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Producto", Modifier.weight(2f), style = MaterialTheme.typography.labelLarge, color = DetailInk)
                    Text("Cantidad", Modifier.weight(1f), style = MaterialTheme.typography.labelLarge, color = DetailInk, textAlign = TextAlign.End)
                    Text("Precio unitario", Modifier.weight(1.2f), style = MaterialTheme.typography.labelLarge, color = DetailInk, textAlign = TextAlign.End)
                    Text("Subtotal", Modifier.weight(1.2f), style = MaterialTheme.typography.labelLarge, color = DetailInk, textAlign = TextAlign.End)
                }
            }
            lines.forEach { line ->
                HorizontalDivider(color = DetailBorder)
                if (wide) {
                    Row(Modifier.fillMaxWidth().padding(12.dp), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Row(Modifier.weight(2f), horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                            DetailIcon(OrderIcon.Box)
                            Text(line.nombreProducto, color = DetailInk, modifier = Modifier.weight(1f))
                        }
                        Text(line.cantidad.toString(), Modifier.weight(1f), color = DetailInk, textAlign = TextAlign.End)
                        Text(line.precioUnitario.display(), Modifier.weight(1.2f), color = DetailInk, textAlign = TextAlign.End)
                        Text(line.subtotal.display(), Modifier.weight(1.2f), color = DetailInk, textAlign = TextAlign.End)
                    }
                } else {
                    Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                            DetailIcon(OrderIcon.Box)
                            Text(line.nombreProducto, color = DetailInk, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                        }
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("${line.cantidad} × ${line.precioUnitario.display()}", color = DetailMuted, style = MaterialTheme.typography.bodyMedium)
                            Text(line.subtotal.display(), color = DetailInk, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
            if (lines.isEmpty()) Text("Sin productos", Modifier.padding(16.dp), color = DetailMuted)
        }
    }
}

@Composable
private fun OrderStateProgress(status: EstadoPedido) {
    if (status == EstadoPedido.CANCELADO) {
        Row(Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp), horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically) {
            OrderUiIcon(OrderIcon.Cancel, modifier = Modifier.size(32.dp), tint = status.foreground())
            OrderStatusBadge(status)
        }
        return
    }
    val steps = listOf(EstadoPedido.PENDIENTE, EstadoPedido.EN_PREPARACION, EstadoPedido.COMPLETADO)
    Row(Modifier.fillMaxWidth().padding(start = 8.dp, end = 8.dp, bottom = 16.dp), verticalAlignment = Alignment.Top) {
        steps.forEachIndexed { index, step ->
            val selected = status == step
            val color = if (selected) step.foreground() else DetailMuted
            Column(Modifier.weight(1f).semantics { contentDescription = if (selected) "Estado actual: ${step.label()}" else step.label() },
                horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Surface(Modifier.size(40.dp), shape = CircleShape, color = if (selected) step.background() else Color(0xFFF3F5F6),
                    border = BorderStroke(1.dp, if (selected) step.foreground().copy(alpha = 0.35f) else DetailBorder)) {
                    OrderUiIcon(step.icon(), modifier = Modifier.padding(9.dp), tint = color)
                }
                Text(step.label(), style = MaterialTheme.typography.labelMedium, color = color,
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal, textAlign = TextAlign.Center)
            }
            if (index < steps.lastIndex) HorizontalDivider(Modifier.width(16.dp).padding(top = 20.dp), color = DetailBorder)
        }
    }
}
