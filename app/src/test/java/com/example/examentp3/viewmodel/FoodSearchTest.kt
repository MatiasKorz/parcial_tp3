package com.example.examentp3.viewmodel

import androidx.lifecycle.SavedStateHandle
import com.example.examentp3.data.local.FakeMealDao
import com.example.examentp3.data.repository.MealRepository
import com.example.examentp3.model.Food
import com.example.examentp3.model.FoodPage
import com.example.examentp3.model.FoodPortion
import com.example.examentp3.model.MealType
import com.example.examentp3.ui.navigation.FoodConfirmationMode
import com.example.examentp3.ui.navigation.NavArgs
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/** Search state and Food Confirmation in Search mode (EXT-05). */
class FoodSearchTest {

    private val chicken = Food("f01", "Chicken breast", 165.0, 31.0, 0.0, 3.6)
    private val rice = Food("f02", "Basmati rice", 130.0, 2.7, 28.0, 0.3)
    private val today = LocalDate.of(2026, 10, 23)

    private fun searchViewModel() = FoodConfirmationViewModel(
        savedStateHandle = SavedStateHandle(mapOf(NavArgs.FOOD_CONFIRMATION_MODE to "search")),
        repository = MealRepository(FakeMealDao()),
    )

    @Test
    fun firstPage_replacesTheResults() {
        val state = FoodSearchUiState(results = listOf(rice), page = 2)
            .withPage(FoodPage(listOf(chicken), page = 1, pageSize = 10, total = 1))
        assertEquals(listOf(chicken), state.results)
        assertFalse(state.hasMore)
    }

    @Test
    fun nextPage_isAppendedWithoutRepeats() {
        val state = FoodSearchUiState(results = listOf(chicken), page = 1)
            .withPage(FoodPage(listOf(chicken, rice), page = 2, pageSize = 10, total = 12))
        assertEquals(listOf(chicken, rice), state.results)
        assertEquals(2, state.page)
    }

    @Test
    fun error_keepsTheResultsShown() {
        val state = FoodSearchUiState(results = listOf(rice), page = 1, isLoading = true).withError(UiError.Server)
        assertEquals(listOf(rice), state.results)
        assertEquals(UiError.Server, state.error)
        assertFalse(state.isEmpty)
    }

    @Test
    fun answeredWithoutResults_isEmptyNotError() {
        val state = FoodSearchUiState(isLoading = true).withPage(FoodPage(emptyList(), 1, 10, 0))
        assertTrue(state.isEmpty)
        assertNull(state.error)
    }

    @Test
    fun beforeTheFirstAnswer_isNotEmpty() {
        assertFalse(FoodSearchUiState().isEmpty)
    }

    @Test
    fun routeArgument_selectsSearchMode() {
        assertEquals(FoodConfirmationMode.Search, searchViewModel().uiState.value.mode)
    }

    @Test
    fun choosingFoods_addsPortionsAndUpdatesTotals() {
        val viewModel = searchViewModel()

        viewModel.onFoodSelect(chicken)
        viewModel.onFoodSelect(rice)
        viewModel.onPortionIncrease("f01")

        val state = viewModel.uiState.value
        assertEquals(listOf(110, 100), state.portions.map { it.grams })
        assertEquals(182 + 130, state.totals.calories) // 181.5 rounds to 182
        assertEquals(34.1 + 2.7, state.totals.proteinGrams, 0.001)
    }

    @Test
    fun choosingTheSameFoodTwice_addsToItsPortion() {
        val portions = listOf(FoodPortion(chicken)).add(chicken)
        assertEquals(listOf(200), portions.map { it.grams })
    }

    @Test
    fun removingAFood_dropsItFromTheTotals() {
        val viewModel = searchViewModel()
        viewModel.onFoodSelect(chicken)
        viewModel.onFoodSelect(rice)

        viewModel.onPortionRemove("f01")

        assertEquals(listOf("f02"), viewModel.uiState.value.portions.map { it.food.id })
        assertEquals(130, viewModel.uiState.value.totals.calories)
    }

    @Test
    fun savingWithoutFoods_showsTheMessageAndDoesNotSave() {
        val viewModel = searchViewModel()

        viewModel.onAddToMeal()

        val state = viewModel.uiState.value
        assertTrue(state.missingFood)
        assertFalse(state.isSaving)
        assertNull(state.savedDate)
    }

    @Test
    fun choosingAFood_clearsTheMissingFoodMessage() {
        val viewModel = searchViewModel()
        viewModel.onAddToMeal()

        viewModel.onFoodSelect(rice)

        assertFalse(viewModel.uiState.value.missingFood)
    }

    @Test
    fun portionsToMeal_joinsNamesAndUsesTheComputedTotals() {
        val meal = listOf(FoodPortion(chicken, 180), FoodPortion(rice, 150)).toMeal(today, MealType.DINNER)

        assertEquals("Chicken breast · Basmati rice", meal.name)
        assertEquals(today, meal.date)
        assertEquals(MealType.DINNER, meal.type)
        assertEquals(297 + 195, meal.calories)
        assertEquals(55.8 + 4.1, meal.proteinGrams, 0.001)
        assertEquals(42.0, meal.carbsGrams, 0.001)
    }
}
