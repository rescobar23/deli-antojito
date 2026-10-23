package com.famessa.deli_antojito.feature.orders.detail

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.hilt.navigation.compose.hiltViewModel
import com.famessa.deli_antojito.domain.model.EstadoPedido
import com.famessa.deli_antojito.feature.orders.components.collectOrderState

@Composable fun OrderDetailRoute(id: Long, onBack: () -> Unit, onEdit: () -> Unit) {
    val model: OrderDetailViewModel = hiltViewModel(key = "order-detail")
    val state by model.uiState.collectOrderState()
    var cancel by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(id) { model.start(id) }
    OrderDetailScreen(state, onBack, onEdit, { target ->
        if (target == EstadoPedido.CANCELADO) cancel = true else model.transition(target)
    }, model::retry)
    if (cancel) OrderCancelDialog({ cancel = false; model.transition(EstadoPedido.CANCELADO) }, { cancel = false })
}

@Composable fun OrderCancelDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(onDismissRequest = onDismiss, title = { Text("Cancelar pedido") },
        text = { Text("¿Estás seguro de que deseas cancelar este pedido? Esta acción no se puede deshacer.") },
        confirmButton = { TextButton(onClick = onConfirm,
            modifier = Modifier.testTag("order_cancel_confirm")) { Text("Confirmar cancelación") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Volver") } })
}
