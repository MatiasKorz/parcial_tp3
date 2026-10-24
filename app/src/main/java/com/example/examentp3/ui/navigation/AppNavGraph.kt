package com.example.examentp3.ui.navigation

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
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
import com.example.examentp3.ui.screens.workout.WorkoutDashboardScreen
import com.example.examentp3.ui.screens.workout.complete.WorkoutCompleteScreen
import com.example.examentp3.ui.screens.workout.exercise.ExerciseDetailsScreen
import com.example.examentp3.ui.screens.workout.active.ActiveWorkoutScreen
import com.example.examentp3.ui.screens.workout.preworkout.PreWorkoutScreen
import com.example.examentp3.ui.screens.workout.details.WorkoutDetailsScreen
import com.example.examentp3.ui.screens.workout.weeklyplan.WeeklyPlanScreen
import com.example.examentp3.ui.screens.login.SignInScreen
import com.example.examentp3.ui.screens.login.SignUpScreen
import com.example.examentp3.ui.screens.notifications.NotificationsScreen
import com.example.examentp3.ui.screens.placeholder.PlaceholderLink
import com.example.examentp3.ui.screens.nutrition.MealRoomCheckSection
import com.example.examentp3.ui.screens.placeholder.PlaceholderScreen
import com.example.examentp3.ui.screens.profile.EditProfileScreen
import com.example.examentp3.ui.screens.profile.GoalsPreferencesScreen
import com.example.examentp3.ui.screens.profile.ProfileDashboardScreen
import com.example.examentp3.ui.screens.profile.SettingsScreen
import com.example.examentp3.ui.screens.progress.NutritionProgressScreen
import com.example.examentp3.ui.screens.progress.ProgressDashboardScreen
import com.example.examentp3.ui.screens.progress.TodaysOverviewScreen
import com.example.examentp3.ui.screens.progress.WorkoutProgressScreen
import com.example.examentp3.ui.screens.voice.VoiceIdleScreen
import com.example.examentp3.viewmodel.EditProfileViewModel
import com.example.examentp3.viewmodel.GoalsPreferencesViewModel
import com.example.examentp3.viewmodel.WorkoutCatalogViewModel
import com.example.examentp3.viewmodel.MealRoomCheckViewModel
import com.example.examentp3.viewmodel.SettingsViewModel

// Provisional ids until the real catalogs exist (REF-04).
private const val DEMO_WORKOUT_ID = "demo-workout"
private const val DEMO_EXERCISE_ID = "demo-exercise"
private const val DEMO_MEAL_ID = "demo-meal"

private val workoutArgs = listOf(navArgument(NavArgs.WORKOUT_ID) { type = NavType.StringType })
private val exerciseArgs = listOf(navArgument(NavArgs.EXERCISE_ID) { type = NavType.StringType })
private val mealArgs = listOf(navArgument(NavArgs.MEAL_ID) { type = NavType.StringType })

