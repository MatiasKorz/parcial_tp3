package com.example.examentp3.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Frame 1:628 typography variants, scoped to the demonstration guide.
internal val Typography.exerciseDetailsTitle
    get() = headlineMedium.copy(lineHeight = 30.sp)
internal val Typography.exerciseDetailsHeader
    get() = titleMedium.copy(fontSize = 16.sp, lineHeight = 22.sp)
internal val Typography.exerciseDetailsSectionTitle
    get() = labelLarge.copy(fontWeight = FontWeight.Bold, lineHeight = 19.sp)
internal val Typography.exerciseDetailsCueLabel
    get() = labelMedium.copy(fontSize = 12.sp, fontWeight = FontWeight.Bold, lineHeight = 16.sp)
internal val Typography.exerciseDetailsAction
    get() = bodyLarge.copy(fontWeight = FontWeight.SemiBold, lineHeight = 20.sp)
