package com.example.examentp3.model

/** Options of "Dietary Preference". Only one can be picked. */
enum class DietaryPreference { Everything, Vegetarian, Vegan, Pescatarian, Other }

/** Options of "Foods You Enjoy". Several can be picked (the Figma frame marks all eight). */
enum class EnjoyedFood { Chicken, Eggs, Fish, Rice, Pasta, GreekYoghurt, Vegetables, Fruit }

/** Options of "What matters most to you?". Several can be picked (both are marked in Figma). */
enum class NutritionPriority { HitProteinTargets, EatMoreConsistently }

/**
 * What the user picked on Profile / 04 — Nutrition Preferences.
 * Not named NutritionPreferences: that name is already the AppDestination.
 */
data class NutritionSelection(
    val diet: DietaryPreference,
    val enjoyedFoods: Set<EnjoyedFood>,
    /** Free text typed by the user, kept in insertion order. */
    val avoidedFoods: List<String>,
    val priorities: Set<NutritionPriority>,
)

// Initial selection of the Figma frame "Profile / 04 — Nutrition Preferences".
val DemoNutritionSelection = NutritionSelection(
    diet = DietaryPreference.Everything,
    enjoyedFoods = EnjoyedFood.entries.toSet(),
    avoidedFoods = listOf("Peanuts", "Shellfish", "Mushrooms"),
    priorities = NutritionPriority.entries.toSet(),
)
