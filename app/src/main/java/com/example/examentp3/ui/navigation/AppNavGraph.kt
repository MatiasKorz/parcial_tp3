package com.example.examentp3.ui.navigation

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.examentp3.model.DemoMealEstimate
import com.example.examentp3.model.DemoWorkout
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
import com.example.examentp3.ui.screens.nutrition.FoodAnalysisScreen
import com.example.examentp3.ui.screens.nutrition.FoodConfirmationScreen
import com.example.examentp3.ui.screens.nutrition.NutritionDashboardScreen
import com.example.examentp3.ui.screens.nutrition.MealDetailsScreen
import com.example.examentp3.ui.screens.nutrition.AddFoodScreen
import com.example.examentp3.ui.screens.nutrition.VoiceFoodLoggingScreen
import com.example.examentp3.ui.screens.nutrition.PhotoAnalysisScreen
import com.example.examentp3.ui.screens.nutrition.TodaysMealsScreen
import com.example.examentp3.ui.screens.placeholder.PlaceholderScreen
import com.example.examentp3.ui.screens.profile.EditProfileScreen
import com.example.examentp3.ui.screens.profile.GoalsPreferencesScreen
import com.example.examentp3.ui.screens.profile.NutritionPreferencesScreen
import com.example.examentp3.ui.screens.profile.ProfileDashboardScreen
import com.example.examentp3.ui.screens.profile.SettingsScreen
import com.example.examentp3.ui.screens.progress.HealthTrendsScreen
import com.example.examentp3.ui.screens.profile.VoiceAiSettingsScreen
import com.example.examentp3.ui.screens.progress.NutritionProgressScreen
import com.example.examentp3.ui.screens.progress.ProgressDashboardScreen
import com.example.examentp3.ui.screens.progress.TodaysOverviewScreen
import com.example.examentp3.ui.screens.progress.WorkoutProgressScreen
import com.example.examentp3.ui.screens.progress.WeeklyReviewScreen
import com.example.examentp3.ui.screens.voice.VoiceErrorScreen
import com.example.examentp3.ui.screens.voice.VoiceConversationScreen
import com.example.examentp3.ui.screens.voice.VoiceSpeakingScreen
import com.example.examentp3.ui.screens.voice.VoiceProcessingScreen
import com.example.examentp3.ui.screens.voice.VoiceListeningScreen
import com.example.examentp3.ui.screens.voice.VoiceIdleScreen
import com.example.examentp3.viewmodel.EditProfileViewModel
import com.example.examentp3.viewmodel.ExerciseDetailsViewModel
import com.example.examentp3.viewmodel.WorkoutDetailsViewModel
import com.example.examentp3.viewmodel.FoodAnalysisViewModel
import com.example.examentp3.viewmodel.FoodConfirmationViewModel
import com.example.examentp3.viewmodel.GoalsPreferencesViewModel
import com.example.examentp3.viewmodel.NutritionPreferencesViewModel
import com.example.examentp3.viewmodel.WorkoutCatalogViewModel
import com.example.examentp3.viewmodel.NutritionDashboardViewModel
import com.example.examentp3.viewmodel.MealDetailsUiState
import com.example.examentp3.viewmodel.MealDetailsViewModel
import com.example.examentp3.viewmodel.SettingsViewModel
import com.example.examentp3.viewmodel.VoiceAiSettingsViewModel
import com.example.examentp3.viewmodel.TodaysMealsViewModel

import kotlinx.coroutines.delay

// Provisional ids until the real catalogs exist (REF-04). Pre Workout does not load its workout yet,
// so this id never reaches the mock; Workout Details uses DemoWorkout instead.
private const val DEMO_WORKOUT_ID = "demo-workout"
private const val DEMO_MEAL_ID = "demo-meal"

