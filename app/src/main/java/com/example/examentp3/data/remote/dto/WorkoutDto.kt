package com.example.examentp3.data.remote.dto

import com.example.examentp3.model.WorkoutSummary
import com.google.gson.annotations.SerializedName

/** Envelope of a successful response: {"data": [...]}. */
data class WorkoutListResponseDto(
    @SerializedName("data") val data: List<WorkoutSummaryDto>?,
)

data class WorkoutSummaryDto(
    @SerializedName("id") val id: String?,
    @SerializedName("name") val name: String?,
    @SerializedName("level") val level: String?,
    @SerializedName("durationMinutes") val durationMinutes: Int?,
    @SerializedName("exerciseCount") val exerciseCount: Int?,
)

/** A workout without an id cannot open its detail, so it is dropped. */
fun WorkoutSummaryDto.toModel(): WorkoutSummary? {
    val safeId = id ?: return null
    return WorkoutSummary(
        id = safeId,
        name = name.orEmpty(),
        level = level.orEmpty(),
        durationMinutes = durationMinutes ?: 0,
        exerciseCount = exerciseCount ?: 0,
    )
}