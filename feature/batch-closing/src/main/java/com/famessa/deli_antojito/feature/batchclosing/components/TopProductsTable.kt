package com.famessa.deli_antojito.feature.batchclosing.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.famessa.deli_antojito.core.ui.theme.*
import com.famessa.deli_antojito.domain.model.cierre.ProductoConsultaCierre

@Composable
fun TopProductsTable(products: List<ProductoConsultaCierre>) {
    BatchSection("Productos más vendidos · Top 5", OrderIcon.Box) {
        BoxWithConstraints {
            val compact = maxWidth < 400.dp
            Column {
                Row(Modifier.fillMaxWidth().background(AquaMist).padding(vertical = 12.dp, horizontal = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("#", Modifier.width(20.dp), style = MaterialTheme.typography.labelSmall)
                    Text("Producto", Modifier.weight(1f), style = MaterialTheme.typography.labelSmall)
                    if (!compact) Text("Cantidad", Modifier.weight(0.6f), style = MaterialTheme.typography.labelSmall, textAlign = TextAlign.Center)
                    Text("Importe bruto", Modifier.weight(0.8f), style = MaterialTheme.typography.labelSmall, textAlign = TextAlign.End)
                }
                if (products.isEmpty()) Text("Sin productos para este período.", Modifier.padding(vertical = 16.dp))
                products.forEachIndexed { index, product ->
                    Row(Modifier.fillMaxWidth().testTag("batch_top_product_${product.productoId}").padding(vertical = 14.dp, horizontal = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("${index + 1}", Modifier.width(20.dp), style = MaterialTheme.typography.bodySmall)
                        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(product.nombreHistorico, style = MaterialTheme.typography.bodyMedium)
                            if (compact) Text("Cantidad: ${product.cantidad}", style = MaterialTheme.typography.bodySmall)
                        }
                        if (!compact) Text(product.cantidad.toString(), Modifier.weight(0.6f),
                            style = MaterialTheme.typography.bodySmall, textAlign = TextAlign.Center)
                        Text(formatBatchMoney(product.bruto.centavos), Modifier.weight(0.8f),
                            style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.End)
                    }
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                }
            }
        }
    }
}
