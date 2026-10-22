package com.example.examentp3.ui.navigation

import androidx.lifecycle.Lifecycle
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.examentp3.ui.navigation.AppDestination.*
import com.example.examentp3.ui.screens.home.HomeSampleUiState
import com.example.examentp3.ui.screens.home.HomeScreen
import com.example.examentp3.ui.screens.placeholder.PlaceholderLink
import com.example.examentp3.ui.screens.placeholder.PlaceholderScreen

// Provisional ids until the real catalogs exist (REF-04).
private const val DEMO_WORKOUT_ID = "demo-workout"
private const val DEMO_EXERCISE_ID = "demo-exercise"
private const val DEMO_MEAL_ID = "demo-meal"

private val workoutArgs = listOf(navArgument(NavArgs.WORKOUT_ID) { type = NavType.StringType })
private val exerciseArgs = listOf(navArgument(NavArgs.EXERCISE_ID) { type = NavType.StringType })
private val mealArgs = listOf(navArgument(NavArgs.MEAL_ID) { type = NavType.StringType })

private class Link(
    val label: String,
    val route: String,
    val options: NavOptionsBuilder.() -> Unit = {},
)

// Not named "to": inside NavGraphBuilder it would clash with Kotlin's infix Pair builder.
private fun linkTo(destination: AppDestination) = Link(destination.frame, destination.route)

private fun NavBackStackEntry.isResumed() = lifecycle.currentState == Lifecycle.State.RESUMED

private fun NavBackStackEntry.argument(name: String): String = arguments?.getString(name).orEmpty()

private fun NavGraphBuilder.placeholder(
    destination: AppDestination,
    navController: NavHostController,
    arguments: List<NamedNavArgument> = emptyList(),
    onBack: ((NavHostController) -> Unit)? = { it.popBackStack() },
    links: (NavBackStackEntry) -> List<Link> = { emptyList() },
) {
    composable(destination.route, arguments = arguments) { entry ->
        PlaceholderScreen(
            title = destination.frame,
            detail = arguments.firstOrNull()?.let { "${it.name}: ${entry.argument(it.name)}" },
            links = links(entry).map { link ->
                PlaceholderLink(link.label) {
                    if (entry.isResumed()) navController.navigate(link.route, link.options)
                }
            },
            onBack = onBack?.let { back -> { if (entry.isResumed()) back(navController) } },
        )
    }
}

