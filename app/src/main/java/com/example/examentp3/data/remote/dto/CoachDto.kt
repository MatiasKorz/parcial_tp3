package com.example.examentp3.data.remote.dto

import com.example.examentp3.model.CoachSuggestion
import com.google.gson.annotations.SerializedName

/** Envelope of GET /v1/coach/suggestions: {"data": [...]}. */
data class CoachSuggestionListResponseDto(
    @SerializedName("data") val data: List<CoachSuggestionDto>?,
)

data class CoachSuggestionDto(
    @SerializedName("id") val id: String?,
    @SerializedName("text") val text: String?,
)

/** A suggestion without text has nothing to ask, so it is dropped. */
fun CoachSuggestionDto.toModel(): CoachSuggestion? {
    val safeText = text?.takeIf { it.isNotBlank() } ?: return null
    return CoachSuggestion(id = id ?: safeText, text = safeText)
}

/**
 * Body of POST /v1/coach/messages. `context` is optional and never sent: Gson skips null fields,
 * and the mock answers 400 to `"context": null` (REF-04).
 */
data class CoachMessageRequestDto(
    @SerializedName("text") val text: String,
)

/** Envelope of POST /v1/coach/messages: {"data": CoachReply}. */
data class CoachReplyResponseDto(
    @SerializedName("data") val data: CoachReplyDto?,
)

data class CoachReplyDto(
    @SerializedName("role") val role: String?,
    @SerializedName("text") val text: String?,
    @SerializedName("topic") val topic: String?,
    @SerializedName("isSimulated") val isSimulated: Boolean?,
)
