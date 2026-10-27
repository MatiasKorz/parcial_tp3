package com.example.examentp3.viewmodel

import androidx.lifecycle.SavedStateHandle
import com.example.examentp3.data.local.FakeMealDao
import com.example.examentp3.data.repository.MealRepository
import com.example.examentp3.model.DemoMealEstimate
import com.example.examentp3.model.MealType
import com.example.examentp3.ui.navigation.FoodConfirmationMode
import com.example.examentp3.ui.navigation.NavArgs
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

// Only synchronous paths: a valid save launches in viewModelScope (Dispatchers.Main), which needs
// kotlinx-coroutines-test. It is covered by validateMealForm + MealEntityMapperTest + the emulator.
class FoodConfirmationViewModelTest {

    private val today = LocalDate.of(2026, 10, 23)

    private fun viewModel(mode: String?, dao: FakeMealDao = FakeMealDao(), mealType: String? = null) =
        FoodConfirmationViewModel(
            savedStateHandle = SavedStateHandle(
                buildMap {
                    mode?.let { put(NavArgs.FOOD_CONFIRMATION_MODE, it) }
                    mealType?.let { put(NavArgs.MEAL_TYPE, it) }
                },
            ),
            repository = MealRepository(dao),
        )

    @Test
    fun manualMode_withChosenType_preselectsIt() {
        val state = viewModel("manual", mealType = "DINNER").uiState.value

        assertEquals(MealType.DINNER, state.draft.type)
    }

    @Test
    fun estimateMode_withChosenType_savesWithThatType() {
        val state = viewModel("estimate", mealType = "BREAKFAST").uiState.value

        // onAddToMeal builds the estimate meal from the draft type; the insert itself needs Dispatchers.Main.
        assertEquals(MealType.BREAKFAST, state.draft.type)
        assertEquals(MealType.BREAKFAST, checkNotNull(state.estimate).toMeal(today, state.draft.type).type)
    }

    @Test
    fun withoutChosenType_keepsLunchDefault() {
        assertEquals(MealType.LUNCH, viewModel("manual").uiState.value.draft.type)
        assertEquals(MealType.LUNCH, viewModel("estimate").uiState.value.draft.type)
    }

    @Test
    fun unknownChosenType_keepsLunchDefault() {
        assertEquals(MealType.LUNCH, viewModel("manual", mealType = "BRUNCH").uiState.value.draft.type)
    }

    @Test
    fun chosenType_isKeptInInitialStateForEveryMode() {
        FoodConfirmationMode.entries.forEach { mode ->
            assertEquals(MealType.SNACK, foodConfirmationInitialState(mode, today, MealType.SNACK).draft.type)
        }
    }

    @Test
    fun manualMode_startsWithEmptyFormAndToday() {
        val state = foodConfirmationInitialState(FoodConfirmationMode.Manual, today)

        assertNull(state.estimate)
        assertEquals(MealFormDraft(date = today), state.draft)
        assertEquals(MealType.LUNCH, state.draft.type)
        assertFalse(state.errors.hasErrors)
        assertEquals(MealTotals(), state.totals)
    }

    @Test
    fun estimateMode_loadsFigmaEstimateForLunch() {
        val state = foodConfirmationInitialState(FoodConfirmationMode.Estimate, today)

        assertEquals(DemoMealEstimate, state.estimate)
        assertEquals(MealType.LUNCH, state.draft.type)
        assertEquals(MealTotals(520, 38.0, 54.0, 14.0), state.totals)
    }

    @Test
    fun routeArgument_selectsTheMode() {
        assertEquals(FoodConfirmationMode.Manual, viewModel("manual").uiState.value.mode)
        assertEquals(FoodConfirmationMode.Estimate, viewModel("estimate").uiState.value.mode)
    }

    @Test
    fun unknownOrMissingMode_fallsBackToEstimate() {
        assertEquals(FoodConfirmationMode.Estimate, viewModel("other").uiState.value.mode)
        assertEquals(FoodConfirmationMode.Estimate, viewModel(null).uiState.value.mode)
    }

    @Test
    fun invalidSave_showsErrorsAndDoesNotInsert() {
        val dao = FakeMealDao()
        val viewModel = viewModel("manual", dao)

        viewModel.onAddToMeal()

        val state = viewModel.uiState.value
        assertEquals(MealFieldError.NameRequired, state.errors.name)
        assertEquals(MealFieldError.Required, state.errors.calories)
        assertFalse(state.isSaving)
        assertNull(state.savedDate)
        assertTrue(dao.entities.isEmpty())
    }

    @Test
    fun editingField_clearsItsError() {
        val viewModel = viewModel("manual")
        viewModel.onAddToMeal()

        viewModel.onCaloriesChange("450")

        val errors = viewModel.uiState.value.errors
        assertNull(errors.calories)
        assertEquals(MealFieldError.NameRequired, errors.name)
        assertEquals("450", viewModel.uiState.value.draft.calories)
    }

    @Test
    fun typingMacros_updatesLiveTotals() {
        val viewModel = viewModel("manual")

        viewModel.onCaloriesChange("450")
        viewModel.onProteinChange("30,5")
        viewModel.onCarbsChange("abc")

        assertEquals(MealTotals(450, 30.5, 0.0, 0.0), viewModel.uiState.value.totals)
    }

    @Test
    fun typeAndDate_areStoredInTheDraft() {
        val viewModel = viewModel("manual")
        val yesterday = LocalDate.now().minusDays(1)

        viewModel.onTypeSelect(MealType.DINNER)
        viewModel.onDateSelect(yesterday)

        assertEquals(MealType.DINNER, viewModel.uiState.value.draft.type)
        assertEquals(yesterday, viewModel.uiState.value.draft.date)
    }

    @Test
    fun estimateToMeal_joinsIngredientNamesAndKeepsFigmaTotals() {
        val meal = DemoMealEstimate.toMeal(today, MealType.LUNCH)

        assertEquals("Grilled Chicken Breast · Basmati Rice · Mixed Steamed Vegetables", meal.name)
        assertEquals(today, meal.date)
        assertEquals(MealType.LUNCH, meal.type)
        assertEquals(520, meal.calories)
        assertEquals(38.0, meal.proteinGrams, 0.0)
        assertEquals(54.0, meal.carbsGrams, 0.0)
        assertEquals(14.0, meal.fatGrams, 0.0)
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
