package com.example.examentp3.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.examentp3.data.repository.MealRepository
import com.example.examentp3.model.DemoMealEstimate
import com.example.examentp3.model.Food
import com.example.examentp3.model.FoodPortion
import com.example.examentp3.model.PORTION_DEFAULT_GRAMS
import com.example.examentp3.model.Meal
import com.example.examentp3.model.MealEstimate
import com.example.examentp3.model.MealType
import com.example.examentp3.ui.navigation.FoodConfirmationMode
import com.example.examentp3.ui.navigation.NavArgs
import com.example.examentp3.ui.navigation.mealTypeFromRouteValue
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import javax.inject.Inject
import kotlin.math.roundToInt
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Review of a meal before logging it (Nutrition / 07). In [FoodConfirmationMode.Manual] it is the
 * RF03 form; in [FoodConfirmationMode.Estimate] it shows the simulated analysis from Figma; in
 * [FoodConfirmationMode.Search] it reviews the foods found in the mock (EXT-05).
 */
data class FoodConfirmationUiState(
    val mode: FoodConfirmationMode,
    /** Only in Estimate mode. */
    val estimate: MealEstimate? = null,
    /** Only in Search mode: the chosen foods and their quantities. */
    val portions: List<FoodPortion> = emptyList(),
    /** Search mode tried to save without foods. */
    val missingFood: Boolean = false,
    /**
     * Manual form. In Estimate mode only its date (today) and type are used to save. The type is the
     * one chosen on the way here (NAV04), or Lunch, as drawn, when none was chosen.
     */
    val draft: MealFormDraft,
    /** Empty until the first attempt to save; editing a field clears its error. */
    val errors: MealFormErrors = MealFormErrors(),
    val isSaving: Boolean = false,
    /** Room failed to insert; the form keeps what was typed. */
    val saveFailed: Boolean = false,
    /** One-shot event: the meal was saved for this date. The NavHost navigates and calls onSavedHandled(). */
    val savedDate: LocalDate? = null,
) {
    val totals: MealTotals
        get() = when (mode) {
            FoodConfirmationMode.Search -> portions.totals()
            else -> estimate?.let { MealTotals(it.calories, it.proteinGrams, it.carbsGrams, it.fatGrams) }
                ?: draft.previewTotals()
        }
}

/** MEAL TOTALS of the chosen portions; macros keep one decimal like each portion. */
internal fun List<FoodPortion>.totals(): MealTotals = MealTotals(
    calories = sumOf { it.calories },
    proteinGrams = sumOf { it.proteinGrams }.roundToOneDecimal(),
    carbsGrams = sumOf { it.carbsGrams }.roundToOneDecimal(),
    fatGrams = sumOf { it.fatGrams }.roundToOneDecimal(),
)

// Adding values of one decimal can leave float noise (0.1 + 0.2), which would reach Room.
private fun Double.roundToOneDecimal(): Double = (this * 10).roundToInt() / 10.0

/** Choosing a food already in the list adds a default portion to it instead of repeating the card. */
internal fun List<FoodPortion>.add(food: Food): List<FoodPortion> =
    if (any { it.food.id == food.id }) {
        map { if (it.food.id == food.id) it.copy(grams = it.grams + PORTION_DEFAULT_GRAMS) else it }
    } else {
        this + FoodPortion(food)
    }

/** Meal saved from the search (EXT-05): the food names joined, with the computed totals (RF03). */
internal fun List<FoodPortion>.toMeal(date: LocalDate, type: MealType): Meal {
    val totals = totals()
    return Meal(
        name = joinToString(" · ") { it.food.name },
        date = date,
        type = type,
        calories = totals.calories,
        proteinGrams = totals.proteinGrams,
        carbsGrams = totals.carbsGrams,
        fatGrams = totals.fatGrams,
    )
}

/** Pure initial state, so tests do not depend on the system clock. */
internal fun foodConfirmationInitialState(
    mode: FoodConfirmationMode,
    today: LocalDate,
    chosenType: MealType? = null,
): FoodConfirmationUiState {
    val draft = MealFormDraft(date = today)
    return FoodConfirmationUiState(
        mode = mode,
        estimate = if (mode == FoodConfirmationMode.Estimate) DemoMealEstimate else null,
        // Without a chosen type (e.g. Home "Log breakfast", D16) the draft keeps its default.
        draft = chosenType?.let { draft.copy(type = it) } ?: draft,
    )
}

