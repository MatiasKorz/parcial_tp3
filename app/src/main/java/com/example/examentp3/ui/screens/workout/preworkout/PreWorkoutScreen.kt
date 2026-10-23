package com.example.examentp3.ui.screens.workout.preworkout

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import com.example.examentp3.R
import com.example.examentp3.ui.components.VoraButtonSize
import com.example.examentp3.ui.components.VoraPrimaryButton
import com.example.examentp3.ui.theme.*

/** Preparation-only demonstration. Workout identity is handled by the navigation destination. */
@Composable
fun PreWorkoutScreen(
    onClose: () -> Unit,
    onStartWorkout: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var showRecommendationNotice by rememberSaveable { mutableStateOf(false) }
    PreWorkoutSystemBars()
    LazyColumn(
        modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.primary)
            .statusBarsPadding().navigationBarsPadding(),
        contentPadding = PaddingValues(
            start = Spacing.screenMargin, end = Spacing.screenMargin,
            top = Spacing.s12, bottom = Spacing.s24,
        ),
        verticalArrangement = Arrangement.spacedBy(Spacing.s20),
    ) {
        item(key = "header") {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    stringResource(R.string.pre_workout_get_ready),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.weight(1f),
                )
                Icon(
                    painterResource(R.drawable.ic_pre_workout_close),
                    contentDescription = stringResource(R.string.pre_workout_close),
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(24.dp).clickable(role = Role.Button, onClick = onClose),
                )
            }
        }
        item(key = "workout") {
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.s4)) {
                Text(
                    stringResource(R.string.pre_workout_title),
                    style = MaterialTheme.typography.preWorkoutTitle,
                    color = MaterialTheme.colorScheme.onPrimary,
                )
                Text(
                    stringResource(R.string.pre_workout_summary),
                    style = MaterialTheme.typography.preWorkoutSummary,
                    color = MaterialTheme.colorScheme.secondary,
                )
            }
        }
        item(key = "preparation") {
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.s12)) {
                PreparationCard {
                    CardLabel(stringResource(R.string.pre_workout_focus_label))
                    Text(
                        stringResource(R.string.pre_workout_focus_title),
                        style = MaterialTheme.typography.preWorkoutFocusTitle,
                        color = MaterialTheme.colorScheme.onPrimary,
                    )
                    Text(
                        stringResource(R.string.pre_workout_focus_body),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.preWorkoutSecondaryText,
                    )
                }
                PreparationCard {
                    CardLabel(stringResource(R.string.pre_workout_session_label))
                    Text(
                        stringResource(R.string.pre_workout_session_title),
                        style = MaterialTheme.typography.preWorkoutSessionTitle,
                        color = MaterialTheme.colorScheme.onPrimary,
                    )
                    Text(
                        stringResource(R.string.pre_workout_session_sets),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.preWorkoutSecondaryText,
                    )
                }
                PreparationCard {
                    CardLabel(stringResource(R.string.pre_workout_tip_label))
                    Text(
                        stringResource(R.string.pre_workout_tip_body),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.secondary,
                    )
                }
            }
        }
        item(key = "recommendation") {
            Recommendation { showRecommendationNotice = true }
        }
        item(key = "start") {
            // Configure the shared button only in this subtree, leaving BASE-01 unchanged.
            val colors = MaterialTheme.colorScheme
            MaterialTheme(
                colorScheme = colors.copy(primary = colors.secondary, onPrimary = colors.primary),
                typography = MaterialTheme.typography.copy(titleSmall = MaterialTheme.typography.preWorkoutStart),
            ) {
                VoraPrimaryButton(
                    text = stringResource(R.string.pre_workout_start),
                    onClick = onStartWorkout,
                    modifier = Modifier.fillMaxWidth(),
                    size = VoraButtonSize.Large,
                )
            }
        }
    }
    if (showRecommendationNotice) {
        AlertDialog(
            onDismissRequest = { showRecommendationNotice = false },
            title = { Text(stringResource(R.string.pre_workout_demo_title)) },
            text = { Text(stringResource(R.string.pre_workout_demo_notice)) },
            confirmButton = {
                TextButton(onClick = { showRecommendationNotice = false }) {
                    Text(stringResource(R.string.pre_workout_ok))
                }
            },
        )
    }
}

@Composable
private fun PreparationCard(content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.preWorkoutCardBackground)
            .border(1.dp, MaterialTheme.colorScheme.preWorkoutCardBorder, MaterialTheme.shapes.medium)
            .padding(Spacing.s16),
        verticalArrangement = Arrangement.spacedBy(Spacing.s8),
        content = content,
    )
}

@Composable
private fun CardLabel(text: String) {
    Text(text, style = MaterialTheme.typography.preWorkoutCardLabel, color = MaterialTheme.colorScheme.tertiary)
}

@Composable
private fun Recommendation(onRecommend: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.preWorkoutRecommendationBackground)
            .padding(Spacing.s16),
        horizontalArrangement = Arrangement.spacedBy(Spacing.s12),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(painterResource(R.drawable.ic_home_orb), contentDescription = null, modifier = Modifier.size(32.dp))
        Column(
            Modifier.weight(1f).clickable(role = Role.Button, onClick = onRecommend),
            verticalArrangement = Arrangement.spacedBy(Spacing.s2),
        ) {
            Text(
                stringResource(R.string.pre_workout_need_anything),
                style = MaterialTheme.typography.preWorkoutRecommendationTitle,
                color = MaterialTheme.colorScheme.secondary,
            )
            Text(
                stringResource(R.string.pre_workout_recommendation),
                style = MaterialTheme.typography.preWorkoutRecommendationBody,
                color = MaterialTheme.colorScheme.preWorkoutSecondaryText,
            )
        }
        Box(
            modifier = Modifier.size(40.dp).clip(VoraPillShape)
                .background(MaterialTheme.colorScheme.tertiary)
                .clickable(role = Role.Button, onClick = onRecommend),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painterResource(R.drawable.ic_pre_workout_mic),
                contentDescription = stringResource(R.string.pre_workout_microphone),
                tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp),
            )
        }
    }
}

/** Use light system icons on this dark destination, restoring the previous settings on exit. */
@Composable
private fun PreWorkoutSystemBars() {
    val view = LocalView.current
    DisposableEffect(view) {
        val activity = generateSequence(view.context) { (it as? ContextWrapper)?.baseContext }
            .filterIsInstance<Activity>().firstOrNull()
        val controller = if (!view.isInEditMode && activity != null)
            WindowCompat.getInsetsController(activity.window, view) else null
        val lightStatus = controller?.isAppearanceLightStatusBars
        val lightNavigation = controller?.isAppearanceLightNavigationBars
        controller?.isAppearanceLightStatusBars = false
        controller?.isAppearanceLightNavigationBars = false
        onDispose {
            if (lightStatus != null) controller?.isAppearanceLightStatusBars = lightStatus
            if (lightNavigation != null) controller?.isAppearanceLightNavigationBars = lightNavigation
        }
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun PreWorkoutPreview() {
    ExamenTP3Theme { PreWorkoutScreen({}, {}) }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 640, fontScale = 1.3f)
@Composable
private fun PreWorkoutCompactPreview() {
    ExamenTP3Theme { PreWorkoutScreen({}, {}) }
}
