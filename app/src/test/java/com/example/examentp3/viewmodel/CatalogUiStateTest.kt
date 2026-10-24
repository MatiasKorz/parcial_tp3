package com.example.examentp3.viewmodel

import com.example.examentp3.model.WorkoutSummary
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CatalogUiStateTest {

    private val cached = listOf(
        WorkoutSummary("w01", "Fuerza inicial", "beginner", 25, 3),
        WorkoutSummary("w02", "Entrenamiento completo", "intermediate", 35, 3),
    )

    @Test
    fun loadingWithoutCache_isLoadingAndNotEmpty() {
        val state = catalogUiState(emptyList(), CatalogRefresh(isLoading = true))
        assertTrue(state.isLoading)
        assertFalse(state.isEmpty)
    }

    @Test
    fun loadingWithCache_keepsShowingTheCache() {
        val state = catalogUiState(cached, CatalogRefresh(isLoading = true))
        assertEquals(cached, state.workouts)
    }

    @Test
    fun successWithWorkouts_showsContent() {
        val state = catalogUiState(cached, CatalogRefresh(isLoading = false))
        assertEquals(cached, state.workouts)
        assertFalse(state.isEmpty)
    }

    @Test
    fun emptyAnswer_showsEmptyEvenWithCache() {
        val state = catalogUiState(cached, CatalogRefresh(isLoading = false, serverSentEmpty = true))
        assertEquals(emptyList<WorkoutSummary>(), state.workouts)
        assertTrue(state.isEmpty)
    }

    @Test
    fun errorWithCache_showsCacheAndError() {
        val state = catalogUiState(cached, CatalogRefresh(isLoading = false, error = UiError.NoConnection))
        assertEquals(cached, state.workouts)
        assertEquals(UiError.NoConnection, state.error)
    }

    @Test
    fun errorWithoutCache_isErrorNotEmpty() {
        val state = catalogUiState(emptyList(), CatalogRefresh(isLoading = false, error = UiError.Server))
        assertEquals(UiError.Server, state.error)
        assertFalse(state.isEmpty)
    }
}
