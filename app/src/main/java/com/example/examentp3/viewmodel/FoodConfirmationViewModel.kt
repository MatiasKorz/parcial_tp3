package com.example.examentp3.viewmodel

import androidx.lifecycle.ViewModel
import com.example.examentp3.model.DemoMealEstimate
import com.example.examentp3.model.MealEstimate
import com.example.examentp3.model.MealType
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Review of a meal before logging it. For now only the estimate coming from the simulated
 * analysis (Food Analysis / Photo Analysis); NUC-04 adds the manual mode and saving.
 */
data class FoodConfirmationUiState(
    val estimate: MealEstimate,
    // The frame says "Add to Lunch"; choosing the type arrives with NUC-04.
    val mealType: MealType = MealType.LUNCH,
)

@HiltViewModel
class FoodConfirmationViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(FoodConfirmationUiState(estimate = DemoMealEstimate))
    val uiState: StateFlow<FoodConfirmationUiState> = _uiState.asStateFlow()
}
