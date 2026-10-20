package com.example.examentp3.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.examentp3.ui.screens.ExampleScreen
import com.example.examentp3.ui.screens.HomeScreen

private object Routes {
    const val HOME = "home"
    const val EXAMPLE = "example"
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.HOME
    ) {
        composable(Routes.HOME) {
            HomeScreen(
                onNavigate = {
                    navController.navigate(Routes.EXAMPLE)
                }
            )
        }

        composable(Routes.EXAMPLE) {
            ExampleScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}