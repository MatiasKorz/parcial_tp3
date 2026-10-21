package com.example.examentp3.ui.navigation

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
    fun everyTabRoot_showsTheBarOnItsOwnTab() {
        BottomTab.entries.forEach { tab ->
            assertEquals(tab, AppDestination.tabRoot(tab).tab)
        }
    }
}