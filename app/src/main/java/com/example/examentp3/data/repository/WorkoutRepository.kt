package com.example.examentp3.data.repository

import com.example.examentp3.data.remote.WorkoutApi
import com.example.examentp3.data.remote.dto.toModel
import com.example.examentp3.model.WorkoutSummary
import com.google.gson.JsonParseException
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.cancellation.CancellationException

@Singleton
class WorkoutRepository @Inject constructor(
    private val api: WorkoutApi,
) {
    /** Failure covers HTTP errors, no connection, timeouts and malformed JSON. */
    suspend fun getWorkouts(): Result<List<WorkoutSummary>> = try {
        val items = api.getWorkouts().data
            ?: throw JsonParseException("Response without data")
        Result.success(items.mapNotNull { it.toModel() })
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Result.failure(e)
    }
}