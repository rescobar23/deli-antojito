package com.famessa.deli_antojito.feature.batchclosing.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.famessa.deli_antojito.core.ui.theme.*
import com.famessa.deli_antojito.domain.model.cierre.CierrePeriodo
import com.famessa.deli_antojito.domain.time.LocalDateParts
import java.text.SimpleDateFormat
import java.util.*

internal fun formatBatchDate(epoch: Long, zone: String): String =
    SimpleDateFormat("dd/MM/yyyy", Locale("es", "MX")).apply {
        timeZone = TimeZone.getTimeZone(zone)
    }.format(Date(epoch))

@Composable
fun PeriodCard(period: CierrePeriodo?, edit: () -> Unit, selectedDate: LocalDateParts? = null) {
    BatchSection("Día de consulta", OrderIcon.Calendar) {
        Surface(onClick = edit, enabled = period != null || selectedDate != null,
            modifier = Modifier.fillMaxWidth().testTag("batch_period_start"),
            shape = RoundedCornerShape(8.dp), color = androidx.compose.ui.graphics.Color.White,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)) {
            Row(Modifier.padding(12.dp), verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Fecha de consulta", style = MaterialTheme.typography.bodySmall)
                    Text(selectedDate?.let { "%02d/%02d/%04d".format(Locale("es", "MX"), it.day, it.month, it.year) } ?: period?.let {
                        formatBatchDate(it.inicioMillis, it.zonaId)
                    } ?: "Sin seleccionar", style = MaterialTheme.typography.bodyMedium)
                }
                OrderUiIcon(OrderIcon.Calendar, modifier = Modifier.size(24.dp), tint = AquaLv4)
            }
        }
        Text(period?.zonaId ?: "Zona del negocio", Modifier.testTag("batch_timezone"),
            style = MaterialTheme.typography.labelMedium, color = AquaLv4)
        Text("Pedidos completados por fecha de creación del día seleccionado",
            style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

