package com.example.examentp3.model

/** Daily total of RF04: calories plus macros, summed from the meals saved for one date. */
data class DailyTotals(
    val calories: Int = 0,
    val proteinGrams: Double = 0.0,
    val carbsGrams: Double = 0.0,
    val fatGrams: Double = 0.0,
)

fun List<Meal>.toDailyTotals(): DailyTotals = DailyTotals(
    calories = sumOf { it.calories },
    proteinGrams = sumOf { it.proteinGrams },
    carbsGrams = sumOf { it.carbsGrams },
    fatGrams = sumOf { it.fatGrams },
)

/** Meals of one [type] in a day; [calories] is the subtotal shown in the section header. */
data class MealSection(val type: MealType, val meals: List<Meal>) {
    val calories: Int get() = meals.sumOf { it.calories }
}

/**
 * One section per [MealType] in enum order (the Figma order), empty types included, so the
 * screen can show "… is not logged yet" for them.
 */
fun List<Meal>.toMealSections(): List<MealSection> {
    val byType = groupBy { it.type }
    return MealType.entries.map { type -> MealSection(type, byType[type].orEmpty()) }
}
