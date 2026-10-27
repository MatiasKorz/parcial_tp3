package com.example.examentp3.ui.navigation

import android.net.Uri
import com.example.examentp3.model.MealType
import java.time.LocalDate

enum class BottomTab { Home, Workout, Nutrition, Progress, Profile }

object NavArgs {
    const val WORKOUT_ID = "workoutId"
    const val EXERCISE_ID = "exerciseId"
    const val MEAL_ID = "mealId"

    /** Optional date of Today's Meals as epoch day; missing means today. */
    const val DATE_EPOCH_DAY = "dateEpochDay"

    /** Mode of Food Confirmation, see [FoodConfirmationMode]. */
    const val FOOD_CONFIRMATION_MODE = "mode"

    /**
     * Optional meal type chosen on the way to Food Confirmation ("+ Add" of a meal type), carried
     * from Add Food through voice or photo until it is saved (NAV04). Missing means none chosen.
     */
    const val MEAL_TYPE = "mealType"
}

/** Value of [NavArgs.MEAL_TYPE] in a route; unknown or missing values mean no type was chosen. */
fun mealTypeFromRouteValue(value: String?): MealType? = MealType.entries.firstOrNull { it.name == value }

/** Route pattern with the optional `mealType` query argument. */
private fun withMealTypeArg(base: String) = "$base?${NavArgs.MEAL_TYPE}={${NavArgs.MEAL_TYPE}}"

/** Concrete route: without a type it stays the plain [base]. */
private fun withMealType(base: String, type: MealType?) =
    if (type == null) base else "$base?${NavArgs.MEAL_TYPE}=${type.name}"

/**
 * Food Confirmation serves two flows on one route: the RF03 manual form ("Log Manually") and the
 * review of the simulated estimate (Food Analysis / Photo Analysis).
 */
enum class FoodConfirmationMode(val routeValue: String) {
    Manual("manual"),
    Estimate("estimate");

    companion object {
        /** Unknown or missing values fall back to [Estimate], the screen as drawn in Figma. */
        fun fromRouteValue(value: String?): FoodConfirmationMode =
            entries.firstOrNull { it.routeValue == value } ?: Estimate
    }
}

