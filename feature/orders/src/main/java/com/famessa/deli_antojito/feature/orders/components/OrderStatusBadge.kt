package com.famessa.deli_antojito.feature.orders.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.Icon
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.text.font.FontWeight
import com.famessa.deli_antojito.core.ui.theme.OrderIcon
import com.famessa.deli_antojito.domain.model.EstadoPedido

fun EstadoPedido.label(): String = when (this) {
    EstadoPedido.PENDIENTE -> "Pendiente"
    EstadoPedido.EN_PREPARACION -> "En preparación"
    EstadoPedido.COMPLETADO -> "Completado"
    EstadoPedido.CANCELADO -> "Cancelado"
}
@Composable fun OrderStatusBadge(status: EstadoPedido) {
    Surface(color = status.background(), shape = RoundedCornerShape(8.dp)) {
        Text(status.label(), color = status.foreground(), style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp))
    }
}

internal fun EstadoPedido.foreground(): Color = when (this) {
    EstadoPedido.PENDIENTE -> Color(0xFFB87400)
    EstadoPedido.EN_PREPARACION -> Color(0xFF005CD6)
    EstadoPedido.COMPLETADO -> Color(0xFF008A16)
    EstadoPedido.CANCELADO -> Color(0xFFD51C2B)
}

internal fun EstadoPedido.background(): Color = when (this) {
    EstadoPedido.PENDIENTE -> Color(0xFFFFF0D4)
    EstadoPedido.EN_PREPARACION -> Color(0xFFE0EDFF)
    EstadoPedido.COMPLETADO -> Color(0xFFE3F5E9)
    EstadoPedido.CANCELADO -> Color(0xFFFFDFE2)
}

internal fun EstadoPedido.icon(): OrderIcon = when (this) {
    EstadoPedido.PENDIENTE -> OrderIcon.Clock
    EstadoPedido.EN_PREPARACION -> OrderIcon.Chef
    EstadoPedido.COMPLETADO -> OrderIcon.Check
    EstadoPedido.CANCELADO -> OrderIcon.Cancel
}
