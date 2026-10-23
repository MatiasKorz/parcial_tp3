package com.example.examentp3.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.examentp3.data.repository.WorkoutRepository
import com.example.examentp3.model.Exercise
import com.example.examentp3.ui.navigation.NavArgs
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class ExerciseDetailsUiState(
    val exercise: Exercise? = null,
    val isLoading: Boolean = true,
)

/**
 * Guide of one exercise, read from the detail cached by [WorkoutDetailsViewModel] (RF01, RF02).
 * The workout id is part of the key because exercise ids repeat across workouts.
 */
@HiltViewModel
class ExerciseDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    repository: WorkoutRepository,
) : ViewModel() {

    private val workoutId: String = checkNotNull(savedStateHandle[NavArgs.WORKOUT_ID])
    private val exerciseId: String = checkNotNull(savedStateHandle[NavArgs.EXERCISE_ID])

    val uiState: StateFlow<ExerciseDetailsUiState> =
        repository.observeExercise(workoutId, exerciseId)
            .map { ExerciseDetailsUiState(exercise = it, isLoading = false) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ExerciseDetailsUiState())
}
