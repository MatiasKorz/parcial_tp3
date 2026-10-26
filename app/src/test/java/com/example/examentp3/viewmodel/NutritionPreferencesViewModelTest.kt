package com.example.examentp3.viewmodel

import com.example.examentp3.data.repository.AuthRepository
import com.example.examentp3.data.repository.ProfileRepository
import com.example.examentp3.model.DemoNutritionSelection
import com.example.examentp3.model.DietaryPreference
import com.example.examentp3.model.EnjoyedFood
import com.example.examentp3.model.NutritionPriority
import com.example.examentp3.model.NutritionSelection
import com.example.examentp3.model.WorkoutPreference
import org.junit.Assert.assertEquals
import org.junit.Test

class NutritionPreferencesViewModelTest {

    private val repository = ProfileRepository(AuthRepository(auth = null), firestore = null)

    @Test
    fun init_loadsSelectionFromRepository() {
        val uiState = NutritionPreferencesViewModel(repository).uiState.value

        assertEquals(DemoNutritionSelection, uiState.draft)
        assertEquals("", uiState.newIngredient)
    }

    @Test
    fun onDietSelect_replacesDiet() {
        val viewModel = NutritionPreferencesViewModel(repository)

        viewModel.onDietSelect(DietaryPreference.Vegan)

        assertEquals(DietaryPreference.Vegan, viewModel.uiState.value.draft.diet)
    }

    @Test
    fun onEnjoyedFoodToggle_unmarksAndMarksFood() {
        val viewModel = NutritionPreferencesViewModel(repository)

        viewModel.onEnjoyedFoodToggle(EnjoyedFood.Fish)
        assertEquals(EnjoyedFood.entries.toSet() - EnjoyedFood.Fish, viewModel.uiState.value.draft.enjoyedFoods)

        viewModel.onEnjoyedFoodToggle(EnjoyedFood.Fish)
        assertEquals(EnjoyedFood.entries.toSet(), viewModel.uiState.value.draft.enjoyedFoods)
    }

    @Test
    fun onIngredientAdd_addsTrimmedIngredientAndClearsField() {
        val viewModel = NutritionPreferencesViewModel(repository)

        viewModel.onNewIngredientChange("  Gluten ")
        viewModel.onIngredientAdd()

        assertEquals(listOf("Peanuts", "Shellfish", "Mushrooms", "Gluten"), viewModel.uiState.value.draft.avoidedFoods)
        assertEquals("", viewModel.uiState.value.newIngredient)
    }

    @Test
    fun onIngredientAdd_ignoresBlankAndDuplicates() {
        val viewModel = NutritionPreferencesViewModel(repository)

        viewModel.onNewIngredientChange("   ")
        viewModel.onIngredientAdd()
        viewModel.onNewIngredientChange("peanuts")
        viewModel.onIngredientAdd()

        assertEquals(DemoNutritionSelection.avoidedFoods, viewModel.uiState.value.draft.avoidedFoods)
        assertEquals("", viewModel.uiState.value.newIngredient)
    }

    @Test
    fun onAvoidedFoodRemove_removesChip() {
        val viewModel = NutritionPreferencesViewModel(repository)

        viewModel.onAvoidedFoodRemove("Shellfish")

        assertEquals(listOf("Peanuts", "Mushrooms"), viewModel.uiState.value.draft.avoidedFoods)
    }

    @Test
    fun onPriorityToggle_unmarksPriority() {
        val viewModel = NutritionPreferencesViewModel(repository)

        viewModel.onPriorityToggle(NutritionPriority.HitProteinTargets)

        assertEquals(setOf(NutritionPriority.EatMoreConsistently), viewModel.uiState.value.draft.priorities)
    }

    @Test
    fun save_thenReopen_showsSavedSelection() {
        NutritionPreferencesViewModel(repository).apply {
            onDietSelect(DietaryPreference.Pescatarian)
            onEnjoyedFoodToggle(EnjoyedFood.Pasta)
            onNewIngredientChange("Gluten")
            onIngredientAdd()
            onAvoidedFoodRemove("Peanuts")
            onPriorityToggle(NutritionPriority.EatMoreConsistently)
            save()
        }

        val reopened = NutritionPreferencesViewModel(repository).uiState.value

        assertEquals(
            NutritionSelection(
                diet = DietaryPreference.Pescatarian,
                enjoyedFoods = EnjoyedFood.entries.toSet() - EnjoyedFood.Pasta,
                avoidedFoods = listOf("Shellfish", "Mushrooms", "Gluten"),
                priorities = setOf(NutritionPriority.HitProteinTargets),
            ),
            reopened.draft,
        )
    }

    @Test
    fun leaveWithoutSaving_thenReopen_discardsDraft() {
        NutritionPreferencesViewModel(repository).apply {
            onDietSelect(DietaryPreference.Vegetarian)
            onAvoidedFoodRemove("Peanuts")
        }

        val reopened = NutritionPreferencesViewModel(repository).uiState.value

        assertEquals(DemoNutritionSelection, reopened.draft)
    }

    @Test
    fun saveNutrition_keepsGoalsAndEditProfileFields() {
        EditProfileViewModel(repository).apply {
            onFirstNameChange("Sam")
            save()
        }
        GoalsPreferencesViewModel(repository).apply {
            onWorkoutSelect(WorkoutPreference.Cardio)
            save()
        }

        NutritionPreferencesViewModel(repository).apply {
            onDietSelect(DietaryPreference.Vegan)
            save()
        }

        val profile = repository.profile.value
        assertEquals("Sam", profile.firstName)
        assertEquals(WorkoutPreference.Cardio, profile.goals.workout)
        assertEquals(DietaryPreference.Vegan, profile.nutrition.diet)
    }
}
