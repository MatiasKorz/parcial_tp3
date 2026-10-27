package com.example.examentp3.model

import org.junit.Assert.assertEquals
import org.junit.Test

/** Nutrition of a portion computed from the values per 100 g of GET /v1/foods (EXT-05). */
class FoodPortionTest {

    private val chicken = Food("f01", "Chicken breast", kcalPer100g = 165.0, proteinPer100g = 31.0, carbsPer100g = 0.0, fatPer100g = 3.6)

    @Test
    fun defaultPortion_is100gAndMatchesThePer100gValues() {
        val portion = FoodPortion(chicken)
        assertEquals(100, portion.grams)
        assertEquals(165, portion.calories)
        assertEquals(31.0, portion.proteinGrams, 0.0)
        assertEquals(3.6, portion.fatGrams, 0.0)
    }

    @Test
    fun portion_scalesWithTheGrams() {
        val portion = FoodPortion(chicken, grams = 180)
        assertEquals(297, portion.calories)
        assertEquals(55.8, portion.proteinGrams, 0.0)
        assertEquals(6.5, portion.fatGrams, 0.0) // 6.48 rounded to one decimal
    }

    @Test
    fun calories_roundToTheNearestWholeNumber() {
        assertEquals(17, FoodPortion(chicken, grams = 10).calories) // 16.5
    }

    @Test
    fun increaseAndDecrease_moveTenGrams() {
        assertEquals(110, FoodPortion(chicken).increase().grams)
        assertEquals(90, FoodPortion(chicken).decrease().grams)
    }

    @Test
    fun decrease_neverGoesBelowTenGrams() {
        assertEquals(10, FoodPortion(chicken, grams = 10).decrease().grams)
    }

    @Test
    fun page_hasMoreWhileTheTotalIsNotReached() {
        assertEquals(true, FoodPage(emptyList(), page = 1, pageSize = 10, total = 12).hasMore)
        assertEquals(false, FoodPage(emptyList(), page = 2, pageSize = 10, total = 12).hasMore)
        assertEquals(false, FoodPage(emptyList(), page = 1, pageSize = 10, total = 0).hasMore)
    }
}
