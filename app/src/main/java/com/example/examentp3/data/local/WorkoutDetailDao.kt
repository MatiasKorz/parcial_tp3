package com.example.examentp3.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutDetailDao {

    @Transaction
    @Query("SELECT * FROM workout_details WHERE workout_id = :workoutId")
    fun observe(workoutId: String): Flow<WorkoutWithExercises?>

    @Query("SELECT * FROM workout_exercises WHERE workout_id = :workoutId AND exercise_id = :exerciseId")
    fun observeExercise(workoutId: String, exerciseId: String): Flow<ExerciseEntity?>

    /** Only called after a successful download, so a failed refresh never touches the cache. */
    @Transaction
    suspend fun replace(workout: WorkoutDetailEntity, exercises: List<ExerciseEntity>) {
        upsertWorkout(workout)
        // Drops exercises the server no longer sends for this workout; other workouts are untouched.
        deleteExercises(workout.workoutId)
        insertExercises(exercises)
    }

    @Upsert
    suspend fun upsertWorkout(workout: WorkoutDetailEntity)

    @Query("DELETE FROM workout_exercises WHERE workout_id = :workoutId")
    suspend fun deleteExercises(workoutId: String)

    @Insert
    suspend fun insertExercises(exercises: List<ExerciseEntity>)
}
