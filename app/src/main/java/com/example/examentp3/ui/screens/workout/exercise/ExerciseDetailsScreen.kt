package com.example.examentp3.ui.screens.workout.exercise

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.examentp3.R
import com.example.examentp3.model.Exercise
import com.example.examentp3.ui.components.VoraPrimaryButton
import com.example.examentp3.ui.theme.*
import com.example.examentp3.viewmodel.ExerciseDetailsUiState

/**
 * Workout / 06 — Exercise Details showing the cached exercise of GET /v1/workouts/{workoutId} (NUC-02):
 * instructions go in "Performance Steps" and the tip in "PRO FORM CUES". The image and "Ask Vora" stay as PAN-4.6.
 */
@Composable
fun ExerciseDetailsScreen(
    uiState: ExerciseDetailsUiState,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var notice by rememberSaveable { mutableStateOf<Int?>(null) }
    val exercise = uiState.exercise
    LazyColumn(
        modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
            .statusBarsPadding().navigationBarsPadding(),
        contentPadding = PaddingValues(
            start = Spacing.screenMargin, end = Spacing.screenMargin,
            top = Spacing.s12, bottom = Spacing.s24,
        ),
        verticalArrangement = Arrangement.spacedBy(Spacing.s16),
    ) {
        item(key = "header") {
            GuideHeader(onBack) { notice = R.string.exercise_details_help_notice }
        }
        if (exercise == null) {
            if (!uiState.isLoading) {
                item(key = "unavailable") { UnavailableMessage() }
                item(key = "back") { BackToWorkout(onBack) }
            }
            return@LazyColumn
        }
        item(key = "exercise") {
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.s4)) {
                Text(exercise.name, style = MaterialTheme.typography.exerciseDetailsTitle)
                Text(
                    stringResource(R.string.exercise_details_target, exercise.muscleGroup),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.voraColors.textSecondary,
                )
            }
        }
        item(key = "demonstration_image") {
            Image(
                painterResource(R.drawable.exercise_details_demo_area),
                contentDescription = stringResource(R.string.exercise_details_image_description),
                modifier = Modifier.fillMaxWidth().aspectRatio(350f / 160f).clip(MaterialTheme.shapes.medium),
                contentScale = ContentScale.Fit,
            )
        }
        item(key = "steps") { PerformanceSteps(exercise.instructions) }
        item(key = "cues") { FormCues(exercise.tip) }
        item(key = "ask_vora") { AskVora { notice = R.string.exercise_details_coach_notice } }
        item(key = "back") { BackToWorkout(onBack) }
    }
    notice?.let { message ->
        AlertDialog(
            onDismissRequest = { notice = null },
            title = { Text(stringResource(R.string.exercise_details_demo_title)) },
            text = { Text(stringResource(message)) },
            confirmButton = {
                TextButton(onClick = { notice = null }) { Text(stringResource(R.string.exercise_details_ok)) }
            },
        )
    }
}

@Composable
private fun GuideHeader(onBack: () -> Unit, onHelp: () -> Unit) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Icon(
            painterResource(R.drawable.ic_exercise_details_back),
            contentDescription = stringResource(R.string.exercise_details_back),
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp).clickable(role = Role.Button, onClick = onBack),
        )
        Text(
            stringResource(R.string.exercise_details_guide),
            style = MaterialTheme.typography.exerciseDetailsHeader,
            modifier = Modifier.weight(1f),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
        )
        Icon(
            painterResource(R.drawable.ic_exercise_details_help),
            contentDescription = stringResource(R.string.exercise_details_help),
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp).clickable(role = Role.Button, onClick = onHelp),
        )
    }
}

@Composable
private fun PerformanceSteps(instructions: String) {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.s10)) {
        Text(stringResource(R.string.exercise_details_steps), style = MaterialTheme.typography.exerciseDetailsSectionTitle)
        // The API sends the instructions as a single text, not as numbered steps.
        Text(instructions, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.voraColors.textSecondary)
    }
}

@Composable
private fun FormCues(tip: String) {
    Column(
        modifier = Modifier.fillMaxWidth().clip(MaterialTheme.shapes.small)
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outline, MaterialTheme.shapes.small)
            .padding(Spacing.s12),
        verticalArrangement = Arrangement.spacedBy(Spacing.s8),
    ) {
        Text(
            stringResource(R.string.exercise_details_cues),
            style = MaterialTheme.typography.exerciseDetailsCueLabel, color = MaterialTheme.colorScheme.primary,
        )
        Text(
            stringResource(R.string.exercise_details_cue, tip),
            style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.voraColors.textSecondary,
        )
    }
}

@Composable
private fun AskVora(onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clip(MaterialTheme.shapes.small)
            .background(MaterialTheme.voraColors.mintSoft)
            .clickable(role = Role.Button, onClick = onClick).padding(Spacing.s12),
        horizontalArrangement = Arrangement.spacedBy(Spacing.s10),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(painterResource(R.drawable.ic_home_orb), contentDescription = null, modifier = Modifier.size(24.dp))
        Text(
            stringResource(R.string.exercise_details_ask),
            style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.weight(1f),
        )
        Icon(
            painterResource(R.drawable.ic_workout_details_chevron), contentDescription = null,
            tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp),
        )
    }
}

/** Not designed in Figma: shown when the exercise's workout was never downloaded (RF02). */
@Composable
private fun UnavailableMessage() {
    Text(
        stringResource(R.string.exercise_details_unavailable),
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.fillMaxWidth().clip(MaterialTheme.shapes.small)
            .background(MaterialTheme.voraColors.mintSoft)
            .padding(Spacing.s12),
    )
}

@Composable
private fun BackToWorkout(onBack: () -> Unit) {
    MaterialTheme(
        typography = MaterialTheme.typography.copy(labelMedium = MaterialTheme.typography.exerciseDetailsAction),
    ) {
        VoraPrimaryButton(
            stringResource(R.string.exercise_details_back_to_workout),
            onBack,
            modifier = Modifier.fillMaxWidth().heightIn(min = 44.dp),
        )
    }
}

// Sample data shaped like the mock fixture e02.
private val PreviewExercise = Exercise(
    id = "e02", name = "Remo con mancuerna", muscleGroup = "Espalda", sets = 3, reps = 10, restSeconds = 60,
    instructions = "Apoyá una mano y llevá la mancuerna hacia el torso.",
    tip = "Usá una carga que puedas controlar.",
)

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun ExerciseDetailsPreview() {
    ExamenTP3Theme { ExerciseDetailsScreen(ExerciseDetailsUiState(PreviewExercise, isLoading = false), {}) }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 640, fontScale = 1.3f)
@Composable
private fun ExerciseDetailsCompactPreview() {
    ExamenTP3Theme { ExerciseDetailsScreen(ExerciseDetailsUiState(PreviewExercise, isLoading = false), {}) }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun ExerciseDetailsUnavailablePreview() {
    ExamenTP3Theme { ExerciseDetailsScreen(ExerciseDetailsUiState(isLoading = false), {}) }
}
