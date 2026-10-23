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
import com.example.examentp3.ui.components.VoraButtonSize
import com.example.examentp3.ui.components.VoraPrimaryButton
import com.example.examentp3.ui.theme.*

@Composable
fun ActiveWorkoutScreen(onFinishWorkout: () -> Unit, modifier: Modifier = Modifier) {
    var demo by remember { mutableStateOf(ActiveWorkoutDemoState()) }
    var notice by remember { mutableStateOf<Int?>(null) }
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
        item(key = "header") { WorkoutHeader(onFinishWorkout) }
        item(key = "volume") { VolumePanel() }
        item(key = "weight") {
            WeightPanel(demo, { demo = demo.decreaseWeight() }, { demo = demo.increaseWeight() })
        }
        item(key = "sets") { SetIndicators(demo) }
        item(key = "rest") {
            RestPanel(
                demo,
                onAddRest = {
                    if (demo.allSetsComplete) notice = R.string.active_workout_sets_complete
                    else demo = demo.addRest()
                },
                onSkip = { demo = demo.skipRest() },
            )
        }
        item(key = "coach") { CoachPanel { notice = R.string.active_workout_voice_notice } }
        item(key = "complete") {
            val colors = MaterialTheme.colorScheme
            MaterialTheme(
                colorScheme = colors.copy(primary = colors.tertiary, onPrimary = colors.primary),
                typography = MaterialTheme.typography.copy(titleSmall = MaterialTheme.typography.activeWorkoutCta),
            ) {
                VoraPrimaryButton(
                    stringResource(R.string.active_workout_complete_set),
                    onClick = {
                        demo = demo.completeSet()
                        if (demo.allSetsComplete) notice = R.string.active_workout_sets_complete
                    },
                    modifier = Modifier.fillMaxWidth(), size = VoraButtonSize.Large,
                )
            }
        }
    }
    notice?.let { message ->
        AlertDialog(
            onDismissRequest = { notice = null },
            title = { Text(stringResource(R.string.active_workout_demo_title)) },
            text = { Text(stringResource(message)) },
            confirmButton = {
                TextButton(onClick = { notice = null }) { Text(stringResource(R.string.active_workout_ok)) }
            },
        )
    }
}

@Composable
private fun WorkoutHeader(onFinish: () -> Unit) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Spacing.s12)) {
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(Spacing.s2)) {
            Text(stringResource(R.string.active_workout_exercise_position), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.secondary)
            Text(stringResource(R.string.active_workout_exercise), style = MaterialTheme.typography.activeWorkoutExercise, color = MaterialTheme.colorScheme.onPrimary)
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
private fun VolumePanel() {
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
            VolumeValue(R.string.active_workout_reps_value, R.string.active_workout_reps)
            VerticalDivider(Modifier.height(32.dp), color = MaterialTheme.colorScheme.preWorkoutCardBorder)
            VolumeValue(R.string.active_workout_matrix_value, R.string.active_workout_matrix)
        }
    }
}

@Composable
private fun VolumeValue(value: Int, label: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(Spacing.s4)) {
        Text(stringResource(value), style = MaterialTheme.typography.activeWorkoutValue, color = MaterialTheme.colorScheme.onPrimary)
        Text(stringResource(label), style = MaterialTheme.typography.activeWorkoutCaption, color = MaterialTheme.colorScheme.preWorkoutSecondaryText)
    }
}

@Composable
private fun WeightPanel(demo: ActiveWorkoutDemoState, onDecrease: () -> Unit, onIncrease: () -> Unit) {
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
            val value = if (demo.weightSteps % 2 == 0) demo.weightKg.toInt().toString() else demo.weightKg.toString()
            Text(stringResource(R.string.active_workout_weight, value), style = MaterialTheme.typography.activeWorkoutWeight, color = MaterialTheme.colorScheme.onPrimary)
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
private fun SetIndicators(demo: ActiveWorkoutDemoState) {
    FlowRow(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalArrangement = Arrangement.spacedBy(Spacing.s8),
    ) {
        for (number in 1..3) {
            val active = !demo.allSetsComplete && demo.currentSet == number
            val complete = number <= demo.completedSets
            val status = stringResource(when {
                complete -> R.string.active_workout_status_complete
                active -> R.string.active_workout_status_current
                else -> R.string.active_workout_status_pending
            })
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
                Text(stringResource(R.string.active_workout_set, number), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onPrimary)
            }
        }
    }
}

@Composable
private fun RestPanel(demo: ActiveWorkoutDemoState, onAddRest: () -> Unit, onSkip: () -> Unit) {
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
                if (demo.restSeconds == 0) stringResource(R.string.active_workout_rest_finished)
                else stringResource(R.string.active_workout_rest_time, demo.restSeconds / 60, demo.restSeconds % 60),
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

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun ActiveWorkoutPreview() {
    ExamenTP3Theme { ActiveWorkoutScreen({}) }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 640, fontScale = 1.3f)
@Composable
private fun ActiveWorkoutCompactPreview() {
    ExamenTP3Theme { ActiveWorkoutScreen({}) }
}
