package com.example.examentp3.ui.screens.workout.active

import android.app.Activity
import android.content.ContextWrapper
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
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import com.example.examentp3.R
import com.example.examentp3.model.Exercise
import com.example.examentp3.model.WorkoutDetail
import com.example.examentp3.ui.components.VoraButtonSize
import com.example.examentp3.ui.components.VoraEmptyState
import com.example.examentp3.ui.components.VoraErrorState
import com.example.examentp3.ui.components.VoraLoadingState
import com.example.examentp3.ui.components.VoraPrimaryButton
import com.example.examentp3.ui.theme.*
import com.example.examentp3.viewmodel.ActiveWorkoutUiState
import com.example.examentp3.viewmodel.LoggedSet
import com.example.examentp3.viewmodel.SetProgress
import com.example.examentp3.viewmodel.UiError

/**
 * Workout / 05 — Active Workout with real set tracking (EXT-06): sets, reps and rest come from the
 * chosen workout. "LAST SESSION" and the coach advice stay as drawn (simulated, see README).
 */
@Composable
fun ActiveWorkoutScreen(
    uiState: ActiveWorkoutUiState,
    onFinishWorkout: () -> Unit,
    onCompleteSet: () -> Unit,
    onDecreaseWeight: () -> Unit,
    onIncreaseWeight: () -> Unit,
    onAddRest: () -> Unit,
    onSkipRest: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // Ephemeral UI state: which notice is open.
    var showVoiceNotice by rememberSaveable { mutableStateOf(false) }
    var showCompleteNotice by rememberSaveable { mutableStateOf(false) }
    ActiveWorkoutSystemBars()
    LazyColumn(
        modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.primary)
            .statusBarsPadding().navigationBarsPadding(),
        contentPadding = PaddingValues(
            start = Spacing.screenMargin, end = Spacing.screenMargin,
            top = Spacing.s12, bottom = Spacing.s24,
        ),
        verticalArrangement = Arrangement.spacedBy(Spacing.s16),
    ) {
        val exercise = uiState.exercise
        if (exercise == null) {
            // Without the workout there are no sets to track; system back leaves the screen.
            when {
                uiState.isLoading -> item(key = "loading") {
                    VoraLoadingState(stringResource(R.string.active_workout_loading))
                }
                uiState.error == UiError.NotFound -> item(key = "not_found") {
                    VoraEmptyState(
                        stringResource(R.string.active_workout_not_found_title),
                        stringResource(uiState.error.message),
                    )
                }
                uiState.error != null -> item(key = "error") {
                    VoraErrorState(
                        stringResource(R.string.active_workout_error_title),
                        stringResource(uiState.error.message),
                        stringResource(R.string.state_retry),
                        onRetry,
                    )
                }
                else -> item(key = "finish") { WorkoutHeader(null, onFinishWorkout) }
            }
            return@LazyColumn
        }
        item(key = "header") { WorkoutHeader(uiState, onFinishWorkout) }
        item(key = "volume") { VolumePanel(exercise.sets, exercise.reps) }
        item(key = "weight") { WeightPanel(uiState.progress.weightKg, onDecreaseWeight, onIncreaseWeight) }
        item(key = "sets") { SetIndicators(uiState) }
        if (uiState.isResting) {
            item(key = "rest") { RestPanel(uiState.progress.restSeconds, onAddRest, onSkipRest) }
        }
        item(key = "coach") { CoachPanel { showVoiceNotice = true } }
        item(key = "complete") {
            val colors = MaterialTheme.colorScheme
            MaterialTheme(
                colorScheme = colors.copy(primary = colors.tertiary, onPrimary = colors.primary),
                typography = MaterialTheme.typography.copy(titleSmall = MaterialTheme.typography.activeWorkoutCta),
            ) {
                VoraPrimaryButton(
                    stringResource(R.string.active_workout_complete_set),
                    onClick = {
                        if (uiState.allSetsComplete) showCompleteNotice = true else onCompleteSet()
                    },
                    modifier = Modifier.fillMaxWidth(), size = VoraButtonSize.Large,
                )
            }
        }
    }
    if (showVoiceNotice) {
        ActiveWorkoutNotice(R.string.active_workout_demo_title, R.string.active_workout_voice_notice) {
            showVoiceNotice = false
        }
    }
    if (showCompleteNotice) {
        ActiveWorkoutNotice(R.string.active_workout_sets_complete_title, R.string.active_workout_sets_complete) {
            showCompleteNotice = false
        }
    }
}

