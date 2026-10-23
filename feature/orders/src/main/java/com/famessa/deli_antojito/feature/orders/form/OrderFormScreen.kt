package com.famessa.deli_antojito.feature.orders.form

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.famessa.deli_antojito.core.ui.theme.*
import com.famessa.deli_antojito.feature.orders.components.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val FormInk = Color(0xFF273744)
private val FormBorder = Color(0xFFE1E5E8)
private val FormMuted = Color(0xFF66727C)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderFormScreen(state: OrderFormState, editing: Boolean, onBack: () -> Unit,
    onClient: (String) -> Unit, onNotes: (String) -> Unit, onQuantity: (String, String) -> Unit,
    onRemove: (String) -> Unit, onAdd: (Long) -> Unit, onSave: () -> Unit, onCatalog: () -> Unit,
    retryCatalog: () -> Unit, onReload: () -> Unit) {
    var selector by rememberSaveable { mutableStateOf(false) }
    val enabled = !state.saving && !state.capturing && !state.loading && state.editable
    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = AquaLv4, unfocusedBorderColor = FormBorder,
        focusedContainerColor = Color.White, unfocusedContainerColor = Color.White,
        focusedTextColor = FormInk, unfocusedTextColor = FormInk)
    Scaffold(Modifier.aquaScreenBackground().testTag("order_form"), containerColor = Color.Transparent, topBar = {
        AquaTopBar(if (editing) "Editar pedido" else "Nuevo pedido", onBack, navigationEnabled = !state.saving)
    }) { padding ->
        if (state.loading) {
            Box(Modifier.padding(padding).fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = AquaLv4)
            }
            return@Scaffold
        }
        Column(Modifier.padding(padding).fillMaxSize().imePadding().verticalScroll(rememberScrollState()).padding(horizontal = 24.dp).padding(top = 26.dp, bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)) {
            if (state.capturing) { LinearProgressIndicator(Modifier.fillMaxWidth()); Text("Añadiendo producto...") }
            state.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            FormSection("Información del cliente", OrderIcon.User)
            Text("Cliente *", color = AquaLv5, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            OutlinedTextField(state.cliente, onClient, placeholder = { Text("Nombre del cliente") }, enabled = enabled,
                leadingIcon = { FormIcon(OrderIcon.User) }, singleLine = true, shape = RoundedCornerShape(12.dp), colors = fieldColors,
                isError = state.error != null && state.cliente.isBlank(),
                modifier = Modifier.fillMaxWidth().heightIn(min = 64.dp).testTag("order_client")
                    .semantics { contentDescription = "Cliente" })
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ReadOnlyField("Fecha del pedido", state.createdAt?.let {
                    SimpleDateFormat("d MMM yyyy", Locale("es", "MX")).format(Date(it))
                } ?: "Al guardar", OrderIcon.Calendar, Modifier.weight(1f))
                ReadOnlyField("Hora", state.createdAt?.let {
                    SimpleDateFormat("HH:mm", Locale("es", "MX")).format(Date(it))
                } ?: "Automática", OrderIcon.Clock, Modifier.weight(1f))
            }

            FormSection("Detalles del pedido", OrderIcon.Orders)
            Surface(shape = RoundedCornerShape(8.dp), border = BorderStroke(1.dp, FormBorder), color = Color.White) {
                Column {
                    Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text("Productos", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = FormInk, modifier = Modifier.weight(1f))
                        OutlinedButton(onClick = { selector = true }, enabled = enabled && state.products.isNotEmpty(), shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp), border = BorderStroke(1.dp, AquaLv4)) {
                            OrderUiIcon(OrderIcon.Add, modifier = Modifier.size(18.dp), tint = AquaLv4)
                            Spacer(Modifier.width(6.dp))
                            Text("Agregar producto", color = AquaLv4)
                        }
                    }
                    BoxWithConstraints {
                        val wide = maxWidth >= 600.dp
                        Column {
                            if (wide) Row(Modifier.fillMaxWidth().background(Color(0xFFF2F6F7)).padding(12.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                Text("Producto", Modifier.weight(1.5f), style = MaterialTheme.typography.labelLarge)
                                Text("Cantidad", Modifier.width(160.dp), style = MaterialTheme.typography.labelLarge)
                                Text("Precio unitario", Modifier.weight(1f), style = MaterialTheme.typography.labelLarge)
                                Text("Subtotal", Modifier.weight(1f), style = MaterialTheme.typography.labelLarge)
                                Spacer(Modifier.width(48.dp))
                            }
                            state.lines.forEach { line ->
                                HorizontalDivider(color = FormBorder)
                                OrderLineEditor(line, wide, enabled, { onQuantity(line.key, it) }, { onRemove(line.key) })
                            }
                        }
                    }
                    if (state.lines.isEmpty()) Text("Sin productos", color = FormMuted, modifier = Modifier.padding(16.dp))
                    state.catalogError?.let {
                        Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(12.dp))
                        TextButton(onClick = retryCatalog) { Text("Reintentar catálogo") }
                    }
                    if (state.products.isEmpty() && state.catalogError == null) {
                        Text("No hay productos activos con precio válido para añadir.", modifier = Modifier.padding(12.dp), color = FormMuted)
                        TextButton(onClick = onCatalog, enabled = !state.saving) { Text("Ir a Productos") }
                    }
                    Column(Modifier.fillMaxWidth().background(Color(0xFFF2F7F8)).padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Subtotal", color = FormInk)
                            Text(state.total?.display() ?: "Revisa cantidades e importes", color = FormInk)
                        }
                        HorizontalDivider(color = FormBorder)
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Total", fontWeight = FontWeight.Bold, color = FormInk, style = MaterialTheme.typography.titleMedium)
                            Text(state.total?.display() ?: "—", color = AquaLv4, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
            FormSection("Notas (opcional)", OrderIcon.Notes)
            OutlinedTextField(state.observaciones, onNotes, enabled = enabled, label = { Text("Observaciones") },
                placeholder = { Text("Agregar notas sobre el pedido...") }, minLines = 3,
                leadingIcon = { OrderUiIcon(OrderIcon.Notes, tint = FormMuted) }, shape = RoundedCornerShape(8.dp), colors = fieldColors,
                modifier = Modifier.fillMaxWidth().testTag("order_notes"))
            FormSection("Estado del pedido", OrderIcon.Clock)
            ReadOnlyField("Estado", state.status.label(), state.status.icon(), Modifier.fillMaxWidth())
            if (editing) TextButton(onClick = onReload, enabled = !state.saving) { Text("Recargar pedido") }
            AquaSaveButton(if (state.saving) "Guardando" else "Guardar pedido", enabled, onSave,
                Modifier.testTag("order_save"))
            Spacer(Modifier.height(8.dp))
        }
    }
    if (selector) OrderProductSelector(state.products, { onAdd(it); selector = false }, { selector = false })
}

