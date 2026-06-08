package com.famessa.deli_antojito.feature.product

import android.R
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.famessa.deli_antojito.core.ui.theme.AquaAccent
import com.famessa.deli_antojito.core.ui.theme.AquaChip
import com.famessa.deli_antojito.core.ui.theme.AquaDeep
import com.famessa.deli_antojito.core.ui.theme.AquaLv1
import com.famessa.deli_antojito.core.ui.theme.AquaLv3
import com.famessa.deli_antojito.core.ui.theme.AquaLv4
import com.famessa.deli_antojito.core.ui.theme.AquaLv5
import com.famessa.deli_antojito.core.ui.theme.AquaMist
import com.famessa.deli_antojito.core.ui.theme.AquaSurface
import com.famessa.deli_antojito.domain.model.ProductListSummary
import com.famessa.deli_antojito.domain.model.Producto
import java.text.NumberFormat
import java.util.Locale

@Composable
fun ProductListScreen(
    state: ProductListUiState,
    onAddProduct: () -> Unit,
    onEditProduct: (Long) -> Unit,
    onRequestDelete: (Producto) -> Unit,
    onConfirmDelete: () -> Unit,
    onCancelDelete: () -> Unit,
    onRetry: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(AquaMist, Color.White, AquaSurface)
                )
            )
            .testTag(ProductTestTags.ListScreen)
    ) {
        WaveBackground(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(210.dp)
        )

        Column(modifier = Modifier.fillMaxSize()) {
            ProductTopBar(onBack = onBack)

            when (state) {
                ProductListUiState.Loading -> LoadingState()
                is ProductListUiState.Empty -> EmptyProducts(summary = state.summary)
                is ProductListUiState.Error -> ErrorState(state.message, onRetry)
                is ProductListUiState.Loaded -> ProductListLoaded(
                    state = state,
                    onEditProduct = onEditProduct,
                    onRequestDelete = onRequestDelete
                )
            }
        }

        ProductAddAction(
            onAddProduct = onAddProduct,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 22.dp, bottom = 26.dp)
        )
    }

    val pendingDelete = (state as? ProductListUiState.Loaded)?.pendingDelete
    if (pendingDelete != null) {
        AlertDialog(
            modifier = Modifier.testTag(ProductTestTags.DeleteConfirm),
            onDismissRequest = onCancelDelete,
            title = { Text("Eliminar producto") },
            text = { Text("Esta acción eliminará ${pendingDelete.nombre} definitivamente.") },
            confirmButton = {
                TextButton(onClick = onConfirmDelete) { Text("Eliminar") }
            },
            dismissButton = {
                TextButton(onClick = onCancelDelete) { Text("Cancelar") }
            }
        )
    }
}

@Composable
private fun ProductTopBar(onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(104.dp)
            .background(
                Brush.horizontalGradient(
                    colors = listOf(AquaDeep, AquaLv4, AquaAccent)
                )
            )
            .padding(horizontal = 24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clickable(onClick = onBack),
            contentAlignment = Alignment.Center
        ) {
            Icon(painterResource(id = com.famessa.deli_antojito.core.ui.R.drawable.ic_home), contentDescription = null, Modifier.size(34.dp), tint = Color.White)
        }
        Text(
            text = "Productos",
            color = Color.White,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .weight(1f)
                .padding(start = 28.dp)
        )
        Icon(painterResource(id = com.famessa.deli_antojito.core.ui.R.drawable.ic_search), contentDescription = null, tint = Color.White, modifier = Modifier.size(42.dp))
    }
}

@Composable
private fun ProductListLoaded(
    state: ProductListUiState.Loaded,
    onEditProduct: (Long) -> Unit,
    onRequestDelete: (Producto) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(
            start = 20.dp,
            top = 24.dp,
            end = 20.dp,
            bottom = 130.dp
        ),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            SummaryCard(state.summary)
        }
        state.errorMessage?.let { message ->
            item {
                Text(
                    text = message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
        item {
            ProductListHeader()
        }
        items(state.productos, key = { it.id }) { product ->
            ProductRow(
                product = product,
                onEdit = { onEditProduct(product.id) },
                onDelete = { onRequestDelete(product) }
            )
        }
    }
}

@Composable
private fun ProductListHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Lista de productos",
            color = AquaLv5,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = AquaMist.copy(alpha = 0.64f),
            tonalElevation = 0.dp,
            border = androidx.compose.foundation.BorderStroke(1.dp, AquaLv1.copy(alpha = 0.8f))
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                //FilterGlyph(color = AquaLv5, modifier = Modifier.size(22.dp))
                Icon(
                    painter = painterResource(id = com.famessa.deli_antojito.core.ui.R.drawable.ic_filter),
                    contentDescription = null,
                    tint = AquaLv5
                )
                Text(
                    text = "Filtrar",
                    color = AquaLv5,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun LoadingState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = AquaLv4)
    }
}