@Composable
private fun ActiveWorkoutNotice(title: Int, message: Int, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(title)) },
        text = { Text(stringResource(message)) },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.active_workout_ok)) }
        },
    )
}

/** [uiState] is null when the workout has no exercises: only "Finish Workout" is left. */
@Composable
private fun WorkoutHeader(uiState: ActiveWorkoutUiState?, onFinish: () -> Unit) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Spacing.s12)) {
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(Spacing.s2)) {
            val exercise = uiState?.exercise
            if (exercise != null) {
                Text(
                    stringResource(R.string.active_workout_exercise_position, uiState.progress.exerciseIndex + 1, uiState.exerciseCount),
                    style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.secondary,
                )
                Text(exercise.name, style = MaterialTheme.typography.activeWorkoutExercise, color = MaterialTheme.colorScheme.onPrimary)
            }
        }
        Text(
            stringResource(R.string.active_workout_finish),
            style = MaterialTheme.typography.activeWorkoutSmall, color = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.clip(VoraPillShape).background(MaterialTheme.colorScheme.activeWorkoutSelectedSet)
                .clickable(role = Role.Button, onClick = onFinish).padding(horizontal = Spacing.s12, vertical = Spacing.s6),
        )
    }
}

@Composable
private fun DarkPanel(content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.activeWorkoutPanel)
            .border(1.dp, MaterialTheme.colorScheme.preWorkoutCardBorder, MaterialTheme.shapes.medium)
            .padding(Spacing.s16),
        verticalArrangement = Arrangement.spacedBy(Spacing.s12),
        content = content,
    )
}

@Composable
private fun VolumePanel(sets: Int, reps: Int) {
    DarkPanel {
        Text(
            stringResource(R.string.active_workout_volume), style = MaterialTheme.typography.activeWorkoutSmall,
            color = MaterialTheme.colorScheme.secondary, modifier = Modifier.align(Alignment.CenterHorizontally),
        )
        Row(
            Modifier.align(Alignment.CenterHorizontally),
            horizontalArrangement = Arrangement.spacedBy(Spacing.s24),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            VolumeValue(stringResource(R.string.active_workout_number, reps), R.string.active_workout_reps)
            VerticalDivider(Modifier.height(32.dp), color = MaterialTheme.colorScheme.preWorkoutCardBorder)
            VolumeValue(stringResource(R.string.active_workout_matrix_value, sets, reps), R.string.active_workout_matrix)
        }
    }
}

@Composable
private fun VolumeValue(value: String, label: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(Spacing.s4)) {
        Text(value, style = MaterialTheme.typography.activeWorkoutValue, color = MaterialTheme.colorScheme.onPrimary)
        Text(stringResource(label), style = MaterialTheme.typography.activeWorkoutCaption, color = MaterialTheme.colorScheme.preWorkoutSecondaryText)
    }
}

/** 45.0 shows as "45", 47.5 as "47.5". */
private fun formatWeight(kg: Double): String = if (kg % 1.0 == 0.0) kg.toInt().toString() else kg.toString()

@Composable
private fun WeightPanel(weightKg: Double, onDecrease: () -> Unit, onIncrease: () -> Unit) {
    FlowRow(
        modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.activeWorkoutPanel, MaterialTheme.shapes.medium).padding(Spacing.s12),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalArrangement = Arrangement.spacedBy(Spacing.s8),
        itemVerticalAlignment = Alignment.CenterVertically,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.s2)) {
            Text(stringResource(R.string.active_workout_last_session), style = MaterialTheme.typography.activeWorkoutCaption, color = MaterialTheme.colorScheme.preWorkoutSecondaryText)
            Text(stringResource(R.string.active_workout_last_volume), style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onPrimary)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(Spacing.s8), verticalAlignment = Alignment.CenterVertically) {
            WeightAction(R.string.active_workout_minus, R.string.active_workout_decrease, onDecrease)
            Text(stringResource(R.string.active_workout_weight, formatWeight(weightKg)), style = MaterialTheme.typography.activeWorkoutWeight, color = MaterialTheme.colorScheme.onPrimary)
            WeightAction(R.string.active_workout_plus, R.string.active_workout_increase, onIncrease)
        }
    }
}

