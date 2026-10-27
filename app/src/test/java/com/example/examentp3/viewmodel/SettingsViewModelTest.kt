package com.example.examentp3.viewmodel

import com.example.examentp3.data.repository.AuthRepository
import com.example.examentp3.data.repository.SettingsRepository
import com.example.examentp3.model.AppearanceMode
import com.example.examentp3.model.UnitSystem
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class SettingsViewModelTest {

    private val repository = SettingsRepository()

    // Without FirebaseAuth, as in a build without google-services.json.
    private val authRepository = AuthRepository(auth = null)

    private fun newViewModel() = SettingsViewModel(repository, authRepository)

    @Test
    fun init_loadsFigmaDefaultsFromRepository() {
        val uiState = newViewModel().uiState.value

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
        val viewModel = newViewModel()

        viewModel.onNotificationsChange(false)

        assertFalse(viewModel.uiState.value.notificationsEnabled)
    }

    @Test
    fun notificationsOff_thenReopen_keepsChoice() {
        newViewModel().onNotificationsChange(false)

        val reopened = newViewModel().uiState.value

        assertFalse(reopened.notificationsEnabled)
    }

    @Test
    fun logOut_withoutFirebase_doesNotCrash() {
        newViewModel().onLogOut()
    }
}
