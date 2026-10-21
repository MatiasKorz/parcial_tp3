package com.example.examentp3.ui.navigation

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.examentp3.R
import com.example.examentp3.ui.components.VoraBottomBar
import com.example.examentp3.ui.components.VoraBottomBarItem

@Composable
fun AppNavigation(navController: NavHostController = rememberNavController()) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentTab = AppDestination.fromRoute(backStackEntry?.destination?.route)?.tab

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            if (currentTab != null) {
                VoraBottomBar(
                    items = bottomBarItems(),
                    selectedIndex = currentTab.ordinal,
                    onSelect = { index -> navController.navigateToTab(BottomTab.entries[index]) },
                )
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = AppDestination.SignIn.route,
            modifier = Modifier
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding),
        ) {
            appGraph(navController)
        }
    }
}

/** Same order as [BottomTab]. */
@Composable
private fun bottomBarItems(): List<VoraBottomBarItem> = listOf(
    VoraBottomBarItem(stringResource(R.string.nav_home), ImageVector.vectorResource(R.drawable.ic_nav_home)),
    VoraBottomBarItem(stringResource(R.string.nav_workout), ImageVector.vectorResource(R.drawable.ic_nav_workout)),
    VoraBottomBarItem(stringResource(R.string.nav_nutrition), ImageVector.vectorResource(R.drawable.ic_nav_nutrition)),
    VoraBottomBarItem(stringResource(R.string.nav_progress), ImageVector.vectorResource(R.drawable.ic_nav_progress)),
    VoraBottomBarItem(stringResource(R.string.nav_profile), ImageVector.vectorResource(R.drawable.ic_nav_profile)),
)

private fun NavHostController.navigateToTab(tab: BottomTab) {
    navigate(AppDestination.tabRoot(tab).route) {
        popUpTo(AppDestination.Home.route) { saveState = true } // Home is always the bottom of the stack after login
        launchSingleTop = true
        restoreState = true
    }
}