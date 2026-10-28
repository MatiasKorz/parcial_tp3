package com.example.examentp3.model

enum class CoachRole { User, Assistant }

/** One message of the coach conversation, kept in Room (EXT-04). */
data class CoachMessage(
    val id: Long = 0,
    val role: CoachRole,
    val text: String,
    /** Epoch milliseconds when it was sent or received. */
    val sentAtMillis: Long,
    /** Only assistant replies: the mock marks them as simulated (SPEC p.4). */
    val isSimulated: Boolean = false,
)

/** A suggested question from GET /v1/coach/suggestions. */
data class CoachSuggestion(val id: String, val text: String)
