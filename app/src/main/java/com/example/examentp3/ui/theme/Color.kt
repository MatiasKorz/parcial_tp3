package com.example.examentp3.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// Brand
val VoraGreen = Color(0xFF123D35)
val VoraMint = Color(0xFF65D6B0)
val VoraLime = Color(0xFFC9E86A)

// Surfaces
val VoraSurface = Color(0xFFFFFFFF)
val VoraBackground = Color(0xFFF7F8F5)
val VoraBorder = Color(0xFFE4E9E6)

// Text
val VoraTextPrimary = Color(0xFF17211F)
val VoraTextSecondary = Color(0xFF68736F)
val VoraTextMuted = Color(0xFF9AA39F)

// State
val VoraAttention = Color(0xFFD95D5D)
val VoraSuccess = Color(0xFF27AE60)

// Figma writes this one as RGBA (#65D6B01F); Compose expects ARGB, so the alpha goes first.
val VoraMintSoft = Color(0x1F65D6B0)

/** Design-system colors that Material 3 has no slot for. */
@Immutable
data class VoraExtraColors(
    val textSecondary: Color = VoraTextSecondary,
    val textMuted: Color = VoraTextMuted,
    val success: Color = VoraSuccess,
    val mintSoft: Color = VoraMintSoft,
)

val LocalVoraExtraColors = staticCompositionLocalOf { VoraExtraColors() }

val MaterialTheme.voraColors: VoraExtraColors
    @Composable
    @ReadOnlyComposable
    get() = LocalVoraExtraColors.current