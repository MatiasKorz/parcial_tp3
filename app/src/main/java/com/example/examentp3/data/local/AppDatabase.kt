package com.example.examentp3.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * Schema policy: version 1 during development; after a schema change, uninstall the app.
 * From the delivery on, every schema change bumps [version] with a Migration.
 * fallbackToDestructiveMigration is not used because it would delete the user's meals (RF04).
 */
@Database(entities = [MealEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun mealDao(): MealDao
}
