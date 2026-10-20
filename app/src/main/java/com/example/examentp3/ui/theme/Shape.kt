package com.example.examentp3.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val VoraShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),   // fields and messages
    medium = RoundedCornerShape(16.dp),  // cards
    large = RoundedCornerShape(20.dp),   // featured panels
    extraLarge = RoundedCornerShape(24.dp),
)

/** Pill shape (100 in Figma). */
val VoraPillShape = RoundedCornerShape(100.dp)

/** Radius seen in the design that has no Material slot. */
val VoraRadius18 = RoundedCornerShape(18.dp)