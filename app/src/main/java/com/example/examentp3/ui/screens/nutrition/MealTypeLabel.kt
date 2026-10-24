package com.example.examentp3.ui.screens.nutrition

import androidx.annotation.StringRes
import com.example.examentp3.R
import com.example.examentp3.model.MealType

/** Visible name of each meal type, as written in the Figma Nutrition frames. Shared by all Nutrition screens. */
@StringRes
fun MealType.labelRes(): Int = when (this) {
    MealType.BREAKFAST -> R.string.meal_type_breakfast
    MealType.LUNCH -> R.string.meal_type_lunch
    MealType.SNACK -> R.string.meal_type_snack
    MealType.DINNER -> R.string.meal_type_dinner
}
