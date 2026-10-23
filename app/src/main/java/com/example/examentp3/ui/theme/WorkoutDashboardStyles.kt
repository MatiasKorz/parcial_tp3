package com.example.examentp3.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Typography
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight

// Screen-specific derivations from BASE-01; global tokens remain unchanged.
internal val ColorScheme.workoutModerateBackground: Color
    get() = onPrimary.copy(alpha = 0.12f)

internal val ColorScheme.workoutPanelSecondary: Color
    get() = onPrimary.copy(alpha = 0.7f)

internal val ColorScheme.workoutCoachingBorder: Color
    get() = secondary.copy(alpha = 0.35f)

internal val Typography.workoutModerateLabel: TextStyle
    get() = labelSmall.copy(fontWeight = FontWeight.Normal)
