package com.famessa.deli_antojito.core.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

/** Shared screen background matching the Products module. */
fun Modifier.aquaScreenBackground(): Modifier = background(
    Brush.verticalGradient(listOf(AquaMist, Color.White, AquaSurface))
)

/** Standard module header; optional navigation is omitted during mandatory initial setup. */
@Composable
fun AquaTopBar(
    title: String,
    onNavigate: (() -> Unit)?,
    navigationIcon: OrderIcon = OrderIcon.Back,
    navigationDescription: String = "Volver",
    navigationEnabled: Boolean = true,
    navigationModifier: Modifier = Modifier,
    actions: @Composable RowScope.() -> Unit = {}
) {
    Row(Modifier.fillMaxWidth().heightIn(min = 104.dp)
        .background(Brush.horizontalGradient(listOf(AquaDeep, AquaLv4, AquaAccent)))
        .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically) {
        if (onNavigate != null) {
            IconButton(onClick = onNavigate, enabled = navigationEnabled, modifier = navigationModifier.size(48.dp)) {
                OrderUiIcon(navigationIcon, navigationDescription, Modifier.size(34.dp),
                    tint = Color.White.copy(alpha = if (navigationEnabled) 1f else 0.5f))
            }
            Spacer(Modifier.width(24.dp))
        }
        Text(title, Modifier.weight(1f), color = Color.White, style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold, maxLines = 2, overflow = TextOverflow.Ellipsis)
        actions()
    }
}

/** Primary form action shared by Products, Orders and business configuration. */
@Composable
fun AquaSaveButton(label: String, enabled: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(onClick = onClick, enabled = enabled, modifier = modifier.fillMaxWidth().heightIn(min = 58.dp),
        shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = AquaLv3, contentColor = Color.White)) {
        OrderUiIcon(OrderIcon.Save, modifier = Modifier.size(26.dp), tint = LocalContentColor.current)
        Spacer(Modifier.width(14.dp))
        Text(label, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
    }
}
