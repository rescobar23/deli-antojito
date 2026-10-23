package com.famessa.deli_antojito.feature.batchclosing.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.famessa.deli_antojito.core.ui.theme.*
import com.famessa.deli_antojito.feature.batchclosing.R

@Composable
fun UnavailablePaymentsCard() {
    BatchSection("Ventas por método de pago", OrderIcon.Archive, Modifier.testTag("batch_financial_unavailable"),
        iconResource = R.drawable.ic_batch_card) {
        listOf("Efectivo", "Tarjeta", "Transferencia", "Otros").forEachIndexed { index, label ->
            if (index > 0) HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            Row(Modifier.fillMaxWidth().heightIn(min = 48.dp),
                verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                val icon = listOf(R.drawable.ic_batch_cash, R.drawable.ic_batch_card,
                    R.drawable.ic_batch_bank, OrderIcon.More.resource)[index]
                Icon(androidx.compose.ui.res.painterResource(icon), null, Modifier.size(24.dp), tint = AquaLv4)
                Text(label, Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium)
                Text(stringResource(R.string.batch_unavailable), Modifier.weight(1f),
                    textAlign = androidx.compose.ui.text.style.TextAlign.End,
                    style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        Text(stringResource(R.string.batch_unavailable_reason), style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
