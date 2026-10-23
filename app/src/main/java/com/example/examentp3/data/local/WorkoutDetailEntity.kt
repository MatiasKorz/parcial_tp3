package com.example.examentp3.data.local

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.example.examentp3.model.Exercise
import com.example.examentp3.model.WorkoutDetail

/** Cached GET /v1/workouts/{workoutId}, so a detail already opened works without the server (RF02). */
@Entity(tableName = "workout_details")
data class WorkoutDetailEntity(
    @PrimaryKey @ColumnInfo(name = "workout_id") val workoutId: String,
    val name: String,
    val level: String,
    @ColumnInfo(name = "duration_minutes") val durationMinutes: Int,
    val description: String,
)

/**
 * Exercise ids repeat across workouts (e01 is in w01 and w03), so the key is the pair.
 * [position] keeps the order the API sent.
 */
@Entity(tableName = "workout_exercises", primaryKeys = ["workout_id", "exercise_id"])
data class ExerciseEntity(
    @ColumnInfo(name = "workout_id") val workoutId: String,
    @ColumnInfo(name = "exercise_id") val exerciseId: String,
    val position: Int,
    val name: String,
    @ColumnInfo(name = "muscle_group") val muscleGroup: String,
    val sets: Int,
    val reps: Int,
    @ColumnInfo(name = "rest_seconds") val restSeconds: Int,
    val instructions: String,
    val tip: String,
)

data class WorkoutWithExercises(
    @Embedded val workout: WorkoutDetailEntity,
    @Relation(parentColumn = "workout_id", entityColumn = "workout_id")
    val exercises: List<ExerciseEntity>,
)

fun WorkoutWithExercises.toModel(): WorkoutDetail = WorkoutDetail(
    id = workout.workoutId,
    name = workout.name,
    level = workout.level,
    durationMinutes = workout.durationMinutes,
    description = workout.description,
    exercises = exercises.sortedBy { it.position }.map { it.toModel() },
)

fun ExerciseEntity.toModel(): Exercise = Exercise(
    id = exerciseId,
    name = name,
    muscleGroup = muscleGroup,
    sets = sets,
    reps = reps,
    restSeconds = restSeconds,
    instructions = instructions,
    tip = tip,
)

fun WorkoutDetail.toEntity(): WorkoutDetailEntity = WorkoutDetailEntity(
    workoutId = id,
    name = name,
    level = level,
    durationMinutes = durationMinutes,
    description = description,
)

fun WorkoutDetail.toExerciseEntities(): List<ExerciseEntity> = exercises.mapIndexed { index, exercise ->
    ExerciseEntity(
        workoutId = id,
        exerciseId = exercise.id,
        position = index,
        name = exercise.name,
        muscleGroup = exercise.muscleGroup,
        sets = exercise.sets,
        reps = exercise.reps,
        restSeconds = exercise.restSeconds,
        instructions = exercise.instructions,
        tip = exercise.tip,
    )
}
