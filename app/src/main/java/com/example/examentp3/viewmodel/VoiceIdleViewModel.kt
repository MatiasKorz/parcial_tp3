package com.example.examentp3.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.examentp3.data.repository.CoachRepository
import com.example.examentp3.model.CoachMessage
import com.example.examentp3.model.CoachRole
import com.example.examentp3.model.CoachSuggestion
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val TAG = "VoiceIdle"

data class VoiceIdleUiState(
    val isLoadingSuggestions: Boolean = false,
    val suggestions: List<CoachSuggestion> = emptyList(),
    val suggestionsError: UiError? = null,
    /** Last question the user asked, from the Room history; null until there is one. */
    val lastQuestion: CoachMessage? = null,
) {
    val hasNoSuggestions: Boolean
        get() = !isLoadingSuggestions && suggestionsError == null && suggestions.isEmpty()
}

internal data class SuggestionsResult(
    val isLoading: Boolean = true,
    val suggestions: List<CoachSuggestion> = emptyList(),
    val error: UiError? = null,
)

internal fun voiceIdleUiState(result: SuggestionsResult, history: List<CoachMessage>): VoiceIdleUiState =
    VoiceIdleUiState(
        isLoadingSuggestions = result.isLoading,
        suggestions = result.suggestions,
        suggestionsError = result.error,
        lastQuestion = history.lastOrNull { it.role == CoachRole.User },
    )

/** Voice / 01 — Idle (EXT-04): suggested questions from the mock and the last question from Room. */
@HiltViewModel
class VoiceIdleViewModel @Inject constructor(
    private val repository: CoachRepository,
) : ViewModel() {

    private val suggestions = MutableStateFlow(SuggestionsResult())

    val uiState: StateFlow<VoiceIdleUiState> =
        combine(suggestions, repository.observeMessages(), ::voiceIdleUiState)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), VoiceIdleUiState(isLoadingSuggestions = true))

    init {
        load()
    }

    fun retry() {
        if (!suggestions.value.isLoading) load()
    }

    private fun load() {
        // Suggestions already shown stay while retrying.
        suggestions.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            repository.getSuggestions()
                .onSuccess { items -> suggestions.update { SuggestionsResult(isLoading = false, suggestions = items) } }
                .onFailure { error ->
                    Log.w(TAG, "GET v1/coach/suggestions failed: ${error.javaClass.simpleName}", error)
                    suggestions.update { it.copy(isLoading = false, error = error.toUiError()) }
                }
        }
    }
}
