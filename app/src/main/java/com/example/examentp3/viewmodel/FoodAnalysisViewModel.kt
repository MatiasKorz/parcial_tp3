package com.example.examentp3.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** Steps drawn in Nutrition / 05: identifying foods, estimating portions, calculating nutrition. */
internal const val FOOD_ANALYSIS_STEP_COUNT = 3
private const val STEP_DURATION_MS = 1_000L
private const val DONE_PAUSE_MS = 400L

/** Visual state of one analysis step, as drawn in Figma (done, in progress, pending). */
enum class FoodAnalysisStepStatus { Done, Active, Pending }

data class FoodAnalysisUiState(
    /** Index of the step in progress; equals [FOOD_ANALYSIS_STEP_COUNT] once every step is done. */
    val activeStep: Int = 0,
    /** The analysis ended: the NavHost opens the review (Nutrition / 07). */
    val isComplete: Boolean = false,
) {
    fun statusOf(step: Int): FoodAnalysisStepStatus = when {
        step < activeStep -> FoodAnalysisStepStatus.Done
        step == activeStep -> FoodAnalysisStepStatus.Active
        else -> FoodAnalysisStepStatus.Pending
    }
}

/**
 * Simulated food analysis (NAV05, SPEC p.6): there is no AI and the optional
 * `POST /v1/nutrition/analyses` is not called. The steps advance on a timer and the result is the
 * fixed estimate that Food Confirmation reviews before saving (SPEC p.10). Nothing is saved here.
 *
 * The timer runs in [viewModelScope], so a rotation does not restart the analysis and leaving the
 * screen cancels it.
 */
@HiltViewModel
class FoodAnalysisViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(FoodAnalysisUiState())
    val uiState: StateFlow<FoodAnalysisUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repeat(FOOD_ANALYSIS_STEP_COUNT) {
                delay(STEP_DURATION_MS)
                _uiState.update { it.copy(activeStep = it.activeStep + 1) }
            }
            delay(DONE_PAUSE_MS)
            _uiState.update { it.copy(isComplete = true) }
        }
    }
}
