package com.example.examentp3.ui.screens.nutrition

import java.text.NumberFormat
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.roundToLong

// Pure Kotlin/Java formatting (no android.*) so it is unit-tested. Locale is fixed because the
// app copy is in English and Figma uses "1,420" and "Tuesday, 8 September".

private val mealDateFormatter = DateTimeFormatter.ofPattern("EEE, d MMM", Locale.ENGLISH)
private val timelineDateFormatter = DateTimeFormatter.ofPattern("EEEE, d MMMM", Locale.ENGLISH)

/** 1420 -> "1,420" (thousands separator as in Figma). */
internal fun formatCalories(calories: Int): String = NumberFormat.getIntegerInstance(Locale.US).format(calories)

/** 38.0 -> "38", 12.5 -> "12.5": one decimal at most, none when the value is whole. */
internal fun formatGrams(grams: Double): String {
    val tenths = (grams * 10).roundToLong()
    return if (tenths % 10 == 0L) (tenths / 10).toString() else String.format(Locale.US, "%.1f", tenths / 10.0)
}

/** Short date of a meal, e.g. "Thu, 23 Oct". */
internal fun formatMealDate(date: LocalDate): String = mealDateFormatter.format(date)

/** Long date of the list, e.g. "Thursday, 23 October" (same format as the Nutrition dashboard). */
internal fun formatTimelineDate(date: LocalDate): String = timelineDateFormatter.format(date)

// Material 3 DatePicker works with milliseconds at UTC midnight. Converting with the local zone
// would move the date one day back in UTC-3, so both directions use UTC.

internal fun utcMillisToLocalDate(millis: Long): LocalDate =
    Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC).toLocalDate()

internal fun LocalDate.toUtcMillis(): Long = atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
