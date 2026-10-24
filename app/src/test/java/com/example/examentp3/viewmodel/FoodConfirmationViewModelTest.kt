package com.example.examentp3.viewmodel

import com.example.examentp3.model.DemoMealEstimate
import com.example.examentp3.model.MealType
import org.junit.Assert.assertEquals
import org.junit.Test

class FoodConfirmationViewModelTest {

    @Test
    fun estimateMode_loadsFigmaEstimateForLunch() {
        val state = FoodConfirmationViewModel().uiState.value

        assertEquals(DemoMealEstimate, state.estimate)
        assertEquals(MealType.LUNCH, state.mealType)
    }

    @Test
    fun demoEstimate_matchesFigmaFrame() {
        assertEquals(
            listOf("Grilled Chicken Breast", "Basmati Rice", "Mixed Steamed Vegetables"),
            DemoMealEstimate.ingredients.map { it.name },
        )
        assertEquals(listOf(180, 150, 100), DemoMealEstimate.ingredients.map { it.grams })
        assertEquals(520, DemoMealEstimate.calories)
        assertEquals(38.0, DemoMealEstimate.proteinGrams, 0.0)
        assertEquals(54.0, DemoMealEstimate.carbsGrams, 0.0)
        assertEquals(14.0, DemoMealEstimate.fatGrams, 0.0)
    }

    @Test
    fun demoEstimate_ingredientCaloriesAddUpToMealTotal() {
        assertEquals(DemoMealEstimate.calories, DemoMealEstimate.ingredients.sumOf { it.calories })
    }

    @Test
    fun demoEstimate_ingredientIdsAreUnique() {
        val ids = DemoMealEstimate.ingredients.map { it.id }
        assertEquals(ids.size, ids.toSet().size)
    }
}
