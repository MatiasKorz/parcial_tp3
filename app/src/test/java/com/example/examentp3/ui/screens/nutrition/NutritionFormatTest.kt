package com.example.examentp3.ui.screens.nutrition

import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Test

class NutritionFormatTest {

    @Test
    fun formatCalories_usesThousandsSeparator() {
        assertEquals("1,420", formatCalories(1420))
        assertEquals("520", formatCalories(520))
    }

    @Test
    fun formatGrams_wholeNumber_hasNoDecimals() {
        assertEquals("38", formatGrams(38.0))
    }

    @Test
    fun formatGrams_fraction_keepsOneDecimal() {
        assertEquals("12.5", formatGrams(12.5))
    }

    @Test
    fun formatGrams_roundsToOneDecimal() {
        assertEquals("38", formatGrams(37.96))
        assertEquals("12.3", formatGrams(12.34))
    }

    @Test
    fun formatMealDate_isShortEnglishDate() {
        assertEquals("Fri, 23 Oct", formatMealDate(LocalDate.of(2026, 10, 23)))
    }

    @Test
    fun formatTimelineDate_isLongEnglishDate() {
        assertEquals("Tuesday, 8 September", formatTimelineDate(LocalDate.of(2026, 9, 8)))
    }

    @Test
    fun utcMillis_roundTrip_keepsTheSameDate() {
        val date = LocalDate.of(2026, 10, 23)

        assertEquals(date, utcMillisToLocalDate(date.toUtcMillis()))
    }
}
