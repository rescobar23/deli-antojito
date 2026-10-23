package com.famessa.deli_antojito.feature.orders.form

import androidx.activity.compose.BackHandler
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import com.famessa.deli_antojito.feature.orders.components.collectOrderState

@Composable fun OrderFormRoute(id: Long?, entryKey: String, onSaved: () -> Unit, onBack: () -> Unit, onCatalog: () -> Unit) {
    val model: OrderFormViewModel = hiltViewModel(key = "order-form")
    val state by model.uiState.collectOrderState()
    var pendingAction by remember { mutableStateOf<(() -> Unit)?>(null) }
    fun leave(action: () -> Unit) {
        if (state.saving) return
        if (state.dirty) pendingAction = action else action()
    }
    LaunchedEffect(id, entryKey) { model.start(id, entryKey) }
    LaunchedEffect(state.saved, state.entryKey) { if (state.saved && state.entryKey == entryKey) onSaved() }
    BackHandler { leave(onBack) }
    OrderFormScreen(state, id != null, { leave(onBack) }, model::client, model::notes, model::quantity,
        model::remove, model::add, model::save, { leave(onCatalog) }, model::loadProducts, { leave(model::reload) })
    if (pendingAction != null) OrderDiscardDialog({ val action = pendingAction; pendingAction = null; action?.invoke() }, { pendingAction = null })
}

@Composable fun OrderDiscardDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(onDismissRequest = onDismiss,
        title = { Text("Cambios sin guardar") }, text = { Text("¿Deseas descartar los cambios del pedido?") },
        confirmButton = { TextButton(onClick = onConfirm) { Text("Descartar cambios") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Continuar editando") } })
}
