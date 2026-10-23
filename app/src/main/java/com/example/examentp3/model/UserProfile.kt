package com.example.examentp3.model

/**
 * Personal data of the demo user. Values are free text because the design shows them that way
 * (no date picker or unit selector, see REF-05).
 */
data class UserProfile(
    val firstName: String,
    val lastName: String,
    val dateOfBirth: String,
    val height: String,
    val weight: String,
    val gender: String,
    val goals: GoalsSelection,
)

// Values from the Figma frames "Profile / 02 — Edit Profile" and "Profile / 03 — Goals & Preferences".
val DemoUserProfile = UserProfile(
    firstName = "Alex",
    lastName = "Morgan",
    dateOfBirth = "14 March 1995",
    height = "178 cm",
    weight = "76 kg",
    gender = "Prefer not to say",
    goals = DemoGoalsSelection,
)
