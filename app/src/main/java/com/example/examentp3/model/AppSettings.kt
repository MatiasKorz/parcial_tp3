package com.example.examentp3.model

/** Unit system. Figma only shows "Metric (kg, cm, km)"; other options are pending in REF-05. */
enum class UnitSystem { Metric }

/** App appearance. Figma only shows "System default"; the design has no dark theme. */
enum class AppearanceMode { SystemDefault }

/** App preferences chosen in Settings (Profile / 06). Defaults are the values shown in Figma. */
data class AppSettings(
    val units: UnitSystem = UnitSystem.Metric,
    val appearance: AppearanceMode = AppearanceMode.SystemDefault,
    val notificationsEnabled: Boolean = true,
)
