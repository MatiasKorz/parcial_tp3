package com.example.examentp3.viewmodel

import androidx.lifecycle.ViewModel
import com.example.examentp3.data.repository.ProfileRepository
import com.example.examentp3.model.GoalsSelection
import com.example.examentp3.model.PrimaryGoal
import com.example.examentp3.model.TrainingFrequency
import com.example.examentp3.model.WorkoutPreference
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/** Draft of the screen: what the user is picking, not saved until [GoalsPreferencesViewModel.save]. */
data class GoalsPreferencesUiState(
    val draft: GoalsSelection,
)

@HiltViewModel
class GoalsPreferencesViewModel @Inject constructor(
    private val repository: ProfileRepository,
) : ViewModel() {

    private val _uiState: MutableStateFlow<GoalsPreferencesUiState>
    val uiState: StateFlow<GoalsPreferencesUiState>

    init {
        // The repository is in memory, so the saved selection is read synchronously.
        _uiState = MutableStateFlow(GoalsPreferencesUiState(draft = repository.profile.value.goals))
        uiState = _uiState.asStateFlow()
    }

    /** "Primary Goal" allows several goals: tapping one marks or unmarks it. */
    fun onGoalToggle(goal: PrimaryGoal) = _uiState.update { state ->
        val goals = state.draft.goals
        state.copy(draft = state.draft.copy(goals = if (goal in goals) goals - goal else goals + goal))
    }

    fun onFrequencySelect(frequency: TrainingFrequency) =
        _uiState.update { it.copy(draft = it.draft.copy(frequency = frequency)) }

    fun onWorkoutSelect(workout: WorkoutPreference) =
        _uiState.update { it.copy(draft = it.draft.copy(workout = workout)) }

    /** Writes the draft to the repository; leaving without calling it discards the draft. */
    fun save() {
        val draft = _uiState.value.draft
        repository.updateProfile { it.copy(goals = draft) }
    }
}
