package com.example.examentp3.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.examentp3.data.repository.MealRepository
import com.example.examentp3.model.DemoMealEstimate
import com.example.examentp3.model.Meal
import com.example.examentp3.model.MealEstimate
import com.example.examentp3.model.MealType
import com.example.examentp3.ui.navigation.FoodConfirmationMode
import com.example.examentp3.ui.navigation.NavArgs
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Review of a meal before logging it (Nutrition / 07). In [FoodConfirmationMode.Manual] it is the
 * RF03 form; in [FoodConfirmationMode.Estimate] it shows the simulated analysis from Figma.
 */
data class FoodConfirmationUiState(
    val mode: FoodConfirmationMode,
    /** Only in Estimate mode. */
    val estimate: MealEstimate? = null,
    /** Manual form. In Estimate mode only its date (today) and type (Lunch, as drawn) are used to save. */
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
        get() = estimate?.let { MealTotals(it.calories, it.proteinGrams, it.carbsGrams, it.fatGrams) }
            ?: draft.previewTotals()
}

/** Pure initial state, so tests do not depend on the system clock. */
internal fun foodConfirmationInitialState(mode: FoodConfirmationMode, today: LocalDate): FoodConfirmationUiState =
    FoodConfirmationUiState(
        mode = mode,
        estimate = if (mode == FoodConfirmationMode.Estimate) DemoMealEstimate else null,
        draft = MealFormDraft(date = today),
    )

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

    /** "Add to …": validates synchronously and only launches the insert when the form is valid. */
    fun onAddToMeal() {
        val state = _uiState.value
        // A second tap while saving or after saving does not insert twice.
        if (state.isSaving || state.savedDate != null) return

        val meal = when (state.mode) {
            FoodConfirmationMode.Estimate ->
                checkNotNull(state.estimate).toMeal(state.draft.date, state.draft.type)
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
