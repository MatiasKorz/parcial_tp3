package com.example.examentp3.viewmodel

import com.example.examentp3.model.Meal
import com.example.examentp3.model.MealType
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MealFormValidationTest {

    private val date = LocalDate.of(2026, 10, 20)

    private val validDraft = MealFormDraft(
        name = "Oatmeal with banana",
        date = date,
        type = MealType.BREAKFAST,
        calories = "420",
        protein = "12",
        carbs = "68.5",
        fat = "9",
    )

    private fun errorsOf(draft: MealFormDraft): MealFormErrors =
        (validateMealForm(draft) as MealFormResult.Invalid).errors

    private fun mealOf(draft: MealFormDraft): Meal = (validateMealForm(draft) as MealFormResult.Valid).meal

    @Test
    fun validDraft_mapsToMealWithAllFields() {
        assertEquals(
            Meal(
                id = 0,
                name = "Oatmeal with banana",
                date = date,
                type = MealType.BREAKFAST,
                calories = 420,
                proteinGrams = 12.0,
                carbsGrams = 68.5,
                fatGrams = 9.0,
            ),
            mealOf(validDraft),
        )
    }

    @Test
    fun emptyName_isRejected() {
        assertEquals(MealFieldError.NameRequired, errorsOf(validDraft.copy(name = "")).name)
    }

    @Test
    fun blankName_isRejected() {
        assertEquals(MealFieldError.NameRequired, errorsOf(validDraft.copy(name = "   ")).name)
    }

    @Test
    fun name_isTrimmedOnSave() {
        assertEquals("Oatmeal", mealOf(validDraft.copy(name = "  Oatmeal  ")).name)
    }

    @Test
    fun negativeCalories_isRejected() {
        assertEquals(MealFieldError.Negative, errorsOf(validDraft.copy(calories = "-5")).calories)
    }

    @Test
    fun negativeMacros_areRejected() {
        val errors = errorsOf(validDraft.copy(protein = "-1", carbs = "-0.5", fat = "-3"))

        assertEquals(MealFieldError.Negative, errors.protein)
        assertEquals(MealFieldError.Negative, errors.carbs)
        assertEquals(MealFieldError.Negative, errors.fat)
    }

    @Test
    fun nonNumericCarbs_isRejected() {
        assertEquals(MealFieldError.NotANumber, errorsOf(validDraft.copy(carbs = "abc")).carbs)
    }

    @Test
    fun nanInfinityAndExponent_areRejected() {
        assertEquals(MealFieldError.NotANumber, errorsOf(validDraft.copy(protein = "NaN")).protein)
        assertEquals(MealFieldError.NotANumber, errorsOf(validDraft.copy(carbs = "Infinity")).carbs)
        assertEquals(MealFieldError.NotANumber, errorsOf(validDraft.copy(fat = "1e3")).fat)
    }

    @Test
    fun emptyFat_isRejected() {
        assertEquals(MealFieldError.Required, errorsOf(validDraft.copy(fat = "")).fat)
    }

    @Test
    fun blankCalories_isRejected() {
        assertEquals(MealFieldError.Required, errorsOf(validDraft.copy(calories = "  ")).calories)
    }

    @Test
    fun decimalCalories_isRejectedAsNotWholeNumber() {
        assertEquals(MealFieldError.NotAWholeNumber, errorsOf(validDraft.copy(calories = "420.5")).calories)
    }

    @Test
    fun caloriesTooLargeForInt_isRejected() {
        assertEquals(MealFieldError.NotANumber, errorsOf(validDraft.copy(calories = "99999999999")).calories)
    }

    @Test
    fun commaDecimal_isAccepted() {
        assertEquals(12.5, mealOf(validDraft.copy(protein = "12,5")).proteinGrams, 0.0)
    }

    @Test
    fun zeroValues_areAccepted() {
        val meal = mealOf(validDraft.copy(calories = "0", protein = "0", carbs = "0.0", fat = "0"))

        assertEquals(0, meal.calories)
        assertEquals(0.0, meal.carbsGrams, 0.0)
    }

    @Test
    fun minusZero_isAccepted() {
        val meal = mealOf(validDraft.copy(calories = "-0", fat = "-0.0"))

        assertEquals(0, meal.calories)
        assertEquals(0.0, meal.fatGrams, 0.0)
        assertFalse(1.0 / meal.fatGrams < 0) // stored as +0.0, not -0.0
    }

    @Test
    fun surroundingSpaces_areIgnored() {
        assertEquals(420, mealOf(validDraft.copy(calories = " 420 ")).calories)
    }

    @Test
    fun allFieldsAreValidatedAtOnce() {
        val errors = errorsOf(MealFormDraft(date = date))

        assertEquals(MealFieldError.NameRequired, errors.name)
        assertEquals(MealFieldError.Required, errors.calories)
        assertEquals(MealFieldError.Required, errors.protein)
        assertEquals(MealFieldError.Required, errors.carbs)
        assertEquals(MealFieldError.Required, errors.fat)
        assertTrue(errors.hasErrors)
    }

    @Test
    fun previewTotals_countsInvalidFieldsAsZero() {
        val draft = MealFormDraft(date = date, calories = "12.5", protein = "20", carbs = "-4", fat = "3,5")

        assertEquals(MealTotals(calories = 0, proteinGrams = 20.0, carbsGrams = 0.0, fatGrams = 3.5), draft.previewTotals())
    }
}
