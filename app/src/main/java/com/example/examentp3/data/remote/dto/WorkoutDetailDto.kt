package com.example.examentp3.data.remote.dto

import com.example.examentp3.model.Exercise
import com.example.examentp3.model.WorkoutDetail
import com.google.gson.annotations.SerializedName

/** Envelope of GET /v1/workouts/{workoutId}: {"data": {...}}. */
data class WorkoutDetailResponseDto(
    @SerializedName("data") val data: WorkoutDetailDto?,
)

data class WorkoutDetailDto(
    @SerializedName("id") val id: String?,
    @SerializedName("name") val name: String?,
    @SerializedName("level") val level: String?,
    @SerializedName("durationMinutes") val durationMinutes: Int?,
    @SerializedName("description") val description: String?,
    @SerializedName("exercises") val exercises: List<ExerciseDto>?,
)

data class ExerciseDto(
    @SerializedName("id") val id: String?,
    @SerializedName("name") val name: String?,
    @SerializedName("muscleGroup") val muscleGroup: String?,
    @SerializedName("sets") val sets: Int?,
    @SerializedName("reps") val reps: Int?,
    @SerializedName("restSeconds") val restSeconds: Int?,
    @SerializedName("instructions") val instructions: String?,
    @SerializedName("tip") val tip: String?,
)

/** Without an id the detail cannot be cached or reopened, so it is dropped. */
fun WorkoutDetailDto.toModel(): WorkoutDetail? {
    val safeId = id ?: return null
    return WorkoutDetail(
        id = safeId,
        name = name.orEmpty(),
        level = level.orEmpty(),
        durationMinutes = durationMinutes ?: 0,
        description = description.orEmpty(),
        exercises = exercises.orEmpty().mapNotNull { it.toModel() },
    )
}

/** An exercise without an id cannot open its guide, so it is dropped. */
fun ExerciseDto.toModel(): Exercise? {
    val safeId = id ?: return null
    return Exercise(
        id = safeId,
        name = name.orEmpty(),
        muscleGroup = muscleGroup.orEmpty(),
        sets = sets ?: 0,
        reps = reps ?: 0,
        restSeconds = restSeconds ?: 0,
        instructions = instructions.orEmpty(),
        tip = tip.orEmpty(),
    )
}
