package com.famessa.deli_antojito.feature.home

import android.annotation.SuppressLint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.famessa.deli_antojito.core.ui.theme.AquaAccent
import com.famessa.deli_antojito.core.ui.theme.AquaChip
import com.famessa.deli_antojito.core.ui.theme.AquaDeep
import com.famessa.deli_antojito.core.ui.theme.AquaLv1
import com.famessa.deli_antojito.core.ui.theme.AquaLv2
import com.famessa.deli_antojito.core.ui.theme.AquaLv3
import com.famessa.deli_antojito.core.ui.theme.AquaLv4
import com.famessa.deli_antojito.core.ui.theme.AquaLv5
import com.famessa.deli_antojito.core.ui.theme.AquaMist
import com.famessa.deli_antojito.core.ui.theme.AquaSurface

@Composable
fun HomeView(
    onAdminClick: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel()
) {
    val businessName by viewModel.businessName.collectAsState()

    HomeContent(
        businessName = businessName,
        onAdminClick = onAdminClick
    )
}

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun HomeContent(
    businessName: String,
    onAdminClick: () -> Unit = {}
) {
    val displayName = businessName.ifBlank {
        "Deli Antojito"
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(AquaMist, Color.White, AquaSurface)
                )
            )
            .testTag("home_screen")
    ) {
        WaveBackground(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(230.dp)
        )

        Column(modifier = Modifier.fillMaxSize()) {
            //HomeTopBar()

            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp)
            ) {
                val cardSpacing = if (maxWidth < 380.dp) 16.dp else 20.dp

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 34.dp, bottom = 196.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    FoodLogo(modifier = Modifier.size(128.dp))
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = displayName,
                        color = AquaLv5,
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.testTag("home_business_name")
                    )
                    /*Text(
                        text = "Gestión inteligente, producción eficiente.",
                        color = AquaDeep,
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 6.dp)
                    )*/

                    Spacer(modifier = Modifier.height(34.dp))
                    Text(
                        text = "¡Bienvenido!",
                        color = AquaLv5,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    /*Text(
                        text = "Administra tu negocio de forma fácil y eficiente\n desde un solo lugar.",
                        color = AquaDeep.copy(alpha = 0.86f),
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 10.dp)
                    )*/

                    Spacer(modifier = Modifier.height(34.dp))
                    ModuleGrid(
                        cardSpacing = cardSpacing,
                        onAdminClick = onAdminClick
                    )
                }
            }
        }
    }
}

@Composable
private fun HomeTopBar() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(94.dp)
            .background(
                Brush.horizontalGradient(
                    colors = listOf(AquaDeep, AquaLv4, AquaAccent)
                )
            )
            .padding(start = 22.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        MenuGlyph(color = Color.White, modifier = Modifier.size(34.dp))
    }
}

@Composable
private fun ModuleGrid(
    cardSpacing: androidx.compose.ui.unit.Dp,
    onAdminClick: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(cardSpacing)) {
        Row(horizontalArrangement = Arrangement.spacedBy(cardSpacing)) {
            ModuleCard(
                title = "Pedidos",
                description = "",
                icon = ModuleIconType.Orders,
                onClick = null,
                modifier = Modifier.weight(1f)
            )
            ModuleCard(
                title = "Productos",
                description = "",
                icon = ModuleIconType.Products,
                onClick = null,
                modifier = Modifier.weight(1f)
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(cardSpacing)) {
            ModuleCard(
                title = "Cierre de venta",
                description = "",
                icon = ModuleIconType.CloseSale,
                onClick = null,
                modifier = Modifier.weight(1f)
            )
            ModuleCard(
                title = "Configuración",
                description = "",
                icon = ModuleIconType.Settings,
                onClick = onAdminClick,
                modifier = Modifier
                    .weight(1f)
                    .testTag("home_business_config_button")
            )
        }
    }
}

