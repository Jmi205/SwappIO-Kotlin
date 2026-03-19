package uniandes.isis3510.rewereable.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme

import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color


import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// 1. Esquema de colores para Modo Oscuro
private val DarkColorScheme = darkColorScheme(
    primary = Primary,
    background = BackgroundDark,
    surface = BackgroundDark,
    onPrimary = TextLight,
    onBackground = TextLight,
    onSurface = TextLight
)

// 2. Esquema de colores para Modo Claro
private val LightColorScheme = lightColorScheme(
    primary = Primary,
    background = BackgroundLight,
    surface = BackgroundLight,
    onPrimary = Color.White,
    onBackground = TextDark,
    onSurface = TextDark
)

// 3. La función principal del Tema
@Composable
fun ReWereableTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    // Esto cambia el color de la barra de estado superior del teléfono (la hora, la batería)
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography, // Conecta con Type.kt
        content = content
    )
}