package com.example.examentp3.viewmodel

import com.example.examentp3.data.local.CoachMessageEntity
import com.example.examentp3.data.local.toModel
import com.example.examentp3.model.CoachMessage
import com.example.examentp3.model.CoachRole
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
        assertEquals(question, voiceIdleUiState(listOf(question, reply)).lastQuestion)
    }

    @Test
    fun idle_withoutHistory_hasNoLastQuestion() {
        // The screen then shows the Figma example card.
        assertNull(voiceIdleUiState(emptyList()).lastQuestion)
    }

    @Test
    fun conversation_withoutHistory_isEmpty() {
        // The screen then shows the Figma thread as an example.
        assertTrue(VoiceConversationUiState().isEmpty)
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
