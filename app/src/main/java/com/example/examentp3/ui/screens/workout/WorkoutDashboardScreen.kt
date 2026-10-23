package com.example.examentp3.ui.screens.workout

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.examentp3.R
import com.example.examentp3.ui.components.VoraCard
import com.example.examentp3.ui.components.VoraLimeButton
import com.example.examentp3.ui.components.VoraOutlinedButton
import com.example.examentp3.ui.components.VoraPanel
import com.example.examentp3.ui.components.VoraTextAction
import com.example.examentp3.ui.theme.ExamenTP3Theme
import com.example.examentp3.ui.theme.Spacing
import com.example.examentp3.ui.theme.VoraPillShape
import com.example.examentp3.ui.theme.voraColors
import com.example.examentp3.ui.theme.workoutCoachingBorder
import com.example.examentp3.ui.theme.workoutModerateBackground
import com.example.examentp3.ui.theme.workoutModerateLabel
import com.example.examentp3.ui.theme.workoutPanelSecondary

/** Demonstration dashboard. The real workout catalog belongs to Weekly Plan (NUC-01). */
@Composable
fun WorkoutDashboardScreen(
    onCalendarClick: () -> Unit,
    onStartWorkout: () -> Unit,
    onWorkoutDetails: () -> Unit,
    onViewProgress: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(
                start = Spacing.screenMargin,
                end = Spacing.screenMargin,
                top = Spacing.s12,
                bottom = Spacing.s24,
            ),
        verticalArrangement = Arrangement.spacedBy(Spacing.s20),
    ) {
        WorkoutHeader(onCalendarClick)
        TodayWorkoutCard(onStartWorkout, onWorkoutDetails)
        WorkoutRecommendation()
        WeeklyActivity()
        ProgressOverview(onViewProgress)
    }
}

@Composable
private fun WorkoutHeader(onCalendarClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().heightIn(min = 53.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.s2)) {
            Text(
                stringResource(R.string.workout_dashboard_goal),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.voraColors.textSecondary,
            )
            Text(
                stringResource(R.string.workout_dashboard_title),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
        IconButton(
            onClick = onCalendarClick,
            modifier = Modifier.size(40.dp)
                .background(MaterialTheme.colorScheme.surface, CircleShape)
                .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape),
        ) {
            Icon(
                painterResource(R.drawable.ic_home_calendar),
                contentDescription = stringResource(R.string.workout_dashboard_calendar),
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}

@Composable
private fun TodayWorkoutCard(onStartWorkout: () -> Unit, onWorkoutDetails: () -> Unit) {
    VoraPanel {
        Row(
            Modifier.fillMaxWidth().heightIn(min = 22.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                stringResource(R.string.workout_dashboard_ready),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.weight(1f),
            )
            Text(
                stringResource(R.string.workout_dashboard_moderate),
                style = MaterialTheme.typography.workoutModerateLabel,
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.workoutModerateBackground, VoraPillShape)
                    .padding(horizontal = Spacing.s10, vertical = Spacing.s4),
            )
        }
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.s4)) {
            Text(
                stringResource(R.string.workout_dashboard_workout),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onPrimary,
            )
            Text(
                stringResource(R.string.workout_dashboard_workout_description),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.workoutPanelSecondary,
            )
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(Spacing.s12)) {
            VoraLimeButton(
                stringResource(R.string.workout_dashboard_start),
                onStartWorkout,
                modifier = Modifier.weight(219f).heightIn(min = 43.dp),
            )
            VoraOutlinedButton(
                stringResource(R.string.workout_dashboard_details),
                onWorkoutDetails,
                modifier = Modifier.weight(79f).heightIn(min = 43.dp),
            )
        }
    }
}

