package com.example.examentp3.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Scoped derivations preserve BASE-01 and all other screens.
internal val Typography.weeklyPlanTitle
    get() = headlineSmall.copy(lineHeight = 33.sp)
internal val Typography.weeklyPlanSectionLabel
    get() = labelSmall.copy(fontWeight = FontWeight.Normal, lineHeight = 15.sp)
internal val Typography.weeklyPlanDayLabel
    get() = labelSmall.copy(fontWeight = FontWeight.Normal, lineHeight = 14.sp)
internal val Typography.weeklyPlanDayNumber
    get() = labelLarge.copy(lineHeight = 20.sp)
internal val Typography.weeklyPlanTodayTitle
    get() = titleMedium.copy(fontSize = 16.sp, lineHeight = 22.sp)
internal val Typography.weeklyPlanWorkoutTitle
    get() = bodyLarge.copy(fontWeight = FontWeight.SemiBold, lineHeight = 20.sp)
internal val Typography.weeklyPlanPrompt
    get() = bodyMedium.copy(fontSize = 12.sp, lineHeight = 16.sp)
internal val ColorScheme.weeklyPlanCoachText
    get() = onPrimary.copy(alpha = 0.7f)
internal val ColorScheme.weeklyPlanPromptBackground
    get() = onPrimary.copy(alpha = 0.08f)
