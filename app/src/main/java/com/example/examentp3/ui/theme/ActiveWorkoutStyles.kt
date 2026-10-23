package com.example.examentp3.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

internal val Typography.activeWorkoutExercise
    get() = titleMedium.copy(lineHeight = 25.sp)
internal val Typography.activeWorkoutSmall
    get() = bodyMedium.copy(fontSize = 12.sp, lineHeight = 16.sp)
internal val Typography.activeWorkoutCaption
    get() = bodySmall.copy(lineHeight = 15.sp)
internal val Typography.activeWorkoutValue
    get() = headlineSmall.copy(fontSize = 28.sp, fontWeight = FontWeight.Bold, lineHeight = 38.sp)
internal val Typography.activeWorkoutWeight
    get() = titleMedium.copy(fontWeight = FontWeight.Bold, lineHeight = 25.sp)
internal val Typography.activeWorkoutBadge
    get() = bodySmall.copy(fontWeight = FontWeight.Bold, lineHeight = 15.sp)
internal val Typography.activeWorkoutRestAction
    get() = bodySmall.copy(fontWeight = FontWeight.SemiBold, lineHeight = 15.sp)
internal val Typography.activeWorkoutCoach
    get() = bodyMedium.copy(fontSize = 14.sp, lineHeight = 20.sp)
internal val Typography.activeWorkoutCta
    get() = titleSmall.copy(fontSize = 16.sp, lineHeight = 22.sp)
internal val ColorScheme.activeWorkoutPanel
    get() = onPrimary.copy(alpha = 0.05f)
internal val ColorScheme.activeWorkoutInactiveSet
    get() = onPrimary.copy(alpha = 0.02f)
internal val ColorScheme.activeWorkoutSelectedSet
    get() = onPrimary.copy(alpha = 0.1f)
internal val ColorScheme.activeWorkoutRestBorder
    get() = secondary.copy(alpha = 0.25f)