@Composable
private fun WorkoutRecommendation() {
    val colors = MaterialTheme.colorScheme
    // Reuse BASE-02's card with local colors; other cards and screens are unaffected.
    MaterialTheme(colorScheme = colors.copy(
        surface = MaterialTheme.voraColors.mintSoft,
        outline = colors.workoutCoachingBorder,
    )) {
        VoraCard {
            Row(
                horizontalArrangement = Arrangement.spacedBy(Spacing.s8),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    painterResource(R.drawable.ic_home_orb),
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier.size(20.dp),
                )
                Text(
                    stringResource(R.string.workout_dashboard_recommends),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
            Text(
                stringResource(R.string.workout_dashboard_recommendation),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

private enum class ActivityStatus(@get:StringRes val description: Int) {
    Complete(R.string.workout_dashboard_completed),
    Rest(R.string.workout_dashboard_rest_day),
    Planned(R.string.workout_dashboard_planned),
}

private data class ActivityDay(
    @param:StringRes val label: Int,
    @param:StringRes val name: Int,
    val status: ActivityStatus,
)

// The order and statuses are the seven static entries from frame 1:213.
private val demoWeek = listOf(
    ActivityDay(R.string.workout_dashboard_monday_short, R.string.workout_dashboard_monday, ActivityStatus.Complete),
    ActivityDay(R.string.workout_dashboard_tuesday_short, R.string.workout_dashboard_tuesday, ActivityStatus.Complete),
    ActivityDay(R.string.workout_dashboard_wednesday_short, R.string.workout_dashboard_wednesday, ActivityStatus.Rest),
    ActivityDay(R.string.workout_dashboard_thursday_short, R.string.workout_dashboard_thursday, ActivityStatus.Complete),
    ActivityDay(R.string.workout_dashboard_friday_short, R.string.workout_dashboard_friday, ActivityStatus.Planned),
    ActivityDay(R.string.workout_dashboard_saturday_short, R.string.workout_dashboard_saturday, ActivityStatus.Rest),
    ActivityDay(R.string.workout_dashboard_sunday_short, R.string.workout_dashboard_sunday, ActivityStatus.Rest),
)

@Composable
private fun WeeklyActivity() {
    VoraCard {
        Text(stringResource(R.string.workout_dashboard_weekly_activity), style = MaterialTheme.typography.titleSmall)
        BoxWithConstraints(Modifier.fillMaxWidth()) {
            // 36 dp matches the frame; narrower windows share the available width.
            val dayWidth = minOf(36.dp, maxWidth / demoWeek.size)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                demoWeek.forEach { day -> ActivityDayIndicator(day, Modifier.width(dayWidth)) }
            }
        }
    }
}

@Composable
private fun ActivityDayIndicator(day: ActivityDay, modifier: Modifier = Modifier) {
    val description = stringResource(
        R.string.workout_dashboard_day_description,
        stringResource(day.name),
        stringResource(day.status.description),
    )
    Column(
        modifier = modifier.clearAndSetSemantics { contentDescription = description },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.s6),
    ) {
        Text(
            stringResource(day.label),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.voraColors.textSecondary,
        )
        val background = when (day.status) {
            ActivityStatus.Complete -> MaterialTheme.colorScheme.secondary
            ActivityStatus.Rest -> MaterialTheme.colorScheme.background
            ActivityStatus.Planned -> MaterialTheme.colorScheme.primary
        }
        Box(
            modifier = Modifier.size(28.dp)
                .background(background, CircleShape)
                .then(if (day.status == ActivityStatus.Rest)
                    Modifier.border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
                else Modifier),
            contentAlignment = Alignment.Center,
        ) {
            when (day.status) {
                ActivityStatus.Complete -> Icon(
                    painterResource(R.drawable.ic_workout_dashboard_check),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(12.dp),
                )
                ActivityStatus.Rest -> Text(
                    stringResource(R.string.workout_dashboard_rest),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.voraColors.textMuted,
                )
                ActivityStatus.Planned -> Text(
                    stringResource(R.string.workout_dashboard_fit),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.tertiary,
                )
            }
        }
    }
}

@Composable
private fun ProgressOverview(onViewProgress: () -> Unit) {
    VoraCard {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(
                stringResource(R.string.workout_dashboard_progress),
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.weight(1f),
            )
            VoraTextAction(stringResource(R.string.workout_dashboard_view_progress), onViewProgress)
        }
        Row(Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
            ProgressMetric(
                R.string.workout_dashboard_streak, R.string.workout_dashboard_streak_value,
                Modifier.weight(1f),
            )
            MetricDivider()
            ProgressMetric(
                R.string.workout_dashboard_workouts, R.string.workout_dashboard_workouts_value,
                Modifier.weight(1f),
            )
            MetricDivider()
            ProgressMetric(
                R.string.workout_dashboard_volume, R.string.workout_dashboard_volume_value,
                Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun ProgressMetric(@StringRes label: Int, @StringRes value: Int, modifier: Modifier = Modifier) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(Spacing.s4)) {
        Text(
            stringResource(label),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.voraColors.textMuted,
        )
        Text(stringResource(value), style = MaterialTheme.typography.titleSmall)
    }
}

@Composable
private fun MetricDivider() {
    Spacer(Modifier.width(Spacing.s12))
    VerticalDivider(Modifier.fillMaxHeight(), color = MaterialTheme.colorScheme.outline)
    Spacer(Modifier.width(Spacing.s12))
}

@Preview(showBackground = true, widthDp = 390, heightDp = 851)
@Composable
private fun WorkoutDashboardPreview() {
    ExamenTP3Theme { WorkoutDashboardScreen({}, {}, {}, {}) }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 640, fontScale = 1.3f)
@Composable
private fun WorkoutDashboardCompactPreview() {
    ExamenTP3Theme { WorkoutDashboardScreen({}, {}, {}, {}) }
}
