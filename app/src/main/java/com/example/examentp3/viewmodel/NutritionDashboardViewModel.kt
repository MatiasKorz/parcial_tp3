package com.example.examentp3.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.examentp3.data.repository.MealRepository
import com.example.examentp3.model.DailyTotals
import com.example.examentp3.model.Meal
import com.example.examentp3.model.MealType
import com.example.examentp3.model.toDailyTotals
import com.example.examentp3.model.toMealSections
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/** Daily goals of the dashboard. No requirement defines real goals, so the app shows sample ones. */
data class NutritionGoals(
    val calories: Int,
    val proteinGrams: Int,
    val carbsGrams: Int,
    val fatGrams: Int,
)

/** Sample goals copied from the Figma frame "Nutrition / 01 — Dashboard". */
val SampleNutritionGoals = NutritionGoals(calories = 2100, proteinGrams = 140, carbsGrams = 220, fatGrams = 65)

/** One row of "Today's Meals": a saved meal, or a meal type with nothing logged yet. */
sealed interface DashboardMealRow {
    data class Logged(val meal: Meal) : DashboardMealRow
    data class NotLogged(val type: MealType) : DashboardMealRow
}

data class NutritionDashboardUiState(
    val date: LocalDate,
    /** Daily total of RF04 (NUC-05), from the meals saved in Room for [date]. */
    val totals: DailyTotals,
    val goals: NutritionGoals = SampleNutritionGoals,
    val mealRows: List<DashboardMealRow>,
    val isLoading: Boolean = false,
) {
    val caloriesPercent: Int get() = caloriesPercent(totals.calories, goals.calories)
}

/** Pure builder of the dashboard state, kept outside the ViewModel so it is unit-tested without Android. */
internal fun nutritionDashboardUiState(date: LocalDate, meals: List<Meal>): NutritionDashboardUiState =
    NutritionDashboardUiState(
        date = date,
        totals = meals.toDailyTotals(),
        mealRows = meals.toDashboardMealRows(),
    )

/**
 * One row per saved meal, grouped in the Figma order of the types (load order inside a type), and a
 * "Not logged yet" row for each type without meals, as the Dinner row of the frame.
 */
internal fun List<Meal>.toDashboardMealRows(): List<DashboardMealRow> =
    toMealSections().flatMap { section ->
        if (section.meals.isEmpty()) {
            listOf(DashboardMealRow.NotLogged(section.type))
        } else {
            section.meals.map { DashboardMealRow.Logged(it) }
        }
    }

/** Whole percent of the goal, truncated as in Figma (1,420 / 2,100 shows "67%"). It can pass 100. */
internal fun caloriesPercent(consumed: Int, goal: Int): Int = if (goal <= 0) 0 else consumed * 100 / goal

/** Fill of a ring or bar, between 0 and 1. */
internal fun progressFraction(consumed: Double, goal: Int): Float =
    if (goal <= 0) 0f else (consumed / goal).toFloat().coerceIn(0f, 1f)

@HiltViewModel
class NutritionDashboardViewModel @Inject constructor(
    repository: MealRepository,
) : ViewModel() {

    // The dashboard always shows today; other dates are browsed in Today's Meals (5.2).
    private val today: LocalDate = LocalDate.now()

    // One Room query: the rows and the daily total come from the same list, so they never disagree.
    val uiState: StateFlow<NutritionDashboardUiState> = repository.observeMealsByDate(today)
        .map { meals -> nutritionDashboardUiState(today, meals) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = nutritionDashboardUiState(today, emptyList()).copy(isLoading = true),
        )
}