/** Meal saved from the simulated estimate (D5): the ingredient names joined, with the Figma totals. */
internal fun MealEstimate.toMeal(date: LocalDate, type: MealType): Meal = Meal(
    name = ingredients.joinToString(" · ") { it.name },
    date = date,
    type = type,
    calories = calories,
    proteinGrams = proteinGrams,
    carbsGrams = carbsGrams,
    fatGrams = fatGrams,
)

@HiltViewModel
class FoodConfirmationViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: MealRepository,
) : ViewModel() {

    // The draft lives here: it survives rotation and is discarded when leaving the screen (D13).
    private val _uiState = MutableStateFlow(
        foodConfirmationInitialState(
            mode = FoodConfirmationMode.fromRouteValue(savedStateHandle.get<String>(NavArgs.FOOD_CONFIRMATION_MODE)),
            today = LocalDate.now(),
            chosenType = mealTypeFromRouteValue(savedStateHandle.get<String>(NavArgs.MEAL_TYPE)),
        ),
    )
    val uiState: StateFlow<FoodConfirmationUiState> = _uiState.asStateFlow()

    fun onNameChange(value: String) =
        _uiState.update { it.copy(draft = it.draft.copy(name = value), errors = it.errors.copy(name = null)) }

    fun onDateSelect(date: LocalDate) = _uiState.update { it.copy(draft = it.draft.copy(date = date)) }

    fun onTypeSelect(type: MealType) = _uiState.update { it.copy(draft = it.draft.copy(type = type)) }

    fun onCaloriesChange(value: String) =
        _uiState.update { it.copy(draft = it.draft.copy(calories = value), errors = it.errors.copy(calories = null)) }

    fun onProteinChange(value: String) =
        _uiState.update { it.copy(draft = it.draft.copy(protein = value), errors = it.errors.copy(protein = null)) }

    fun onCarbsChange(value: String) =
        _uiState.update { it.copy(draft = it.draft.copy(carbs = value), errors = it.errors.copy(carbs = null)) }

    fun onFatChange(value: String) =
        _uiState.update { it.copy(draft = it.draft.copy(fat = value), errors = it.errors.copy(fat = null)) }

    fun onFoodSelect(food: Food) =
        _uiState.update { it.copy(portions = it.portions.add(food), missingFood = false) }

    fun onPortionIncrease(foodId: String) = updatePortion(foodId) { it.increase() }

    fun onPortionDecrease(foodId: String) = updatePortion(foodId) { it.decrease() }

    fun onPortionRemove(foodId: String) =
        _uiState.update { state -> state.copy(portions = state.portions.filterNot { it.food.id == foodId }) }

    private fun updatePortion(foodId: String, transform: (FoodPortion) -> FoodPortion) =
        _uiState.update { state ->
            state.copy(portions = state.portions.map { if (it.food.id == foodId) transform(it) else it })
        }

    /** "Add to …": validates synchronously and only launches the insert when the form is valid. */
    fun onAddToMeal() {
        val state = _uiState.value
        // A second tap while saving or after saving does not insert twice.
        if (state.isSaving || state.savedDate != null) return

        val meal = when (state.mode) {
            FoodConfirmationMode.Estimate ->
                checkNotNull(state.estimate).toMeal(state.draft.date, state.draft.type)
            FoodConfirmationMode.Search -> {
                if (state.portions.isEmpty()) {
                    _uiState.update { it.copy(missingFood = true, saveFailed = false) }
                    return
                }
                state.portions.toMeal(state.draft.date, state.draft.type)
            }
            FoodConfirmationMode.Manual -> when (val result = validateMealForm(state.draft)) {
                is MealFormResult.Valid -> result.meal
                is MealFormResult.Invalid -> {
                    _uiState.update { it.copy(errors = result.errors, saveFailed = false) }
                    return
                }
            }
        }

        _uiState.update { it.copy(isSaving = true, saveFailed = false) }
        viewModelScope.launch {
            try {
                repository.addMeal(meal)
                _uiState.update { it.copy(isSaving = false, savedDate = meal.date) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update { it.copy(isSaving = false, saveFailed = true) }
            }
        }
    }

    fun onSavedHandled() = _uiState.update { it.copy(savedDate = null) }
}
