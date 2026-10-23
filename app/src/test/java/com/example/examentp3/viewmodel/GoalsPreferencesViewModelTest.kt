package com.example.examentp3.viewmodel

import com.example.examentp3.data.repository.ProfileRepository
import com.example.examentp3.model.CoachingStyle
import com.example.examentp3.model.DemoGoalsSelection
import com.example.examentp3.model.GoalsSelection
import com.example.examentp3.model.PrimaryGoal
import com.example.examentp3.model.TrainingFrequency
import com.example.examentp3.model.WorkoutPreference
import org.junit.Assert.assertEquals
import org.junit.Test

class GoalsPreferencesViewModelTest {

    private val repository = ProfileRepository()

    @Test
    fun init_loadsSelectionFromRepository() {
        val uiState = GoalsPreferencesViewModel(repository).uiState.value

        assertEquals(DemoGoalsSelection, uiState.draft)
    }

    @Test
    fun onGoalToggle_marksAndUnmarksGoal() {
        val viewModel = GoalsPreferencesViewModel(repository)

        viewModel.onGoalToggle(PrimaryGoal.LoseBodyFat)
        viewModel.onGoalToggle(PrimaryGoal.BuildStrength)

        assertEquals(
            setOf(PrimaryGoal.ImproveFitness, PrimaryGoal.LoseBodyFat),
            viewModel.uiState.value.draft.goals,
        )
    }

    @Test
    fun save_thenReopen_showsSavedSelection() {
        GoalsPreferencesViewModel(repository).apply {
            onGoalToggle(PrimaryGoal.BuildStrength)
            onGoalToggle(PrimaryGoal.BuildHealthyHabits)
            onFrequencySelect(TrainingFrequency.SixPlusDays)
            onWorkoutSelect(WorkoutPreference.Mobility)
            save()
        }

        val reopened = GoalsPreferencesViewModel(repository).uiState.value

        assertEquals(
            GoalsSelection(
                goals = setOf(PrimaryGoal.ImproveFitness, PrimaryGoal.BuildHealthyHabits),
                frequency = TrainingFrequency.SixPlusDays,
                workout = WorkoutPreference.Mobility,
                coachingStyle = CoachingStyle.Balanced,
            ),
            reopened.draft,
        )
    }

    @Test
    fun leaveWithoutSaving_thenReopen_discardsDraft() {
        GoalsPreferencesViewModel(repository).onFrequencySelect(TrainingFrequency.ThreeDays)

        val reopened = GoalsPreferencesViewModel(repository).uiState.value

        assertEquals(DemoGoalsSelection, reopened.draft)
    }

    @Test
    fun saveGoals_keepsEditProfileFields() {
        EditProfileViewModel(repository).apply {
            onFirstNameChange("Sam")
            onGenderChange("Female")
            save()
        }

        GoalsPreferencesViewModel(repository).apply {
            onWorkoutSelect(WorkoutPreference.Cardio)
            save()
        }

        val profile = repository.profile.value
        assertEquals("Sam", profile.firstName)
        assertEquals("Female", profile.gender)
        assertEquals(WorkoutPreference.Cardio, profile.goals.workout)
    }
}
