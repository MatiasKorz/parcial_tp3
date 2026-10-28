package com.example.examentp3.data.repository

import com.example.examentp3.data.local.CoachMessageDao
import com.example.examentp3.data.local.CoachMessageEntity
import com.example.examentp3.data.remote.CoachApi
import com.example.examentp3.data.remote.dto.CoachMessageRequestDto
import com.example.examentp3.data.remote.dto.CoachReplyDto
import com.example.examentp3.data.remote.dto.CoachReplyResponseDto
import com.example.examentp3.data.remote.dto.CoachSuggestionDto
import com.example.examentp3.data.remote.dto.CoachSuggestionListResponseDto
import com.example.examentp3.model.CoachRole
import java.io.IOException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/** [CoachRepository] asks the mock with Retrofit and keeps the whole conversation in Room (EXT-04). */
class CoachRepositoryTest {

    private val api = FakeCoachApi()
    private val dao = FakeCoachMessageDao()
    private val repository = CoachRepository(api, dao)

    @Test
    fun getSuggestions_mapsTheTextsAndDropsEmptyOnes() = runBlocking {
        api.suggestions = {
            CoachSuggestionListResponseDto(listOf(CoachSuggestionDto("s1", "How am I doing?"), CoachSuggestionDto("s2", null)))
        }

        assertEquals(listOf("How am I doing?"), repository.getSuggestions().getOrThrow().map { it.text })
    }

    @Test
    fun getSuggestions_emptyScenario_isAnEmptyListNotAFailure() = runBlocking {
        api.suggestions = { CoachSuggestionListResponseDto(emptyList()) }

        assertEquals(emptyList<Any>(), repository.getSuggestions().getOrThrow())
    }

    @Test
    fun getSuggestions_responseWithoutData_isFailure() = runBlocking {
        api.suggestions = { CoachSuggestionListResponseDto(null) }

        assertTrue(repository.getSuggestions().isFailure)
    }

    @Test
    fun send_savesTheQuestionAndTheSimulatedReply() = runBlocking {
        api.reply = { CoachReplyResponseDto(CoachReplyDto("assistant", "Try tofu.", "nutrition", true)) }

        val result = repository.send("Vegetarian dinner?")

        assertTrue(result.isSuccess)
        assertEquals("Vegetarian dinner?", api.lastRequest?.text)
        val history = repository.observeMessages().first()
        assertEquals(listOf(CoachRole.User, CoachRole.Assistant), history.map { it.role })
        assertEquals("Try tofu.", history.last().text)
        assertTrue(history.last().isSimulated)
    }

    @Test
    fun send_failedReply_keepsTheQuestionInTheHistory() = runBlocking {
        api.reply = { throw IOException("no connection") }

        val result = repository.send("Hello")

        assertTrue(result.isFailure)
        assertEquals(listOf("Hello"), repository.observeMessages().first().map { it.text })
    }

    @Test
    fun requestReply_afterFailure_addsOnlyTheReply() = runBlocking {
        api.reply = { throw IOException("no connection") }
        repository.send("Hello")

        api.reply = { CoachReplyResponseDto(CoachReplyDto("assistant", "Hi!", "general", true)) }
        repository.requestReply("Hello")

        assertEquals(listOf("Hello", "Hi!"), repository.observeMessages().first().map { it.text })
    }

    @Test
    fun requestReply_replyWithoutText_isFailureAndSavesNothing() = runBlocking {
        api.reply = { CoachReplyResponseDto(CoachReplyDto("assistant", null, null, true)) }

        assertTrue(repository.requestReply("Hello").isFailure)
        assertTrue(repository.observeMessages().first().isEmpty())
    }
}

private class FakeCoachApi : CoachApi {
    var suggestions: () -> CoachSuggestionListResponseDto = { CoachSuggestionListResponseDto(emptyList()) }
    var reply: () -> CoachReplyResponseDto = { CoachReplyResponseDto(null) }
    var lastRequest: CoachMessageRequestDto? = null

    override suspend fun getSuggestions(): CoachSuggestionListResponseDto = suggestions()

    override suspend fun sendMessage(body: CoachMessageRequestDto): CoachReplyResponseDto {
        lastRequest = body
        return reply()
    }
}

private class FakeCoachMessageDao : CoachMessageDao {
    private val rows = MutableStateFlow<List<CoachMessageEntity>>(emptyList())

    override suspend fun insert(message: CoachMessageEntity): Long {
        val id = rows.value.size + 1L
        rows.update { it + message.copy(id = id) }
        return id
    }

    override fun observeAll(): Flow<List<CoachMessageEntity>> = rows
}