internal fun NavGraphBuilder.appGraph(navController: NavHostController) {
    // Access
    placeholder(SignIn, navController, onBack = null) {
        listOf(
            Link("Sign In", Home.route) {
                popUpTo(SignIn.route) { inclusive = true }
                launchSingleTop = true
            },
            Link("Sign up here", SignUp.route),
        )
    }
    placeholder(SignUp, navController) {
        listOf(
            Link("Sign Up", Home.route) { // inferred: not drawn as a connection in Figma
                popUpTo(SignIn.route) { inclusive = true }
                launchSingleTop = true
            },
        )
    }

    // Home
    composable(Home.route) { entry ->
        HomeScreen(
            uiState = HomeSampleUiState,
            onNotificationsClick = { if (entry.isResumed()) navController.navigate(Notifications.route) },
            onStartVoiceSession = { if (entry.isResumed()) navController.navigate(VoiceIdle.route) },
            onStartMyWorkout = {
                if (entry.isResumed()) navController.navigate(PreWorkout.createRoute(DEMO_WORKOUT_ID))
            },
            onLogBreakfast = { if (entry.isResumed()) navController.navigate(AddFood.route) },
            onTodaysOverviewClick = { if (entry.isResumed()) navController.navigate(TodaysOverview.route) },
            onStartWorkout = {
                if (entry.isResumed()) navController.navigate(PreWorkout.createRoute(DEMO_WORKOUT_ID))
            },
        )
    }
    placeholder(Notifications, navController)

    // Coach: closing returns to where it was opened from
    val closeCoach: (NavHostController) -> Unit = { it.popBackStack(VoiceIdle.route, inclusive = true) }
    placeholder(VoiceIdle, navController, onBack = closeCoach) { listOf(linkTo(VoiceListening)) }
    placeholder(VoiceListening, navController, onBack = closeCoach) { listOf(linkTo(VoiceProcessing)) }
    placeholder(VoiceProcessing, navController, onBack = closeCoach) {
        listOf(linkTo(VoiceSpeaking), linkTo(VoiceError))
    }
    placeholder(VoiceSpeaking, navController, onBack = closeCoach) { listOf(linkTo(VoiceConversation)) }
    placeholder(VoiceConversation, navController, onBack = closeCoach)
    placeholder(VoiceError, navController, onBack = closeCoach)

    // Workout
    placeholder(WorkoutDashboard, navController, onBack = null) {
        listOf(
            linkTo(WeeklyPlan),
            Link(WorkoutDetails.frame, WorkoutDetails.createRoute(DEMO_WORKOUT_ID)),
            Link(PreWorkout.frame, PreWorkout.createRoute(DEMO_WORKOUT_ID)),
        )
    }
    placeholder(WeeklyPlan, navController) {
        listOf(Link(WorkoutDetails.frame, WorkoutDetails.createRoute(DEMO_WORKOUT_ID)))
    }
    placeholder(WorkoutDetails, navController, workoutArgs) { entry ->
        val id = entry.argument(NavArgs.WORKOUT_ID)
        listOf(
            Link(PreWorkout.frame, PreWorkout.createRoute(id)),
            Link(ExerciseDetails.frame, ExerciseDetails.createRoute(DEMO_EXERCISE_ID)),
        )
    }
    placeholder(PreWorkout, navController, workoutArgs) { entry ->
        listOf(Link(ActiveWorkout.frame, ActiveWorkout.createRoute(entry.argument(NavArgs.WORKOUT_ID))))
    }
    placeholder(ActiveWorkout, navController, workoutArgs) {
        listOf(
            Link(ExerciseDetails.frame, ExerciseDetails.createRoute(DEMO_EXERCISE_ID)),
            linkTo(WorkoutComplete),
        )
    }
    placeholder(ExerciseDetails, navController, exerciseArgs)
    placeholder(WorkoutComplete, navController) {
        listOf(
            Link("Done", WorkoutDashboard.route) {
                popUpTo(WorkoutDashboard.route)
                launchSingleTop = true
            },
        )
    }

    // Nutrition
    placeholder(NutritionDashboard, navController, onBack = null) {
        listOf(linkTo(TodaysMeals), linkTo(AddFood))
    }
    placeholder(TodaysMeals, navController) {
        listOf(Link(MealDetails.frame, MealDetails.createRoute(DEMO_MEAL_ID)), linkTo(AddFood))
    }
    placeholder(AddFood, navController) { listOf(linkTo(VoiceFoodLogging), linkTo(PhotoAnalysis)) }
    placeholder(VoiceFoodLogging, navController) { listOf(linkTo(FoodAnalysis)) }
    placeholder(FoodAnalysis, navController) { listOf(linkTo(FoodConfirmation)) }
    placeholder(PhotoAnalysis, navController) { listOf(linkTo(FoodConfirmation)) }
    placeholder(FoodConfirmation, navController) {
        listOf(Link("Add to meal", TodaysMeals.route) { popUpTo(NutritionDashboard.route) })
    }
    placeholder(MealDetails, navController, mealArgs)

    // Progress
    placeholder(ProgressDashboard, navController, onBack = null) {
        listOf(
            linkTo(TodaysOverview),
            linkTo(WorkoutProgress),
            linkTo(NutritionProgress),
            linkTo(HealthTrends),
            linkTo(WeeklyReview),
        )
    }
    placeholder(TodaysOverview, navController)
    placeholder(WorkoutProgress, navController)
    placeholder(NutritionProgress, navController)
    placeholder(HealthTrends, navController)
    placeholder(WeeklyReview, navController)

    // Profile
    placeholder(ProfileDashboard, navController, onBack = null) {
        listOf(
            linkTo(EditProfile),
            linkTo(GoalsPreferences),
            linkTo(NutritionPreferences),
            linkTo(VoiceAiSettings),
            linkTo(Settings),
        )
    }
    placeholder(EditProfile, navController)
    placeholder(GoalsPreferences, navController)
    placeholder(NutritionPreferences, navController)
    placeholder(VoiceAiSettings, navController)
    placeholder(Settings, navController) {
        listOf(
            Link("Log out", SignIn.route) { // inferred: not drawn as a connection in Figma
                popUpTo(navController.graph.id) { inclusive = true }
                launchSingleTop = true
            },
        )
    }
}