// Optional query argument: null means today (resolved in TodaysMealsViewModel, not at graph build time).
private val todaysMealsArgs = listOf(
    navArgument(NavArgs.DATE_EPOCH_DAY) {
        type = NavType.StringType
        nullable = true
        defaultValue = null
    },
)

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
    composable(SignIn.route) { entry ->
        SignInScreen(
            onSignIn = {
                if (entry.isResumed()) {
                    navController.navigate(Home.route) {
                        popUpTo(SignIn.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            },
            onSignUp = {
                if (entry.isResumed()) navController.navigate(SignUp.route)
            },
        )
    }
    composable(SignUp.route) { entry ->
        SignUpScreen(
            onBack = { if (entry.isResumed()) navController.popBackStack() },
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
    composable(Notifications.route) { NotificationsScreen() }

    // Coach: closing returns to where it was opened from
    val closeCoach: (NavHostController) -> Unit = { it.popBackStack(VoiceIdle.route, inclusive = true) }
    composable(VoiceIdle.route) { entry ->
        VoiceIdleScreen(
            onBack = { if (entry.isResumed()) closeCoach(navController) },
            onTapToTalk = { if (entry.isResumed()) navController.navigate(VoiceListening.route) },
            onTypeInstead = { if (entry.isResumed()) navController.navigate(VoiceConversation.route) },
            onLastConversationClick = { if (entry.isResumed()) navController.navigate(VoiceConversation.route) },
            onStartMyWorkoutClick = {
                if (entry.isResumed()) navController.navigate(PreWorkout.createRoute(DEMO_WORKOUT_ID))
            },
            onLogMyBreakfastClick = { if (entry.isResumed()) navController.navigate(AddFood.route) },
            onWhatToEatClick = { if (entry.isResumed()) navController.navigate(VoiceConversation.route) },
            onHowAmIDoingClick = { if (entry.isResumed()) navController.navigate(VoiceConversation.route) },
        )
    }
    placeholder(VoiceListening, navController, onBack = closeCoach) { listOf(linkTo(VoiceProcessing)) }
    placeholder(VoiceProcessing, navController, onBack = closeCoach) {
        listOf(linkTo(VoiceSpeaking), linkTo(VoiceError))
    }
    placeholder(VoiceSpeaking, navController, onBack = closeCoach) { listOf(linkTo(VoiceConversation)) }
    placeholder(VoiceConversation, navController, onBack = closeCoach)
    placeholder(VoiceError, navController, onBack = closeCoach)

    // Workout
    composable(WorkoutDashboard.route) { entry ->
        WorkoutDashboardScreen(
            onCalendarClick = { if (entry.isResumed()) navController.navigate(WeeklyPlan.route) },
            onStartWorkout = {
                if (entry.isResumed()) navController.navigate(PreWorkout.createRoute(DEMO_WORKOUT_ID))
            },
            onWorkoutDetails = {
                if (entry.isResumed()) navController.navigate(WorkoutDetails.createRoute(DEMO_WORKOUT_ID))
            },
            onViewProgress = { if (entry.isResumed()) navController.navigate(WorkoutProgress.route) },
        )
    }
    composable(WeeklyPlan.route) { entry ->
        val viewModel: WorkoutCatalogViewModel = hiltViewModel()
        val uiState by viewModel.uiState.collectAsState()
        WeeklyPlanScreen(
            uiState = uiState,
            onWorkoutDetails = {
                if (entry.isResumed()) navController.navigate(WorkoutDetails.createRoute(DEMO_WORKOUT_ID))
            },
            onWorkoutClick = { workoutId ->
                if (entry.isResumed()) navController.navigate(WorkoutDetails.createRoute(workoutId))
            },
        )
    }
    composable(WorkoutDetails.route, workoutArgs) { entry ->
        val workoutId = entry.argument(NavArgs.WORKOUT_ID)
        WorkoutDetailsScreen(
            onBack = { if (entry.isResumed()) navController.popBackStack() },
            onStartWorkout = {
                if (entry.isResumed()) navController.navigate(PreWorkout.createRoute(workoutId))
            },
            onExerciseClick = {
                if (entry.isResumed()) navController.navigate(ExerciseDetails.createRoute(DEMO_EXERCISE_ID))
            },
        )
    }
    composable(PreWorkout.route, workoutArgs) { entry ->
        val workoutId = entry.argument(NavArgs.WORKOUT_ID)
        PreWorkoutScreen(
            onClose = { if (entry.isResumed()) navController.popBackStack() },
            onStartWorkout = {
                if (entry.isResumed()) navController.navigate(ActiveWorkout.createRoute(workoutId))
            },
        )
    }
    composable(ActiveWorkout.route, workoutArgs) { entry ->
        ActiveWorkoutScreen(
            onFinishWorkout = {
                if (entry.isResumed()) {
                    navController.navigate(WorkoutComplete.route) {
                        popUpTo(entry.destination.id) { inclusive = true }
                    }
                }
            },
        )
    }
    composable(ExerciseDetails.route, exerciseArgs) { entry ->
        ExerciseDetailsScreen(
            onBack = { if (entry.isResumed()) navController.popBackStack() },
        )
    }
    composable(WorkoutComplete.route) { entry ->
        WorkoutCompleteScreen(
            onDone = {
                if (entry.isResumed() &&
                    !navController.popBackStack(WorkoutDashboard.route, inclusive = false)
                ) {
                    // Home-origin sessions may not have a Workout Dashboard entry yet.
                    navController.navigate(WorkoutDashboard.route) {
                        popUpTo(Home.route)
                        launchSingleTop = true
                    }
                }
            },
        )
    }

    // Nutrition
    composable(NutritionDashboard.route) { entry ->
        val viewModel: MealRoomCheckViewModel = hiltViewModel()
        val uiState by viewModel.uiState.collectAsState()
        PlaceholderScreen(
            title = NutritionDashboard.frame,
            links = listOf(Link(TodaysMeals.frame, TodaysMeals.createRoute()), linkTo(AddFood)).map { link ->
                PlaceholderLink(link.label) { if (entry.isResumed()) navController.navigate(link.route) }
            },
        ) {
            MealRoomCheckSection(uiState = uiState, onInsertClick = viewModel::insertSampleMeal)
        }
    }
    placeholder(TodaysMeals, navController, todaysMealsArgs) {
        listOf(Link(MealDetails.frame, MealDetails.createRoute(DEMO_MEAL_ID)), linkTo(AddFood))
    }
    placeholder(AddFood, navController) { listOf(linkTo(VoiceFoodLogging), linkTo(PhotoAnalysis)) }
    placeholder(VoiceFoodLogging, navController) { listOf(linkTo(FoodAnalysis)) }
    placeholder(FoodAnalysis, navController) { listOf(linkTo(FoodConfirmation)) }
    placeholder(PhotoAnalysis, navController) { listOf(linkTo(FoodConfirmation)) }
    placeholder(FoodConfirmation, navController) {
        listOf(Link("Add to meal", TodaysMeals.createRoute()) { popUpTo(NutritionDashboard.route) })
    }
    placeholder(MealDetails, navController, mealArgs)

    // Progress
    composable(ProgressDashboard.route) { entry ->
        ProgressDashboardScreen(
            onSeeWeeklyReview = { if (entry.isResumed()) navController.navigate(WeeklyReview.route) },
            onWorkoutClick = { if (entry.isResumed()) navController.navigate(WorkoutProgress.route) },
            onNutritionClick = { if (entry.isResumed()) navController.navigate(NutritionProgress.route) },
            onActivityClick = { if (entry.isResumed()) navController.navigate(HealthTrends.route) },
        )
    }
    composable(TodaysOverview.route) { entry ->
        TodaysOverviewScreen(
            onStartWorkout = {
                if (entry.isResumed()) navController.navigate(PreWorkout.createRoute(DEMO_WORKOUT_ID))
            },
        )
    }
    composable(WorkoutProgress.route) { entry ->
        WorkoutProgressScreen(
            onBack = { if (entry.isResumed()) navController.popBackStack() },
        )
    }
    composable(NutritionProgress.route) { entry ->
        NutritionProgressScreen(
            onBack = { if (entry.isResumed()) navController.popBackStack() },
        )
    }
    placeholder(HealthTrends, navController)
    placeholder(WeeklyReview, navController)

    // Profile
    composable(ProfileDashboard.route) { entry ->
        ProfileDashboardScreen(
            onSettingsClick = { if (entry.isResumed()) navController.navigate(Settings.route) },
            onEditProfileClick = { if (entry.isResumed()) navController.navigate(EditProfile.route) },
            onReviewPreferencesClick = { if (entry.isResumed()) navController.navigate(VoiceAiSettings.route) },
            onGoalsClick = { if (entry.isResumed()) navController.navigate(GoalsPreferences.route) },
            onPreferencesClick = { if (entry.isResumed()) navController.navigate(NutritionPreferences.route) },
            onConnectedDataClick = { if (entry.isResumed()) navController.navigate(Settings.route) },
            onAccountClick = { if (entry.isResumed()) navController.navigate(Settings.route) },
        )
    }
    composable(EditProfile.route) { entry ->
        val viewModel: EditProfileViewModel = hiltViewModel()
        val uiState by viewModel.uiState.collectAsState()
        EditProfileScreen(
            uiState = uiState,
            onBack = { if (entry.isResumed()) navController.popBackStack() },
            onFirstNameChange = viewModel::onFirstNameChange,
            onLastNameChange = viewModel::onLastNameChange,
            onDateOfBirthChange = viewModel::onDateOfBirthChange,
            onHeightChange = viewModel::onHeightChange,
            onWeightChange = viewModel::onWeightChange,
            onGenderChange = viewModel::onGenderChange,
            onSave = {
                if (entry.isResumed()) {
                    viewModel.save()
                    navController.popBackStack()
                }
            },
        )
    }
    composable(GoalsPreferences.route) { entry ->
        val viewModel: GoalsPreferencesViewModel = hiltViewModel()
        val uiState by viewModel.uiState.collectAsState()
        GoalsPreferencesScreen(
            uiState = uiState,
            onBack = { if (entry.isResumed()) navController.popBackStack() },
            onGoalToggle = viewModel::onGoalToggle,
            onFrequencySelect = viewModel::onFrequencySelect,
            onWorkoutSelect = viewModel::onWorkoutSelect,
            onSave = {
                if (entry.isResumed()) {
                    viewModel.save()
                    navController.popBackStack()
                }
            },
        )
    }
    placeholder(NutritionPreferences, navController)
    placeholder(VoiceAiSettings, navController)
    composable(Settings.route) { entry ->
        val viewModel: SettingsViewModel = hiltViewModel()
        val uiState by viewModel.uiState.collectAsState()
        SettingsScreen(
            uiState = uiState,
            onBack = { if (entry.isResumed()) navController.popBackStack() },
            onAccountClick = { if (entry.isResumed()) navController.navigate(EditProfile.route) },
            onNotificationsChange = viewModel::onNotificationsChange,
            onLogOut = {
                // Inferred: not drawn as a connection in Figma. Clears the back stack.
                if (entry.isResumed()) {
                    navController.navigate(SignIn.route) {
                        popUpTo(navController.graph.id) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            },
        )
    }
}
