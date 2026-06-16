package com.famessa.deli_antojito.feature.home

import android.annotation.SuppressLint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.famessa.deli_antojito.core.ui.theme.AquaChip
import com.famessa.deli_antojito.core.ui.theme.AquaDeep
import com.famessa.deli_antojito.core.ui.theme.AquaLv1
import com.famessa.deli_antojito.core.ui.theme.AquaLv2
import com.famessa.deli_antojito.core.ui.theme.AquaLv3
import com.famessa.deli_antojito.core.ui.theme.AquaLv5
import com.famessa.deli_antojito.core.ui.theme.AquaMist
import com.famessa.deli_antojito.core.ui.theme.AquaSurface

@Composable
fun HomeView(
    onAdminClick: () -> Unit = {},
    onProductClick: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel()
) {
    val businessName by viewModel.businessName.collectAsState()

    HomeContent(
        businessName = businessName,
        onAdminClick = onAdminClick,
        onProductClick = onProductClick
    )
}

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun HomeContent(
    businessName: String,
    onAdminClick: () -> Unit = {},
    onProductClick: () -> Unit = {}
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
                    Image(
                        painterResource(id = com.famessa.deli_antojito.core.ui.R.mipmap.ic_launcher_round),
                        contentDescription = null,
                        Modifier.size(128.dp),
                    )

                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = displayName,
                        color = AquaLv5,
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.testTag("home_business_name")
                    )

                    Spacer(modifier = Modifier.height(34.dp))
                    Text(
                        text = "¡Bienvenido!",
                        color = AquaLv5,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(34.dp))
                    ModuleGrid(
                        cardSpacing = cardSpacing,
                        onAdminClick = onAdminClick,
                        onProductClick = onProductClick
                    )
                }
            }
        }
    }
}
/*
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
*/
@Composable
private fun ModuleGrid(
    cardSpacing: androidx.compose.ui.unit.Dp,
    onAdminClick: () -> Unit,
    onProductClick: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(cardSpacing)) {
        Row(horizontalArrangement = Arrangement.spacedBy(cardSpacing)) {
            ModuleCard(
                title = "Pedidos",
                description = "Administra tus pedidos",
                icon = ModuleIconType.Orders,
                onClick = null,
                modifier = Modifier.weight(1f)
            )
            ModuleCard(
                title = "Productos",
                description = "Administra tus productos",
                icon = ModuleIconType.Products,
                onClick = onProductClick,
                modifier = Modifier
                    .weight(1f)
                    .testTag("home_products_button")
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(cardSpacing)) {
            ModuleCard(
                title = "Cierre de venta",
                description = "Finaliza el dia de ventas",
                icon = ModuleIconType.CloseSale,
                onClick = null,
                modifier = Modifier.weight(1f)
            )
            ModuleCard(
                title = "Configuración",
                description = "Configura tu negocio",
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
        .height(180.dp)
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
                when(icon){
                    ModuleIconType.Orders -> Icon(painterResource(id = com.famessa.deli_antojito.core.ui.R.drawable.ic_orders), contentDescription = null, Modifier.size(70.dp), tint = AquaLv3.copy())
                    ModuleIconType.Products -> Icon(painterResource(id = com.famessa.deli_antojito.core.ui.R.drawable.ic_products), contentDescription = null, Modifier.size(70.dp), tint = AquaLv3.copy())
                    ModuleIconType.CloseSale -> Icon(painterResource(id = com.famessa.deli_antojito.core.ui.R.drawable.ic_batch_close), contentDescription = null, Modifier.size(70.dp), tint = AquaLv3.copy())
                    ModuleIconType.Settings -> Icon(painterResource(id = com.famessa.deli_antojito.core.ui.R.drawable.ic_settings), contentDescription = null, Modifier.size(70.dp), tint = AquaLv3.copy())
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
