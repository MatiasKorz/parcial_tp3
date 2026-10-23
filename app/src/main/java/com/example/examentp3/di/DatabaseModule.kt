package com.example.examentp3.di

import android.content.Context
import androidx.room.Room
import com.example.examentp3.data.local.AppDatabase
import com.example.examentp3.data.local.MealDao
import com.example.examentp3.data.local.WorkoutDao
import com.example.examentp3.data.local.WorkoutDetailDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "app.db")
            .addMigrations(AppDatabase.MIGRATION_1_2, AppDatabase.MIGRATION_2_3)
            .build()

    @Provides
    fun provideMealDao(db: AppDatabase): MealDao = db.mealDao()

    @Provides
    fun provideWorkoutDao(db: AppDatabase): WorkoutDao = db.workoutDao()

    @Provides
    fun provideWorkoutDetailDao(db: AppDatabase): WorkoutDetailDao = db.workoutDetailDao()
}
