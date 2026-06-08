package com.famessa.deli_antojito.feature.businessconfig

import androidx.compose.foundation.background
import androidx.compose.foundation.Canvas
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
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

object BusinessConfigTestTags {
    const val Screen = "business_config_screen"
    const val NameInput = "business_name_input"
    const val SaveButton = "business_config_save"
    const val RetryButton = "business_config_retry"
    const val CancelButton = "business_config_cancel"
    const val Error = "business_config_error"
    const val Validation = "business_config_validation"
}

@Composable
fun BusinessConfigScreen(
    state: BusinessConfigUiState,
    onNameChange: (String) -> Unit,
    onSave: () -> Unit,
    onRetry: () -> Unit,
    onCancel: () -> Unit,
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
            .semantics { contentDescription = "Pantalla de configuracion del negocio" }
            .testTag(BusinessConfigTestTags.Screen)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            BusinessConfigTopBar(onCancel = onCancel)

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
                    .padding(top = 24.dp, bottom = 28.dp),
                verticalArrangement = Arrangement.spacedBy(22.dp)
            ) {
                IntroCard()

                Text(
                    text = "Información del negocio",
                    color = AquaLv5,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                FormLabel("Nombre del negocio *")
                BusinessNameField(
                    value = state.businessName,
                    onValueChange = onNameChange,
                    enabled = !state.isSaving
                )
                Text(
                    text = "Este nombre se mostrará en toda la aplicación.",
                    color = AquaDeep.copy(alpha = 0.72f),
                    style = MaterialTheme.typography.bodyMedium
                )

                state.validationMessage?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.testTag(BusinessConfigTestTags.Validation)
                    )
                }

                state.errorMessage?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.testTag(BusinessConfigTestTags.Error)
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = onSave,
                    enabled = !state.isSaving && !state.isInvalidLocalData,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AquaLv3,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .testTag(BusinessConfigTestTags.SaveButton)
                ) {
                    SaveGlyph(color = Color.White, modifier = Modifier.size(26.dp))
                    Text(
                        text = if (state.isSaving) "Guardando" else "Guardar cambios",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 14.dp)
                    )
                }

                if (state.errorMessage != null || state.isInvalidLocalData) {
                    TextButton(
                        onClick = onRetry,
                        enabled = !state.isSaving,
                        modifier = Modifier.testTag(BusinessConfigTestTags.RetryButton)
                    ) {
                        Text("Reintentar")
                    }
                }
            }
        }
    }
}

@Composable
private fun BusinessConfigTopBar(onCancel: () -> Unit) {
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
                .clickable(onClick = onCancel),
            contentAlignment = Alignment.Center
        ) {
            BackGlyph(color = Color.White, modifier = Modifier.size(34.dp))
        }
        Text(
            text = "Configuración",
            color = Color.White,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 26.dp)
        )
    }
}

@Composable
private fun IntroCard() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White.copy(alpha = 0.92f),
        tonalElevation = 0.dp,
        shadowElevation = 5.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(68.dp),
                shape = CircleShape,
                color = AquaChip.copy(alpha = 0.76f)
            ) {
                GearGlyph(
                    color = AquaLv5,
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxSize()
                )
            }
            Column(modifier = Modifier.padding(start = 18.dp)) {
                Text(
                    text = "Ajustes de la aplicación",
                    color = AquaDeep,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Personaliza la información de tu negocio.",
                    color = AquaDeep.copy(alpha = 0.72f),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
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
private fun BusinessNameField(
    value: String,
    onValueChange: (String) -> Unit,
    enabled: Boolean
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .testTag(BusinessConfigTestTags.NameInput),
        placeholder = { Text("Mi Negocio", color = AquaDeep.copy(alpha = 0.42f)) },
        singleLine = true,
        enabled = enabled,
        leadingIcon = { FieldIconBubble() }
    )
}

@Composable
private fun FieldIconBubble() {
    Surface(
        modifier = Modifier.size(38.dp),
        shape = CircleShape,
        color = AquaChip.copy(alpha = 0.68f)
    ) {
        StoreGlyph(
            color = AquaLv5,
            modifier = Modifier
                .padding(9.dp)
                .fillMaxSize()
        )
    }
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
private fun GearGlyph(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        drawCircle(color = color, radius = size.minDimension * 0.18f, style = Stroke(width = size.minDimension * 0.1f))
        repeat(8) { index ->
            val angle = Math.toRadians((index * 45).toDouble())
            val inner = Offset(
                x = center.x + kotlin.math.cos(angle).toFloat() * size.minDimension * 0.28f,
                y = center.y + kotlin.math.sin(angle).toFloat() * size.minDimension * 0.28f
            )
            val outer = Offset(
                x = center.x + kotlin.math.cos(angle).toFloat() * size.minDimension * 0.42f,
                y = center.y + kotlin.math.sin(angle).toFloat() * size.minDimension * 0.42f
            )
            drawLine(color, inner, outer, strokeWidth = size.minDimension * 0.08f, cap = androidx.compose.ui.graphics.StrokeCap.Round)
        }
    }
}

@Composable
private fun StoreGlyph(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val stroke = Stroke(width = size.width * 0.1f, cap = androidx.compose.ui.graphics.StrokeCap.Round)
        val p = Path().apply {
            moveTo(size.width * 0.18f, size.height * 0.38f)
            lineTo(size.width * 0.82f, size.height * 0.38f)
            lineTo(size.width * 0.74f, size.height * 0.18f)
            lineTo(size.width * 0.26f, size.height * 0.18f)
            close()
        }
        drawPath(p, color, style = stroke)
        drawRoundRect(
            color = color,
            topLeft = Offset(size.width * 0.24f, size.height * 0.38f),
            size = Size(size.width * 0.52f, size.height * 0.46f),
            cornerRadius = CornerRadius(size.width * 0.04f),
            style = stroke
        )
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
