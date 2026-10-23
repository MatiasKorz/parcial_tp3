package com.example.examentp3.ui.screens.workout.details

import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.examentp3.R
import com.example.examentp3.ui.components.VoraPrimaryButton
import com.example.examentp3.ui.theme.*

private data class DemoExercise(@param:StringRes val name: Int, @param:StringRes val prescription: Int)
private val demoExercises = listOf(
    DemoExercise(R.string.workout_details_bench, R.string.workout_details_bench_sets),
    DemoExercise(R.string.workout_details_row, R.string.workout_details_row_sets),
    DemoExercise(R.string.workout_details_shoulder, R.string.workout_details_shoulder_sets),
    DemoExercise(R.string.workout_details_raise, R.string.workout_details_raise_sets),
    DemoExercise(R.string.workout_details_tricep, R.string.workout_details_tricep_sets),
)

/** Figma-only demonstration; real detail models and state belong to NUC-02/NUC-03. */
@Composable
fun WorkoutDetailsScreen(
    onBack: () -> Unit,
    onStartWorkout: () -> Unit,
    onExerciseClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var notice by rememberSaveable { mutableStateOf<Int?>(null) }
    LazyColumn(
        modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
            .statusBarsPadding().navigationBarsPadding(),
        contentPadding = PaddingValues(
            start = Spacing.screenMargin, end = Spacing.screenMargin,
            top = Spacing.s12, bottom = Spacing.s24,
        ),
        verticalArrangement = Arrangement.spacedBy(Spacing.s10),
    ) {
        item(key = "overview") {
            // Combined with the lazy list gap, preserves 16 dp before exercises.
            Column(
                Modifier.padding(bottom = Spacing.s6),
                verticalArrangement = Arrangement.spacedBy(Spacing.s16),
            ) {
                OverviewHeader(onBack) { notice = R.string.workout_details_menu_notice }
                WorkoutOverview()
                CoachNote()
            }
        }
        itemsIndexed(demoExercises, key = { _, exercise -> exercise.name }) { index, exercise ->
            ExerciseRow(index + 1, exercise, onExerciseClick)
        }
        item(key = "actions") {
            Column(
                Modifier.padding(top = Spacing.s6),
                verticalArrangement = Arrangement.spacedBy(Spacing.s8),
            ) {
                // Only this button inherits the frame's action typography.
                MaterialTheme(typography = MaterialTheme.typography.copy(labelMedium = MaterialTheme.typography.workoutDetailsAction)) {
                    VoraPrimaryButton(
                        stringResource(R.string.workout_details_start), onStartWorkout,
                        modifier = Modifier.fillMaxWidth().heightIn(min = 44.dp),
                    )
                }
                Surface(
                    onClick = { notice = R.string.workout_details_edit_notice },
                    modifier = Modifier.fillMaxWidth(),
                    shape = VoraPillShape,
                    color = MaterialTheme.colorScheme.background,
                    contentColor = MaterialTheme.colorScheme.primary,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                ) {
                    Box(Modifier.padding(horizontal = Spacing.s16, vertical = Spacing.s12), contentAlignment = Alignment.Center) {
                        Text(stringResource(R.string.workout_details_edit), style = MaterialTheme.typography.workoutDetailsAction)
                    }
                }
            }
        }
    }
    notice?.let { message ->
        AlertDialog(
            onDismissRequest = { notice = null },
            title = { Text(stringResource(R.string.workout_details_demo_title)) },
            text = { Text(stringResource(message)) },
            confirmButton = {
                TextButton(onClick = { notice = null }) { Text(stringResource(R.string.workout_details_ok)) }
            },
        )
    }
}

@Composable
private fun OverviewHeader(onBack: () -> Unit, onMenu: () -> Unit) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Icon(
            painterResource(R.drawable.ic_workout_details_back),
            contentDescription = stringResource(R.string.workout_details_back),
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp).clickable(role = Role.Button, onClick = onBack),
        )
        Text(
            stringResource(R.string.workout_details_overview),
            style = MaterialTheme.typography.workoutDetailsOverview,
            modifier = Modifier.weight(1f),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
        )
        Icon(
            painterResource(R.drawable.ic_workout_details_more),
            contentDescription = stringResource(R.string.workout_details_menu),
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp).clickable(role = Role.Button, onClick = onMenu),
        )
    }
}

@Composable
private fun WorkoutOverview() {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.s8)) {
        Text(stringResource(R.string.workout_details_title), style = MaterialTheme.typography.workoutDetailsTitle)
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(Spacing.s12),
            verticalArrangement = Arrangement.spacedBy(Spacing.s4),
        ) {
            Text(stringResource(R.string.workout_details_duration), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.voraColors.textSecondary)
            Text(stringResource(R.string.workout_details_bullet), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.voraColors.textMuted)
            Text(stringResource(R.string.workout_details_level), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.voraColors.textSecondary)
            Text(stringResource(R.string.workout_details_bullet), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.voraColors.textMuted)
            Text(stringResource(R.string.workout_details_count), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.voraColors.textSecondary)
        }
        Text(stringResource(R.string.workout_details_focus), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.voraColors.textSecondary)
    }
}

@Composable
private fun CoachNote() {
    Column(
        Modifier.fillMaxWidth().background(MaterialTheme.voraColors.mintSoft, MaterialTheme.shapes.small).padding(Spacing.s12),
        verticalArrangement = Arrangement.spacedBy(Spacing.s8),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Spacing.s6)) {
            Image(painterResource(R.drawable.ic_home_orb), contentDescription = null, modifier = Modifier.size(16.dp))
            Text(stringResource(R.string.workout_details_coach_title), style = MaterialTheme.typography.workoutDetailsNoteLabel, color = MaterialTheme.colorScheme.primary)
        }
        Text(stringResource(R.string.workout_details_coach_body), style = MaterialTheme.typography.workoutDetailsNoteBody, color = MaterialTheme.colorScheme.primary)
    }
}

@Composable
private fun ExerciseRow(number: Int, exercise: DemoExercise, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            Modifier.padding(Spacing.s12),
            horizontalArrangement = Arrangement.spacedBy(Spacing.s12),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                Modifier.size(32.dp).background(MaterialTheme.colorScheme.background, MaterialTheme.shapes.extraSmall),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    stringResource(R.string.workout_details_number, number),
                    style = MaterialTheme.typography.workoutDetailsNumber, color = MaterialTheme.colorScheme.primary,
                )
            }
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(Spacing.s2)) {
                Text(stringResource(exercise.name), style = MaterialTheme.typography.workoutDetailsExercise)
                Text(stringResource(exercise.prescription), style = MaterialTheme.typography.workoutDetailsPrescription, color = MaterialTheme.voraColors.textSecondary)
            }
            Icon(
                painterResource(R.drawable.ic_workout_details_chevron), contentDescription = null,
                tint = MaterialTheme.voraColors.textMuted, modifier = Modifier.size(16.dp),
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun WorkoutDetailsPreview() {
    ExamenTP3Theme { WorkoutDetailsScreen({}, {}, {}) }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 640, fontScale = 1.3f)
@Composable
private fun WorkoutDetailsCompactPreview() {
    ExamenTP3Theme { WorkoutDetailsScreen({}, {}, {}) }
}
