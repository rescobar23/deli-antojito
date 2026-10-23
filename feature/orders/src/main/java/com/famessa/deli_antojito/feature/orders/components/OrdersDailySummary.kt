package com.famessa.deli_antojito.feature.orders.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.famessa.deli_antojito.feature.orders.list.OrdersSummaryState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.contentDescription
import com.famessa.deli_antojito.core.ui.theme.*
import com.famessa.deli_antojito.domain.model.EstadoPedido

@Composable fun OrdersDailySummary(state: OrdersSummaryState, retry: () -> Unit) {
    Card(Modifier.fillMaxWidth().testTag("orders_summary"), shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F8F8)), border = BorderStroke(1.dp, Color(0xFFDCECEE))) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Resumen del día", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color(0xFF273744))
            when {
                state.error != null -> { Text(state.error); TextButton(onClick = retry) { Text("Reintentar resumen") } }
                state.value == null -> Text("Cargando resumen...")
                else -> {
                    val counts = state.value.conteos
                    BoxWithConstraints {
                        val columns = if (maxWidth < 480.dp) 2 else 4
                        val metrics = listOf(
                            Triple("Pedidos", counts.totalPedidos, null),
                            Triple("Pendientes", counts.pendientes, EstadoPedido.PENDIENTE),
                            Triple("En preparación", counts.enPreparacion, EstadoPedido.EN_PREPARACION),
                            Triple("Completados", counts.completados, EstadoPedido.COMPLETADO)
                        )
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            metrics.chunked(columns).forEach { group ->
                                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    group.forEach { (label, count, status) ->
                                        Row(Modifier.weight(1f).semantics { contentDescription = "$label: $count" },
                                            horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                                            OrderUiIcon(status?.icon() ?: OrderIcon.Orders, modifier = Modifier.size(26.dp), tint = status?.foreground() ?: AquaLv4)
                                            Column {
                                                Text(count.toString(), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color(0xFF273744))
                                                Text(label, style = MaterialTheme.typography.labelMedium, color = Color(0xFF516572))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
