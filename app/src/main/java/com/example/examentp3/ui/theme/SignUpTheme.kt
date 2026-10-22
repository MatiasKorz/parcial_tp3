package com.example.examentp3.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/** Overrides are limited to SignUp; BASE-01 and SignIn keep their existing tokens. */
@Composable
internal fun SignUpTheme(content: @Composable () -> Unit) {
    val colors = MaterialTheme.colorScheme.copy(
        primary = Color(0xFF0E382F),
        onPrimary = Color.White,
        secondary = Color(0xFF34D399),
        onSecondary = Color(0xFF065F46),
        tertiary = Color(0xFFCCE65E),
        onTertiary = Color(0xFF0E382F),
        background = Color(0xFFF3F9F5),
        onBackground = Color(0xFF0E382F),
        surface = Color.White,
        onSurface = Color(0xFF0E382F),
        onSurfaceVariant = Color(0xFF6B8780),
        surfaceVariant = Color(0xFFDCF5E9),
        outline = Color(0xFFDCF5E9),
        outlineVariant = Color(0xFF6B7280),
    )
    MaterialTheme(
        colorScheme = colors,
        typography = MaterialTheme.typography,
        shapes = MaterialTheme.shapes.copy(medium = RoundedCornerShape(24.dp)),
        content = content,
    )
}
