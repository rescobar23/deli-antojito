package com.famessa.deli_antojito.feature.product

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.famessa.deli_antojito.core.ui.theme.AquaAccent
import com.famessa.deli_antojito.core.ui.theme.AquaChip
import com.famessa.deli_antojito.core.ui.theme.AquaDeep
import com.famessa.deli_antojito.core.ui.theme.AquaLv1
import com.famessa.deli_antojito.core.ui.theme.AquaLv3
import com.famessa.deli_antojito.core.ui.theme.AquaLv4
import com.famessa.deli_antojito.core.ui.theme.AquaLv5
import com.famessa.deli_antojito.core.ui.theme.AquaMist
import com.famessa.deli_antojito.core.ui.theme.AquaSurface

@Composable
fun ProductEditScreen(
    state: ProductFormState,
    isEdit: Boolean,
    onNameChange: (String) -> Unit,
    onPriceChange: (String) -> Unit,
    onActiveChange: (Boolean) -> Unit,
    onImageClick: () -> Unit,
    onSave: () -> Unit,
    onBack: () -> Unit,
    onConfirmDiscard: () -> Unit,
    onCancelDiscard: () -> Unit,
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
            .testTag(ProductTestTags.EditScreen)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            ProductEditTopBar(
                title = if (isEdit) "Editar producto" else "Agregar producto",
                onBack = onBack
            )

            if (state.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = AquaLv4)
                }
                return@Column
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp)
                    .padding(top = 26.dp, bottom = 28.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                state.validationMessage?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.testTag(ProductTestTags.Validation)
                    )
                }
                state.errorMessage?.let {
                    Text(text = it, color = MaterialTheme.colorScheme.error)
                }

                FormLabel("Imagen del producto")
                ImagePickerBox(
                    image = state.img,
                    onClick = onImageClick,
                    enabled = !state.isSaving,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                LabeledField(
                    label = "Nombre del producto *",
                    value = state.nombre,
                    onValueChange = onNameChange,
                    placeholder = "Ej. Alitas",
                    enabled = !state.isSaving,
                    icon = FieldIconType.Tag,
                    modifier = Modifier.testTag(ProductTestTags.NameInput)
                )

                //DescriptionField(enabled = !state.isSaving)

                LabeledField(
                    label = "Precio base *",
                    value = state.precioBase,
                    onValueChange = onPriceChange,
                    placeholder = "$ 0.00",
                    enabled = !state.isSaving,
                    icon = FieldIconType.Money,
                    modifier = Modifier.testTag(ProductTestTags.PriceInput)
                )

                ActiveSelector(
                    active = state.activo,
                    enabled = !state.isSaving,
                    onActiveChange = onActiveChange,
                    modifier = Modifier.testTag(ProductTestTags.ActiveSwitch)
                )

                Button(
                    onClick = onSave,
                    enabled = !state.isSaving,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AquaLv3,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(58.dp)
                        .testTag(ProductTestTags.SaveButton)
                ) {
                    SaveGlyph(color = Color.White, modifier = Modifier.size(26.dp))
                    Text(
                        text = if (state.isSaving) "Guardando" else "Guardar producto",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 14.dp)
                    )
                }
            }
        }
    }

    if (state.showDiscardConfirmation) {
        AlertDialog(
            onDismissRequest = onCancelDiscard,
            title = { Text("Descartar cambios") },
            text = { Text("Los cambios no guardados se perderán.") },
            confirmButton = { TextButton(onClick = onConfirmDiscard) { Text("Descartar") } },
            dismissButton = { TextButton(onClick = onCancelDiscard) { Text("Cancelar") } }
        )
    }
}

@Composable
private fun ProductEditTopBar(title: String, onBack: () -> Unit) {
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
            BackGlyph(color = Color.White, modifier = Modifier.size(34.dp))
        }
        Text(
            text = title,
            color = Color.White,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 26.dp)
        )
        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
private fun FormLabel(text: String) {
    Text(
        text = text,
        color = AquaLv5,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold
    )
}

