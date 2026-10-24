package com.example.examentp3.model

data class WorkoutDetail(
    val id: String,
    val name: String,
    val level: String,
    val durationMinutes: Int,
    val description: String,
    val exercises: List<Exercise>,
)

data class Exercise(
    val id: String,
    val name: String,
    val muscleGroup: String,
    val sets: Int,
    val reps: Int,
    val restSeconds: Int,
    val instructions: String,
    val tip: String,
)
