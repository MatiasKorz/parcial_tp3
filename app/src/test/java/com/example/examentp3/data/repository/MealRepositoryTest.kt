package com.example.examentp3.data.repository

import com.example.examentp3.data.local.FakeMealDao
import com.example.examentp3.model.Meal
import com.example.examentp3.model.MealType
import java.time.LocalDate
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class MealRepositoryTest {

    private val today = LocalDate.of(2026, 10, 23)
    private val yesterday = today.minusDays(1)

    private val dao = FakeMealDao()
    private val repository = MealRepository(dao)

    private fun meal(name: String, date: LocalDate = today, calories: Int = 100) = Meal(
        name = name,
        date = date,
        type = MealType.LUNCH,
        calories = calories,
        proteinGrams = 10.0,
        carbsGrams = 20.0,
        fatGrams = 5.0,
    )

    @Test
    fun observeMealsByDate_excludesMealsOfOtherDates() = runBlocking {
        repository.addMeal(meal("Today meal"))
        repository.addMeal(meal("Yesterday meal", date = yesterday))

        val meals = repository.observeMealsByDate(today).first()

        assertEquals(listOf("Today meal"), meals.map { it.name })
    }

    @Test
    fun observeMealsByDate_keepsInsertionOrder() = runBlocking {
        repository.addMeal(meal("First"))
        repository.addMeal(meal("Second"))

        assertEquals(listOf("First", "Second"), repository.observeMealsByDate(today).first().map { it.name })
    }

    @Test
    fun observeDailyTotals_increasesAfterAddMeal() = runBlocking {
        repository.addMeal(meal("Breakfast", calories = 420))
        val before = repository.observeDailyTotals(today).first()

        repository.addMeal(meal("Lunch", calories = 520))
        val after = repository.observeDailyTotals(today).first()

        assertEquals(420, before.calories)
        assertEquals(940, after.calories)
        assertEquals(20.0, after.proteinGrams, 0.0)
    }

    @Test
    fun observeDailyTotals_decreasesAfterDeleteMeal() = runBlocking {
        repository.addMeal(meal("Breakfast", calories = 420))
        val lunchId = repository.addMeal(meal("Lunch", calories = 520))

        repository.deleteMeal(lunchId)

        assertEquals(420, repository.observeDailyTotals(today).first().calories)
    }

    @Test
    fun observeDailyTotals_ignoresOtherDates() = runBlocking {
        repository.addMeal(meal("Yesterday", date = yesterday, calories = 300))

        assertEquals(0, repository.observeDailyTotals(today).first().calories)
    }

    @Test
    fun deleteMeal_existingId_returnsTrue() = runBlocking {
        val id = repository.addMeal(meal("Lunch"))

        assertTrue(repository.deleteMeal(id))
        assertTrue(dao.entities.isEmpty())
    }

    @Test
    fun deleteMeal_unknownId_returnsFalse() = runBlocking {
        repository.addMeal(meal("Lunch"))

        assertFalse(repository.deleteMeal(999))
        assertEquals(1, dao.entities.size)
    }

    @Test
    fun observeMeal_returnsTheMealWithThatId() = runBlocking {
        repository.addMeal(meal("First"))
        val secondId = repository.addMeal(meal("Second"))

        assertEquals("Second", repository.observeMeal(secondId).first()?.name)
    }

    @Test
    fun observeMeal_afterDelete_emitsNull() = runBlocking {
        val id = repository.addMeal(meal("Lunch"))

        repository.deleteMeal(id)

        assertNull(repository.observeMeal(id).first())
    }
}
