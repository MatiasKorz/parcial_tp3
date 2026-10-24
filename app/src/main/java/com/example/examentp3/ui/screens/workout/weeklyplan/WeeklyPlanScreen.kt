package com.example.examentp3.ui.screens.workout.weeklyplan

import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.examentp3.R
import com.example.examentp3.viewmodel.WorkoutCatalogUiState
import com.example.examentp3.ui.components.VoraCard
import com.example.examentp3.ui.components.VoraEmptyState
import com.example.examentp3.ui.components.VoraErrorBanner
import com.example.examentp3.ui.components.VoraErrorState
import com.example.examentp3.ui.components.VoraLoadingState
import com.example.examentp3.model.WorkoutSummary
import com.example.examentp3.viewmodel.UiError
import com.example.examentp3.ui.theme.*

/** Keeps the Figma demonstration plan separate from the Room-backed NUC-01 catalog. */
@Composable
fun WeeklyPlanScreen(
    uiState: WorkoutCatalogUiState,
    onWorkoutDetails: () -> Unit,
    onWorkoutClick: (String) -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var selectedDay by rememberSaveable { mutableIntStateOf(1) }
    var coachPrompt by rememberSaveable { mutableStateOf<Int?>(null) }
    LazyColumn(
        modifier = modifier.fillMaxSize()
            .background(MaterialTheme.colorScheme.background).statusBarsPadding(),
        contentPadding = PaddingValues(
            start = Spacing.screenMargin, end = Spacing.screenMargin,
            top = Spacing.s12, bottom = Spacing.s24,
        ),
        verticalArrangement = Arrangement.spacedBy(Spacing.s20),
    ) {
        item(key = "header") {
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.s4)) {
                Text(stringResource(R.string.weekly_plan_title), style = MaterialTheme.typography.weeklyPlanTitle)
                Text(
                    stringResource(R.string.weekly_plan_subtitle),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.voraColors.textSecondary,
                )
            }
        }
        item(key = "calendar") {
            WeekCalendar(selectedDay) { selectedDay = it }
        }
        item(key = "workouts") {
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.s12)) {
                Text(
                    stringResource(R.string.weekly_plan_demo_section),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.voraColors.textSecondary,
                )
                TodayCard(onWorkoutDetails)
                PlannedWorkoutCard(
                    R.string.weekly_plan_wednesday, R.string.weekly_plan_recovery,
                    R.string.weekly_plan_recovery_summary, true, onWorkoutDetails,
                )
                PlannedWorkoutCard(
                    R.string.weekly_plan_thursday, R.string.weekly_plan_lower_body,
                    R.string.weekly_plan_lower_body_summary, false, onWorkoutDetails,
                )
            }
        }
        item(key = "coach") {
            CoachPanel { coachPrompt = it }
        }
        item(key = "catalog_header") {
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.s4)) {
                Text(stringResource(R.string.weekly_plan_catalog_title), style = MaterialTheme.typography.titleMedium)
                Text(
                    stringResource(R.string.weekly_plan_catalog_description),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.voraColors.textSecondary,
                )
            }
        }
        // RF05: content first (with loading or error alongside), then loading, then error, then empty.
        when {
            uiState.workouts.isNotEmpty() -> {
                if (uiState.isLoading) {
                    item(key = "catalog_loading") { VoraLoadingState(stringResource(R.string.weekly_plan_catalog_loading)) }
                }
                uiState.error?.let { error ->
                    item(key = "catalog_error") {
                        VoraErrorBanner(
                            stringResource(R.string.weekly_plan_catalog_cached, stringResource(error.message)),
                            stringResource(R.string.state_retry),
                            onRetry,
                        )
                    }
                }
                items(uiState.workouts, key = { "catalog:${it.id}" }) { workout ->
                    WorkoutCatalogCard(workout = workout, onClick = { onWorkoutClick(workout.id) })
                }
            }
            uiState.isLoading -> item(key = "catalog_loading") {
                VoraLoadingState(stringResource(R.string.weekly_plan_catalog_loading))
            }
            uiState.error != null -> item(key = "catalog_error") {
                VoraErrorState(
                    stringResource(R.string.weekly_plan_catalog_error_title),
                    stringResource(uiState.error.message),
                    stringResource(R.string.state_retry),
                    onRetry,
                )
            }
            else -> item(key = "catalog_empty") {
                VoraEmptyState(
                    stringResource(R.string.weekly_plan_catalog_empty_title),
                    stringResource(R.string.weekly_plan_catalog_empty_message),
                )
            }
        }
    }
    coachPrompt?.let { prompt ->
        AlertDialog(
            onDismissRequest = { coachPrompt = null },
            title = { Text(stringResource(prompt)) },
            text = { Text(stringResource(R.string.weekly_plan_demo_notice)) },
            confirmButton = {
                TextButton(onClick = { coachPrompt = null }) {
                    Text(stringResource(R.string.weekly_plan_dismiss))
                }
            },
        )
    }
}

