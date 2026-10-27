package com.example.examentp3.ui.navigation

import com.example.examentp3.model.MealType
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class AppDestinationTest {

    @Test
    fun inventory_hasAll37Frames() {
        assertEquals(37, AppDestination.all.size)
    }

    @Test
    fun routes_areUnique() {
        assertEquals(AppDestination.all.size, AppDestination.all.map { it.route }.toSet().size)
    }

    @Test
    fun bottomBar_isShownInExactly16Frames() {
        assertEquals(16, AppDestination.all.count { it.tab != null })
    }

    @Test
    fun todaysMeals_createRouteWithoutDate_opensOnToday() {
        assertEquals("todays_meals", AppDestination.TodaysMeals.createRoute())
    }

    @Test
    fun todaysMeals_createRouteWithDate_passesEpochDay() {
        val date = LocalDate.of(2026, 10, 23)

        assertEquals(
            "todays_meals?${NavArgs.DATE_EPOCH_DAY}=${date.toEpochDay()}",
            AppDestination.TodaysMeals.createRoute(date),
        )
    }

    @Test
    fun todaysMeals_routeDeclaresTheOptionalDate() {
        assertEquals("todays_meals?dateEpochDay={dateEpochDay}", AppDestination.TodaysMeals.route)
    }

    @Test
    fun addFood_createRouteWithoutType_isThePlainRoute() {
        assertEquals("add_food", AppDestination.AddFood.createRoute())
    }

    @Test
    fun addFood_createRouteWithType_passesTheType() {
        assertEquals("add_food?mealType=DINNER", AppDestination.AddFood.createRoute(MealType.DINNER))
    }

    @Test
    fun foodConfirmation_routeDeclaresModeAndOptionalType() {
        assertEquals("food_confirmation/{mode}?mealType={mealType}", AppDestination.FoodConfirmation.route)
        assertEquals(
            "food_confirmation/estimate?mealType=SNACK",
            AppDestination.FoodConfirmation.createRoute(FoodConfirmationMode.Estimate, MealType.SNACK),
        )
        assertEquals("food_confirmation/manual", AppDestination.FoodConfirmation.createRoute(FoodConfirmationMode.Manual))
    }

    @Test
    fun voiceAndPhotoRoutes_passTheTypeOn() {
        assertEquals("voice_food_logging?mealType=LUNCH", AppDestination.VoiceFoodLogging.createRoute(MealType.LUNCH))
        assertEquals("food_analysis?mealType=LUNCH", AppDestination.FoodAnalysis.createRoute(MealType.LUNCH))
        assertEquals("photo_analysis?mealType=LUNCH", AppDestination.PhotoAnalysis.createRoute(MealType.LUNCH))
    }

    @Test
    fun mealTypeRouteValue_roundTripsEveryType() {
        MealType.entries.forEach { type -> assertEquals(type, mealTypeFromRouteValue(type.name)) }
    }

    @Test
    fun mealTypeRouteValue_unknownOrMissing_meansNoType() {
        assertNull(mealTypeFromRouteValue("BRUNCH"))
        assertNull(mealTypeFromRouteValue(null))
    }

    @Test
    fun everyTabRoot_showsTheBarOnItsOwnTab() {
        BottomTab.entries.forEach { tab ->
            assertEquals(tab, AppDestination.tabRoot(tab).tab)
        }
    }
}