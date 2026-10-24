package com.example.examentp3.viewmodel

import androidx.lifecycle.SavedStateHandle
import com.example.examentp3.data.local.FakeMealDao
import com.example.examentp3.data.repository.MealRepository
import com.example.examentp3.model.Meal
import com.example.examentp3.model.MealType
import com.example.examentp3.ui.navigation.NavArgs
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TodaysMealsViewModelTest {

    private val today = LocalDate.of(2026, 10, 23)

    private fun meal(type: MealType, calories: Int) = Meal(
        name = type.name,
        date = today,
        type = type,
        calories = calories,
        proteinGrams = 10.0,
        carbsGrams = 0.0,
        fatGrams = 0.0,
    )

    @Test
    fun todaysMealsUiState_totalIsSumOfTheDay() {
        val state = todaysMealsUiState(today, listOf(meal(MealType.BREAKFAST, 420), meal(MealType.LUNCH, 520)), today)

        assertEquals(940, state.totals.calories)
        assertEquals(20.0, state.totals.proteinGrams, 0.0)
        assertEquals(4, state.sections.size)
    }

    @Test
    fun todaysMealsUiState_isTodayOnlyForToday() {
        assertTrue(todaysMealsUiState(today, emptyList(), today).isToday)
        assertFalse(todaysMealsUiState(today.minusDays(1), emptyList(), today).isToday)
    }

    @Test
    fun initialEpochDay_withoutArgument_isToday() {
        assertEquals(today.toEpochDay(), initialEpochDay(null, today))
    }

    @Test
    fun initialEpochDay_invalidArgument_isToday() {
        assertEquals(today.toEpochDay(), initialEpochDay("not-a-day", today))
    }

    @Test
    fun initialEpochDay_withArgument_usesIt() {
        val date = LocalDate.of(2026, 10, 1)

        assertEquals(date.toEpochDay(), initialEpochDay(date.toEpochDay().toString(), today))
    }

    @Test
    fun routeArgument_setsTheInitialDate() {
        val date = LocalDate.of(2026, 10, 1)
        val viewModel = viewModel(SavedStateHandle(mapOf(NavArgs.DATE_EPOCH_DAY to date.toEpochDay().toString())))

        assertEquals(date, viewModel.uiState.value.date)
    }

    @Test
    fun onPreviousDay_savesTheDayBeforeInSavedStateHandle() {
        val date = LocalDate.of(2026, 10, 1)
        val handle = SavedStateHandle(mapOf(NavArgs.DATE_EPOCH_DAY to date.toEpochDay().toString()))

        viewModel(handle).onPreviousDay()

        // A new ViewModel with the same handle (as after process death) opens on the saved day.
        assertEquals(date.minusDays(1), viewModel(handle).uiState.value.date)
    }

    @Test
    fun onDateSelected_savesTheChosenDayInSavedStateHandle() {
        val handle = SavedStateHandle()
        val chosen = LocalDate.of(2026, 9, 8)

        viewModel(handle).onDateSelected(chosen)

        assertEquals(chosen, viewModel(handle).uiState.value.date)
    }

    private fun viewModel(handle: SavedStateHandle) = TodaysMealsViewModel(MealRepository(FakeMealDao()), handle)
}
