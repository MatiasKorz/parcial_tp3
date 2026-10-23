package com.example.examentp3.viewmodel

import com.example.examentp3.data.repository.SettingsRepository
import com.example.examentp3.model.AppearanceMode
import com.example.examentp3.model.UnitSystem
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class SettingsViewModelTest {

    private val repository = SettingsRepository()

    @Test
    fun init_loadsFigmaDefaultsFromRepository() {
        val uiState = SettingsViewModel(repository).uiState.value

        assertEquals(
            SettingsUiState(
                units = UnitSystem.Metric,
                appearance = AppearanceMode.SystemDefault,
                notificationsEnabled = true,
            ),
            uiState,
        )
    }

    @Test
    fun notificationsOff_updatesUiState() {
        val viewModel = SettingsViewModel(repository)

        viewModel.onNotificationsChange(false)

        assertFalse(viewModel.uiState.value.notificationsEnabled)
    }

    @Test
    fun notificationsOff_thenReopen_keepsChoice() {
        SettingsViewModel(repository).onNotificationsChange(false)

        val reopened = SettingsViewModel(repository).uiState.value

        assertFalse(reopened.notificationsEnabled)
    }
}
