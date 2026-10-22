package com.example.examentp3.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MealDao {

    @Insert
    suspend fun insert(meal: MealEntity): Long

    @Query("SELECT * FROM meals ORDER BY date_epoch_day DESC, id DESC")
    fun observeAll(): Flow<List<MealEntity>>
}
