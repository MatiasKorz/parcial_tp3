package com.example.examentp3.ui.screens.workout.active

import org.junit.Assert.*
import org.junit.Test

class ActiveWorkoutDemoStateTest {
    @Test fun initialState_matchesFigma() {
        val state = ActiveWorkoutDemoState()
        assertEquals(1, state.completedSets)
        assertEquals(2, state.currentSet)
        assertEquals(45.0, state.weightKg, 0.0)
        assertEquals(78, state.restSeconds)
    }

    @Test fun completeCurrentSet_advancesToThirdAndRestartsSimulatedRest() {
        val next = ActiveWorkoutDemoState(restSeconds = 0).completeSet()
        assertEquals(2, next.completedSets)
        assertEquals(3, next.currentSet)
        assertEquals(78, next.restSeconds)
        assertFalse(next.allSetsComplete)
    }

    @Test fun completeFinalSet_finishesRestAndDoesNotCreateExtraSets() {
        val finished = ActiveWorkoutDemoState().completeSet().completeSet()
        assertTrue(finished.allSetsComplete)
        assertEquals(3, finished.completedSets)
        assertEquals(0, finished.restSeconds)
        assertEquals(finished, finished.completeSet())
    }

    @Test fun addRest_addsThirtySecondsToCurrentValue() {
        assertEquals(108, ActiveWorkoutDemoState().addRest().restSeconds)
    }

    @Test fun skipRest_endsRestWithoutChangingSetOrWeight() {
        val initial = ActiveWorkoutDemoState()
        assertEquals(initial.copy(restSeconds = 0), initial.skipRest())
    }

    @Test fun addRest_afterSkipStartsThirtySecondSimulatedRest() {
        assertEquals(30, ActiveWorkoutDemoState().skipRest().addRest().restSeconds)
    }

    @Test fun addRest_afterAllSetsCompleteDoesNotRestartRest() {
        val finished = ActiveWorkoutDemoState().completeSet().completeSet()
        assertEquals(finished, finished.addRest())
    }

    @Test fun weightControls_useTwoPointFiveKgStepsWithoutChangingSetsOrRest() {
        val initial = ActiveWorkoutDemoState()
        val increased = initial.increaseWeight()
        assertEquals(47.5, increased.weightKg, 0.0)
        assertEquals(initial, increased.decreaseWeight())
        assertEquals(initial.completedSets, increased.completedSets)
        assertEquals(initial.restSeconds, increased.restSeconds)
    }

    @Test fun decreaseWeight_neverBecomesNegative() {
        var state = ActiveWorkoutDemoState()
        repeat(30) { state = state.decreaseWeight() }
        assertEquals(0.0, state.weightKg, 0.0)
    }
}
