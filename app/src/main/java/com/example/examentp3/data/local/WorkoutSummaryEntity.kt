package com.example.examentp3.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.examentp3.model.WorkoutSummary

/** Cached row of the GET /v1/workouts catalog (RF02). */
@Entity(tableName = "workouts")
data class WorkoutSummaryEntity(
    @PrimaryKey val id: String,
    val name: String,
    val level: String,
    @ColumnInfo(name = "duration_minutes") val durationMinutes: Int,
    @ColumnInfo(name = "exercise_count") val exerciseCount: Int,
)

fun WorkoutSummaryEntity.toModel(): WorkoutSummary = WorkoutSummary(
    id = id,
    name = name,
    level = level,
    durationMinutes = durationMinutes,
    exerciseCount = exerciseCount,
)

fun WorkoutSummary.toEntity(): WorkoutSummaryEntity = WorkoutSummaryEntity(
    id = id,
    name = name,
    level = level,
    durationMinutes = durationMinutes,
    exerciseCount = exerciseCount,
)
