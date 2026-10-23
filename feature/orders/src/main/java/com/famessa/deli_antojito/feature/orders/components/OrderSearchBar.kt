package com.famessa.deli_antojito.feature.orders.components

import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.famessa.deli_antojito.core.ui.theme.*

@Composable fun OrderSearchBar(value: String, onChange: (String) -> Unit) {
    OutlinedTextField(value, onChange, placeholder = { Text("Buscar pedidos...") }, singleLine = true,
        leadingIcon = { OrderUiIcon(OrderIcon.Search, tint = Color(0xFF7D858C)) },
        shape = RoundedCornerShape(8.dp), colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = Color.White, focusedContainerColor = Color.White,
            unfocusedBorderColor = Color(0xFFE1E5E7), focusedBorderColor = AquaLv4),
        modifier = Modifier.fillMaxWidth().testTag("orders_search"))
}
