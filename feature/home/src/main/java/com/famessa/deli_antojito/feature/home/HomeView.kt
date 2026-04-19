package com.famessa.deli_antojito.feature.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.famessa.deli_antojito.domain.model.Producto

@Composable
fun HomeView(viewModel: HomeViewModel, onAdminClick: () -> Unit = {}) {
    val nombre by viewModel.nombre
    val precioBase by viewModel.precioBase
    val categoria by viewModel.categoria
    val mensaje by viewModel.mensaje
    val productos by viewModel.productos.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Gestión de Productos",
                style = MaterialTheme.typography.headlineMedium
            )
            Button(onClick = onAdminClick) {
                Text("Admin")
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Formulario de alta
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Alta de Producto",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = nombre,
                    onValueChange = viewModel::onNombreChange,
                    label = { Text("Nombre del Producto") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = precioBase,
                    onValueChange = viewModel::onPrecioBaseChange,
                    label = { Text("Precio Base") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = categoria,
                    onValueChange = viewModel::onCategoriaChange,
                    label = { Text("Categoría") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { viewModel.guardarProducto() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Guardar Producto")
                }

                if (mensaje.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = mensaje,
                        color = if (mensaje.contains("Error")) {
                            MaterialTheme.colorScheme.error
                        } else {
                            MaterialTheme.colorScheme.primary
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Lista de productos
        Text(
            text = "Productos Registrados",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            Text("Cargando productos...")
        } else if (productos.isEmpty()) {
            Text("No hay productos registrados")
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(productos) { producto ->
                    ProductoItem(producto = producto, onDelete = { viewModel.eliminarProducto(it) })
                }
            }
        }
    }
}

@Composable
fun ProductoItem(producto: Producto, onDelete: (Long) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = producto.nombre,
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    text = "Categoría: ${producto.categoria}",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "Precio: $${producto.precioBase}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Button(
                onClick = { onDelete(producto.id) }
            ) {
                Text("Eliminar")
            }
        }
    }
}
