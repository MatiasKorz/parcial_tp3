package com.example.examentp3.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/** Version 2 adds the workout cache while preserving all existing meal data. */
@Database(entities = [MealEntity::class, WorkoutSummaryEntity::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun mealDao(): MealDao
    abstract fun workoutDao(): WorkoutDao

    companion object {
        // IF NOT EXISTS also supports development installs that already cached workouts at v1.
        val MIGRATION_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `workouts` (
                        `id` TEXT NOT NULL,
                        `name` TEXT NOT NULL,
                        `level` TEXT NOT NULL,
                        `duration_minutes` INTEGER NOT NULL,
                        `exercise_count` INTEGER NOT NULL,
                        PRIMARY KEY(`id`)
                    )
                    """.trimIndent(),
                )
            }
        }
    }
}
