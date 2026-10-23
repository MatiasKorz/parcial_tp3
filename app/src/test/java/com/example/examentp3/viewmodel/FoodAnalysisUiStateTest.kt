package com.example.examentp3.viewmodel

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class FoodAnalysisUiStateTest {

    private fun FoodAnalysisUiState.statuses() = (0 until FOOD_ANALYSIS_STEP_COUNT).map { statusOf(it) }

    @Test
    fun initialState_firstStepActive_restPending_notComplete() {
        val state = FoodAnalysisUiState()

        assertEquals(
            listOf(FoodAnalysisStepStatus.Active, FoodAnalysisStepStatus.Pending, FoodAnalysisStepStatus.Pending),
            state.statuses(),
        )
        assertFalse(state.isComplete)
    }

    @Test
    fun figmaState_secondStepActive() {
        val state = FoodAnalysisUiState(activeStep = 1)

        assertEquals(
            listOf(FoodAnalysisStepStatus.Done, FoodAnalysisStepStatus.Active, FoodAnalysisStepStatus.Pending),
            state.statuses(),
        )
    }

    @Test
    fun afterLastStep_everyStepDone() {
        val state = FoodAnalysisUiState(activeStep = FOOD_ANALYSIS_STEP_COUNT)

        assertEquals(List(FOOD_ANALYSIS_STEP_COUNT) { FoodAnalysisStepStatus.Done }, state.statuses())
    }
}