@Composable
private fun FormSection(title: String, icon: OrderIcon) {
    Row(Modifier.padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
        Surface(shape = CircleShape, color = Color(0xFFE5F2F2), modifier = Modifier.size(40.dp)) {
            OrderUiIcon(icon, modifier = Modifier.padding(10.dp), tint = AquaLv4)
        }
        Text(title, style = MaterialTheme.typography.titleMedium, color = AquaLv4, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun FormIcon(icon: OrderIcon) {
    Surface(shape = RoundedCornerShape(6.dp), color = Color(0xFFE5F2F2), modifier = Modifier.size(36.dp)) {
        OrderUiIcon(icon, modifier = Modifier.padding(8.dp), tint = AquaLv4)
    }
}

@Composable
private fun ReadOnlyField(label: String, value: String, icon: OrderIcon, modifier: Modifier) {
    Surface(modifier, shape = RoundedCornerShape(8.dp), border = BorderStroke(1.dp, FormBorder), color = Color.White) {
        Row(Modifier.padding(12.dp), horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
            FormIcon(icon)
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(label, color = FormMuted, style = MaterialTheme.typography.labelMedium)
                Text(value, color = FormInk, style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}

@Composable
private fun OrderLineEditor(line: OrderFormLine, wide: Boolean, enabled: Boolean, onQuantity: (String) -> Unit, onRemove: () -> Unit) {
    val subtotal = try { (line.snapshot.precio * (line.quantity.toIntOrNull() ?: 0)).display() }
        catch (_: IllegalArgumentException) { "Cantidad o importe inválido" }
    val product: @Composable (Modifier) -> Unit = { modifier ->
        Row(modifier, verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            FormIcon(OrderIcon.Box)
            Text(line.snapshot.nombre, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium, color = FormInk)
        }
    }
    val remove: @Composable () -> Unit = {
        IconButton(onClick = onRemove, enabled = enabled) {
            OrderUiIcon(OrderIcon.Trash, "Retirar ${line.snapshot.nombre}", tint = Color(0xFFD51C2B))
        }
    }
    if (wide) {
        Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            product(Modifier.weight(1.5f))
            QuantityEditor(line, enabled, onQuantity, Modifier.width(160.dp))
            Text(line.snapshot.precio.display(), Modifier.weight(1f).semantics { contentDescription = "Precio unitario: ${line.snapshot.precio.display()}" }, color = FormInk)
            Text(subtotal, Modifier.weight(1f), color = FormInk)
            remove()
        }
    } else {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) { product(Modifier.weight(1f)); remove() }
            Text("Precio unitario: ${line.snapshot.precio.display()}", color = FormMuted, style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.semantics { contentDescription = "Precio unitario: ${line.snapshot.precio.display()}" })
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                QuantityEditor(line, enabled, onQuantity, Modifier.weight(1.3f))
                Column(horizontalAlignment = Alignment.End, modifier = Modifier.weight(1f)) {
                    Text("Subtotal", style = MaterialTheme.typography.labelMedium, color = FormMuted)
                    Text(subtotal, color = FormInk, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
private fun QuantityEditor(line: OrderFormLine, enabled: Boolean, onChange: (String) -> Unit, modifier: Modifier) {
    val quantity = line.quantity.toIntOrNull()
    Column(modifier) {
        Text("Cantidad *", style = MaterialTheme.typography.labelSmall, color = FormMuted)
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { quantity?.let { onChange((it - 1).toString()) } }, enabled = enabled && quantity != null && quantity > 1, modifier = Modifier.size(40.dp)) {
                OrderUiIcon(OrderIcon.Minus, "Disminuir cantidad de ${line.snapshot.nombre}", tint = AquaLv4)
            }
            OutlinedTextField(line.quantity, onChange, enabled = enabled, singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = quantity == null || quantity <= 0, shape = RoundedCornerShape(6.dp),
                textStyle = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f).widthIn(min = 48.dp).semantics { contentDescription = "Cantidad de ${line.snapshot.nombre}" })
            IconButton(onClick = { quantity?.let { onChange((it + 1).toString()) } }, enabled = enabled && quantity != null && quantity in 1 until Int.MAX_VALUE, modifier = Modifier.size(40.dp)) {
                OrderUiIcon(OrderIcon.Add, "Aumentar cantidad de ${line.snapshot.nombre}", tint = AquaLv4)
            }
        }
    }
}
