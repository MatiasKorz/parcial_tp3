package com.example.examentp3.model

/**
 * Figma demo workout cards mapped to real ids of the mock catalog (REF-04: w01-w03),
 * so their "Details" opens a workout that GET /v1/workouts/{workoutId} can return instead of a 404.
 * Each card takes the mock workout closest in level and duration, and no two cards share one (NAV04).
 */
enum class DemoWorkout(val workoutId: String) {
    /** "Upper Body Strength", 42 min, Intermediate: w02 is the only intermediate workout (35 min). */
    UpperBody("w02"),

    /** "Active Recovery", 25 min: w01 also lasts 25 min. */
    ActiveRecovery("w01"),

    /** "Lower Body Strength", 45 min: w03 is the remaining one (30 min). */
    LowerBody("w03"),
}
