package com.example.examentp3.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Version 2 adds the workout cache and version 3 the workout detail cache (NUC-02),
 * both preserving all existing meal data.
 */
@Database(
    entities = [MealEntity::class, WorkoutSummaryEntity::class, WorkoutDetailEntity::class, ExerciseEntity::class],
    version = 3,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun mealDao(): MealDao
    abstract fun workoutDao(): WorkoutDao
    abstract fun workoutDetailDao(): WorkoutDetailDao

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

        val MIGRATION_2_3: Migration = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `workout_details` (
                        `workout_id` TEXT NOT NULL,
                        `name` TEXT NOT NULL,
                        `level` TEXT NOT NULL,
                        `duration_minutes` INTEGER NOT NULL,
                        `description` TEXT NOT NULL,
                        PRIMARY KEY(`workout_id`)
                    )
                    """.trimIndent(),
                )
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `workout_exercises` (
                        `workout_id` TEXT NOT NULL,
                        `exercise_id` TEXT NOT NULL,
                        `position` INTEGER NOT NULL,
                        `name` TEXT NOT NULL,
                        `muscle_group` TEXT NOT NULL,
                        `sets` INTEGER NOT NULL,
                        `reps` INTEGER NOT NULL,
                        `rest_seconds` INTEGER NOT NULL,
                        `instructions` TEXT NOT NULL,
                        `tip` TEXT NOT NULL,
                        PRIMARY KEY(`workout_id`, `exercise_id`)
                    )
                    """.trimIndent(),
                )
            }
        }
    }
}
