package com.example.examentp3.ui.screens.workout.details

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
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.examentp3.R
import com.example.examentp3.model.Exercise
import com.example.examentp3.model.WorkoutDetail
import com.example.examentp3.ui.components.VoraPrimaryButton
import com.example.examentp3.ui.screens.workout.weeklyplan.levelLabel
import com.example.examentp3.ui.theme.*
import com.example.examentp3.viewmodel.DetailUnavailable
import com.example.examentp3.viewmodel.WorkoutDetailsUiState

/**
 * Workout / 03 — Workout Details with the data of GET /v1/workouts/{workoutId}, cached in Room (NUC-02).
 * The coach note, "Edit workout" and the menu stay as PAN-4.3 demonstrations; loading and retry belong to NUC-03.
 */
@Composable
fun WorkoutDetailsScreen(
    uiState: WorkoutDetailsUiState,
    onBack: () -> Unit,
    onStartWorkout: () -> Unit,
    onExerciseClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var notice by rememberSaveable { mutableStateOf<Int?>(null) }
    val workout = uiState.workout
    LazyColumn(
        modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
            .statusBarsPadding().navigationBarsPadding(),
        contentPadding = PaddingValues(
            start = Spacing.screenMargin, end = Spacing.screenMargin,
            top = Spacing.s12, bottom = Spacing.s24,
        ),
        verticalArrangement = Arrangement.spacedBy(Spacing.s10),
    ) {
        item(key = "header") {
            OverviewHeader(onBack) { notice = R.string.workout_details_menu_notice }
        }
        if (workout == null) {
            uiState.unavailable?.let { reason ->
                item(key = "unavailable") { UnavailableMessage(reason, Modifier.padding(top = Spacing.s6)) }
            }
            return@LazyColumn
        }
        item(key = "overview") {
            // Combined with the lazy list gap, preserves 16 dp before exercises.
            Column(
                Modifier.padding(top = Spacing.s6, bottom = Spacing.s6),
                verticalArrangement = Arrangement.spacedBy(Spacing.s16),
            ) {
                WorkoutOverview(workout)
                CoachNote()
            }
        }
        itemsIndexed(workout.exercises, key = { _, exercise -> exercise.id }) { index, exercise ->
            ExerciseRow(index + 1, exercise) { onExerciseClick(exercise.id) }
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
private fun WorkoutOverview(workout: WorkoutDetail) {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.s8)) {
        Text(workout.name, style = MaterialTheme.typography.workoutDetailsTitle)
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(Spacing.s12),
            verticalArrangement = Arrangement.spacedBy(Spacing.s4),
        ) {
            val count = workout.exercises.size
            Text(stringResource(R.string.workout_details_duration, workout.durationMinutes), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.voraColors.textSecondary)
            Text(stringResource(R.string.workout_details_bullet), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.voraColors.textMuted)
            Text(levelLabel(workout.level), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.voraColors.textSecondary)
            Text(stringResource(R.string.workout_details_bullet), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.voraColors.textMuted)
            Text(pluralStringResource(R.plurals.weekly_plan_exercise_count, count, count), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.voraColors.textSecondary)
        }
        val focus = workout.exercises.map { it.muscleGroup }.filter { it.isNotBlank() }.distinct()
        if (focus.isNotEmpty()) {
            Text(
                stringResource(R.string.workout_details_focus, focus.joinToString(" • ")),
                style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.voraColors.textSecondary,
            )
        }
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

/** Not designed in Figma: a state inside the detail, styled like the frame's coach note (RF02). */
@Composable
private fun UnavailableMessage(reason: DetailUnavailable, modifier: Modifier = Modifier) {
    Text(
        stringResource(reason.message),
        style = MaterialTheme.typography.workoutDetailsNoteBody,
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier.fillMaxWidth()
            .background(MaterialTheme.voraColors.mintSoft, MaterialTheme.shapes.small)
            .padding(Spacing.s12),
    )
}

@Composable
private fun ExerciseRow(number: Int, exercise: Exercise, onClick: () -> Unit) {
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
                Text(exercise.name, style = MaterialTheme.typography.workoutDetailsExercise)
                Text(
                    stringResource(R.string.workout_details_prescription, exercise.sets, exercise.reps, exercise.restSeconds),
                    style = MaterialTheme.typography.workoutDetailsPrescription, color = MaterialTheme.voraColors.textSecondary,
                )
            }
            Icon(
                painterResource(R.drawable.ic_workout_details_chevron), contentDescription = null,
                tint = MaterialTheme.voraColors.textMuted, modifier = Modifier.size(16.dp),
            )
        }
    }
}

// Sample data shaped like the mock fixture w01.
private val PreviewWorkout = WorkoutDetail(
    id = "w01", name = "Fuerza inicial", level = "beginner", durationMinutes = 25,
    description = "Rutina de ejemplo para el trabajo práctico.",
    exercises = listOf(
        Exercise("e01", "Sentadilla", "Piernas", 3, 10, 60, "Separá los pies y flexioná las rodillas con control.", "Usá una carga que puedas controlar."),
        Exercise("e02", "Remo con mancuerna", "Espalda", 3, 10, 60, "Apoyá una mano y llevá la mancuerna hacia el torso.", "Usá una carga que puedas controlar."),
        Exercise("e03", "Press con mancuernas", "Pecho", 3, 10, 60, "Empujá las mancuernas con un movimiento controlado.", "Usá una carga que puedas controlar."),
    ),
)

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun WorkoutDetailsPreview() {
    ExamenTP3Theme {
        WorkoutDetailsScreen(WorkoutDetailsUiState(workout = PreviewWorkout, isLoading = false), {}, {}, {})
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 640, fontScale = 1.3f)
@Composable
private fun WorkoutDetailsCompactPreview() {
    ExamenTP3Theme {
        WorkoutDetailsScreen(WorkoutDetailsUiState(workout = PreviewWorkout, isLoading = false), {}, {}, {})
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun WorkoutDetailsOfflinePreview() {
    ExamenTP3Theme {
        WorkoutDetailsScreen(
            WorkoutDetailsUiState(isLoading = false, unavailable = DetailUnavailable.NotAvailableOffline),
            {}, {}, {},
        )
    }
}
