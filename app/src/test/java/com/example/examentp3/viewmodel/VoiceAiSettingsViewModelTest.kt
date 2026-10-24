package com.example.examentp3.viewmodel

import com.example.examentp3.data.repository.SettingsRepository
import com.example.examentp3.model.CoachStyle
import com.example.examentp3.model.ResponseStyle
import com.example.examentp3.model.VoiceCoachSettings
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class VoiceAiSettingsViewModelTest {

    private val repository = SettingsRepository()

    @Test
    fun init_loadsFigmaDefaults() {
        val settings = VoiceAiSettingsViewModel(repository).uiState.value.settings

        assertEquals(CoachStyle.Balanced, settings.coachStyle)
        assertEquals(ResponseStyle.Balanced, settings.responseStyle)
        VoiceToggle.entries.forEach { assertTrue(it.name, settings.isOn(it)) }
    }

    @Test
    fun selectCoachStyle_updatesUiState() {
        val viewModel = VoiceAiSettingsViewModel(repository)

        viewModel.onCoachStyleSelect(CoachStyle.Direct)

        assertEquals(CoachStyle.Direct, viewModel.uiState.value.settings.coachStyle)
    }

    @Test
    fun selectResponseStyle_updatesUiState() {
        val viewModel = VoiceAiSettingsViewModel(repository)

        viewModel.onResponseStyleSelect(ResponseStyle.Detailed)

        assertEquals(ResponseStyle.Detailed, viewModel.uiState.value.settings.responseStyle)
    }

    @Test
    fun toggleOff_changesOnlyThatToggle() {
        val viewModel = VoiceAiSettingsViewModel(repository)

        viewModel.onToggleChange(VoiceToggle.HapticFeedback, false)

        val settings = viewModel.uiState.value.settings
        assertEquals(VoiceCoachSettings(hapticFeedback = false), settings)
    }

    @Test
    fun changes_thenReopen_keepChoices() {
        VoiceAiSettingsViewModel(repository).apply {
            onCoachStyleSelect(CoachStyle.Supportive)
            onResponseStyleSelect(ResponseStyle.Concise)
            onToggleChange(VoiceToggle.AutoPlayCoaching, false)
        }

        val reopened = VoiceAiSettingsViewModel(repository).uiState.value.settings

        assertEquals(
            VoiceCoachSettings(
                coachStyle = CoachStyle.Supportive,
                responseStyle = ResponseStyle.Concise,
                autoPlayCoaching = false,
            ),
            reopened,
        )
    }

    @Test
    fun voiceChanges_andNotifications_doNotOverwriteEachOther() {
        SettingsViewModel(repository).onNotificationsChange(false)
        VoiceAiSettingsViewModel(repository).onToggleChange(VoiceToggle.VoiceFeedback, false)

        val saved = repository.settings.value
        assertFalse(saved.notificationsEnabled)
        assertFalse(saved.voiceCoach.voiceFeedback)
    }
}