@Composable
private fun WeightAction(symbol: Int, description: Int, onClick: () -> Unit) {
    val label = stringResource(description)
    Box(
        Modifier.size(36.dp).clip(VoraPillShape).background(MaterialTheme.colorScheme.preWorkoutRecommendationBackground)
            .semantics { contentDescription = label }.clickable(role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(stringResource(symbol), style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onPrimary)
    }
}

@Composable
private fun SetIndicators(uiState: ActiveWorkoutUiState) {
    FlowRow(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Spacing.s8),
        verticalArrangement = Arrangement.spacedBy(Spacing.s8),
    ) {
        for (number in 1..uiState.setCount) {
            val active = uiState.currentSet == number
            // The load chosen for the set stays with it once it is logged.
            val logged = uiState.loggedSets.getOrNull(number - 1)
            val complete = logged != null
            val status = when {
                logged != null -> stringResource(R.string.active_workout_status_logged, formatWeight(logged.weightKg))
                active -> stringResource(R.string.active_workout_status_current)
                else -> stringResource(R.string.active_workout_status_pending)
            }
            val label = stringResource(R.string.active_workout_set_description, number, status)
            Row(
                Modifier.widthIn(min = 110.dp).clip(MaterialTheme.shapes.small)
                    .background(if (active) MaterialTheme.colorScheme.activeWorkoutSelectedSet else MaterialTheme.colorScheme.activeWorkoutInactiveSet)
                    .border(1.dp, if (active) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.preWorkoutCardBorder, MaterialTheme.shapes.small)
                    .semantics(mergeDescendants = true) { contentDescription = label }
                    .padding(Spacing.s12),
                horizontalArrangement = Arrangement.spacedBy(Spacing.s8),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                val badge = when {
                    complete -> MaterialTheme.colorScheme.secondary
                    active -> MaterialTheme.colorScheme.tertiary
                    else -> MaterialTheme.colorScheme.preWorkoutRecommendationBackground
                }
                Box(Modifier.size(20.dp).background(badge, VoraPillShape), contentAlignment = Alignment.Center) {
                    Text(stringResource(R.string.active_workout_number, number), style = MaterialTheme.typography.activeWorkoutBadge, color = MaterialTheme.colorScheme.primary)
                }
                Column {
                    Text(stringResource(R.string.active_workout_set, number), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onPrimary)
                    if (logged != null) {
                        Text(
                            stringResource(R.string.active_workout_weight, formatWeight(logged.weightKg)),
                            style = MaterialTheme.typography.activeWorkoutCaption,
                            color = MaterialTheme.colorScheme.preWorkoutSecondaryText,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RestPanel(restSeconds: Int, onAddRest: () -> Unit, onSkip: () -> Unit) {
    FlowRow(
        modifier = Modifier.fillMaxWidth().clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.voraColors.mintSoft)
            .border(1.dp, MaterialTheme.colorScheme.activeWorkoutRestBorder, MaterialTheme.shapes.medium).padding(Spacing.s12),
        horizontalArrangement = Arrangement.SpaceBetween, verticalArrangement = Arrangement.spacedBy(Spacing.s8),
        itemVerticalAlignment = Alignment.CenterVertically,
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(Spacing.s8), verticalAlignment = Alignment.CenterVertically) {
            Icon(painterResource(R.drawable.ic_active_workout_clock), contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(16.dp))
            Text(
                stringResource(R.string.active_workout_rest_time, restSeconds / 60, restSeconds % 60),
                style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.secondary,
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(Spacing.s8)) {
            RestAction(R.string.active_workout_add_rest, false, onAddRest)
            RestAction(R.string.active_workout_skip, true, onSkip)
        }
    }
}

@Composable
private fun RestAction(label: Int, filled: Boolean, onClick: () -> Unit) {
    Text(
        stringResource(label), style = MaterialTheme.typography.activeWorkoutRestAction,
        color = if (filled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
        modifier = Modifier.clip(VoraPillShape)
            .background(if (filled) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary)
            .then(if (!filled) Modifier.border(1.dp, MaterialTheme.colorScheme.secondary, VoraPillShape) else Modifier)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = Spacing.s10, vertical = Spacing.s4),
    )
}

@Composable
private fun CoachPanel(onVoice: () -> Unit) {
    DarkPanel {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Row(Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(Spacing.s6), verticalAlignment = Alignment.CenterVertically) {
                Image(painterResource(R.drawable.ic_home_orb), contentDescription = null, modifier = Modifier.size(18.dp))
                Text(stringResource(R.string.active_workout_coach), style = MaterialTheme.typography.activeWorkoutBadge, color = MaterialTheme.colorScheme.secondary)
            }
            Text(
                stringResource(R.string.active_workout_voice), style = MaterialTheme.typography.activeWorkoutCaption,
                color = MaterialTheme.colorScheme.preWorkoutSecondaryText,
                modifier = Modifier.clickable(role = Role.Button, onClick = onVoice),
            )
        }
        Text(stringResource(R.string.active_workout_advice), style = MaterialTheme.typography.activeWorkoutCoach, color = MaterialTheme.colorScheme.onPrimary)
    }
}

@Composable
private fun ActiveWorkoutSystemBars() {
    val view = LocalView.current
    DisposableEffect(view) {
        val activity = generateSequence(view.context) { (it as? ContextWrapper)?.baseContext }.filterIsInstance<Activity>().firstOrNull()
        val controller = if (!view.isInEditMode && activity != null) WindowCompat.getInsetsController(activity.window, view) else null
        val status = controller?.isAppearanceLightStatusBars
        val navigation = controller?.isAppearanceLightNavigationBars
        controller?.isAppearanceLightStatusBars = false
        controller?.isAppearanceLightNavigationBars = false
        onDispose {
            if (status != null) controller?.isAppearanceLightStatusBars = status
            if (navigation != null) controller?.isAppearanceLightNavigationBars = navigation
        }
    }
}

private val PreviewExercise = Exercise(
    id = "e2", name = "Seated Cable Row", muscleGroup = "back", sets = 3, reps = 12,
    restSeconds = 78, instructions = "", tip = "",
)

// Figma state: second of five exercises, first set logged at 45 kg and resting 01:18.
private val PreviewState = ActiveWorkoutUiState(
    workout = WorkoutDetail(
        id = "w02", name = "Upper Body", level = "intermediate", durationMinutes = 35, description = "",
        exercises = List(5) { PreviewExercise.copy(id = "e$it") },
    ),
    progress = SetProgress(
        exerciseIndex = 1,
        loggedSets = listOf(List(3) { LoggedSet(45.0, 12) }, listOf(LoggedSet(45.0, 12))),
        restSeconds = 78,
    ),
)

@Composable
private fun ActiveWorkoutPreviewContent(uiState: ActiveWorkoutUiState) {
    ExamenTP3Theme { ActiveWorkoutScreen(uiState, {}, {}, {}, {}, {}, {}, {}) }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun ActiveWorkoutPreview() = ActiveWorkoutPreviewContent(PreviewState)

@Preview(showBackground = true, widthDp = 360, heightDp = 640, fontScale = 1.3f)
@Composable
private fun ActiveWorkoutCompactPreview() = ActiveWorkoutPreviewContent(PreviewState)

@Preview(showBackground = true, widthDp = 390, heightDp = 640)
@Composable
private fun ActiveWorkoutErrorPreview() =
    ActiveWorkoutPreviewContent(ActiveWorkoutUiState(error = UiError.NoConnection))