private val workoutArgs = listOf(navArgument(NavArgs.WORKOUT_ID) { type = NavType.StringType })
private val exerciseArgs = workoutArgs + navArgument(NavArgs.EXERCISE_ID) { type = NavType.StringType }
private val mealArgs = listOf(navArgument(NavArgs.MEAL_ID) { type = NavType.StringType })
// Optional query argument: null means no meal type was chosen (Food Confirmation keeps its default).
private val mealTypeArgs = listOf(
    navArgument(NavArgs.MEAL_TYPE) {
        type = NavType.StringType
        nullable = true
        defaultValue = null
    },
)
private val foodConfirmationArgs = listOf(
    navArgument(NavArgs.FOOD_CONFIRMATION_MODE) { type = NavType.StringType },
) + mealTypeArgs

/** Meal type chosen earlier in the Nutrition flow, to pass on to the next route (NAV04). */
private fun NavBackStackEntry.mealType() = mealTypeFromRouteValue(arguments?.getString(NavArgs.MEAL_TYPE))

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
            onLogBreakfast = { if (entry.isResumed()) navController.navigate(AddFood.createRoute()) },
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
            onLogMyBreakfastClick = { if (entry.isResumed()) navController.navigate(AddFood.createRoute()) },
            onWhatToEatClick = { if (entry.isResumed()) navController.navigate(VoiceConversation.route) },
            onHowAmIDoingClick = { if (entry.isResumed()) navController.navigate(VoiceConversation.route) },
        )
    }
    composable(VoiceListening.route) { entry ->
        VoiceListeningScreen(
            onBack = {
                if (entry.isResumed()) navController.popBackStack(VoiceIdle.route, inclusive = false)
            },
            onProcess = { if (entry.isResumed()) navController.navigate(VoiceProcessing.route) },
            onSimulateError = {
                if (entry.isResumed() && navController.currentBackStackEntry == entry) {
                    navController.navigate(VoiceError.route) { launchSingleTop = true }
                }
            },
        )
    }
    composable(VoiceProcessing.route) { entry ->
        var exitHandled by remember(entry) { mutableStateOf(false) }
        val exitOnce: (() -> Unit) -> Unit = { action ->
            if (!exitHandled && entry.isResumed() && navController.currentBackStackEntry == entry) {
                exitHandled = true
                action()
            }
        }
        LaunchedEffect(entry) {
            // Losing RESUMED cancels the wait; returning starts a fresh demo interval.
            entry.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                delay(3_000)
                exitOnce {
                    navController.navigate(VoiceSpeaking.route) {
                        popUpTo(VoiceProcessing.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            }
        }
        VoiceProcessingScreen(
            onBack = { exitOnce { navController.popBackStack(VoiceListening.route, inclusive = false) } },
            onCancel = { exitOnce { navController.popBackStack(VoiceIdle.route, inclusive = false) } },
        )
    }
    composable(VoiceSpeaking.route) { entry ->
        var exitHandled by remember(entry) { mutableStateOf(false) }
        val returnTo: (AppDestination) -> Unit = { destination ->
            if (!exitHandled && entry.isResumed() && navController.currentBackStackEntry == entry) {
                exitHandled = true
                navController.popBackStack(destination.route, inclusive = false)
            }
        }
        VoiceSpeakingScreen(
            onBack = { returnTo(VoiceIdle) },
            onInterrupt = { returnTo(VoiceListening) },
        )
    }
    composable(VoiceConversation.route) { entry ->
        val navigateOnce: (() -> Unit) -> Unit = { action ->
            if (entry.isResumed() && navController.currentBackStackEntry == entry) action()
        }
        VoiceConversationScreen(
            onBack = { navigateOnce { navController.popBackStack(VoiceIdle.route, inclusive = false) } },
            onMicrophone = {
                navigateOnce { navController.navigate(VoiceListening.route) { launchSingleTop = true } }
            },
            onListen = {
                navigateOnce {
                    // Speaking's existing Interrupt action requires Listening below it.
                    navController.navigate(VoiceListening.route) { launchSingleTop = true }
                    navController.navigate(VoiceSpeaking.route) { launchSingleTop = true }
                }
            },
        )
    }
    composable(VoiceError.route) { entry ->
        val navigateOnce: (() -> Unit) -> Unit = { action ->
            if (entry.isResumed() && navController.currentBackStackEntry == entry) action()
        }
        VoiceErrorScreen(
            onBack = { navigateOnce { navController.popBackStack(VoiceIdle.route, inclusive = false) } },
            onRetry = { navigateOnce { navController.popBackStack(VoiceListening.route, inclusive = false) } },
            onTypeInstead = {
                navigateOnce {
                    // Reuse an existing local thread when Error was entered from Conversation.
                    if (!navController.popBackStack(VoiceConversation.route, inclusive = false)) {
                        navController.navigate(VoiceConversation.route) {
                            popUpTo(VoiceIdle.route) { inclusive = false }
                            launchSingleTop = true
                        }
                    }
                }
            },
        )
    }

    // Workout
    composable(WorkoutDashboard.route) { entry ->
        WorkoutDashboardScreen(
            onCalendarClick = { if (entry.isResumed()) navController.navigate(WeeklyPlan.route) },
            onStartWorkout = {
                if (entry.isResumed()) navController.navigate(PreWorkout.createRoute(DEMO_WORKOUT_ID))
            },
            // The card shows Figma's "Upper Body Strength": open the mock workout mapped to it.
            onWorkoutDetails = {
                if (entry.isResumed()) {
                    navController.navigate(WorkoutDetails.createRoute(DemoWorkout.UpperBody.workoutId))
                }
            },
            onViewProgress = { if (entry.isResumed()) navController.navigate(WorkoutProgress.route) },
        )
    }
    composable(WeeklyPlan.route) { entry ->
        val viewModel: WorkoutCatalogViewModel = hiltViewModel()
        val uiState by viewModel.uiState.collectAsState()
        WeeklyPlanScreen(
            uiState = uiState,
            onWorkoutClick = { workoutId ->
                if (entry.isResumed()) navController.navigate(WorkoutDetails.createRoute(workoutId))
            },
            onRetry = viewModel::retry,
        )
    }
    composable(WorkoutDetails.route, workoutArgs) { entry ->
        val workoutId = entry.argument(NavArgs.WORKOUT_ID)
        val viewModel: WorkoutDetailsViewModel = hiltViewModel()
        val uiState by viewModel.uiState.collectAsState()
        WorkoutDetailsScreen(
            uiState = uiState,
            onBack = { if (entry.isResumed()) navController.popBackStack() },
            onStartWorkout = {
                if (entry.isResumed()) navController.navigate(PreWorkout.createRoute(workoutId))
            },
            onExerciseClick = { exerciseId ->
                if (entry.isResumed()) navController.navigate(ExerciseDetails.createRoute(workoutId, exerciseId))
            },
            onRetry = viewModel::retry,
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
        val viewModel: ExerciseDetailsViewModel = hiltViewModel()
        val uiState by viewModel.uiState.collectAsState()
        ExerciseDetailsScreen(
            uiState = uiState,
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
        val viewModel: NutritionDashboardViewModel = hiltViewModel()
        val uiState by viewModel.uiState.collectAsState()
        NutritionDashboardScreen(
            uiState = uiState,
            // Inferred: the calendar picks the date of the RF04 list and opens it there.
            onDateSelected = { date ->
                if (entry.isResumed()) navController.navigate(TodaysMeals.createRoute(date))
            },
            onViewHistory = { if (entry.isResumed()) navController.navigate(TodaysMeals.createRoute()) },
            onMealClick = { mealId ->
                if (entry.isResumed()) navController.navigate(MealDetails.createRoute(mealId.toString()))
            },
            // "+ Add" of a meal type carries that type to Food Confirmation (NAV04).
            onAddMeal = { type -> if (entry.isResumed()) navController.navigate(AddFood.createRoute(type)) },
            onVoice = { if (entry.isResumed()) navController.navigate(VoiceFoodLogging.createRoute()) },
            onPhoto = { if (entry.isResumed()) navController.navigate(PhotoAnalysis.createRoute()) },
        )
    }
    composable(TodaysMeals.route, todaysMealsArgs) { entry ->
        val viewModel: TodaysMealsViewModel = hiltViewModel()
        val uiState by viewModel.uiState.collectAsState()
        TodaysMealsScreen(
            uiState = uiState,
            onPreviousDay = viewModel::onPreviousDay,
            onNextDay = viewModel::onNextDay,
            onDateSelected = viewModel::onDateSelected,
            onMealClick = { mealId ->
                if (entry.isResumed()) navController.navigate(MealDetails.createRoute(mealId.toString()))
            },
            // Inferred: "+ Add <type>" opens Add Food with that meal type (NAV04).
            onAddMeal = { type -> if (entry.isResumed()) navController.navigate(AddFood.createRoute(type)) },
        )
    }
    // From Add Food on, each step passes the chosen meal type (if any) to the next route.
    composable(AddFood.route, mealTypeArgs) { entry ->
        AddFoodScreen(
            onBack = { if (entry.isResumed()) navController.popBackStack() },
            onStartVoiceLogging = {
                if (entry.isResumed()) navController.navigate(VoiceFoodLogging.createRoute(entry.mealType()))
            },
            onSnapPhoto = { if (entry.isResumed()) navController.navigate(PhotoAnalysis.createRoute(entry.mealType())) },
            onLogManually = {
                if (entry.isResumed()) {
                    navController.navigate(FoodConfirmation.createRoute(FoodConfirmationMode.Manual, entry.mealType()))
                }
            },
        )
    }
    composable(VoiceFoodLogging.route, mealTypeArgs) { entry ->
        VoiceFoodLoggingScreen(
            onClose = { if (entry.isResumed()) navController.popBackStack() },
            // Stays in the stack: back from the analysis returns to listening.
            onDoneSpeaking = { if (entry.isResumed()) navController.navigate(FoodAnalysis.createRoute(entry.mealType())) },
            // Typing replaces listening: back from the manual form returns to where voice was opened.
            onTypeInstead = {
                if (entry.isResumed()) {
                    navController.navigate(FoodConfirmation.createRoute(FoodConfirmationMode.Manual, entry.mealType())) {
                        popUpTo(VoiceFoodLogging.route) { inclusive = true }
                    }
                }
            },
        )
    }
    composable(FoodAnalysis.route, mealTypeArgs) { entry ->
        val viewModel: FoodAnalysisViewModel = hiltViewModel()
        val uiState by viewModel.uiState.collectAsState()
        // The simulated analysis ends by itself: open the review and drop this screen from the back stack,
        // so going back from the review does not land on an analysis that would advance again.
        LaunchedEffect(uiState.isComplete) {
            if (uiState.isComplete) {
                navController.navigate(FoodConfirmation.createRoute(FoodConfirmationMode.Estimate, entry.mealType())) {
                    popUpTo(FoodAnalysis.route) { inclusive = true }
                }
            }
        }
        FoodAnalysisScreen(
            uiState = uiState,
            // Cancel, the header button and system back return to Voice Food Logging without saving.
            onCancel = { if (entry.isResumed()) navController.popBackStack() },
        )
    }
    composable(PhotoAnalysis.route, mealTypeArgs) { entry ->
        PhotoAnalysisScreen(
            estimate = DemoMealEstimate, // simulated analysis (SPEC p.10), same data that 5.7 reviews
            onBack = { if (entry.isResumed()) navController.popBackStack() },
            onReviewAndAdd = {
                if (entry.isResumed()) {
                    navController.navigate(FoodConfirmation.createRoute(FoodConfirmationMode.Estimate, entry.mealType()))
                }
            },
        )
    }
    composable(FoodConfirmation.route, foodConfirmationArgs) { entry ->
        val viewModel: FoodConfirmationViewModel = hiltViewModel()
        val uiState by viewModel.uiState.collectAsState()
        // Saving is asynchronous: once Room confirms, open the list on the meal's date (D12).
        LaunchedEffect(uiState.savedDate) {
            uiState.savedDate?.let { date ->
                navController.navigate(TodaysMeals.createRoute(date)) { popUpTo(NutritionDashboard.route) }
                viewModel.onSavedHandled()
            }
        }
        FoodConfirmationScreen(
            uiState = uiState,
            onBack = { if (entry.isResumed()) navController.popBackStack() },
            onNameChange = viewModel::onNameChange,
            onDateSelect = viewModel::onDateSelect,
            onTypeSelect = viewModel::onTypeSelect,
            onCaloriesChange = viewModel::onCaloriesChange,
            onProteinChange = viewModel::onProteinChange,
            onCarbsChange = viewModel::onCarbsChange,
            onFatChange = viewModel::onFatChange,
            onAddToMeal = { if (entry.isResumed()) viewModel.onAddToMeal() },
        )
    }
    composable(MealDetails.route, mealArgs) { entry ->
        val viewModel: MealDetailsViewModel = hiltViewModel()
        val uiState by viewModel.uiState.collectAsState()
        val lifecycleState by entry.lifecycle.currentStateFlow.collectAsState()
        // Leave only after Room confirmed the delete: popping first would cancel viewModelScope.
        // Keyed on the lifecycle too, so a delete that ends while paused still pops on resume.
        LaunchedEffect(uiState, lifecycleState) {
            if (uiState is MealDetailsUiState.Deleted && entry.isResumed()) navController.popBackStack()
        }
        MealDetailsScreen(
            uiState = uiState,
            onBack = { if (entry.isResumed()) navController.popBackStack() },
            onDeleteConfirmed = viewModel::deleteMeal,
        )
    }

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
    composable(HealthTrends.route) { entry ->
        HealthTrendsScreen(
            onBack = { if (entry.isResumed()) navController.popBackStack() },
        )
    }
    composable(WeeklyReview.route) { entry ->
        WeeklyReviewScreen(
            onClose = { if (entry.isResumed()) navController.popBackStack() },
            onViewNextWeekPlan = { if (entry.isResumed()) navController.navigate(WeeklyPlan.route) },
            onAskVora = { if (entry.isResumed()) navController.navigate(VoiceIdle.route) },
        )
    }

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
    composable(NutritionPreferences.route) { entry ->
        val viewModel: NutritionPreferencesViewModel = hiltViewModel()
        val uiState by viewModel.uiState.collectAsState()
        NutritionPreferencesScreen(
            uiState = uiState,
            onBack = { if (entry.isResumed()) navController.popBackStack() },
            onDietSelect = viewModel::onDietSelect,
            onEnjoyedFoodToggle = viewModel::onEnjoyedFoodToggle,
            onNewIngredientChange = viewModel::onNewIngredientChange,
            onIngredientAdd = viewModel::onIngredientAdd,
            onAvoidedFoodRemove = viewModel::onAvoidedFoodRemove,
            onPriorityToggle = viewModel::onPriorityToggle,
            onSave = {
                if (entry.isResumed()) {
                    viewModel.save()
                    navController.popBackStack()
                }
            },
        )
    }
    composable(VoiceAiSettings.route) { entry ->
        val viewModel: VoiceAiSettingsViewModel = hiltViewModel()
        val uiState by viewModel.uiState.collectAsState()
        VoiceAiSettingsScreen(
            uiState = uiState,
            onBack = { if (entry.isResumed()) navController.popBackStack() },
            onCoachStyleSelect = viewModel::onCoachStyleSelect,
            onResponseStyleSelect = viewModel::onResponseStyleSelect,
            onToggleChange = viewModel::onToggleChange,
            // "from Settings" in the notice: Profile / 06 (NAV03).
            onPrivacyClick = { if (entry.isResumed()) navController.navigate(Settings.route) },
        )
    }
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
