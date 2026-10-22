package com.example.examentp3.data.repository

import com.example.examentp3.data.local.WorkoutDao
import com.example.examentp3.data.local.toEntity
import com.example.examentp3.data.local.toModel
import com.example.examentp3.data.remote.WorkoutApi
import com.example.examentp3.data.remote.dto.toModel
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
}
