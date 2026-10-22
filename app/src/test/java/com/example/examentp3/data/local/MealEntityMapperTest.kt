package com.example.examentp3.data.local

import com.example.examentp3.model.Meal
import com.example.examentp3.model.MealType
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Test

class MealEntityMapperTest {

    private val meal = Meal(
        id = 7,
        name = "Chicken · rice · vegetables",
        date = LocalDate.of(2026, 10, 21),
        type = MealType.LUNCH,
        calories = 520,
        proteinGrams = 38.0,
        carbsGrams = 54.0,
        fatGrams = 14.0,
    )

    @Test
    fun mealToEntity_storesDateAsEpochDay() {
        assertEquals(LocalDate.of(2026, 10, 21).toEpochDay(), meal.toEntity().dateEpochDay)
    }

    @Test
    fun mealToEntity_storesTypeByName() {
        assertEquals("LUNCH", meal.toEntity().mealType)
    }

    @Test
    fun entityToModel_roundTripKeepsAllFields() {
        assertEquals(meal, meal.toEntity().toModel())
    }
}
