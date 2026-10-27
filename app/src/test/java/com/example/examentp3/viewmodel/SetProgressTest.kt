package com.example.examentp3.viewmodel

import com.example.examentp3.model.Exercise
import com.example.examentp3.model.WorkoutDetail
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SetProgressTest {

    private fun exercise(id: String, sets: Int, reps: Int = 10, rest: Int = 60) = Exercise(
        id = id, name = id, muscleGroup = "", sets = sets, reps = reps,
        restSeconds = rest, instructions = "", tip = "",
    )

    private val exercises = listOf(exercise("a", sets = 2, rest = 90), exercise("b", sets = 1, rest = 45))

    @Test
    fun initialState_startsAtFirstSetWithFigmaLoadAndNoRest() {
        val progress = SetProgress()
        assertEquals(0, progress.exerciseIndex)
        assertEquals(45.0, progress.weightKg, 0.0)
        assertEquals(0, progress.restSeconds)
        assertFalse(progress.allSetsComplete(exercises))
    }

    @Test
    fun completeSet_logsLoadAndRepsAndStartsExerciseRest() {
        val progress = SetProgress().increaseWeight().completeSet(exercises)
        assertEquals(listOf(LoggedSet(47.5, 10)), progress.loggedFor(0))
        assertEquals(0, progress.exerciseIndex)
        assertEquals(90, progress.restSeconds)
    }

    @Test
    fun completeLastSetOfExercise_movesToNextExercise() {
        val progress = SetProgress().completeSet(exercises).completeSet(exercises)
        assertEquals(1, progress.exerciseIndex)
        assertEquals(2, progress.loggedFor(0).size)
        assertTrue(progress.loggedFor(1).isEmpty())
    }

    @Test
    fun completeLastSetOfWorkout_finishesWithoutRestAndIgnoresMoreSets() {
        val finished = SetProgress().completeSet(exercises).completeSet(exercises).completeSet(exercises)
        assertTrue(finished.allSetsComplete(exercises))
        assertEquals(0, finished.restSeconds)
        assertEquals(finished, finished.completeSet(exercises))
    }

    @Test
    fun setCount_comesFromTheChosenWorkout() {
        val five = listOf(exercise("x", sets = 5))
        var progress = SetProgress()
        repeat(4) { progress = progress.completeSet(five) }
        assertFalse(progress.allSetsComplete(five))
        assertTrue(progress.completeSet(five).allSetsComplete(five))
    }

    @Test
    fun addRest_addsThirtySeconds() {
        assertEquals(30, SetProgress().addRest(exercises).restSeconds)
    }

    @Test
    fun addRest_afterWorkoutFinishedDoesNothing() {
        val finished = SetProgress(exerciseIndex = 1, loggedSets = listOf(emptyList(), listOf(LoggedSet(45.0, 10))))
        assertEquals(finished, finished.addRest(exercises))
    }

    @Test
    fun skipRest_endsRestKeepingSetsAndLoad() {
        val resting = SetProgress().completeSet(exercises)
        val skipped = resting.skipRest()
        assertEquals(0, skipped.restSeconds)
        assertEquals(resting.loggedSets, skipped.loggedSets)
        assertEquals(resting.weightKg, skipped.weightKg, 0.0)
    }

    @Test
    fun tick_countsDownAndStopsAtZero() {
        assertEquals(1, SetProgress(restSeconds = 2).tick().restSeconds)
        assertEquals(0, SetProgress(restSeconds = 0).tick().restSeconds)
    }

    @Test
    fun decreaseWeight_neverBecomesNegative() {
        var progress = SetProgress()
        repeat(30) { progress = progress.decreaseWeight() }
        assertEquals(0.0, progress.weightKg, 0.0)
    }

    @Test
    fun uiState_exposesCurrentExerciseAndSet() {
        val workout = WorkoutDetail("w02", "Upper", "intermediate", 35, "", exercises)
        val state = activeWorkoutUiState(workout, DetailRefresh(isLoading = false), SetProgress().completeSet(exercises))
        assertEquals("a", state.exercise?.id)
        assertEquals(2, state.setCount)
        assertEquals(2, state.currentSet)
        assertTrue(state.isResting)
    }

    @Test
    fun uiState_withoutWorkoutIsNotComplete() {
        val state = activeWorkoutUiState(null, DetailRefresh(isLoading = false, error = UiError.Server), SetProgress())
        assertNull(state.exercise)
        assertFalse(state.allSetsComplete)
        assertEquals(UiError.Server, state.error)
    }
}
