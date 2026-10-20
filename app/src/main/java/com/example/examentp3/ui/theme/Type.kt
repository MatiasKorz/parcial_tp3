package com.example.examentp3.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.examentp3.R

val Manrope = FontFamily(
    Font(R.font.manrope_regular, FontWeight.Normal),
    Font(R.font.manrope_medium, FontWeight.Medium),
    Font(R.font.manrope_semibold, FontWeight.SemiBold),
    Font(R.font.manrope_bold, FontWeight.Bold),
)

private val base = Typography()

val Typography = Typography(
    // Slots the design does not define keep Material's size, but use Manrope.
    displayLarge = base.displayLarge.copy(fontFamily = Manrope),
    displayMedium = base.displayMedium.copy(fontFamily = Manrope),
    displaySmall = base.displaySmall.copy(fontFamily = Manrope),
    headlineLarge = base.headlineLarge.copy(fontFamily = Manrope),
    headlineMedium = base.headlineMedium.copy(fontFamily = Manrope),
    titleSmall = base.titleSmall.copy(fontFamily = Manrope),
    bodyLarge = base.bodyLarge.copy(fontFamily = Manrope),

    // Design levels (Figma name -> Material slot; the mapping is a proposal)
    headlineSmall = TextStyle(fontFamily = Manrope, fontWeight = FontWeight.SemiBold, fontSize = 24.sp), // highlighted figure
    titleLarge = TextStyle(fontFamily = Manrope, fontWeight = FontWeight.Bold, fontSize = 20.sp),       // screen title
    titleMedium = TextStyle(fontFamily = Manrope, fontWeight = FontWeight.SemiBold, fontSize = 18.sp),   // card title
    labelLarge = TextStyle(fontFamily = Manrope, fontWeight = FontWeight.SemiBold, fontSize = 14.sp),    // primary action
    labelMedium = TextStyle(fontFamily = Manrope, fontWeight = FontWeight.SemiBold, fontSize = 13.sp),   // contextual action
    bodyMedium = TextStyle(fontFamily = Manrope, fontWeight = FontWeight.Normal, fontSize = 13.sp, lineHeight = 18.sp), // body
    labelSmall = TextStyle(fontFamily = Manrope, fontWeight = FontWeight.SemiBold, fontSize = 10.sp),    // label
    bodySmall = TextStyle(fontFamily = Manrope, fontWeight = FontWeight.Normal, fontSize = 11.sp),       // time
)