package com.example.examentp3.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Frame 1:510 variants; BASE-01 and other destinations keep their existing values.
internal val Typography.preWorkoutTitle
    get() = headlineSmall.copy(fontSize = 26.sp, lineHeight = 36.sp)
internal val Typography.preWorkoutSummary
    get() = bodyMedium.copy(fontSize = 14.sp, lineHeight = 19.sp)
internal val Typography.preWorkoutCardLabel
    get() = labelMedium.copy(fontSize = 12.sp, fontWeight = FontWeight.Bold, lineHeight = 16.sp)
internal val Typography.preWorkoutFocusTitle
    get() = bodyLarge.copy(fontWeight = FontWeight.SemiBold, lineHeight = 20.sp)
internal val Typography.preWorkoutSessionTitle
    get() = labelLarge.copy(lineHeight = 19.sp)
internal val Typography.preWorkoutRecommendationTitle
    get() = labelMedium.copy(lineHeight = 18.sp)
internal val Typography.preWorkoutRecommendationBody
    get() = bodyMedium.copy(fontSize = 12.sp, lineHeight = 16.sp)
internal val Typography.preWorkoutStart
    get() = titleSmall.copy(fontSize = 16.sp, lineHeight = 22.sp)
internal val ColorScheme.preWorkoutCardBackground
    get() = onPrimary.copy(alpha = 0.04f)
internal val ColorScheme.preWorkoutCardBorder
    get() = onPrimary.copy(alpha = 0.12f)
internal val ColorScheme.preWorkoutRecommendationBackground
    get() = onPrimary.copy(alpha = 0.08f)
internal val ColorScheme.preWorkoutSecondaryText
    get() = onPrimary.copy(alpha = 0.7f)
