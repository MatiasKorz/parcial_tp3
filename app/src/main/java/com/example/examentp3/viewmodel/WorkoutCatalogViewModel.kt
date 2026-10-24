package com.example.examentp3.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.examentp3.data.repository.WorkoutRepository
import com.example.examentp3.model.WorkoutSummary
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val TAG = "WorkoutCatalog"

data class WorkoutCatalogUiState(
    val workouts: List<WorkoutSummary> = emptyList(),
    val isLoading: Boolean = false,
    val error: UiError? = null,
) {
    val isEmpty: Boolean get() = !isLoading && error == null && workouts.isEmpty()
}

/** Result of the last GET /v1/workouts, kept apart from the Room cache. */
internal data class CatalogRefresh(
    val isLoading: Boolean = true,
    val error: UiError? = null,
    val serverSentEmpty: Boolean = false,
)

/**
 * An empty answer shows the empty state but keeps Room: if the server stops later,
 * the cached catalog is shown again together with the error (RF05, decision declared in the README).
 */
internal fun catalogUiState(cached: List<WorkoutSummary>, refresh: CatalogRefresh): WorkoutCatalogUiState =
    WorkoutCatalogUiState(
        workouts = if (refresh.serverSentEmpty) emptyList() else cached,
        isLoading = refresh.isLoading,
        error = refresh.error,
    )

/** Catalog of GET /v1/workouts shown from Room (NUC-01) with loading, empty and error states (NUC-03). */
@HiltViewModel
class WorkoutCatalogViewModel @Inject constructor(
    private val repository: WorkoutRepository,
) : ViewModel() {

    private val refresh = MutableStateFlow(CatalogRefresh())

    val uiState: StateFlow<WorkoutCatalogUiState> =
        combine(repository.observeWorkouts(), refresh, ::catalogUiState)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), WorkoutCatalogUiState(isLoading = true))

    init {
        load()
    }

    fun retry() {
        if (!refresh.value.isLoading) load()
    }

    private fun load() {
        refresh.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            repository.refreshWorkouts()
                .onSuccess { count ->
                    refresh.update { CatalogRefresh(isLoading = false, serverSentEmpty = count == 0) }
                }
                .onFailure { error ->
                    Log.w(TAG, "GET v1/workouts failed: ${error.javaClass.simpleName}", error)
                    // A failure shows the cache again, even after an empty answer.
                    refresh.update { CatalogRefresh(isLoading = false, error = error.toUiError()) }
                }
        }
    }
}
