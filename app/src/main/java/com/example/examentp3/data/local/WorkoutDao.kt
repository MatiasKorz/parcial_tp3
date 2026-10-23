package com.example.examentp3.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutDao {

    // Upsert, not replace-all: an empty or failed refresh never wipes the cached catalog.
    @Upsert
    suspend fun upsertAll(workouts: List<WorkoutSummaryEntity>)

    @Query("SELECT * FROM workouts ORDER BY id")
    fun observeAll(): Flow<List<WorkoutSummaryEntity>>
}