@Composable
private fun ImagePickerBox(
    image: String?,
    onClick: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    val bitmap = remember(image) {
        image?.let {
            runCatching {
                val bytes = Base64.decode(it, Base64.DEFAULT)
                BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            }.getOrNull()
        }
    }

    Box(
        modifier = modifier
            .size(200.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White.copy(alpha = 0.4f))
            .testTag(ProductTestTags.ImagePicker)
            .drawBehind {
                drawRoundRect(
                    color = AquaLv1,
                    size = size,
                    cornerRadius = CornerRadius(14.dp.toPx(), 14.dp.toPx()),
                    style = Stroke(
                        width = 1.5.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 10f), 0f)
                    )
                )
            }
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (bitmap != null) {
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "Imagen del producto",
                contentScale = ContentScale.FillBounds,
                modifier = Modifier
                    .fillMaxSize()
            )
            Surface(
                color = Color.White.copy(alpha = 0.1f),
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 8.dp)
            ) {
                Text(
                    text = "Cambiar imagen",
                    color = AquaLv5,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 8.dp)
                )
            }
        } else {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Surface(
                    modifier = Modifier.size(72.dp),
                    shape = CircleShape,
                    color = AquaChip.copy(alpha = 0.72f)
                ) {
                    CameraGlyph(
                        color = AquaLv5,
                        modifier = Modifier
                            .padding(18.dp)
                    )
                }
                Text(
                    text = "Agregar imagen",
                    color = AquaLv5,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 14.dp)
                )
                Text(
                    text = "JPG o PNG. Máx. 2MB",
                    color = AquaDeep.copy(alpha = 0.72f),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun LabeledField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    enabled: Boolean,
    icon: FieldIconType,
    modifier: Modifier = Modifier
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        FormLabel(label)
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            enabled = enabled,
            placeholder = { Text(placeholder, color = AquaDeep.copy(alpha = 0.42f)) },
            leadingIcon = { FieldIconBubble(type = icon) },
            shape = RoundedCornerShape(12.dp),
            modifier = modifier
                .fillMaxWidth()
                .height(64.dp)
        )
    }
}

@Composable
private fun FieldIconBubble(type: FieldIconType) {
    Surface(
        modifier = Modifier.size(38.dp),
        shape = CircleShape,
        color = AquaChip.copy(alpha = 0.68f)
    ) {
        FieldIcon(
            type = type,
            color = AquaLv5,
            modifier = Modifier
                .padding(9.dp)
                .fillMaxSize()
        )
    }
}

@Composable
private fun ActiveSelector(
    active: Boolean,
    enabled: Boolean,
    onActiveChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        FormLabel("Activo *")
        Surface(
            modifier = modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = Color.White.copy(alpha = 0.68f),
            border = androidx.compose.foundation.BorderStroke(1.dp, AquaLv1.copy(alpha = 0.8f))
        ) {
            Column {
                StatusOption(
                    selected = active,
                    title = "Activo",
                    description = "El producto estará disponible para la venta.",
                    onClick = { onActiveChange(true) },
                    enabled = enabled,
                    highlighted = active
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(AquaLv1.copy(alpha = 0.5f))
                )
                StatusOption(
                    selected = !active,
                    title = "Inactivo",
                    description = "El producto no estará disponible para la venta.",
                    onClick = { onActiveChange(false) },
                    enabled = enabled,
                    highlighted = !active
                )
            }
        }
    }
}

@Composable
private fun StatusOption(
    selected: Boolean,
    title: String,
    description: String,
    onClick: () -> Unit,
    enabled: Boolean,
    highlighted: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(if (highlighted) AquaSurface.copy(alpha = 0.62f) else Color.Transparent)
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioGlyph(
            selected = selected,
            color = AquaLv4,
            modifier = Modifier.size(32.dp)
        )
        Column(modifier = Modifier.padding(start = 18.dp)) {
            Text(
                text = title,
                color = if (selected) AquaLv5 else AquaDeep.copy(alpha = 0.82f),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = description,
                color = AquaDeep.copy(alpha = 0.78f),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 3.dp)
            )
        }
    }
}

private enum class FieldIconType {
    Tag,
    Comment,
    Money
}

@Composable
private fun BackGlyph(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        drawLine(color, Offset(size.width * 0.22f, size.height * 0.5f), Offset(size.width * 0.78f, size.height * 0.5f), strokeWidth = size.width * 0.1f, cap = androidx.compose.ui.graphics.StrokeCap.Round)
        drawLine(color, Offset(size.width * 0.22f, size.height * 0.5f), Offset(size.width * 0.48f, size.height * 0.24f), strokeWidth = size.width * 0.1f, cap = androidx.compose.ui.graphics.StrokeCap.Round)
        drawLine(color, Offset(size.width * 0.22f, size.height * 0.5f), Offset(size.width * 0.48f, size.height * 0.76f), strokeWidth = size.width * 0.1f, cap = androidx.compose.ui.graphics.StrokeCap.Round)
    }
}