sealed class AppDestination(
    val route: String,
    val frame: String,
    val tab: BottomTab? = null,
) {
    // Access
    data object SignIn : AppDestination("sign_in", "Login - 01 — SignIn")
    data object SignUp : AppDestination("sign_up", "Login - 02 — SignUp")

    // Home
    data object Home : AppDestination("home", "Home / 01 — Dashboard", BottomTab.Home)
    data object Notifications : AppDestination("notifications", "Home / 02 — Notifications", BottomTab.Home)

    // Coach
    data object VoiceIdle : AppDestination("voice_idle", "Voice / 01 — Idle")
    data object VoiceListening : AppDestination("voice_listening", "Voice / 02 — Listening")
    data object VoiceProcessing : AppDestination("voice_processing", "Voice / 03 — Processing")
    data object VoiceSpeaking : AppDestination("voice_speaking", "Voice / 04 — Speaking")
    data object VoiceConversation : AppDestination("voice_conversation", "Voice / 05 — Conversation")
    data object VoiceError : AppDestination("voice_error", "Voice / 06 — Error")

    // Workout
    data object WorkoutDashboard : AppDestination("workout", "Workout / 01 — Dashboard", BottomTab.Workout)
    data object WeeklyPlan : AppDestination("weekly_plan", "Workout / 02 — Weekly Plan", BottomTab.Workout)
    data object WorkoutDetails : AppDestination("workout_details/{${NavArgs.WORKOUT_ID}}", "Workout / 03 — Workout Details") {
        fun createRoute(workoutId: String) = "workout_details/${Uri.encode(workoutId)}"
    }
    data object PreWorkout : AppDestination("pre_workout/{${NavArgs.WORKOUT_ID}}", "Workout / 04 — Pre Workout") {
        fun createRoute(workoutId: String) = "pre_workout/${Uri.encode(workoutId)}"
    }
    data object ActiveWorkout : AppDestination("active_workout/{${NavArgs.WORKOUT_ID}}", "Workout / 05 — Active Workout") {
        fun createRoute(workoutId: String) = "active_workout/${Uri.encode(workoutId)}"
    }
    // Exercise ids repeat across workouts, so the route carries both ids (REF-04).
    data object ExerciseDetails : AppDestination(
        "exercise_details/{${NavArgs.WORKOUT_ID}}/{${NavArgs.EXERCISE_ID}}",
        "Workout / 06 — Exercise Details",
    ) {
        fun createRoute(workoutId: String, exerciseId: String) =
            "exercise_details/${Uri.encode(workoutId)}/${Uri.encode(exerciseId)}"
    }
    data object WorkoutComplete : AppDestination("workout_complete", "Workout / 07 — Workout Complete")

    // Nutrition
    data object NutritionDashboard : AppDestination("nutrition", "Nutrition / 01 — Dashboard", BottomTab.Nutrition)
    data object TodaysMeals : AppDestination(
        "todays_meals?${NavArgs.DATE_EPOCH_DAY}={${NavArgs.DATE_EPOCH_DAY}}",
        "Nutrition / 02 — Today's Meals",
        BottomTab.Nutrition,
    ) {
        /** Without [date] the list opens on today. */
        fun createRoute(date: LocalDate? = null) =
            if (date == null) "todays_meals" else "todays_meals?${NavArgs.DATE_EPOCH_DAY}=${date.toEpochDay()}"
    }
    // Add Food, Voice Food Logging, Food Analysis and Photo Analysis pass the optional meal type on.
    data object AddFood : AppDestination(withMealTypeArg("add_food"), "Nutrition / 03 — Add Food", BottomTab.Nutrition) {
        fun createRoute(type: MealType? = null) = withMealType("add_food", type)
    }
    data object VoiceFoodLogging : AppDestination(withMealTypeArg("voice_food_logging"), "Nutrition / 04 — Voice Food Logging") {
        fun createRoute(type: MealType? = null) = withMealType("voice_food_logging", type)
    }
    data object FoodAnalysis : AppDestination(withMealTypeArg("food_analysis"), "Nutrition / 05 — Food Analysis") {
        fun createRoute(type: MealType? = null) = withMealType("food_analysis", type)
    }
    data object PhotoAnalysis : AppDestination(
        withMealTypeArg("photo_analysis"),
        "Nutrition / 06 — Photo Analysis",
        BottomTab.Nutrition,
    ) {
        fun createRoute(type: MealType? = null) = withMealType("photo_analysis", type)
    }
    data object FoodConfirmation : AppDestination(
        withMealTypeArg("food_confirmation/{${NavArgs.FOOD_CONFIRMATION_MODE}}"),
        "Nutrition / 07 — Food Confirmation",
        BottomTab.Nutrition,
    ) {
        fun createRoute(mode: FoodConfirmationMode, type: MealType? = null) =
            withMealType("food_confirmation/${mode.routeValue}", type)
    }
    data object MealDetails : AppDestination("meal_details/{${NavArgs.MEAL_ID}}", "Nutrition / 08 — Meal Details", BottomTab.Nutrition) {
        fun createRoute(mealId: String) = "meal_details/${Uri.encode(mealId)}"
    }

    // Progress
    data object TodaysOverview : AppDestination("todays_overview", "Progress / 01 — Today's Overview", BottomTab.Progress)
    data object ProgressDashboard : AppDestination("progress", "Progress / 02 — Dashboard", BottomTab.Progress)
    data object WorkoutProgress : AppDestination("workout_progress", "Progress / 03 — Workout Progress", BottomTab.Progress)
    data object NutritionProgress : AppDestination("nutrition_progress", "Progress / 04 — Nutrition Progress", BottomTab.Progress)
    data object HealthTrends : AppDestination("health_trends", "Progress / 05 — Health Trends", BottomTab.Progress)
    data object WeeklyReview : AppDestination("weekly_review", "Progress / 06 — Weekly Review")

    // Profile
    data object ProfileDashboard : AppDestination("profile", "Profile / 01 — Dashboard", BottomTab.Profile)
    data object EditProfile : AppDestination("edit_profile", "Profile / 02 — Edit Profile")
    data object GoalsPreferences : AppDestination("goals_preferences", "Profile / 03 — Goals & Preferences")
    data object NutritionPreferences : AppDestination("nutrition_preferences", "Profile / 04 — Nutrition Preferences")
    data object VoiceAiSettings : AppDestination("voice_ai_settings", "Profile / 05 — Voice & AI Settings")
    data object Settings : AppDestination("settings", "Profile / 06 — Settings")

    companion object {
        /** The 37 entries of the Figma inventory. */
        val all: List<AppDestination> by lazy {
            listOf(
                SignIn, SignUp,
                Home, Notifications,
                VoiceIdle, VoiceListening, VoiceProcessing, VoiceSpeaking, VoiceConversation, VoiceError,
                WorkoutDashboard, WeeklyPlan, WorkoutDetails, PreWorkout, ActiveWorkout, ExerciseDetails, WorkoutComplete,
                NutritionDashboard, TodaysMeals, AddFood, VoiceFoodLogging, FoodAnalysis, PhotoAnalysis, FoodConfirmation, MealDetails,
                TodaysOverview, ProgressDashboard, WorkoutProgress, NutritionProgress, HealthTrends, WeeklyReview,
                ProfileDashboard, EditProfile, GoalsPreferences, NutritionPreferences, VoiceAiSettings, Settings,
            )
        }

        fun fromRoute(route: String?): AppDestination? = all.firstOrNull { it.route == route }

        /** First screen of each tab. Progress opens on its dashboard (Progress / 02): open question in REF-05. */
        fun tabRoot(tab: BottomTab): AppDestination = when (tab) {
            BottomTab.Home -> Home
            BottomTab.Workout -> WorkoutDashboard
            BottomTab.Nutrition -> NutritionDashboard
            BottomTab.Progress -> ProgressDashboard
            BottomTab.Profile -> ProfileDashboard
        }
    }
}