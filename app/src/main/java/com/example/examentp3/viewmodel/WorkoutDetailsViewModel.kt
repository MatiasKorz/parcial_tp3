package com.example.examentp3.viewmodel

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.examentp3.data.repository.WorkoutRepository
import com.example.examentp3.model.WorkoutDetail
import com.example.examentp3.ui.navigation.NavArgs
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val TAG = "WorkoutDetails"

data class WorkoutDetailsUiState(
    val workout: WorkoutDetail? = null,
    val isLoading: Boolean = false,
    val error: UiError? = null,
)

/** Result of the last GET /v1/workouts/{workoutId}, kept apart from the Room cache. */
internal data class DetailRefresh(
    val isLoading: Boolean = true,
    val error: UiError? = null,
)

/** A cached copy is always shown: a failed refresh only adds the error next to it (RF02, RF05). */
internal fun workoutDetailsUiState(cached: WorkoutDetail?, refresh: DetailRefresh): WorkoutDetailsUiState =
    WorkoutDetailsUiState(workout = cached, isLoading = refresh.isLoading, error = refresh.error)

/** Detail of GET /v1/workouts/{workoutId} shown from Room (NUC-02) with loading and error with retry (NUC-03). */
@HiltViewModel
class WorkoutDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: WorkoutRepository,
) : ViewModel() {

    private val workoutId: String = checkNotNull(savedStateHandle[NavArgs.WORKOUT_ID])

    private val refresh = MutableStateFlow(DetailRefresh())

    val uiState: StateFlow<WorkoutDetailsUiState> =
        combine(repository.observeWorkoutDetail(workoutId), refresh, ::workoutDetailsUiState)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), WorkoutDetailsUiState(isLoading = true))

    init {
        load()
    }

    fun retry() {
        if (!refresh.value.isLoading) load()
    }

    private fun load() {
        refresh.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            repository.refreshWorkoutDetail(workoutId)
                .onSuccess { refresh.update { DetailRefresh(isLoading = false) } }
                .onFailure { error ->
                    Log.w(TAG, "GET v1/workouts/$workoutId failed: ${error.javaClass.simpleName}", error)
                    refresh.update { DetailRefresh(isLoading = false, error = error.toUiError()) }
                }
        }
    }
}
