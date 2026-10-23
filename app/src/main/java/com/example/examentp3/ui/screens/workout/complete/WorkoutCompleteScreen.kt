package com.example.examentp3.ui.screens.workout.complete

import android.app.Activity
import android.content.ContextWrapper
import androidx.activity.compose.BackHandler
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.examentp3.R
import com.example.examentp3.ui.components.VoraCard
import com.example.examentp3.ui.components.VoraPrimaryButton
import com.example.examentp3.ui.theme.*

private enum class Difficulty(@param:StringRes val label: Int) {
    Great(R.string.workout_complete_great),
    Good(R.string.workout_complete_good),
    Tough(R.string.workout_complete_tough),
    TooEasy(R.string.workout_complete_too_easy),
}

/** Fixed Figma summary and local rating; no persisted metrics or feedback. */
@Composable
fun WorkoutCompleteScreen(onDone: () -> Unit, modifier: Modifier = Modifier) {
    var difficulty by remember { mutableStateOf(Difficulty.Good) }
    BackHandler(onBack = onDone)
    CompleteSystemBars()
    LazyColumn(
        modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
            .statusBarsPadding().navigationBarsPadding(),
        contentPadding = PaddingValues(
            start = Spacing.s24, end = Spacing.s24, top = Spacing.s20, bottom = Spacing.s24,
        ),
        verticalArrangement = Arrangement.spacedBy(Spacing.s20),
    ) {
        item(key = "completed") {
            Text(
                stringResource(R.string.workout_complete_completed),
                style = MaterialTheme.typography.workoutCompleteLabel,
                color = MaterialTheme.colorScheme.secondary,
                textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth(),
            )
        }
        item(key = "victory") {
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Image(
                    painterResource(R.drawable.workout_complete_victory_orb),
                    contentDescription = null, modifier = Modifier.size(180.dp),
                )
            }
        }
        item(key = "message") {
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.s6)) {
                Text(
                    stringResource(R.string.workout_complete_title),
                    style = MaterialTheme.typography.workoutCompleteTitle,
                    textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth(),
                )
                Text(
                    stringResource(R.string.workout_complete_message),
                    style = MaterialTheme.typography.workoutCompleteMessage,
                    color = MaterialTheme.voraColors.textSecondary,
                    textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth(),
                )
            }
        }
        item(key = "metrics") {
            VoraCard {
                FlowRow(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalArrangement = Arrangement.spacedBy(Spacing.s12),
                ) {
                    Metric(R.string.workout_complete_duration_value, R.string.workout_complete_duration)
                    Metric(R.string.workout_complete_exercises_value, R.string.workout_complete_exercises)
                    Metric(R.string.workout_complete_volume_value, R.string.workout_complete_volume)
                }
            }
        }
        item(key = "insight") { Insight() }
        item(key = "rating") {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(Spacing.s10)) {
                Text(
                    stringResource(R.string.workout_complete_rate),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.voraColors.textSecondary,
                    textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth(),
                )
                FlowRow(
                    Modifier.fillMaxWidth().selectableGroup(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.s8, Alignment.CenterHorizontally),
                    verticalArrangement = Arrangement.spacedBy(Spacing.s8),
                ) {
                    Difficulty.entries.forEach { option ->
                        DifficultyOption(option, difficulty == option) { difficulty = option }
                    }
                }
            }
        }
        item(key = "done") {
            MaterialTheme(typography = MaterialTheme.typography.copy(labelMedium = MaterialTheme.typography.workoutCompleteAction)) {
                VoraPrimaryButton(
                    stringResource(R.string.workout_complete_done), onDone,
                    modifier = Modifier.fillMaxWidth().heightIn(min = 44.dp),
                )
            }
        }
    }
}

@Composable
private fun Metric(@StringRes value: Int, @StringRes label: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(Spacing.s4)) {
        Text(stringResource(value), style = MaterialTheme.typography.workoutCompleteMetric)
        Text(stringResource(label), style = MaterialTheme.typography.workoutCompleteMetricLabel, color = MaterialTheme.voraColors.textMuted)
    }
}

@Composable
private fun Insight() {
    Column(
        Modifier.fillMaxWidth().background(MaterialTheme.voraColors.mintSoft, MaterialTheme.shapes.medium).padding(Spacing.s16),
        verticalArrangement = Arrangement.spacedBy(Spacing.s8),
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(Spacing.s6), verticalAlignment = Alignment.CenterVertically) {
            Image(painterResource(R.drawable.ic_home_orb), contentDescription = null, modifier = Modifier.size(16.dp))
            Text(
                stringResource(R.string.workout_complete_insight),
                style = MaterialTheme.typography.workoutCompleteInsightLabel, color = MaterialTheme.colorScheme.primary,
            )
        }
        Text(
            stringResource(R.string.workout_complete_insight_body),
            style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary,
        )
    }
}

@Composable
private fun DifficultyOption(option: Difficulty, selected: Boolean, onSelect: () -> Unit) {
    Text(
        stringResource(option.label),
        style = MaterialTheme.typography.workoutCompleteRating,
        color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.clip(VoraPillShape)
            .background(if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outline, VoraPillShape)
            .selectable(selected = selected, role = Role.RadioButton, onClick = onSelect)
            .padding(horizontal = Spacing.s14, vertical = Spacing.s8),
    )
}

// Apply after navigation transitions, when the outgoing dark destination has disposed.
@Composable
private fun CompleteSystemBars() {
    val view = LocalView.current
    val owner = LocalLifecycleOwner.current
    DisposableEffect(owner, view) {
        val activity = generateSequence(view.context) { (it as? ContextWrapper)?.baseContext }
            .filterIsInstance<Activity>().firstOrNull()
        val controller = if (!view.isInEditMode && activity != null)
            WindowCompat.getInsetsController(activity.window, view) else null
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                controller?.isAppearanceLightStatusBars = true
                controller?.isAppearanceLightNavigationBars = true
            }
        }
        owner.lifecycle.addObserver(observer)
        // Both exits lead to the light Dashboard, so keep its dark system icons.
        onDispose { owner.lifecycle.removeObserver(observer) }
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun WorkoutCompletePreview() {
    ExamenTP3Theme { WorkoutCompleteScreen({}) }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 640, fontScale = 1.3f)
@Composable
private fun WorkoutCompleteCompactPreview() {
    ExamenTP3Theme { WorkoutCompleteScreen({}) }
}
