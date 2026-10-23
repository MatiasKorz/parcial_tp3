package com.example.examentp3.viewmodel

import androidx.lifecycle.ViewModel
import com.example.examentp3.data.repository.ProfileRepository
import com.example.examentp3.model.DietaryPreference
import com.example.examentp3.model.EnjoyedFood
import com.example.examentp3.model.NutritionPriority
import com.example.examentp3.model.NutritionSelection
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Draft of the screen (not saved until [NutritionPreferencesViewModel.save]) plus the text
 * being typed in "Add an ingredient...".
 */
data class NutritionPreferencesUiState(
    val draft: NutritionSelection,
    val newIngredient: String = "",
)

@HiltViewModel
class NutritionPreferencesViewModel @Inject constructor(
    private val repository: ProfileRepository,
) : ViewModel() {

    private val _uiState: MutableStateFlow<NutritionPreferencesUiState>
    val uiState: StateFlow<NutritionPreferencesUiState>

    init {
        // The repository is in memory, so the saved selection is read synchronously.
        _uiState = MutableStateFlow(NutritionPreferencesUiState(draft = repository.profile.value.nutrition))
        uiState = _uiState.asStateFlow()
    }

    /** "Dietary Preference" allows a single option. */
    fun onDietSelect(diet: DietaryPreference) =
        _uiState.update { it.copy(draft = it.draft.copy(diet = diet)) }

    /** "Foods You Enjoy" allows several foods: tapping one marks or unmarks it. */
    fun onEnjoyedFoodToggle(food: EnjoyedFood) = _uiState.update { state ->
        val foods = state.draft.enjoyedFoods
        state.copy(draft = state.draft.copy(enjoyedFoods = if (food in foods) foods - food else foods + food))
    }

    fun onNewIngredientChange(text: String) =
        _uiState.update { it.copy(newIngredient = text) }

    /**
     * Adds the typed ingredient to "Foods to Avoid" and clears the field. Blank text and
     * ingredients already in the list (ignoring case) are not added.
     */
    fun onIngredientAdd() = _uiState.update { state ->
        val ingredient = state.newIngredient.trim()
        val avoided = state.draft.avoidedFoods
        when {
            ingredient.isEmpty() -> state.copy(newIngredient = "")
            avoided.any { it.equals(ingredient, ignoreCase = true) } -> state.copy(newIngredient = "")
            else -> state.copy(
                draft = state.draft.copy(avoidedFoods = avoided + ingredient),
                newIngredient = "",
            )
        }
    }

    fun onAvoidedFoodRemove(food: String) =
        _uiState.update { it.copy(draft = it.draft.copy(avoidedFoods = it.draft.avoidedFoods - food)) }

    /** "What matters most to you?" allows several priorities: tapping one marks or unmarks it. */
    fun onPriorityToggle(priority: NutritionPriority) = _uiState.update { state ->
        val priorities = state.draft.priorities
        state.copy(
            draft = state.draft.copy(
                priorities = if (priority in priorities) priorities - priority else priorities + priority,
            ),
        )
    }

    /** Writes the draft to the repository; leaving without calling it discards the draft. */
    fun save() {
        val draft = _uiState.value.draft
        repository.updateProfile { it.copy(nutrition = draft) }
    }
}
