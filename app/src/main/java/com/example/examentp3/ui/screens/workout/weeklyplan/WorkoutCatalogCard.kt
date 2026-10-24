package com.example.examentp3.ui.screens.workout.weeklyplan

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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


@Composable
internal fun WorkoutCatalogCard(
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
internal fun levelLabel(level: String): String = when (level) {
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
private fun WorkoutCatalogCardPreview() {
    ExamenTP3Theme { WorkoutCatalogCard(workout = PreviewWorkouts.first(), onClick = {}) }
}
