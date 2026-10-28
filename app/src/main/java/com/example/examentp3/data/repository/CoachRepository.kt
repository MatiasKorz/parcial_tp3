package com.example.examentp3.data.repository

import com.example.examentp3.data.local.CoachMessageDao
import com.example.examentp3.data.local.toEntity
import com.example.examentp3.data.local.toModel
import com.example.examentp3.data.remote.CoachApi
import com.example.examentp3.data.remote.dto.CoachMessageRequestDto
import com.example.examentp3.data.remote.dto.toModel
import com.example.examentp3.model.CoachMessage
import com.example.examentp3.model.CoachRole
import com.example.examentp3.model.CoachSuggestion
import com.google.gson.JsonParseException
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/** Limits of `text` in POST /v1/coach/messages (openapi.json). */
const val COACH_MESSAGE_MAX_LENGTH = 2000

/**
 * The coach of EXT-04: suggestions and replies come from the mock with Retrofit, and the whole
 * conversation lives in Room. The mock does not keep messages (SPEC p.4), so Room is the history.
 */
@Singleton
class CoachRepository @Inject constructor(
    private val api: CoachApi,
    private val dao: CoachMessageDao,
) {
    fun observeMessages(): Flow<List<CoachMessage>> =
        dao.observeAll().map { list -> list.map { it.toModel() } }

    /** Not cached: an empty list is a valid answer (scenario `empty`), a failure is not. */
    suspend fun getSuggestions(): Result<List<CoachSuggestion>> = runCatchingNetwork {
        val items = api.getSuggestions().data ?: throw JsonParseException("Response without data")
        items.mapNotNull { it.toModel() }
    }

    /**
     * Saves the user's message first, so it stays in the history even when the reply fails,
     * and then asks for the reply.
     */
    suspend fun send(text: String): Result<Unit> = runCatchingNetwork {
        dao.insert(CoachMessage(role = CoachRole.User, text = text, sentAtMillis = System.currentTimeMillis()).toEntity())
    }.fold(onSuccess = { requestReply(text) }, onFailure = { Result.failure(it) })

    /** Asks for the reply to a message already in the history; used again by "Retry". */
    suspend fun requestReply(text: String): Result<Unit> = runCatchingNetwork {
        val reply = api.sendMessage(CoachMessageRequestDto(text)).data
        val replyText = reply?.text?.takeIf { it.isNotBlank() } ?: throw JsonParseException("Response without text")
        dao.insert(
            CoachMessage(
                role = CoachRole.Assistant,
                text = replyText,
                sentAtMillis = System.currentTimeMillis(),
                isSimulated = reply.isSimulated == true,
            ).toEntity(),
        )
        Unit
    }

    private inline fun <T> runCatchingNetwork(block: () -> T): Result<T> = try {
        Result.success(block())
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Result.failure(e)
    }
}
