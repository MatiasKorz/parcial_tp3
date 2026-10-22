package com.example.examentp3.data.repository

import com.example.examentp3.data.local.MealDao
import com.example.examentp3.data.local.toEntity
import com.example.examentp3.data.local.toModel
import com.example.examentp3.model.Meal
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Singleton
class MealRepository @Inject constructor(private val mealDao: MealDao) {

    fun observeMeals(): Flow<List<Meal>> = mealDao.observeAll().map { list -> list.map { it.toModel() } }

    suspend fun addMeal(meal: Meal): Long = mealDao.insert(meal.toEntity())
}