@Composable
private fun EmptyProducts(summary: ProductListSummary) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 24.dp)
            .testTag(ProductTestTags.EmptyState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SummaryCard(summary)
        Spacer(modifier = Modifier.height(72.dp))
        Text(
            text = "No hay productos registrados",
            color = AquaLv5,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Text(
            text = "Agrega tu primer producto para iniciar el catálogo.",
            color = AquaDeep.copy(alpha = 0.82f),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 10.dp)
        )
    }
}

@Composable
private fun ErrorState(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = message,
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(12.dp))
        Button(onClick = onRetry, modifier = Modifier.testTag(ProductTestTags.RetryButton)) {
            Text("Reintentar")
        }
    }
}

@Composable
private fun SummaryCard(summary: ProductListSummary) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 7.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = AquaDeep.copy(alpha = 0.12f),
                spotColor = AquaDeep.copy(alpha = 0.15f)
            ),
        shape = RoundedCornerShape(16.dp),
        color = Color.White.copy(alpha = 0.92f)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 20.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SummaryMetric(
                title = "Total de productos",
                value = summary.totalProductos.toString(),
                icon = SummaryIconType.Box,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(14.dp))
            SummaryMetric(
                title = "Activos",
                value = summary.totalActivos.toString(),
                icon = SummaryIconType.Bag,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun SummaryMetric(
    title: String,
    value: String,
    icon: SummaryIconType,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Surface(
            modifier = Modifier.size(64.dp),
            shape = CircleShape,
            color = AquaChip.copy(alpha = 0.78f)
        ) {
            SummaryIcon(
                type = icon,
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize()
            )
        }
        Column {
            Text(
                text = title,
                color = AquaLv5,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2
            )
            Text(
                text = value,
                color = AquaLv4,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun ProductRow(product: Producto, onEdit: () -> Unit, onDelete: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 5.dp,
                shape = RoundedCornerShape(14.dp),
                ambientColor = AquaDeep.copy(alpha = 0.09f),
                spotColor = AquaDeep.copy(alpha = 0.13f)
            )
            .clickable(onClick = onEdit),
        shape = RoundedCornerShape(14.dp),
        color = Color.White.copy(alpha = 0.94f)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 14.dp, vertical = 10.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ProductAvatar(product = product)
            Spacer(modifier = Modifier.width(18.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = product.nombre,
                    color = AquaDeep,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = productSubtitle(product),
                    color = AquaDeep.copy(alpha = 0.78f),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(top = 3.dp)
                )
                Text(
                    text = currencyFormatter().format(product.precioBase),
                    color = AquaLv5,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 6.dp)
                )
            }
            StatusPill(active = product.activo)
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clickable(onClick = onDelete),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = com.famessa.deli_antojito.core.ui.R.drawable.ic_delete),
                    contentDescription = null,
                    tint = Color.Red,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
private fun ProductAvatar(product: Producto) {
    val bitmap = remember(product.img) {
        product.img?.let { image ->
            runCatching {
                val bytes = Base64.decode(image, Base64.DEFAULT)
                BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            }.getOrNull()
        }
    }

    Surface(
        modifier = Modifier.size(76.dp),
        shape = CircleShape,
        color = AquaChip.copy(alpha = 0.58f)
    ) {
        if (bitmap != null) {
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = product.nombre,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        } else {
            Box(contentAlignment = Alignment.Center) {
                ProductPlaceholderGlyph(
                    initial = product.nombre.take(1).uppercase(Locale.getDefault()),
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
private fun StatusPill(active: Boolean) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = if (active) AquaChip.copy(alpha = 0.88f) else AquaSurface.copy(alpha = 0.9f)
    ) {
        Text(
            text = if (active) "Activo" else "Inactivo",
            color = AquaLv5,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 8.dp)
        )
    }
}

@Composable
private fun ProductAddAction(onAddProduct: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.End
    ) {
        FloatingActionButton(
            onClick = onAddProduct,
            containerColor = AquaLv3,
            contentColor = Color.White,
            shape = CircleShape,
            modifier = Modifier
                .size(74.dp)
                .testTag(ProductTestTags.AddButton)
        ) {
            Icon(
                painter = painterResource(id = com.famessa.deli_antojito.core.ui.R.drawable.ic_add),
                contentDescription = null,
                modifier = Modifier.size(38.dp),
                tint = Color.White
            )
        }
        Text(
            text = "Agregar producto",
            color = AquaLv5,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 10.dp)
        )
    }
}

private enum class SummaryIconType {
    Box,
    Bag
}

@Composable
private fun WaveBackground(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        fun wavePath(top: Float, leftDip: Float, rightDip: Float) = Path().apply {
            moveTo(0f, size.height * top)
            cubicTo(
                size.width * 0.25f,
                size.height * leftDip,
                size.width * 0.48f,
                size.height * rightDip,
                size.width,
                size.height * (top - 0.12f)
            )
            lineTo(size.width, size.height)
            lineTo(0f, size.height)
            close()
        }

        drawPath(wavePath(0.16f, 0.32f, 0.68f), AquaChip.copy(alpha = 0.5f))
        drawPath(wavePath(0.44f, 0.84f, 0.34f), AquaLv1.copy(alpha = 0.74f))
        drawPath(wavePath(0.76f, 1.02f, 0.58f), AquaLv3.copy(alpha = 0.86f))
    }
}

@Composable
private fun SummaryIcon(type: SummaryIconType, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val stroke = Stroke(width = size.minDimension * 0.09f, cap = androidx.compose.ui.graphics.StrokeCap.Round)
        when (type) {
            SummaryIconType.Box -> {
                val p = Path().apply {
                    moveTo(size.width * 0.5f, size.height * 0.08f)
                    lineTo(size.width * 0.9f, size.height * 0.3f)
                    lineTo(size.width * 0.9f, size.height * 0.72f)
                    lineTo(size.width * 0.5f, size.height * 0.94f)
                    lineTo(size.width * 0.1f, size.height * 0.72f)
                    lineTo(size.width * 0.1f, size.height * 0.3f)
                    close()
                }
                drawPath(p, AquaLv5, style = stroke)
                drawLine(AquaLv5, Offset(size.width * 0.5f, size.height * 0.5f), Offset(size.width * 0.5f, size.height * 0.94f), strokeWidth = stroke.width, cap = androidx.compose.ui.graphics.StrokeCap.Round)
                drawLine(AquaLv5, Offset(size.width * 0.1f, size.height * 0.3f), Offset(size.width * 0.5f, size.height * 0.5f), strokeWidth = stroke.width, cap = androidx.compose.ui.graphics.StrokeCap.Round)
                drawLine(AquaLv5, Offset(size.width * 0.9f, size.height * 0.3f), Offset(size.width * 0.5f, size.height * 0.5f), strokeWidth = stroke.width, cap = androidx.compose.ui.graphics.StrokeCap.Round)
            }
            SummaryIconType.Bag -> {
                drawRoundRect(
                    color = AquaLv5,
                    topLeft = Offset(size.width * 0.18f, size.height * 0.32f),
                    size = Size(size.width * 0.64f, size.height * 0.56f),
                    style = stroke
                )
                drawArc(
                    color = AquaLv5,
                    startAngle = 180f,
                    sweepAngle = 180f,
                    useCenter = false,
                    topLeft = Offset(size.width * 0.34f, size.height * 0.08f),
                    size = Size(size.width * 0.32f, size.height * 0.42f),
                    style = stroke
                )
            }
        }
    }
}

@Composable
private fun ProductPlaceholderGlyph(initial: String, modifier: Modifier = Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(AquaLv1.copy(alpha = 0.5f), radius = size.minDimension * 0.48f)
            drawCircle(AquaChip.copy(alpha = 0.55f), radius = size.minDimension * 0.34f)
        }
        Text(
            text = initial,
            color = AquaLv5,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

private fun productSubtitle(product: Producto): String =
    if (product.activo) "Producto disponible" else "Producto inactivo"

private fun currencyFormatter(): NumberFormat =
    NumberFormat.getCurrencyInstance(Locale("es", "MX"))
