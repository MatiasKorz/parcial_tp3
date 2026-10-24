package com.example.examentp3.data.repository

import com.example.examentp3.data.local.WorkoutDao
import com.example.examentp3.data.local.WorkoutDetailDao
import com.example.examentp3.data.local.toEntity
import com.example.examentp3.data.local.toExerciseEntities
import com.example.examentp3.data.local.toModel
import com.example.examentp3.data.remote.WorkoutApi
import com.example.examentp3.data.remote.dto.toModel
import com.example.examentp3.model.Exercise
import com.example.examentp3.model.WorkoutDetail
import com.example.examentp3.model.WorkoutSummary
import com.google.gson.JsonParseException
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Singleton
class WorkoutRepository @Inject constructor(
    private val api: WorkoutApi,
    private val workoutDao: WorkoutDao,
    private val workoutDetailDao: WorkoutDetailDao,
) {
    /** Room is the single source the UI reads, so the catalog survives without the server (RF02). */
    fun observeWorkouts(): Flow<List<WorkoutSummary>> =
        workoutDao.observeAll().map { list -> list.map { it.toModel() } }

    /** Downloads the catalog and caches it. Failure covers HTTP errors, no connection, timeouts and malformed JSON. */
    suspend fun refreshWorkouts(): Result<Unit> = try {
        val items = api.getWorkouts().data
            ?: throw JsonParseException("Response without data")
        workoutDao.upsertAll(items.mapNotNull { it.toModel()?.toEntity() })
        Result.success(Unit)
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Result.failure(e)
    }

    /** Emits null while the detail was never downloaded (RF02). */
    fun observeWorkoutDetail(workoutId: String): Flow<WorkoutDetail?> =
        workoutDetailDao.observe(workoutId).map { it?.toModel() }

    fun observeExercise(workoutId: String, exerciseId: String): Flow<Exercise?> =
        workoutDetailDao.observeExercise(workoutId, exerciseId).map { it?.toModel() }

    /** Downloads one detail and caches it. On failure the cached copy, if any, stays as it was. */
    suspend fun refreshWorkoutDetail(workoutId: String): Result<Unit> = try {
        val workout = api.getWorkout(workoutId).data?.toModel()
            ?: throw JsonParseException("Response without data")
        workoutDetailDao.replace(workout.toEntity(), workout.toExerciseEntities())
        Result.success(Unit)
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Result.failure(e)
    }
}