@Composable
private fun WeekCalendar(selectedDay: Int, onSelectDay: (Int) -> Unit) {
    val labels = listOf(
        R.string.weekly_plan_mon, R.string.weekly_plan_tue, R.string.weekly_plan_wed,
        R.string.weekly_plan_thu, R.string.weekly_plan_fri, R.string.weekly_plan_sat,
        R.string.weekly_plan_sun,
    )
    BoxWithConstraints(Modifier.fillMaxWidth()) {
        val dayWidth = minOf(42.dp, maxWidth / labels.size)
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            labels.forEachIndexed { index, label ->
                val isSelected = selectedDay == index
                Column(
                    modifier = Modifier.width(dayWidth).heightIn(min = 56.dp)
                        .clip(CircleShape)
                        .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.background)
                        .semantics { selected = isSelected }
                        .clickable(role = Role.Tab) { onSelectDay(index) }
                        .padding(vertical = Spacing.s8),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(Spacing.s6),
                ) {
                    Text(
                        stringResource(label), style = MaterialTheme.typography.weeklyPlanDayLabel,
                        color = if (isSelected) MaterialTheme.colorScheme.tertiary else MaterialTheme.voraColors.textSecondary,
                    )
                    Text(
                        stringResource(R.string.weekly_plan_day_number, index + 7),
                        style = MaterialTheme.typography.weeklyPlanDayNumber,
                        color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
        }
    }
}

@Composable
private fun TodayCard(onClick: () -> Unit) {
    VoraCard(
        Modifier.clip(MaterialTheme.shapes.medium)
            .clickable(role = Role.Button, onClick = onClick)
            .border(1.dp, MaterialTheme.colorScheme.primary, MaterialTheme.shapes.medium),
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(
                stringResource(R.string.weekly_plan_today),
                style = MaterialTheme.typography.weeklyPlanSectionLabel,
                color = MaterialTheme.colorScheme.primary, modifier = Modifier.weight(1f),
            )
            Text(
                stringResource(R.string.weekly_plan_ready),
                style = MaterialTheme.typography.weeklyPlanDayLabel,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.background(MaterialTheme.colorScheme.secondary, MaterialTheme.shapes.extraSmall)
                    .padding(horizontal = Spacing.s8, vertical = Spacing.s2),
            )
        }
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.s2)) {
            Text(stringResource(R.string.weekly_plan_upper_body), style = MaterialTheme.typography.weeklyPlanTodayTitle)
            Text(
                stringResource(R.string.weekly_plan_upper_body_summary),
                style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.voraColors.textSecondary,
            )
        }
    }
}