@Composable
private fun ModuleCard(
    title: String,
    description: String,
    icon: ModuleIconType,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(20.dp)
    val surfaceModifier = modifier
        .height(166.dp)
        .shadow(
            elevation = 7.dp,
            shape = shape,
            ambientColor = AquaDeep.copy(alpha = 0.16f),
            spotColor = AquaDeep.copy(alpha = 0.18f)
        )

    val content: @Composable () -> Unit = {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp, vertical = 18.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Surface(
                    modifier = Modifier.size(70.dp),
                    shape = CircleShape,
                    color = AquaChip.copy(alpha = 0.78f)
                ) {
                    ModuleIcon(
                        type = icon,
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxSize()
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = title,
                    color = AquaLv5,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    maxLines = 1
                )
                Text(
                    text = description,
                    color = AquaDeep.copy(alpha = 0.82f),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 6.dp)
                )
            }
            ChevronGlyph(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 14.dp)
                    .size(24.dp),
                color = AquaLv5.copy(alpha = if (onClick == null) 0.52f else 1f)
            )
        }
    }

    if (onClick == null) {
        Surface(
            modifier = surfaceModifier,
            shape = shape,
            color = Color.White.copy(alpha = 0.92f),
            content = content
        )
    } else {
        Surface(
            onClick = onClick,
            modifier = surfaceModifier,
            shape = shape,
            color = Color.White.copy(alpha = 0.94f),
            content = content
        )
    }
}

private enum class ModuleIconType {
    Orders,
    Products,
    CloseSale,
    Settings
}

@Composable
private fun FoodLogo(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val stroke = Stroke(width = size.minDimension * 0.025f, cap = StrokeCap.Round)
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(AquaLv2, AquaLv3),
                center = center,
                radius = size.minDimension * 0.58f
            ),
            radius = size.minDimension * 0.48f
        )
        drawCircle(
            color = Color.White.copy(alpha = 0.9f),
            radius = size.minDimension * 0.49f,
            style = Stroke(width = size.minDimension * 0.018f)
        )

        val taco = Path().apply {
            moveTo(size.width * 0.20f, size.height * 0.58f)
            quadraticTo(size.width * 0.30f, size.height * 0.34f, size.width * 0.50f, size.height * 0.42f)
            quadraticTo(size.width * 0.35f, size.height * 0.56f, size.width * 0.20f, size.height * 0.58f)
        }
        drawPath(taco, Color.White.copy(alpha = 0.9f), style = stroke)
        drawLine(AquaLv5, Offset(size.width * 0.27f, size.height * 0.52f), Offset(size.width * 0.43f, size.height * 0.48f), strokeWidth = stroke.width)

        repeat(5) { index ->
            val x = size.width * (0.45f + index * 0.04f)
            drawLine(
                color = Color.White.copy(alpha = 0.92f),
                start = Offset(x, size.height * 0.58f),
                end = Offset(x + size.width * 0.03f, size.height * 0.32f),
                strokeWidth = stroke.width * 0.82f,
                cap = StrokeCap.Round
            )
        }

        drawRoundRect(
            color = Color.White.copy(alpha = 0.88f),
            topLeft = Offset(size.width * 0.42f, size.height * 0.48f),
            size = Size(size.width * 0.18f, size.height * 0.23f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(size.width * 0.02f)
        )
        drawLine(AquaLv5, Offset(size.width * 0.46f, size.height * 0.52f), Offset(size.width * 0.56f, size.height * 0.52f), strokeWidth = stroke.width)

        val chili = Path().apply {
            moveTo(size.width * 0.64f, size.height * 0.62f)
            cubicTo(size.width * 0.78f, size.height * 0.34f, size.width * 0.95f, size.height * 0.5f, size.width * 0.72f, size.height * 0.72f)
            cubicTo(size.width * 0.66f, size.height * 0.73f, size.width * 0.62f, size.height * 0.69f, size.width * 0.64f, size.height * 0.62f)
        }
        drawPath(chili, Color.White.copy(alpha = 0.9f), style = stroke)
    }
}

@Composable
private fun MenuGlyph(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        repeat(3) { index ->
            val y = size.height * (0.24f + index * 0.26f)
            drawLine(
                color = color,
                start = Offset(size.width * 0.08f, y),
                end = Offset(size.width * 0.92f, y),
                strokeWidth = size.height * 0.1f,
                cap = StrokeCap.Round
            )
        }
    }
}

@Composable
private fun ChevronGlyph(modifier: Modifier = Modifier, color: Color) {
    Canvas(modifier = modifier) {
        drawLine(
            color = color,
            start = Offset(size.width * 0.32f, size.height * 0.18f),
            end = Offset(size.width * 0.68f, size.height * 0.50f),
            strokeWidth = size.width * 0.14f,
            cap = StrokeCap.Round
        )
        drawLine(
            color = color,
            start = Offset(size.width * 0.68f, size.height * 0.50f),
            end = Offset(size.width * 0.32f, size.height * 0.82f),
            strokeWidth = size.width * 0.14f,
            cap = StrokeCap.Round
        )
    }
}

