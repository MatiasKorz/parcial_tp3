package com.example.examentp3.viewmodel

import androidx.lifecycle.ViewModel
import com.example.examentp3.data.repository.ProfileRepository
import com.example.examentp3.model.UserProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/** Draft of the form: what the user is typing, not saved until [EditProfileViewModel.save]. */
data class EditProfileUiState(
    val firstName: String = "",
    val lastName: String = "",
    val dateOfBirth: String = "",
    val height: String = "",
    val weight: String = "",
    val gender: String = "",
)

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val repository: ProfileRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditProfileUiState())
    val uiState: StateFlow<EditProfileUiState> = _uiState.asStateFlow()

    init {
        // The repository is in memory, so the current value is read synchronously.
        val saved = repository.profile.value
        _uiState.update { saved.toEditProfileUiState() }
    }

    fun onFirstNameChange(value: String) = _uiState.update { it.copy(firstName = value) }

    fun onLastNameChange(value: String) = _uiState.update { it.copy(lastName = value) }

    fun onDateOfBirthChange(value: String) = _uiState.update { it.copy(dateOfBirth = value) }

    fun onHeightChange(value: String) = _uiState.update { it.copy(height = value) }

    fun onWeightChange(value: String) = _uiState.update { it.copy(weight = value) }

    fun onGenderChange(value: String) = _uiState.update { it.copy(gender = value) }

    /** Writes the draft to the repository; leaving without calling it discards the draft. */
    fun save() {
        val draft = _uiState.value
        repository.updateProfile { profile ->
            profile.copy(
                firstName = draft.firstName,
                lastName = draft.lastName,
                dateOfBirth = draft.dateOfBirth,
                height = draft.height,
                weight = draft.weight,
                gender = draft.gender,
            )
        }
    }
}

private fun UserProfile.toEditProfileUiState() = EditProfileUiState(
    firstName = firstName,
    lastName = lastName,
    dateOfBirth = dateOfBirth,
    height = height,
    weight = weight,
    gender = gender,
)
