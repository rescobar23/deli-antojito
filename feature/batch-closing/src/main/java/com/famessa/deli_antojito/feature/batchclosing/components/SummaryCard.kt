package com.famessa.deli_antojito.feature.batchclosing.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.famessa.deli_antojito.core.ui.theme.*
import com.famessa.deli_antojito.domain.model.cierre.ResumenConsultaCierre
import com.famessa.deli_antojito.feature.batchclosing.R
import java.math.BigDecimal
import java.text.NumberFormat
import java.util.Locale

internal fun formatBatchMoney(cents: Long): String =
    NumberFormat.getCurrencyInstance(Locale("es", "MX")).format(BigDecimal.valueOf(cents, 2))

@Composable
fun SummaryCard(summary: ResumenConsultaCierre?) {
    BatchSection("Resumen de ventas", OrderIcon.Orders, iconResource = R.drawable.ic_batch_chart) {
        Text(androidx.compose.ui.res.stringResource(R.string.batch_not_collected),
            color = AquaLv4, style = MaterialTheme.typography.bodySmall)
        BoxWithConstraints {
            val columns = if (maxWidth >= 700.dp) 4 else if (maxWidth >= 280.dp) 2 else 1
            val labels = listOf("Pedidos completados", "Importe bruto de pedidos", "Descuentos", "Total neto")
            val values = listOf(summary?.totalPedidos?.toString() ?: "—",
                summary?.let { formatBatchMoney(it.bruto.centavos) } ?: "—", "No disponible", "No disponible")
            val icons = listOf(OrderIcon.Orders.resource, R.drawable.ic_batch_coins, R.drawable.ic_batch_discount, R.drawable.ic_batch_wallet)
            val colors = listOf(Color(0xFFEEF8F2), Color(0xFFEDF8FA), Color(0xFFFFF7E9), Color(0xFFEDF4FF))
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                (0..3).chunked(columns).forEach { indices ->
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.height(IntrinsicSize.Min)) {
                        indices.forEach { index ->
                            Column(Modifier.weight(1f).fillMaxHeight().background(colors[index], RoundedCornerShape(8.dp)).padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(androidx.compose.ui.res.painterResource(icons[index]), null, Modifier.size(28.dp), tint = AquaLv4)
                                val tag = when {
                                    summary == null -> Modifier
                                    index == 0 -> Modifier.testTag("batch_order_count")
                                    index == 1 -> Modifier.testTag("batch_gross_amount")
                                    else -> Modifier
                                }
                                Text(values[index], tag, color = AquaLv4,
                                    style = if (index < 2) MaterialTheme.typography.titleLarge else MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold)
                                Text(labels[index], style = MaterialTheme.typography.bodySmall,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                            }
                        }
                    }
                }
            }
        }
    }
}