@Composable
private fun ModuleIcon(
    type: ModuleIconType,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val stroke = Stroke(width = size.minDimension * 0.09f, cap = StrokeCap.Round)
        when (type) {
            ModuleIconType.Orders -> {
                drawRoundRect(
                    color = AquaLv5,
                    topLeft = Offset(size.width * 0.18f, size.height * 0.16f),
                    size = Size(size.width * 0.64f, size.height * 0.72f),
                    style = stroke
                )
                drawRoundRect(
                    color = AquaLv5,
                    topLeft = Offset(size.width * 0.38f, size.height * 0.04f),
                    size = Size(size.width * 0.24f, size.height * 0.2f),
                    style = stroke
                )
                listOf(0.38f, 0.54f, 0.70f).forEach { y ->
                    drawLine(AquaLv5, Offset(size.width * 0.42f, size.height * y), Offset(size.width * 0.68f, size.height * y), strokeWidth = stroke.width, cap = StrokeCap.Round)
                    drawLine(AquaLv5, Offset(size.width * 0.27f, size.height * (y - 0.02f)), Offset(size.width * 0.32f, size.height * (y + 0.03f)), strokeWidth = stroke.width * 0.72f, cap = StrokeCap.Round)
                }
            }
            ModuleIconType.Products -> {
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
                drawLine(AquaLv5, Offset(size.width * 0.5f, size.height * 0.5f), Offset(size.width * 0.5f, size.height * 0.94f), strokeWidth = stroke.width, cap = StrokeCap.Round)
                drawLine(AquaLv5, Offset(size.width * 0.1f, size.height * 0.3f), Offset(size.width * 0.5f, size.height * 0.5f), strokeWidth = stroke.width, cap = StrokeCap.Round)
                drawLine(AquaLv5, Offset(size.width * 0.9f, size.height * 0.3f), Offset(size.width * 0.5f, size.height * 0.5f), strokeWidth = stroke.width, cap = StrokeCap.Round)
            }
            ModuleIconType.CloseSale -> {
                drawRoundRect(
                    color = AquaLv5,
                    topLeft = Offset(size.width * 0.16f, size.height * 0.34f),
                    size = Size(size.width * 0.68f, size.height * 0.5f),
                    style = stroke
                )
                drawLine(AquaLv5, Offset(size.width * 0.12f, size.height * 0.26f), Offset(size.width * 0.88f, size.height * 0.26f), strokeWidth = stroke.width, cap = StrokeCap.Round)
                drawLine(AquaLv5, Offset(size.width * 0.43f, size.height * 0.34f), Offset(size.width * 0.43f, size.height * 0.55f), strokeWidth = stroke.width, cap = StrokeCap.Round)
                drawLine(AquaLv5, Offset(size.width * 0.57f, size.height * 0.34f), Offset(size.width * 0.57f, size.height * 0.55f), strokeWidth = stroke.width, cap = StrokeCap.Round)
            }
            ModuleIconType.Settings -> {
                drawCircle(AquaLv5, radius = size.minDimension * 0.18f, style = stroke)
                repeat(8) { index ->
                    val angle = Math.toRadians((index * 45).toDouble())
                    val inner = Offset(
                        x = center.x + kotlin.math.cos(angle).toFloat() * size.minDimension * 0.30f,
                        y = center.y + kotlin.math.sin(angle).toFloat() * size.minDimension * 0.30f
                    )
                    val outer = Offset(
                        x = center.x + kotlin.math.cos(angle).toFloat() * size.minDimension * 0.43f,
                        y = center.y + kotlin.math.sin(angle).toFloat() * size.minDimension * 0.43f
                    )
                    drawLine(AquaLv5, inner, outer, strokeWidth = stroke.width, cap = StrokeCap.Round)
                }
            }
        }
    }
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

        drawPath(wavePath(0.18f, 0.32f, 0.7f), AquaChip.copy(alpha = 0.58f))
        drawPath(wavePath(0.46f, 0.86f, 0.34f), AquaLv1.copy(alpha = 0.78f))
        drawPath(wavePath(0.74f, 1.04f, 0.58f), AquaLv3.copy(alpha = 0.88f))
    }
}
