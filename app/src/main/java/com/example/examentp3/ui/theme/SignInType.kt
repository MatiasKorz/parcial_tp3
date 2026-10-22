package com.example.examentp3.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp

/** Screen-specific styles derived from BASE-01 without changing the global typography. */
internal val Typography.signInSubtitle: TextStyle
    get() = labelSmall.copy(fontSize = 12.sp, letterSpacing = 1.sp)

internal val Typography.signInDescription: TextStyle
    get() = bodyMedium.copy(fontSize = 14.sp, lineHeight = 20.sp)
