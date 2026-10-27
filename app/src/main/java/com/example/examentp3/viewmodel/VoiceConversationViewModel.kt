package com.example.examentp3.viewmodel

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.examentp3.data.repository.COACH_MESSAGE_MAX_LENGTH
import com.example.examentp3.data.repository.CoachRepository
import com.example.examentp3.model.CoachMessage
import com.example.examentp3.ui.navigation.NavArgs
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val TAG = "VoiceConversation"

data class VoiceConversationUiState(
    val messages: List<CoachMessage> = emptyList(),
    /** Waiting for the coach's reply. */
    val isSending: Boolean = false,
    /** The last reply failed; the history stays and "Retry" asks again. */
    val error: UiError? = null,
) {
    val isEmpty: Boolean get() = messages.isEmpty() && !isSending && error == null
}

internal data class SendStatus(
    val isSending: Boolean = false,
    val error: UiError? = null,
    /** Message whose reply failed, to ask again without saving it twice. */
    val unanswered: String? = null,
)

/** Text the mock accepts (1 to 2000 characters), or null when there is nothing to send. */
internal fun coachMessageText(raw: String): String? =
    raw.trim().take(COACH_MESSAGE_MAX_LENGTH).takeIf { it.isNotEmpty() }

/** Voice / 05 — Conversation (EXT-04): the Room history, with replies from POST /v1/coach/messages. */
@HiltViewModel
class VoiceConversationViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val repository: CoachRepository,
) : ViewModel() {

    private val status = MutableStateFlow(SendStatus())

    val uiState: StateFlow<VoiceConversationUiState> =
        combine(repository.observeMessages(), status) { messages, send ->
            VoiceConversationUiState(messages = messages, isSending = send.isSending, error = send.error)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), VoiceConversationUiState())

    init {
        // A suggested question opens the conversation already asked. It is cleared from the
        // SavedStateHandle so a recreated ViewModel does not ask it again.
        savedStateHandle.get<String>(NavArgs.COACH_PROMPT)?.let { prompt ->
            savedStateHandle[NavArgs.COACH_PROMPT] = null
            onSend(prompt)
        }
    }

    fun onSend(raw: String) {
        val text = coachMessageText(raw) ?: return
        if (status.value.isSending) return
        status.update { SendStatus(isSending = true) }
        viewModelScope.launch { handle(text, repository.send(text)) }
    }

    fun retry() {
        val text = status.value.unanswered ?: return
        if (status.value.isSending) return
        status.update { SendStatus(isSending = true) }
        viewModelScope.launch { handle(text, repository.requestReply(text)) }
    }

    private fun handle(text: String, result: Result<Unit>) {
        result
            .onSuccess { status.update { SendStatus() } }
            .onFailure { error ->
                Log.w(TAG, "POST v1/coach/messages failed: ${error.javaClass.simpleName}", error)
                status.update { SendStatus(error = error.toUiError(), unanswered = text) }
            }
    }
}
