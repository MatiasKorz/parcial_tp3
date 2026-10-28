package com.example.examentp3.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.examentp3.data.repository.CoachRepository
import com.example.examentp3.model.CoachMessage
import com.example.examentp3.model.CoachRole
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class VoiceIdleUiState(
    /** Last question the user asked, from the Room history; null until there is one (the card shows the Figma example). */
    val lastQuestion: CoachMessage? = null,
)

internal fun voiceIdleUiState(history: List<CoachMessage>): VoiceIdleUiState =
    VoiceIdleUiState(lastQuestion = history.lastOrNull { it.role == CoachRole.User })

/**
 * Voice / 01 — Idle (EXT-04): "LAST CONVERSATION" from the Room history. The suggested questions are the
 * four of the Figma (the mock's suggestions come in Spanish and would break the screen's fidelity).
 */
@HiltViewModel
class VoiceIdleViewModel @Inject constructor(
    repository: CoachRepository,
) : ViewModel() {

    val uiState: StateFlow<VoiceIdleUiState> =
        repository.observeMessages()
            .map(::voiceIdleUiState)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), VoiceIdleUiState())
}
