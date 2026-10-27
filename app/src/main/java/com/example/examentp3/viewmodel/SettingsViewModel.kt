package com.example.examentp3.viewmodel

import androidx.lifecycle.ViewModel
import com.example.examentp3.data.repository.AuthRepository
import com.example.examentp3.data.repository.SettingsRepository
import com.example.examentp3.model.AppSettings
import com.example.examentp3.model.AppearanceMode
import com.example.examentp3.model.UnitSystem
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class SettingsUiState(
    val units: UnitSystem = UnitSystem.Metric,
    val appearance: AppearanceMode = AppearanceMode.SystemDefault,
    val notificationsEnabled: Boolean = true,
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: SettingsRepository,
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        // The repository is in memory, so the current value is read synchronously.
        val saved = repository.settings.value
        _uiState.update { saved.toSettingsUiState() }
    }

    /** Saves the choice right away, so it is still there when Settings is opened again (NAV04). */
    fun onNotificationsChange(enabled: Boolean) {
        repository.updateSettings { it.copy(notificationsEnabled = enabled) }
        _uiState.update { it.copy(notificationsEnabled = enabled) }
    }

    /** "Log out" (EXT-01): closes the Firebase session, if any. The NavHost then opens Sign In. */
    fun onLogOut() = authRepository.signOut()
}

private fun AppSettings.toSettingsUiState() = SettingsUiState(
    units = units,
    appearance = appearance,
    notificationsEnabled = notificationsEnabled,
)
