package com.example.examentp3.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.examentp3.data.repository.WorkoutRepository
import com.example.examentp3.model.WorkoutSummary
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

private const val TAG = "WorkoutCatalog"

data class WorkoutCatalogUiState(
    val workouts: List<WorkoutSummary> = emptyList(),
)

/** Catalog of GET /v1/workouts shown from Room (NUC-01). Loading, empty and error states belong to NUC-03. */
@HiltViewModel
class WorkoutCatalogViewModel @Inject constructor(
    private val repository: WorkoutRepository,
) : ViewModel() {

    val uiState: StateFlow<WorkoutCatalogUiState> =
        repository.observeWorkouts()
            .map { WorkoutCatalogUiState(workouts = it) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), WorkoutCatalogUiState())

    init {
        viewModelScope.launch {
            // Until NUC-03 shows the error state, a failed refresh keeps the cached catalog and is only logged.
            repository.refreshWorkouts()
                .onFailure { error -> Log.w(TAG, "GET v1/workouts failed: ${error.javaClass.simpleName}", error) }
        }
    }
}
