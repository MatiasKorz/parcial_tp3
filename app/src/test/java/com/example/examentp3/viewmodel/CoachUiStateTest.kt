package com.example.examentp3.viewmodel

import com.example.examentp3.data.local.CoachMessageEntity
import com.example.examentp3.data.local.toModel
import com.example.examentp3.model.CoachMessage
import com.example.examentp3.model.CoachRole
import com.example.examentp3.model.CoachSuggestion
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CoachUiStateTest {

    private val question = CoachMessage(1, CoachRole.User, "Create a 30-minute workout for me.", 1_000)
    private val reply = CoachMessage(2, CoachRole.Assistant, "Here it is.", 2_000, isSimulated = true)

    @Test
    fun messageText_isTrimmed() {
        assertEquals("Hello", coachMessageText("  Hello \n"))
    }

    @Test
    fun messageText_blank_isNotSent() {
        assertNull(coachMessageText("   "))
    }

    @Test
    fun messageText_longerThanTheContract_isCutTo2000() {
        assertEquals(2000, coachMessageText("a".repeat(2500))?.length)
    }

    @Test
    fun idle_lastQuestion_isTheLastUserMessage() {
        val state = voiceIdleUiState(SuggestionsResult(isLoading = false), listOf(question, reply))
        assertEquals(question, state.lastQuestion)
    }

    @Test
    fun idle_withoutHistory_hasNoLastQuestion() {
        assertNull(voiceIdleUiState(SuggestionsResult(isLoading = false), emptyList()).lastQuestion)
    }

    @Test
    fun idle_validEmptyAnswer_isEmptyNotError() {
        val state = voiceIdleUiState(SuggestionsResult(isLoading = false), emptyList())
        assertTrue(state.hasNoSuggestions)
        assertNull(state.suggestionsError)
    }

    @Test
    fun idle_failure_isErrorNotEmpty() {
        val state = voiceIdleUiState(SuggestionsResult(isLoading = false, error = UiError.Server), emptyList())
        assertFalse(state.hasNoSuggestions)
        assertEquals(UiError.Server, state.suggestionsError)
    }

    @Test
    fun idle_failedRetry_keepsTheSuggestionsShown() {
        val suggestions = listOf(CoachSuggestion("s1", "How am I doing?"))
        val state = voiceIdleUiState(
            SuggestionsResult(isLoading = false, suggestions = suggestions, error = UiError.NoConnection),
            emptyList(),
        )
        assertEquals(suggestions, state.suggestions)
        assertEquals(UiError.NoConnection, state.suggestionsError)
    }

    @Test
    fun conversation_errorWithHistory_isNotEmpty() {
        val state = VoiceConversationUiState(messages = listOf(question), error = UiError.Server)
        assertFalse(state.isEmpty)
    }

    @Test
    fun entity_unknownRole_readsAsAssistant() {
        val entity = CoachMessageEntity(id = 1, role = "system", text = "x", sentAtMillis = 0, isSimulated = true)
        assertEquals(CoachRole.Assistant, entity.toModel().role)
    }
}
