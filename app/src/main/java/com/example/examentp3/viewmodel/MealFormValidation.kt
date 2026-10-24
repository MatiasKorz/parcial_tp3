package com.example.examentp3.viewmodel

import androidx.annotation.StringRes
import com.example.examentp3.R
import com.example.examentp3.model.Meal
import com.example.examentp3.model.MealType
import java.time.LocalDate

// Pure Kotlin validation of the RF03 manual form (no android.*), so it runs in JUnit without an emulator.

/** Raw text of the RF03 form, as typed. Numbers stay as text until they are validated. */
data class MealFormDraft(
    val name: String = "",
    val date: LocalDate,
    val type: MealType = MealType.LUNCH,
    val calories: String = "",
    val protein: String = "",
    val carbs: String = "",
    val fat: String = "",
)

enum class MealFieldError(@param:StringRes val message: Int) {
    NameRequired(R.string.food_confirmation_error_name_required),
    Required(R.string.food_confirmation_error_required),
    NotANumber(R.string.food_confirmation_error_not_a_number),
    NotAWholeNumber(R.string.food_confirmation_error_whole_number),
    Negative(R.string.food_confirmation_error_negative),
}

data class MealFormErrors(
    val name: MealFieldError? = null,
    val calories: MealFieldError? = null,
    val protein: MealFieldError? = null,
    val carbs: MealFieldError? = null,
    val fat: MealFieldError? = null,
) {
    val hasErrors: Boolean get() = listOf(name, calories, protein, carbs, fat).any { it != null }
}

sealed interface MealFormResult {
    data class Valid(val meal: Meal) : MealFormResult
    data class Invalid(val errors: MealFormErrors) : MealFormResult
}

/** Totals shown in the MEAL TOTALS panel. */
data class MealTotals(
    val calories: Int = 0,
    val proteinGrams: Double = 0.0,
    val carbsGrams: Double = 0.0,
    val fatGrams: Double = 0.0,
)

// Digits with an optional decimal part. toDouble() alone would accept "NaN", "Infinity" and "1e3".
private val decimalPattern = Regex("""^-?\d+(\.\d+)?$""")
private val wholePattern = Regex("""^-?\d+$""")

/** Result of parsing one numeric field: either a value or the error to show under it. */
private sealed interface ParsedNumber<out T> {
    data class Value<T>(val value: T) : ParsedNumber<T>
    data class Error(val error: MealFieldError) : ParsedNumber<Nothing>
}

/** Accepts "." or "," as decimal separator; surrounding spaces are ignored. */
private fun normalize(text: String): String = text.trim().replace(',', '.')

private fun parseGrams(text: String): ParsedNumber<Double> {
    val value = normalize(text)
    return when {
        value.isEmpty() -> ParsedNumber.Error(MealFieldError.Required)
        // A very long digit string still matches the pattern but overflows to Infinity.
        !decimalPattern.matches(value) || !value.toDouble().isFinite() -> ParsedNumber.Error(MealFieldError.NotANumber)
        // "-0" and "-0.0" are zero, which is allowed.
        value.toDouble() < 0 -> ParsedNumber.Error(MealFieldError.Negative)
        // Adding 0.0 turns -0.0 into 0.0.
        else -> ParsedNumber.Value(value.toDouble() + 0.0)
    }
}

private fun parseCalories(text: String): ParsedNumber<Int> {
    val value = normalize(text)
    return when {
        value.isEmpty() -> ParsedNumber.Error(MealFieldError.Required)
        !decimalPattern.matches(value) -> ParsedNumber.Error(MealFieldError.NotANumber)
        value.toDouble() < 0 -> ParsedNumber.Error(MealFieldError.Negative)
        // The model stores calories as Int.
        !wholePattern.matches(value) -> ParsedNumber.Error(MealFieldError.NotAWholeNumber)
        else -> value.toIntOrNull()?.let { ParsedNumber.Value(it) }
            ?: ParsedNumber.Error(MealFieldError.NotANumber) // does not fit in an Int
    }
}

private fun <T> ParsedNumber<T>.errorOrNull(): MealFieldError? = (this as? ParsedNumber.Error)?.error

private fun <T> ParsedNumber<T>.valueOr(default: T): T = (this as? ParsedNumber.Value)?.value ?: default

/** Validates every field at once, so all errors are shown after the first attempt to save. */
fun validateMealForm(draft: MealFormDraft): MealFormResult {
    val name = draft.name.trim()
    val calories = parseCalories(draft.calories)
    val protein = parseGrams(draft.protein)
    val carbs = parseGrams(draft.carbs)
    val fat = parseGrams(draft.fat)

    val errors = MealFormErrors(
        name = if (name.isEmpty()) MealFieldError.NameRequired else null,
        calories = calories.errorOrNull(),
        protein = protein.errorOrNull(),
        carbs = carbs.errorOrNull(),
        fat = fat.errorOrNull(),
    )
    if (errors.hasErrors) return MealFormResult.Invalid(errors)

    return MealFormResult.Valid(
        Meal(
            name = name,
            date = draft.date,
            type = draft.type,
            calories = calories.valueOr(0),
            proteinGrams = protein.valueOr(0.0),
            carbsGrams = carbs.valueOr(0.0),
            fatGrams = fat.valueOr(0.0),
        ),
    )
}

/** Live totals while typing: an empty or invalid field counts as 0. */
fun MealFormDraft.previewTotals(): MealTotals = MealTotals(
    calories = parseCalories(calories).valueOr(0),
    proteinGrams = parseGrams(protein).valueOr(0.0),
    carbsGrams = parseGrams(carbs).valueOr(0.0),
    fatGrams = parseGrams(fat).valueOr(0.0),
)
