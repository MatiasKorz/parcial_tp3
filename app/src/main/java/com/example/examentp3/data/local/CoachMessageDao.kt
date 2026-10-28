package com.example.examentp3.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CoachMessageDao {

    @Insert
    suspend fun insert(message: CoachMessageEntity): Long

    // The id follows insertion order, so it also breaks ties between messages of the same millisecond.
    @Query("SELECT * FROM coach_messages ORDER BY sent_at_millis ASC, id ASC")
    fun observeAll(): Flow<List<CoachMessageEntity>>
}
