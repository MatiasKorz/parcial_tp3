package com.example.examentp3.ui.screens.home

import androidx.annotation.DrawableRes
import com.example.examentp3.R

/** Data shown by the Home dashboard. Texts that never change live in strings.xml. */
data class HomeUiState(
    val userName: String,
    @param:DrawableRes val avatar: Int,
    val dailyGoalPercent: Int,
    val caloriesConsumed: Int,
    val caloriesGoal: Int,
    val proteinGrams: Int,
    val proteinGoalGrams: Int,
    val carbsGrams: Int,
    val carbsGoalGrams: Int,
    val steps: Int,
    val distanceKm: Double,
    val activeKcal: Int,
    val workout: RecommendedWorkout,
)

data class RecommendedWorkout(
    val name: String,
    val durationMinutes: Int,
    val exerciseCount: Int,
    val level: String,
    val estimatedVolumeKg: Int,
)

/** Demo screen: sample values copied from the Figma frame "Home / 01 — Dashboard". */
val HomeSampleUiState = HomeUiState(
    userName = "Alex Morgan",
    avatar = R.drawable.img_home_avatar,
    dailyGoalPercent = 72,
    caloriesConsumed = 1420,
    caloriesGoal = 2100,
    proteinGrams = 104,
    proteinGoalGrams = 140,
    carbsGrams = 152,
    carbsGoalGrams = 220,
    steps = 7842,
    distanceKm = 5.6,
    activeKcal = 312,
    workout = RecommendedWorkout(
        name = "Upper Body Strength",
        durationMinutes = 42,
        exerciseCount = 5,
        level = "Intermediate",
        estimatedVolumeKg = 2340,
    ),
)
