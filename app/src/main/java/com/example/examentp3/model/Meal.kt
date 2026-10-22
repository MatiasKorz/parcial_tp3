package com.example.examentp3.model

import java.time.LocalDate

enum class MealType { BREAKFAST, LUNCH, SNACK, DINNER }

data class Meal(
    val id: Long = 0,
    val name: String,
    val date: LocalDate,
    val type: MealType,
    val calories: Int,
    val proteinGrams: Double,
    val carbsGrams: Double,
    val fatGrams: Double,
)
