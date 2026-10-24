package com.example.examentp3.data.repository

import com.example.examentp3.data.local.MealDao
import com.example.examentp3.data.local.toEntity
import com.example.examentp3.data.local.toModel
import com.example.examentp3.model.DailyTotals
import com.example.examentp3.model.Meal
import com.example.examentp3.model.toDailyTotals
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Singleton
class MealRepository @Inject constructor(private val mealDao: MealDao) {

    fun observeMeals(): Flow<List<Meal>> = mealDao.observeAll().map { list -> list.map { it.toModel() } }

    suspend fun addMeal(meal: Meal): Long = mealDao.insert(meal.toEntity())

    /** Meals of [date] in insertion order. Room emits again whenever the meals table changes. */
    fun observeMealsByDate(date: LocalDate): Flow<List<Meal>> =
        mealDao.observeByDate(date.toEpochDay()).map { list -> list.map { it.toModel() } }

    /** Daily total of RF04, computed from the same query as the list so both always agree. */
    fun observeDailyTotals(date: LocalDate): Flow<DailyTotals> =
        observeMealsByDate(date).map { it.toDailyTotals() }

    /** Emits null when the meal does not exist or was deleted. */
    fun observeMeal(id: Long): Flow<Meal?> = mealDao.observeById(id).map { it?.toModel() }

    /** Returns true if a meal was deleted, false if [id] did not exist. */
    suspend fun deleteMeal(id: Long): Boolean = mealDao.deleteById(id) > 0
}
