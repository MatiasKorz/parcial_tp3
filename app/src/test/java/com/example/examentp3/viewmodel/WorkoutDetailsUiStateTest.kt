package com.example.examentp3.viewmodel

import com.example.examentp3.model.WorkoutDetail
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class WorkoutDetailsUiStateTest {

    private val cached = WorkoutDetail(
        id = "w01", name = "Fuerza inicial", level = "beginner", durationMinutes = 25,
        description = "Rutina de ejemplo.", exercises = emptyList(),
    )

    @Test
    fun loadingWithoutCache_isLoadingWithoutWorkout() {
        val state = workoutDetailsUiState(null, DetailRefresh(isLoading = true))
        assertTrue(state.isLoading)
        assertNull(state.workout)
    }

    @Test
    fun loadingWithCache_keepsShowingTheCache() {
        val state = workoutDetailsUiState(cached, DetailRefresh(isLoading = true))
        assertEquals(cached, state.workout)
        assertTrue(state.isLoading)
    }

    @Test
    fun success_showsContentWithoutError() {
        val state = workoutDetailsUiState(cached, DetailRefresh(isLoading = false))
        assertEquals(cached, state.workout)
        assertFalse(state.isLoading)
        assertNull(state.error)
    }

    @Test
    fun errorWithCache_showsCacheAndError() {
        val state = workoutDetailsUiState(cached, DetailRefresh(isLoading = false, error = UiError.NoConnection))
        assertEquals(cached, state.workout)
        assertEquals(UiError.NoConnection, state.error)
    }

    @Test
    fun errorWithoutCache_isError() {
        val state = workoutDetailsUiState(null, DetailRefresh(isLoading = false, error = UiError.Server))
        assertNull(state.workout)
        assertEquals(UiError.Server, state.error)
    }
}