@Composable
private fun PlannedWorkoutCard(
    @StringRes day: Int,
    @StringRes title: Int,
    @StringRes summary: Int,
    showOrb: Boolean,
    onClick: () -> Unit,
) {
    VoraCard(Modifier.clip(MaterialTheme.shapes.medium).clickable(role = Role.Button, onClick = onClick)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(Spacing.s4)) {
                Text(stringResource(day), style = MaterialTheme.typography.weeklyPlanSectionLabel, color = MaterialTheme.voraColors.textMuted)
                Text(stringResource(title), style = MaterialTheme.typography.weeklyPlanWorkoutTitle)
                Text(stringResource(summary), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.voraColors.textSecondary)
            }
            if (showOrb) {
                Spacer(Modifier.width(Spacing.s8))
                Image(painterResource(R.drawable.ic_home_orb), contentDescription = null, modifier = Modifier.size(24.dp))
            }
        }
    }
}

@Composable
private fun CoachPanel(onPrompt: (Int) -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.primary).padding(Spacing.s16),
        verticalArrangement = Arrangement.spacedBy(Spacing.s12),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Spacing.s8)) {
            Image(painterResource(R.drawable.ic_home_orb), contentDescription = null, modifier = Modifier.size(20.dp))
            Text(
                stringResource(R.string.weekly_plan_coach_title),
                style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.secondary,
            )
        }
        Text(
            stringResource(R.string.weekly_plan_coach_body),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.weeklyPlanCoachText,
        )
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(Spacing.s8),
            verticalArrangement = Arrangement.spacedBy(Spacing.s8),
        ) {
            CoachChip(R.string.weekly_plan_tired, onPrompt)
            CoachChip(R.string.weekly_plan_move_workout, onPrompt)
        }
    }
}

@Composable
private fun CoachChip(@StringRes label: Int, onPrompt: (Int) -> Unit) {
    Text(
        stringResource(label), style = MaterialTheme.typography.weeklyPlanPrompt,
        color = MaterialTheme.colorScheme.onPrimary,
        modifier = Modifier.clip(VoraPillShape)
            .background(MaterialTheme.colorScheme.weeklyPlanPromptBackground)
            .clickable(role = Role.Button) { onPrompt(label) }
            .padding(horizontal = Spacing.s12, vertical = Spacing.s6),
    )
}

// Sample data shaped like the mock fixtures (w01-w02).
private val PreviewCatalog = listOf(
    WorkoutSummary(id = "w01", name = "Fuerza inicial", level = "beginner", durationMinutes = 25, exerciseCount = 3),
    WorkoutSummary(id = "w02", name = "Entrenamiento completo", level = "intermediate", durationMinutes = 35, exerciseCount = 3),
)

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun WeeklyPlanPreview() {
    ExamenTP3Theme { WeeklyPlanScreen(WorkoutCatalogUiState(workouts = PreviewCatalog), {}, {}, {}) }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 640, fontScale = 1.3f)
@Composable
private fun WeeklyPlanCompactPreview() {
    ExamenTP3Theme { WeeklyPlanScreen(WorkoutCatalogUiState(workouts = PreviewCatalog), {}, {}, {}) }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 1400)
@Composable
private fun WeeklyPlanLoadingPreview() {
    ExamenTP3Theme { WeeklyPlanScreen(WorkoutCatalogUiState(isLoading = true), {}, {}, {}) }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 1400)
@Composable
private fun WeeklyPlanEmptyPreview() {
    ExamenTP3Theme { WeeklyPlanScreen(WorkoutCatalogUiState(), {}, {}, {}) }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 1400)
@Composable
private fun WeeklyPlanErrorPreview() {
    ExamenTP3Theme { WeeklyPlanScreen(WorkoutCatalogUiState(error = UiError.Server), {}, {}, {}) }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 1400)
@Composable
private fun WeeklyPlanCachedErrorPreview() {
    ExamenTP3Theme {
        WeeklyPlanScreen(WorkoutCatalogUiState(workouts = PreviewCatalog, error = UiError.NoConnection), {}, {}, {})
    }
}
