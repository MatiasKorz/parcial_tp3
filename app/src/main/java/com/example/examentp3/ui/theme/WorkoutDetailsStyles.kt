package com.example.examentp3.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Exact sizes recovered from Workout Details; no global typography changes.
internal val Typography.workoutDetailsTitle
    get() = headlineSmall.copy(fontSize = 26.sp, lineHeight = 36.sp)
internal val Typography.workoutDetailsOverview
    get() = titleMedium.copy(fontSize = 16.sp, lineHeight = 22.sp)
internal val Typography.workoutDetailsNoteLabel
    get() = labelMedium.copy(fontSize = 12.sp, lineHeight = 16.sp)
internal val Typography.workoutDetailsNoteBody
    get() = bodyMedium.copy(lineHeight = 18.sp)
internal val Typography.workoutDetailsExercise
    get() = bodyLarge.copy(fontWeight = FontWeight.SemiBold, lineHeight = 20.sp)
internal val Typography.workoutDetailsPrescription
    get() = bodyMedium.copy(fontSize = 12.sp, lineHeight = 16.sp)
internal val Typography.workoutDetailsNumber
    get() = labelLarge.copy(fontWeight = FontWeight.Bold, lineHeight = 19.sp)
internal val Typography.workoutDetailsAction
    get() = bodyLarge.copy(fontWeight = FontWeight.SemiBold, lineHeight = 20.sp)
