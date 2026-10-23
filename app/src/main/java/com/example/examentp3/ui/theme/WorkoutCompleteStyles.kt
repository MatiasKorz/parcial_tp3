package com.example.examentp3.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Frame 1:673 variants, without modifying BASE-01.
internal val Typography.workoutCompleteLabel
    get() = labelLarge.copy(fontWeight = FontWeight.Bold, lineHeight = 19.sp)
internal val Typography.workoutCompleteTitle
    get() = headlineSmall.copy(lineHeight = 33.sp)
internal val Typography.workoutCompleteMessage
    get() = bodyMedium.copy(fontSize = 14.sp, lineHeight = 19.sp)
internal val Typography.workoutCompleteMetric
    get() = titleMedium.copy(fontWeight = FontWeight.Bold, lineHeight = 25.sp)
internal val Typography.workoutCompleteMetricLabel
    get() = bodySmall.copy(lineHeight = 15.sp)
internal val Typography.workoutCompleteInsightLabel
    get() = labelMedium.copy(fontSize = 12.sp, fontWeight = FontWeight.Bold, lineHeight = 16.sp)
internal val Typography.workoutCompleteRating
    get() = labelMedium.copy(lineHeight = 18.sp)
internal val Typography.workoutCompleteAction
    get() = bodyLarge.copy(fontWeight = FontWeight.SemiBold, lineHeight = 20.sp)
