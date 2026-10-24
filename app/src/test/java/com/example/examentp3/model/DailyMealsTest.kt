package com.example.examentp3.model

import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DailyMealsTest {

    private val date = LocalDate.of(2026, 10, 23)

    private fun meal(type: MealType, calories: Int, protein: Double = 0.0, carbs: Double = 0.0, fat: Double = 0.0) =
        Meal(
            name = type.name,
            date = date,
            type = type,
            calories = calories,
            proteinGrams = protein,
            carbsGrams = carbs,
            fatGrams = fat,
        )

    @Test
    fun toDailyTotals_sumsCaloriesAndMacros() {
        val totals = listOf(
            meal(MealType.BREAKFAST, 420, protein = 20.0, carbs = 50.0, fat = 10.0),
            meal(MealType.LUNCH, 520, protein = 38.0, carbs = 54.0, fat = 14.5),
        ).toDailyTotals()

        assertEquals(DailyTotals(calories = 940, proteinGrams = 58.0, carbsGrams = 104.0, fatGrams = 24.5), totals)
    }

    @Test
    fun toDailyTotals_emptyList_isZero() {
        assertEquals(DailyTotals(), emptyList<Meal>().toDailyTotals())
    }

    @Test
    fun toMealSections_returnsFourTypesInFigmaOrder() {
        val sections = listOf(meal(MealType.DINNER, 300), meal(MealType.BREAKFAST, 420)).toMealSections()

        assertEquals(
            listOf(MealType.BREAKFAST, MealType.LUNCH, MealType.SNACK, MealType.DINNER),
            sections.map { it.type },
        )
    }

    @Test
    fun toMealSections_keepsEmptyTypes() {
        val sections = emptyList<Meal>().toMealSections()

        assertEquals(4, sections.size)
        assertTrue(sections.all { it.meals.isEmpty() && it.calories == 0 })
    }

    @Test
    fun toMealSections_sectionCaloriesIsSumOfItsMeals() {
        val sections = listOf(
            meal(MealType.LUNCH, 280),
            meal(MealType.LUNCH, 190),
            meal(MealType.SNACK, 240),
        ).toMealSections()

        assertEquals(470, sections.first { it.type == MealType.LUNCH }.calories)
        assertEquals(240, sections.first { it.type == MealType.SNACK }.calories)
    }
}
