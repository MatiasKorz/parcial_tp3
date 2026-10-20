package com.example.examentp3.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color

private val VoraColorScheme = lightColorScheme(
    primary = VoraGreen,
    onPrimary = Color.White,        // not specified in Figma
    secondary = VoraMint,
    onSecondary = VoraGreen,        // Figma: green text on mint
    tertiary = VoraLime,
    onTertiary = VoraGreen,         // Figma: green text on lime
    background = VoraBackground,
    onBackground = VoraTextPrimary,
    surface = VoraSurface,
    onSurface = VoraTextPrimary,
    onSurfaceVariant = VoraTextSecondary,
    outline = VoraBorder,
    error = VoraAttention,
    onError = Color.White,          // not specified in Figma
)

@Composable
fun ExamenTP3Theme(content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalVoraExtraColors provides VoraExtraColors()) {
        MaterialTheme(
            colorScheme = VoraColorScheme,
            typography = Typography,
            shapes = VoraShapes,
            content = content,
        )
    }
}