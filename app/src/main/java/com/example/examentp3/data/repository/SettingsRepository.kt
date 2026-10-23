package com.example.examentp3.data.repository

import com.example.examentp3.model.AppSettings
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * In-memory app settings of the demo user. Being a singleton, what is saved lasts for the whole
 * app session (NAV04) and is lost when the process ends, which the card allows (SPEC p.12).
 */
@Singleton
class SettingsRepository @Inject constructor() {

    private val _settings = MutableStateFlow(AppSettings())
    val settings: StateFlow<AppSettings> = _settings.asStateFlow()

    /** Applies [transform] atomically, so each caller only changes the fields it owns. */
    fun updateSettings(transform: (AppSettings) -> AppSettings) {
        _settings.update(transform)
    }
}
