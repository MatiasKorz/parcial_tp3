package com.example.examentp3.viewmodel

import com.example.examentp3.data.repository.ProfileRepository
import com.example.examentp3.model.DemoUserProfile
import org.junit.Assert.assertEquals
import org.junit.Test

class EditProfileViewModelTest {

    private val repository = ProfileRepository()

    @Test
    fun init_loadsProfileFromRepository() {
        val uiState = EditProfileViewModel(repository).uiState.value

        assertEquals(DemoUserProfile.firstName, uiState.firstName)
        assertEquals(DemoUserProfile.gender, uiState.gender)
    }

    @Test
    fun save_thenReopen_showsSavedValues() {
        EditProfileViewModel(repository).apply {
            onFirstNameChange("Sam")
            onLastNameChange("Lee")
            onDateOfBirthChange("2 June 1990")
            onHeightChange("165 cm")
            onWeightChange("60 kg")
            onGenderChange("Female")
            save()
        }

        val reopened = EditProfileViewModel(repository).uiState.value

        assertEquals(
            EditProfileUiState(
                firstName = "Sam",
                lastName = "Lee",
                dateOfBirth = "2 June 1990",
                height = "165 cm",
                weight = "60 kg",
                gender = "Female",
            ),
            reopened,
        )
    }

    @Test
    fun leaveWithoutSaving_thenReopen_discardsDraft() {
        EditProfileViewModel(repository).onFirstNameChange("Sam")

        val reopened = EditProfileViewModel(repository).uiState.value

        assertEquals(DemoUserProfile.firstName, reopened.firstName)
    }
}
