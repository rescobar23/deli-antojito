package com.famessa.deli_antojito.feature.orders.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.famessa.deli_antojito.domain.model.*
import com.famessa.deli_antojito.domain.usecase.pedido.ValidatePedidoConsultaUseCase
import java.math.BigDecimal

@OptIn(ExperimentalMaterial3Api::class)
@Composable fun OrderFilters(query: PedidoConsulta, onApply: (PedidoConsulta) -> Unit, onCancel: () -> Unit) {
    var from by rememberSaveable { mutableStateOf(query.desde?.inputText().orEmpty()) }
    var through by rememberSaveable { mutableStateOf(query.hasta?.inputText().orEmpty()) }
    var min by rememberSaveable { mutableStateOf(query.minimo?.let { BigDecimal.valueOf(it.centavos, 2).toPlainString() }.orEmpty()) }
    var max by rememberSaveable { mutableStateOf(query.maximo?.let { BigDecimal.valueOf(it.centavos, 2).toPlainString() }.orEmpty()) }
    var selected by rememberSaveable(stateSaver = listSaver<Set<EstadoPedido>, String>(
        save = { states -> states.map { it.name } },
        restore = { names -> names.mapNotNull { name -> EstadoPedido.entries.find { it.name == name } }.toSet() }
    )) { mutableStateOf(query.estadosAvanzados) }
    var error by rememberSaveable { mutableStateOf<String?>(null) }
    var dateField by remember { mutableStateOf<String?>(null) }
    AlertDialog(onDismissRequest = onCancel, title = { Text("Filtros de pedidos") }, text = {
        Column(Modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(from, { from = it }, label = { Text("Fecha inicial (AAAA-MM-DD)") })
            TextButton(onClick = { dateField = "from" }) { Text("Elegir fecha inicial") }
            OutlinedTextField(through, { through = it }, label = { Text("Fecha final (AAAA-MM-DD)") })
            TextButton(onClick = { dateField = "through" }) { Text("Elegir fecha final") }
            OutlinedTextField(min, { min = it }, label = { Text("Importe mínimo") })
            OutlinedTextField(max, { max = it }, label = { Text("Importe máximo") })
            EstadoPedido.entries.forEach { status ->
                Row { Checkbox(status in selected, { checked -> selected = if (checked) selected + status else selected - status },
                    modifier = Modifier.testTag("order_filter_state_${status.name}")); Text(status.label(), Modifier.padding(top = 12.dp)) }
            }
            error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            TextButton(onClick = { from = ""; through = ""; min = ""; max = ""; selected = emptySet(); error = null }) { Text("Limpiar") }
        }
    }, confirmButton = {
        TextButton(onClick = {
            try {
                val value = query.copy(desde = parseOrderDate(from), hasta = parseOrderDate(through),
                    minimo = min.takeIf { it.isNotBlank() }?.let(::parsePedidoAmount), maximo = max.takeIf { it.isNotBlank() }?.let(::parsePedidoAmount), estadosAvanzados = selected)
                require(ValidatePedidoConsultaUseCase()(value))
                onApply(value)
            } catch (_: IllegalArgumentException) { error = "Revisa fechas e importes: usa rangos ordenados, importes positivos o cero y hasta dos decimales." }
        }) { Text("Aplicar") }
    }, dismissButton = { TextButton(onClick = onCancel) { Text("Cancelar") } })
    if (dateField != null) {
        val picker = rememberDatePickerState()
        DatePickerDialog(onDismissRequest = { dateField = null }, confirmButton = {
            TextButton(onClick = {
                picker.selectedDateMillis?.let { millis ->
                    val text = orderDateSelection(millis).inputText()
                    if (dateField == "from") from = text else through = text
                }
                dateField = null
            }) { Text("Aceptar") }
        }, dismissButton = { TextButton(onClick = { dateField = null }) { Text("Cancelar") } }) { DatePicker(picker) }
    }
}
