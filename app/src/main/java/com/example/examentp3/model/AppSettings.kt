package com.example.examentp3.model

/** Unit system. Figma only shows "Metric (kg, cm, km)"; other options are pending in REF-05. */
enum class UnitSystem { Metric }

/** App appearance. Figma only shows "System default"; the design has no dark theme. */
enum class AppearanceMode { SystemDefault }

/**
 * "Coaching Style" of Profile / 05 — Voice & AI Settings. Kept apart from [CoachingStyle]
 * (Profile / 03) until REF-05 says whether both are the same setting.
 */
enum class CoachStyle { Supportive, Balanced, Direct }

/** "Response Style" of Profile / 05 — Voice & AI Settings. Only one can be picked. */
enum class ResponseStyle { Concise, Balanced, Detailed }

/**
 * Voice & AI choices. They are only stored: no speech, audio or vibration is produced (NAV05).
 * Defaults are the state drawn in the Figma frame.
 */
data class VoiceCoachSettings(
    val coachStyle: CoachStyle = CoachStyle.Balanced,
    val responseStyle: ResponseStyle = ResponseStyle.Balanced,
    val speakResponsesAloud: Boolean = true,
    val autoPlayCoaching: Boolean = true,
    val allowInterruptions: Boolean = true,
    val voiceFeedback: Boolean = true,
    val hapticFeedback: Boolean = true,
)

/** App preferences chosen in Settings (Profile / 06). Defaults are the values shown in Figma. */
data class AppSettings(
    val units: UnitSystem = UnitSystem.Metric,
    val appearance: AppearanceMode = AppearanceMode.SystemDefault,
    val notificationsEnabled: Boolean = true,
    val voiceCoach: VoiceCoachSettings = VoiceCoachSettings(),
)
