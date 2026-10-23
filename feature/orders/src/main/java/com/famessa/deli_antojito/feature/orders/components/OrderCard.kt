package com.famessa.deli_antojito.feature.orders.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.famessa.deli_antojito.domain.model.*
import java.text.NumberFormat
import java.text.DateFormat
import java.math.BigDecimal
import java.util.Currency
import java.util.Date
import java.text.SimpleDateFormat
import java.util.Locale
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.famessa.deli_antojito.core.ui.theme.*

fun Money.display(): String = NumberFormat.getCurrencyInstance(java.util.Locale("es", "MX")).apply {
    currency = Currency.getInstance("MXN")
}.format(BigDecimal.valueOf(centavos, 2))
fun orderDate(millis: Long): String = SimpleDateFormat("d MMM yyyy  •  HH:mm", Locale("es", "MX")).format(Date(millis))
fun orderNumber(number: Long): String = "#" + number.toString().padStart(5, '0')

@Composable fun OrderCard(order: PedidoResumen, onClick: () -> Unit) {
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth().testTag("order_card_${order.id}"),
        shape = RoundedCornerShape(8.dp), border = BorderStroke(1.dp, Color(0xFFE7E9EB)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)) {
        Row(Modifier.padding(horizontal = 14.dp, vertical = 16.dp), verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Surface(shape = CircleShape, color = order.estado.background(), modifier = Modifier.size(44.dp)) {
                OrderUiIcon(order.estado.icon(), modifier = Modifier.padding(10.dp), tint = order.estado.foreground())
            }
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(orderNumber(order.numero), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color(0xFF273744))
                Text(order.cliente, style = MaterialTheme.typography.bodyMedium, color = Color(0xFF465460))
                Text(orderDate(order.fechaCreacion), style = MaterialTheme.typography.labelSmall, color = Color(0xFF626B73))
            }
            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OrderStatusBadge(order.estado)
                Text(order.total.display(), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = AquaLv4)
            }
            OrderUiIcon(OrderIcon.Chevron, modifier = Modifier.size(18.dp), tint = Color(0xFF273744))
        }
    }
}
