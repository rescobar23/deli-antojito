package com.famessa.deli_antojito.feature.batchclosing.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.famessa.deli_antojito.core.ui.theme.*

@Composable
internal fun BatchSection(title: String, icon: OrderIcon, modifier: Modifier = Modifier,
    iconResource: Int = icon.resource,
    content: @Composable ColumnScope.() -> Unit) {
    Column(modifier.fillMaxWidth().background(Color.White).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(Modifier.size(40.dp).background(AquaMist, CircleShape), contentAlignment = Alignment.Center) {
                Icon(androidx.compose.ui.res.painterResource(iconResource), null, Modifier.size(24.dp), tint = AquaLv4)
            }
            Text(title, color = AquaLv4, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }
        content()
    }
}