@Composable
private fun CameraGlyph(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val stroke = Stroke(width = size.width * 0.1f, cap = androidx.compose.ui.graphics.StrokeCap.Round)
        drawRoundRect(color, topLeft = Offset(size.width * 0.12f, size.height * 0.28f), size = Size(size.width * 0.76f, size.height * 0.56f), cornerRadius = CornerRadius(size.width * 0.12f), style = stroke)
        drawRoundRect(color, topLeft = Offset(size.width * 0.34f, size.height * 0.14f), size = Size(size.width * 0.32f, size.height * 0.18f), cornerRadius = CornerRadius(size.width * 0.06f), style = stroke)
        drawCircle(color, radius = size.width * 0.14f, center = Offset(size.width * 0.5f, size.height * 0.56f), style = stroke)
    }
}

@Composable
private fun FieldIcon(type: FieldIconType, color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val stroke = Stroke(width = size.width * 0.1f, cap = androidx.compose.ui.graphics.StrokeCap.Round)
        when (type) {
            FieldIconType.Tag -> {
                val p = Path().apply {
                    moveTo(size.width * 0.18f, size.height * 0.18f)
                    lineTo(size.width * 0.64f, size.height * 0.18f)
                    lineTo(size.width * 0.86f, size.height * 0.42f)
                    lineTo(size.width * 0.42f, size.height * 0.86f)
                    lineTo(size.width * 0.18f, size.height * 0.62f)
                    close()
                }
                drawPath(p, color, style = stroke)
                drawCircle(color, radius = size.width * 0.05f, center = Offset(size.width * 0.38f, size.height * 0.38f))
            }
            FieldIconType.Comment -> {
                drawRoundRect(color, topLeft = Offset(size.width * 0.12f, size.height * 0.18f), size = Size(size.width * 0.76f, size.height * 0.56f), cornerRadius = CornerRadius(size.width * 0.12f), style = stroke)
                drawLine(color, Offset(size.width * 0.32f, size.height * 0.74f), Offset(size.width * 0.42f, size.height * 0.88f), strokeWidth = stroke.width, cap = androidx.compose.ui.graphics.StrokeCap.Round)
                listOf(0.34f, 0.5f, 0.66f).forEach { x ->
                    drawCircle(color, radius = size.width * 0.04f, center = Offset(size.width * x, size.height * 0.48f))
                }
            }
            FieldIconType.Money -> {
                drawCircle(color, radius = size.width * 0.38f, center = Offset(size.width * 0.5f, size.height * 0.5f), style = stroke)
                drawLine(color, Offset(size.width * 0.5f, size.height * 0.26f), Offset(size.width * 0.5f, size.height * 0.74f), strokeWidth = stroke.width * 0.8f, cap = androidx.compose.ui.graphics.StrokeCap.Round)
                drawLine(color, Offset(size.width * 0.38f, size.height * 0.38f), Offset(size.width * 0.62f, size.height * 0.38f), strokeWidth = stroke.width * 0.75f, cap = androidx.compose.ui.graphics.StrokeCap.Round)
                drawLine(color, Offset(size.width * 0.38f, size.height * 0.62f), Offset(size.width * 0.62f, size.height * 0.62f), strokeWidth = stroke.width * 0.75f, cap = androidx.compose.ui.graphics.StrokeCap.Round)
            }
        }
    }
}

@Composable
private fun RadioGlyph(selected: Boolean, color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        drawCircle(
            color = if (selected) color else AquaLv1,
            radius = size.minDimension * 0.42f,
            style = Stroke(width = size.minDimension * 0.12f)
        )
        if (selected) {
            drawCircle(color = color, radius = size.minDimension * 0.24f)
        }
    }
}

@Composable
private fun SaveGlyph(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val stroke = Stroke(width = size.width * 0.09f, cap = androidx.compose.ui.graphics.StrokeCap.Round)
        drawRoundRect(color, topLeft = Offset(size.width * 0.18f, size.height * 0.12f), size = Size(size.width * 0.64f, size.height * 0.76f), cornerRadius = CornerRadius(size.width * 0.04f), style = stroke)
        drawLine(color, Offset(size.width * 0.32f, size.height * 0.12f), Offset(size.width * 0.32f, size.height * 0.42f), strokeWidth = stroke.width, cap = androidx.compose.ui.graphics.StrokeCap.Round)
        drawLine(color, Offset(size.width * 0.32f, size.height * 0.42f), Offset(size.width * 0.68f, size.height * 0.42f), strokeWidth = stroke.width, cap = androidx.compose.ui.graphics.StrokeCap.Round)
        drawLine(color, Offset(size.width * 0.32f, size.height * 0.68f), Offset(size.width * 0.68f, size.height * 0.68f), strokeWidth = stroke.width, cap = androidx.compose.ui.graphics.StrokeCap.Round)
    }
}
