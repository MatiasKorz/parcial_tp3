package com.example.examentp3.viewmodel

import com.example.examentp3.data.local.FakeMealDao
import com.example.examentp3.data.repository.MealRepository
import com.example.examentp3.model.Meal
import com.example.examentp3.model.MealType
import java.time.LocalDate
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class NutritionDashboardViewModelTest {

    private val today = LocalDate.of(2026, 10, 23)

    private fun meal(
        type: MealType,
        calories: Int,
        id: Long = 0,
        date: LocalDate = today,
        protein: Double = 0.0,
        carbs: Double = 0.0,
        fat: Double = 0.0,
    ) = Meal(
        id = id,
        name = "${type.name} $id",
        date = date,
        type = type,
        calories = calories,
        proteinGrams = protein,
        carbsGrams = carbs,
        fatGrams = fat,
    )

    @Test
    fun uiState_totalsAreSumOfTheDay() {
        val state = nutritionDashboardUiState(
            today,
            listOf(
                meal(MealType.BREAKFAST, 420, protein = 30.0, carbs = 52.0, fat = 12.0),
                meal(MealType.LUNCH, 520, protein = 38.0, carbs = 54.0, fat = 14.5),
            ),
        )

        assertEquals(940, state.totals.calories)
        assertEquals(68.0, state.totals.proteinGrams, 0.0)
        assertEquals(106.0, state.totals.carbsGrams, 0.0)
        assertEquals(26.5, state.totals.fatGrams, 0.0)
        assertEquals(SampleNutritionGoals, state.goals)
    }

    @Test
    fun mealRows_figmaDay_matchesFrame() {
        val breakfast = meal(MealType.BREAKFAST, 420, id = 1)
        val lunch = meal(MealType.LUNCH, 520, id = 2)
        val snack = meal(MealType.SNACK, 240, id = 3)

        // Saved out of order: the rows follow the Figma order of the types.
        val rows = listOf(snack, breakfast, lunch).toDashboardMealRows()

        assertEquals(
            listOf(
                DashboardMealRow.Logged(breakfast),
                DashboardMealRow.Logged(lunch),
                DashboardMealRow.Logged(snack),
                DashboardMealRow.NotLogged(MealType.DINNER),
            ),
            rows,
        )
    }

    @Test
    fun mealRows_emptyDay_hasFourNotLoggedRowsInFigmaOrder() {
        assertEquals(
            MealType.entries.map { DashboardMealRow.NotLogged(it) },
            emptyList<Meal>().toDashboardMealRows(),
        )
    }

    @Test
    fun mealRows_twoMealsOfSameType_twoRowsInLoadOrder() {
        val first = meal(MealType.LUNCH, 520, id = 1)
        val second = meal(MealType.LUNCH, 300, id = 2)

        val rows = listOf(first, second).toDashboardMealRows()

        assertEquals(
            listOf(
                DashboardMealRow.NotLogged(MealType.BREAKFAST),
                DashboardMealRow.Logged(first),
                DashboardMealRow.Logged(second),
                DashboardMealRow.NotLogged(MealType.SNACK),
                DashboardMealRow.NotLogged(MealType.DINNER),
            ),
            rows,
        )
    }

    @Test
    fun caloriesPercent_figmaValues_is67() {
        // 1,420 / 2,100 = 67.6 %: Figma shows 67, so the percent is truncated.
        assertEquals(67, caloriesPercent(1420, 2100))
    }

    @Test
    fun caloriesPercent_zeroGoal_isZero() {
        assertEquals(0, caloriesPercent(500, 0))
    }

    @Test
    fun caloriesPercent_overGoal_isAbove100() {
        assertEquals(112, caloriesPercent(2352, 2100))
    }

    @Test
    fun progressFraction_isClampedBetweenZeroAndOne() {
        assertEquals(0f, progressFraction(0.0, 140), 0f)
        assertEquals(0.5f, progressFraction(70.0, 140), 0f)
        assertEquals(1f, progressFraction(200.0, 140), 0f)
        assertEquals(0f, progressFraction(50.0, 0), 0f)
    }

    @Test
    fun dashboard_totalFollowsRoomAfterAddAndDelete() = runBlocking {
        val repository = MealRepository(FakeMealDao())
        val dashboard = repository.observeMealsByDate(today).map { nutritionDashboardUiState(today, it) }

        repository.addMeal(meal(MealType.LUNCH, 520))
        repository.addMeal(meal(MealType.DINNER, 300, date = today.minusDays(1)))
        assertEquals(520, dashboard.first().totals.calories)

        val breakfastId = repository.addMeal(meal(MealType.BREAKFAST, 420))
        assertEquals(940, dashboard.first().totals.calories)

        repository.deleteMeal(breakfastId)
        val state = dashboard.first()
        assertEquals(520, state.totals.calories)
        assertTrue(state.mealRows.contains(DashboardMealRow.NotLogged(MealType.BREAKFAST)))
    }

    @Test
    fun initialState_isLoadingForToday() {
        val viewModel = NutritionDashboardViewModel(MealRepository(FakeMealDao()))

        val state = viewModel.uiState.value
        assertTrue(state.isLoading)
        assertEquals(LocalDate.now(), state.date)
    }
}
