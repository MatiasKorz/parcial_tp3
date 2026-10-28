package com.example.examentp3.viewmodel

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.examentp3.data.repository.WorkoutRepository
import com.example.examentp3.model.Exercise
import com.example.examentp3.model.WorkoutDetail
import com.example.examentp3.ui.navigation.NavArgs
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val TAG = "ActiveWorkout"

/** Load steps of 2.5 kg; Figma starts the set at 45 kg. */
internal const val WEIGHT_STEP_KG = 2.5
internal const val INITIAL_WEIGHT_STEPS = 18
internal const val EXTRA_REST_SECONDS = 30

/** One completed set, with the load chosen for it (EXT-06). */
data class LoggedSet(val weightKg: Double, val reps: Int)

/**
 * Progress of the session, independent of Android so it can be unit tested. The sets and rest of each
 * exercise come from the chosen workout; the session is not persisted (open question in EXT-06).
 */
data class SetProgress(
    val exerciseIndex: Int = 0,
    /** Completed sets of each exercise, by exercise position. */
    val loggedSets: List<List<LoggedSet>> = emptyList(),
    val weightSteps: Int = INITIAL_WEIGHT_STEPS,
    val restSeconds: Int = 0,
) {
    val weightKg: Double get() = weightSteps * WEIGHT_STEP_KG

    fun loggedFor(index: Int): List<LoggedSet> = loggedSets.getOrNull(index).orEmpty()

    fun allSetsComplete(exercises: List<Exercise>): Boolean =
        exercises.isEmpty() ||
            (exerciseIndex == exercises.lastIndex && loggedFor(exerciseIndex).size >= exercises.last().setCount)

    /** Logs the current set with the current load, then rests or moves on to the next exercise. */
    fun completeSet(exercises: List<Exercise>): SetProgress {
        if (allSetsComplete(exercises)) return this
        val exercise = exercises[exerciseIndex]
        val logged = loggedFor(exerciseIndex) + LoggedSet(weightKg, exercise.reps)
        val updated = List(maxOf(loggedSets.size, exerciseIndex + 1)) { index ->
            if (index == exerciseIndex) logged else loggedFor(index)
        }
        val exerciseDone = logged.size >= exercise.setCount
        val isLastExercise = exerciseIndex == exercises.lastIndex
        return copy(
            exerciseIndex = if (exerciseDone && !isLastExercise) exerciseIndex + 1 else exerciseIndex,
            loggedSets = updated,
            // No rest after the last set of the workout.
            restSeconds = if (exerciseDone && isLastExercise) 0 else exercise.restSeconds.coerceAtLeast(0),
        )
    }

    fun addRest(exercises: List<Exercise>): SetProgress =
        if (allSetsComplete(exercises)) this else copy(restSeconds = restSeconds + EXTRA_REST_SECONDS)

    fun skipRest(): SetProgress = copy(restSeconds = 0)

    fun tick(): SetProgress = copy(restSeconds = (restSeconds - 1).coerceAtLeast(0))

    fun increaseWeight(): SetProgress = copy(weightSteps = weightSteps + 1)

    fun decreaseWeight(): SetProgress = copy(weightSteps = (weightSteps - 1).coerceAtLeast(0))
}

/** A set count of zero would never finish the exercise. */
private val Exercise.setCount: Int get() = sets.coerceAtLeast(1)

data class ActiveWorkoutUiState(
    val workout: WorkoutDetail? = null,
    val progress: SetProgress = SetProgress(),
    val isLoading: Boolean = false,
    val error: UiError? = null,
) {
    private val exercises: List<Exercise> get() = workout?.exercises.orEmpty()

    val exercise: Exercise? get() = exercises.getOrNull(progress.exerciseIndex)
    val exerciseCount: Int get() = exercises.size
    val setCount: Int get() = exercise?.setCount ?: 0
    val loggedSets: List<LoggedSet> get() = progress.loggedFor(progress.exerciseIndex)
    val allSetsComplete: Boolean get() = workout != null && progress.allSetsComplete(exercises)

    /** 1-based; null once every set of the exercise is logged. */
    val currentSet: Int? get() = if (allSetsComplete) null else loggedSets.size + 1
    val isResting: Boolean get() = progress.restSeconds > 0
}

internal fun activeWorkoutUiState(
    cached: WorkoutDetail?,
    refresh: DetailRefresh,
    progress: SetProgress,
): ActiveWorkoutUiState = ActiveWorkoutUiState(
    workout = cached,
    progress = progress,
    isLoading = refresh.isLoading,
    error = refresh.error,
)

/**
 * Set tracking of Workout / 05 (EXT-06) for the workout of the route. The session lives here, so
 * rotating keeps the logged sets and the rest countdown.
 */
@HiltViewModel
class ActiveWorkoutViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: WorkoutRepository,
) : ViewModel() {

    private val workoutId: String = checkNotNull(savedStateHandle[NavArgs.WORKOUT_ID])

    private val refresh = MutableStateFlow(DetailRefresh())
    private val progress = MutableStateFlow(SetProgress())
    private var restCountdown: Job? = null

    val uiState: StateFlow<ActiveWorkoutUiState> =
        combine(repository.observeWorkoutDetail(workoutId), refresh, progress, ::activeWorkoutUiState)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ActiveWorkoutUiState(isLoading = true))

    init {
        // Opened from Workout Details the workout is already in Room; only download it when it is missing,
        // so a failed refresh never interrupts a session in progress.
        viewModelScope.launch {
            if (repository.observeWorkoutDetail(workoutId).first() == null) load()
            else refresh.update { DetailRefresh(isLoading = false) }
        }
    }

    fun retry() {
        if (!refresh.value.isLoading) load()
    }

    fun onCompleteSet() = updateProgress { it.completeSet(exercises()) }

    fun onAddRest() = updateProgress { it.addRest(exercises()) }

    fun onSkipRest() = updateProgress { it.skipRest() }

    fun onIncreaseWeight() = updateProgress { it.increaseWeight() }

    fun onDecreaseWeight() = updateProgress { it.decreaseWeight() }

    private fun exercises(): List<Exercise> = uiState.value.workout?.exercises.orEmpty()

    private fun updateProgress(transform: (SetProgress) -> SetProgress) {
        if (uiState.value.workout == null) return
        progress.update(transform)
        startRestCountdown()
    }

    /** One second per tick while resting; skipping ends the loop. */
    private fun startRestCountdown() {
        if (restCountdown?.isActive == true || progress.value.restSeconds == 0) return
        restCountdown = viewModelScope.launch {
            while (progress.value.restSeconds > 0) {
                delay(1_000)
                progress.update { it.tick() }
            }
        }
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
