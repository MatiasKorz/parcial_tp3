package com.example.examentp3.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.examentp3.data.repository.ProfileRepository
import com.example.examentp3.model.UserProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

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

    // Once the user types, a late answer from Firestore must not overwrite the draft.
    private var isDraftEdited = false

    init {
        // The in-memory copy is shown at once, also offline.
        repository.syncWithSession()
        _uiState.update { repository.profile.value.toEditProfileUiState() }
        // With a Firebase session, the saved profile is loaded from Firestore (EXT-02).
        if (repository.isRemoteEnabled) {
            viewModelScope.launch {
                repository.refresh()
                if (!isDraftEdited) _uiState.update { repository.profile.value.toEditProfileUiState() }
            }
        }
    }

    fun onFirstNameChange(value: String) = edit { it.copy(firstName = value) }

    fun onLastNameChange(value: String) = edit { it.copy(lastName = value) }

    fun onDateOfBirthChange(value: String) = edit { it.copy(dateOfBirth = value) }

    fun onHeightChange(value: String) = edit { it.copy(height = value) }

    fun onWeightChange(value: String) = edit { it.copy(weight = value) }

    fun onGenderChange(value: String) = edit { it.copy(gender = value) }

    /** Writes the draft to the repository (and Firestore, if signed in); leaving without it discards the draft. */
    fun save() {
        val draft = _uiState.value
        repository.savePersonalData { profile ->
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

    private fun edit(transform: (EditProfileUiState) -> EditProfileUiState) {
        isDraftEdited = true
        _uiState.update(transform)
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
