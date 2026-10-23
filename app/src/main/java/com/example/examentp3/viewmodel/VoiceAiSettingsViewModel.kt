package com.example.examentp3.viewmodel

import androidx.lifecycle.ViewModel
import com.example.examentp3.data.repository.SettingsRepository
import com.example.examentp3.model.CoachStyle
import com.example.examentp3.model.ResponseStyle
import com.example.examentp3.model.VoiceCoachSettings
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/** The five on/off rows of Profile / 05 — Voice & AI Settings. */
enum class VoiceToggle { SpeakResponsesAloud, AutoPlayCoaching, AllowInterruptions, VoiceFeedback, HapticFeedback }

data class VoiceAiSettingsUiState(
    val settings: VoiceCoachSettings = VoiceCoachSettings(),
)

/** Current value of [toggle] in these settings. */
fun VoiceCoachSettings.isOn(toggle: VoiceToggle): Boolean = when (toggle) {
    VoiceToggle.SpeakResponsesAloud -> speakResponsesAloud
    VoiceToggle.AutoPlayCoaching -> autoPlayCoaching
    VoiceToggle.AllowInterruptions -> allowInterruptions
    VoiceToggle.VoiceFeedback -> voiceFeedback
    VoiceToggle.HapticFeedback -> hapticFeedback
}

@HiltViewModel
class VoiceAiSettingsViewModel @Inject constructor(
    private val repository: SettingsRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(VoiceAiSettingsUiState())
    val uiState: StateFlow<VoiceAiSettingsUiState> = _uiState.asStateFlow()

    init {
        // The repository is in memory, so the saved choices are read synchronously.
        val saved = repository.settings.value.voiceCoach
        _uiState.update { it.copy(settings = saved) }
    }

    fun onCoachStyleSelect(style: CoachStyle) = save { it.copy(coachStyle = style) }

    fun onResponseStyleSelect(style: ResponseStyle) = save { it.copy(responseStyle = style) }

    fun onToggleChange(toggle: VoiceToggle, enabled: Boolean) = save {
        when (toggle) {
            VoiceToggle.SpeakResponsesAloud -> it.copy(speakResponsesAloud = enabled)
            VoiceToggle.AutoPlayCoaching -> it.copy(autoPlayCoaching = enabled)
            VoiceToggle.AllowInterruptions -> it.copy(allowInterruptions = enabled)
            VoiceToggle.VoiceFeedback -> it.copy(voiceFeedback = enabled)
            VoiceToggle.HapticFeedback -> it.copy(hapticFeedback = enabled)
        }
    }

    /**
     * There is no "Save" button in Figma, so every change is stored right away (NAV04).
     * Only the voice part of the settings changes: Notifications (Profile / 06) is kept.
     */
    private fun save(transform: (VoiceCoachSettings) -> VoiceCoachSettings) {
        repository.updateSettings { it.copy(voiceCoach = transform(it.voiceCoach)) }
        _uiState.update { it.copy(settings = transform(it.settings)) }
    }
}
