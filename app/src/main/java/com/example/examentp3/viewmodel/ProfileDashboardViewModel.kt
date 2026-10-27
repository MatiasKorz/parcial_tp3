package com.example.examentp3.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.examentp3.data.repository.ProfileRepository
import com.example.examentp3.model.UserProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** What Profile / 01 shows from the saved profile: the name of the personal card (EXT-02). */
data class ProfileDashboardUiState(val fullName: String)

/** "Alex Morgan" in the demo profile; blank parts are skipped so a missing last name leaves no stray space. */
internal fun UserProfile.toProfileDashboardUiState() =
    ProfileDashboardUiState(fullName = listOf(firstName, lastName).filter { it.isNotBlank() }.joinToString(" "))

@HiltViewModel
class ProfileDashboardViewModel @Inject constructor(
    private val repository: ProfileRepository,
) : ViewModel() {

    // Runs before uiState below reads the profile: a previous account's data is dropped first.
    init {
        repository.syncWithSession()
    }

    // Follows the repository, so a profile saved in Edit Profile shows up when coming back.
    val uiState: StateFlow<ProfileDashboardUiState> = repository.profile
        .map { it.toProfileDashboardUiState() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), repository.profile.value.toProfileDashboardUiState())

    init {
        // Once per ViewModel, not per recomposition: after reopening the app, the profile comes from Firestore.
        if (repository.isRemoteEnabled) viewModelScope.launch { repository.refresh() }
    }
}
