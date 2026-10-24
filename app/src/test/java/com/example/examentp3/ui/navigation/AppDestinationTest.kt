package com.example.examentp3.ui.navigation

import java.time.LocalDate
import org.junit.Assert.assertEquals
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
    fun everyTabRoot_showsTheBarOnItsOwnTab() {
        BottomTab.entries.forEach { tab ->
            assertEquals(tab, AppDestination.tabRoot(tab).tab)
        }
    }
}