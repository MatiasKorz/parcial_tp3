package com.example.examentp3.viewmodel

import android.util.Log
import androidx.annotation.StringRes
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.examentp3.R
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
import retrofit2.HttpException

private const val TAG = "WorkoutDetails"

/** Why a detail with no cached copy cannot be shown. Loading and error with retry belong to NUC-03. */
enum class DetailUnavailable(@get:StringRes val message: Int) {
    NotAvailableOffline(R.string.workout_details_offline),
    NotFound(R.string.workout_details_not_found),
}

data class WorkoutDetailsUiState(
    val workout: WorkoutDetail? = null,
    val isLoading: Boolean = true,
    val unavailable: DetailUnavailable? = null,
)

/** 404 means the workout does not exist; any other failure is treated as "not downloaded yet" (RF02). */
internal fun Throwable.toDetailUnavailable(): DetailUnavailable =
    if (this is HttpException && code() == 404) DetailUnavailable.NotFound else DetailUnavailable.NotAvailableOffline

/** Detail of GET /v1/workouts/{workoutId}: shown from Room and refreshed every time it opens (NUC-02). */
@HiltViewModel
class WorkoutDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: WorkoutRepository,
) : ViewModel() {

    private val workoutId: String = checkNotNull(savedStateHandle[NavArgs.WORKOUT_ID])

    private val isRefreshing = MutableStateFlow(true)
    private val refreshFailure = MutableStateFlow<DetailUnavailable?>(null)

    val uiState: StateFlow<WorkoutDetailsUiState> =
        combine(repository.observeWorkoutDetail(workoutId), isRefreshing, refreshFailure) { workout, refreshing, failure ->
            WorkoutDetailsUiState(
                workout = workout,
                isLoading = workout == null && refreshing,
                // A cached copy wins over a failed refresh: the detail keeps working offline.
                unavailable = if (workout == null) failure else null,
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), WorkoutDetailsUiState())

    init {
        viewModelScope.launch {
            repository.refreshWorkoutDetail(workoutId).onFailure { error ->
                Log.w(TAG, "GET v1/workouts/$workoutId failed: ${error.javaClass.simpleName}", error)
                refreshFailure.update { error.toDetailUnavailable() }
            }
            isRefreshing.update { false }
        }
    }
}
