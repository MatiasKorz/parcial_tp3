package com.example.examentp3.model

import kotlin.math.roundToInt

/** A food of GET /v1/foods, with its nutrition per 100 g (EXT-05). */
data class Food(
    val id: String,
    val name: String,
    val kcalPer100g: Double,
    val proteinPer100g: Double,
    val carbsPer100g: Double,
    val fatPer100g: Double,
)

/** One page of results; [total] counts every match, not only this page. */
data class FoodPage(
    val items: List<Food>,
    val page: Int,
    val pageSize: Int,
    val total: Int,
) {
    val hasMore: Boolean get() = page * pageSize < total
}

/** Quantity steps of the "-" and "+" buttons (open question in EXT-05, agreed with the team). */
const val PORTION_DEFAULT_GRAMS = 100
const val PORTION_STEP_GRAMS = 10
const val PORTION_MIN_GRAMS = 10

/** A chosen food and its quantity; the nutrition of the portion is computed from the values per 100 g. */
data class FoodPortion(val food: Food, val grams: Int = PORTION_DEFAULT_GRAMS) {
    private val factor: Double get() = grams / 100.0

    val calories: Int get() = (food.kcalPer100g * factor).roundToInt()
    val proteinGrams: Double get() = (food.proteinPer100g * factor).roundToOneDecimal()
    val carbsGrams: Double get() = (food.carbsPer100g * factor).roundToOneDecimal()
    val fatGrams: Double get() = (food.fatPer100g * factor).roundToOneDecimal()

    fun increase(): FoodPortion = copy(grams = grams + PORTION_STEP_GRAMS)
    fun decrease(): FoodPortion = copy(grams = (grams - PORTION_STEP_GRAMS).coerceAtLeast(PORTION_MIN_GRAMS))
}

private fun Double.roundToOneDecimal(): Double = (this * 10).roundToInt() / 10.0
