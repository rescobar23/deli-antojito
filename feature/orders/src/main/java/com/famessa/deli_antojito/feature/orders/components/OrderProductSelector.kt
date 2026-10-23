package com.famessa.deli_antojito.feature.orders.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.famessa.deli_antojito.feature.orders.form.ProductChoice

@Composable fun OrderProductSelector(products: List<ProductChoice>, onSelect: (Long) -> Unit, onDismiss: () -> Unit) {
    AlertDialog(onDismissRequest = onDismiss, title = { Text("Seleccionar producto") }, text = {
        LazyColumn { items(products, key = { it.id }) { product ->
            TextButton(onClick = { onSelect(product.id) }, modifier = Modifier.fillMaxWidth()) {
                Text("${product.name} · ${product.price.display()}")
            }
        } }
    }, confirmButton = { TextButton(onClick = onDismiss) { Text("Cerrar") } })
}
