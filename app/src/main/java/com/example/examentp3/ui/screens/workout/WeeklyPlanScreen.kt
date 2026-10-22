package com.example.examentp3.ui.screens.workout

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.examentp3.R
import com.example.examentp3.model.WorkoutSummary
import com.example.examentp3.ui.theme.ExamenTP3Theme
import com.example.examentp3.ui.theme.Spacing
import com.example.examentp3.ui.theme.voraColors
import com.example.examentp3.viewmodel.WorkoutCatalogUiState

/**
 * Workout / 02 — Weekly Plan hosting the catalog of GET /v1/workouts (NUC-01).
 * The day strip, the "TODAY'S WORKOUT" highlight and the "Need to adjust your week?" card are PAN-4.2.
 */
@Composable
fun WeeklyPlanScreen(
    uiState: WorkoutCatalogUiState,
    onWorkoutClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding(),
        contentPadding = PaddingValues(
            start = Spacing.screenMargin,
            end = Spacing.screenMargin,
            top = Spacing.s12,
            bottom = Spacing.s24,
        ),
        verticalArrangement = Arrangement.spacedBy(Spacing.s12),
    ) {
        item { Header(modifier = Modifier.padding(bottom = Spacing.s8)) }
        items(uiState.workouts, key = { it.id }) { workout ->
            WorkoutCard(workout = workout, onClick = { onWorkoutClick(workout.id) })
        }
    }
}

@Composable
private fun Header(modifier: Modifier = Modifier) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(Spacing.s4)) {
        Text(
            text = stringResource(R.string.weekly_plan_title),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Text(
            text = stringResource(R.string.weekly_plan_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.voraColors.textSecondary,
        )
    }
}

@Composable
private fun WorkoutCard(
    workout: WorkoutSummary,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.small)
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outline, MaterialTheme.shapes.small)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = Spacing.s16, vertical = Spacing.s14),
        verticalArrangement = Arrangement.spacedBy(Spacing.s4),
    ) {
        Text(
            text = workout.name,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
            text = stringResource(
                R.string.weekly_plan_workout_meta,
                workout.durationMinutes,
                pluralStringResource(R.plurals.weekly_plan_exercise_count, workout.exerciseCount, workout.exerciseCount),
                levelLabel(workout.level),
            ),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.voraColors.textSecondary,
        )
    }
}

/** The API sends the level in lowercase English; the Figma shows it capitalized ("Intermediate"). */
@Composable
private fun levelLabel(level: String): String = when (level) {
    "beginner" -> stringResource(R.string.workout_level_beginner)
    "intermediate" -> stringResource(R.string.workout_level_intermediate)
    "advanced" -> stringResource(R.string.workout_level_advanced)
    else -> level.replaceFirstChar { it.uppercase() }
}

// Sample data shaped like the mock fixtures (w01-w03).
private val PreviewWorkouts = listOf(
    WorkoutSummary(id = "w01", name = "Fuerza inicial", level = "beginner", durationMinutes = 25, exerciseCount = 3),
    WorkoutSummary(id = "w02", name = "Entrenamiento completo", level = "intermediate", durationMinutes = 35, exerciseCount = 3),
    WorkoutSummary(id = "w03", name = "Fuerza y constancia", level = "beginner", durationMinutes = 30, exerciseCount = 3),
)

@Preview(showBackground = true)
@Composable
private fun WeeklyPlanScreenPreview() {
    ExamenTP3Theme {
        WeeklyPlanScreen(
            uiState = WorkoutCatalogUiState(workouts = PreviewWorkouts),
            onWorkoutClick = {},
        )
    }
}
