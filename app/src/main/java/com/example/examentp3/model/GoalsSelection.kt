package com.example.examentp3.model

/** Options of "Primary Goal". Several can be picked at once (the Figma frame marks two). */
enum class PrimaryGoal { BuildStrength, ImproveFitness, LoseBodyFat, BuildHealthyHabits }

/** Options of "Training Frequency". Only one can be picked. */
enum class TrainingFrequency { ThreeDays, FourDays, FiveDays, SixPlusDays }

/** Options of "Workout Preference". Only one can be picked. */
enum class WorkoutPreference { Strength, Cardio, Hiit, Mobility, Mixed }

/** Options of "Coaching Style". The design only draws "Balanced" (open question in REF-05). */
enum class CoachingStyle { Balanced }

/**
 * What the user picked on Profile / 03 — Goals & Preferences.
 * Not named GoalsPreferences: that name is already the AppDestination.
 */
data class GoalsSelection(
    val goals: Set<PrimaryGoal>,
    val frequency: TrainingFrequency,
    val workout: WorkoutPreference,
    val coachingStyle: CoachingStyle,
)

// Initial selection of the Figma frame "Profile / 03 — Goals & Preferences".
val DemoGoalsSelection = GoalsSelection(
    goals = setOf(PrimaryGoal.BuildStrength, PrimaryGoal.ImproveFitness),
    frequency = TrainingFrequency.FourDays,
    workout = WorkoutPreference.Strength,
    coachingStyle = CoachingStyle.Balanced,
)
