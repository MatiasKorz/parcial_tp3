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

    // There is no meal time in Room, so meals of a day keep their insertion order.
    @Query("SELECT * FROM meals WHERE date_epoch_day = :epochDay ORDER BY id ASC")
    fun observeByDate(epochDay: Long): Flow<List<MealEntity>>

    @Query("SELECT * FROM meals WHERE id = :id")
    fun observeById(id: Long): Flow<MealEntity?>

    /** Returns the number of deleted rows: 0 when the id does not exist. */
    @Query("DELETE FROM meals WHERE id = :id")
    suspend fun deleteById(id: Long): Int
}
