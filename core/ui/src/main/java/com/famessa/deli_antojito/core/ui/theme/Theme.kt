package com.famessa.deli_antojito.core.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val FamessaLightColorScheme = lightColorScheme(
    primary = AquaLv5,
    secondary = AquaLv4,
    tertiary = AquaLv3,
    background = AquaLv2,
    surface = AquaLv1
)

private val FamessaDarkColorScheme = darkColorScheme(
    primary = AquaLv1,
    secondary = AquaLv2,
    tertiary = AquaLv3,
    background = AquaLv4,
    surface = AquaLv5
)

@Composable
fun Deli_antojitoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> FamessaDarkColorScheme
        else -> FamessaLightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
