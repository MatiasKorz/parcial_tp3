package com.example.examentp3.model

/** One ingredient detected by the (simulated) food analysis. */
data class EstimatedIngredient(
    val id: Int,
    val name: String,
    val grams: Int,
    val calories: Int,
)

/** Result of the food analysis: detected ingredients and the totals of the whole meal. */
data class MealEstimate(
    val ingredients: List<EstimatedIngredient>,
    val calories: Int,
    val proteinGrams: Double,
    val carbsGrams: Double,
    val fatGrams: Double,
)

// Values from the Figma frame "Nutrition / 07 — Food Confirmation" (simulated analysis, SPEC p.10).
// The frame gives no macros per ingredient, so the totals are stored as drawn.
val DemoMealEstimate = MealEstimate(
    ingredients = listOf(
        EstimatedIngredient(id = 1, name = "Grilled Chicken Breast", grams = 180, calories = 280),
        EstimatedIngredient(id = 2, name = "Basmati Rice", grams = 150, calories = 190),
        EstimatedIngredient(id = 3, name = "Mixed Steamed Vegetables", grams = 100, calories = 50),
    ),
    calories = 520,
    proteinGrams = 38.0,
    carbsGrams = 54.0,
    fatGrams = 14.0,
)
