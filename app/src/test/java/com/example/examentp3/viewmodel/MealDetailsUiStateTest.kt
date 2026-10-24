package com.example.examentp3.viewmodel

import com.example.examentp3.model.Meal
import com.example.examentp3.model.MealType
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Test

class MealDetailsUiStateTest {

    private val meal = Meal(
        id = 3,
        name = "Chicken · rice · vegetables",
        date = LocalDate.of(2026, 10, 23),
        type = MealType.LUNCH,
        calories = 520,
        proteinGrams = 38.0,
        carbsGrams = 54.0,
        fatGrams = 14.0,
    )

    @Test
    fun existingMeal_isContent() {
        assertEquals(MealDetailsUiState.Content(meal), mealDetailsUiState(meal, MealDeleteStatus.Idle))
    }

    @Test
    fun missingMeal_isNotFound() {
        assertEquals(MealDetailsUiState.NotFound, mealDetailsUiState(null, MealDeleteStatus.Idle))
    }

    @Test
    fun failedDelete_keepsContentAndFlagsTheError() {
        assertEquals(
            MealDetailsUiState.Content(meal, deleteFailed = true),
            mealDetailsUiState(meal, MealDeleteStatus.Failed),
        )
    }

    @Test
    fun rowGoneWhileDeleting_isLoadingNotNotFound() {
        assertEquals(MealDetailsUiState.Loading, mealDetailsUiState(null, MealDeleteStatus.InProgress))
    }

    @Test
    fun confirmedDelete_isDeleted() {
        assertEquals(MealDetailsUiState.Deleted, mealDetailsUiState(null, MealDeleteStatus.Done))
    }
}
