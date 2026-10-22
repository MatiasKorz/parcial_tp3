package com.example.examentp3.ui.screens.home

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.examentp3.R
import com.example.examentp3.ui.components.VoraCard
import com.example.examentp3.ui.components.VoraInfoMessage
import com.example.examentp3.ui.components.VoraLimeButton
import com.example.examentp3.ui.components.VoraMacroBar
import com.example.examentp3.ui.components.VoraPanel
import com.example.examentp3.ui.components.VoraPrimaryButton
import com.example.examentp3.ui.components.VoraProgressRing
import com.example.examentp3.ui.theme.ExamenTP3Theme
import com.example.examentp3.ui.theme.Spacing
import com.example.examentp3.ui.theme.VoraPillShape
import com.example.examentp3.ui.theme.voraColors
import java.util.Locale

@Composable
fun HomeScreen(
    uiState: HomeUiState,
    onNotificationsClick: () -> Unit,
    onStartVoiceSession: () -> Unit,
    onStartMyWorkout: () -> Unit,
    onLogBreakfast: () -> Unit,
    onTodaysOverviewClick: () -> Unit,
    onStartWorkout: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .statusBarsPadding()
            .padding(
                start = Spacing.screenMargin,
                end = Spacing.screenMargin,
                top = Spacing.s12,
                bottom = Spacing.s24,
            ),
        verticalArrangement = Arrangement.spacedBy(Spacing.s20),
    ) {
        HomeHeader(
            userName = uiState.userName,
            avatar = uiState.avatar,
            onNotificationsClick = onNotificationsClick,
        )
        CoachPanel(
            onStartVoiceSession = onStartVoiceSession,
            onStartMyWorkout = onStartMyWorkout,
            onLogBreakfast = onLogBreakfast,
        )
        VoraInfoMessage(
            text = stringResource(R.string.home_tip),
            icon = ImageVector.vectorResource(R.drawable.ic_home_sparkles),
        )
        TodaysOverview(uiState = uiState, onClick = onTodaysOverviewClick)
        RecommendedWorkoutCard(workout = uiState.workout, onStartWorkout = onStartWorkout)
    }
}

@Composable
private fun HomeHeader(
    userName: String,
    @DrawableRes avatar: Int,
    onNotificationsClick: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(Spacing.s12),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painter = painterResource(avatar),
                contentDescription = null, // decorative: the name is next to it
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape),
            )
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.s2)) {
                Text(
                    text = stringResource(R.string.home_greeting),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.voraColors.textSecondary,
                )
                Text(
                    text = userName,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                )
            }
        }
        // The drawable already includes the white circle, its border and the unread dot.
        Image(
            painter = painterResource(R.drawable.ic_home_bell),
            contentDescription = stringResource(R.string.home_notifications_description),
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .clickable(role = Role.Button, onClick = onNotificationsClick),
        )
    }
}

@Composable
private fun CoachPanel(
    onStartVoiceSession: () -> Unit,
    onStartMyWorkout: () -> Unit,
    onLogBreakfast: () -> Unit,
) {
    val onPanel = MaterialTheme.colorScheme.onPrimary
    VoraPanel {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(Spacing.s10),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_home_orb),
                    contentDescription = null, // decorative
                    modifier = Modifier.size(36.dp),
                )
                Text(
                    text = stringResource(R.string.home_coach_name),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.secondary,
                )
            }
            Text(
                text = stringResource(R.string.home_coach_status),
                // Figma: 11 SemiBold; the theme has 11 only as bodySmall (Regular).
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                color = onPanel,
                modifier = Modifier
                    .clip(VoraPillShape)
                    .background(onPanel.copy(alpha = 0.12f))
                    .padding(horizontal = Spacing.s10, vertical = Spacing.s4),
            )
        }
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.s6)) {
            Text(
                text = stringResource(R.string.home_coach_question),
                style = MaterialTheme.typography.headlineMedium,
                color = onPanel,
            )
            Text(
                text = stringResource(R.string.home_coach_hint),
                style = MaterialTheme.typography.bodyMedium,
                color = onPanel.copy(alpha = 0.6f),
            )
        }
        VoraLimeButton(
            text = stringResource(R.string.home_start_voice_session),
            onClick = onStartVoiceSession,
            leadingIcon = ImageVector.vectorResource(R.drawable.ic_home_mic),
            modifier = Modifier.fillMaxWidth(),
        )
        Row(horizontalArrangement = Arrangement.spacedBy(Spacing.s8)) {
            CoachChip(text = stringResource(R.string.home_start_my_workout), onClick = onStartMyWorkout)
            CoachChip(text = stringResource(R.string.home_log_breakfast), onClick = onLogBreakfast)
        }
    }
}

/** Translucent chip that only appears on the dark coach panel. */
@Composable
private fun CoachChip(text: String, onClick: () -> Unit) {
    val onPanel = MaterialTheme.colorScheme.onPrimary
    Text(
        text = text,
        // Figma: 12 Medium; closest theme size is 13 (bodyMedium).
        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
        color = onPanel,
        modifier = Modifier
            .clip(VoraPillShape)
            .background(onPanel.copy(alpha = 0.12f))
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = Spacing.s12, vertical = Spacing.s8),
    )
}

@Composable
private fun TodaysOverview(uiState: HomeUiState, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(role = Role.Button, onClick = onClick),
        verticalArrangement = Arrangement.spacedBy(Spacing.s12),
    ) {
        Text(
            text = stringResource(R.string.home_todays_overview),
            // Figma: 16 SemiBold; closest theme size is 15 (titleSmall).
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onBackground,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(Spacing.s12)) {
            DailyGoalCard(percent = uiState.dailyGoalPercent)
            NutritionCard(uiState = uiState, modifier = Modifier.weight(1f))
        }
        ActivityCard(uiState = uiState)
    }
}

@Composable
private fun DailyGoalCard(percent: Int) {
    VoraCard(modifier = Modifier.width(110.dp)) {
        VoraProgressRing(
            progress = percent / 100f,
            centerText = stringResource(R.string.home_percent, percent),
            trackColor = MaterialTheme.colorScheme.background,
            strokeWidth = 4.8.dp, // ring asset: 64 dp with a 4.8 dp stroke
            modifier = Modifier
                .size(64.dp)
                .align(Alignment.CenterHorizontally),
        )
        Text(
            text = stringResource(R.string.home_daily_goal),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.voraColors.textSecondary,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )
    }
}

@Composable
private fun NutritionCard(uiState: HomeUiState, modifier: Modifier = Modifier) {
    val kcalGoal = stringResource(R.string.home_kcal_goal, uiState.caloriesGoal.grouped())
    val mutedColor = MaterialTheme.voraColors.textMuted
    val goalStyle = MaterialTheme.typography.bodySmall
    VoraCard(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.home_nutrition),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.voraColors.textSecondary,
            )
            Text(
                text = buildAnnotatedString {
                    append("${uiState.caloriesConsumed.grouped()} ")
                    withStyle(
                        SpanStyle(
                            color = mutedColor,
                            fontSize = goalStyle.fontSize,
                            fontWeight = goalStyle.fontWeight,
                        ),
                    ) { append(kcalGoal) }
                },
                // Figma: 14 Bold; closest theme size is 15 (titleSmall).
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.s6)) {
            VoraMacroBar(
                label = stringResource(R.string.home_protein),
                valueText = stringResource(
                    R.string.home_grams_progress,
                    uiState.proteinGrams,
                    uiState.proteinGoalGrams,
                ),
                progress = uiState.proteinGrams.toFloat() / uiState.proteinGoalGrams,
                color = MaterialTheme.colorScheme.primary,
            )
            VoraMacroBar(
                label = stringResource(R.string.home_carbs),
                valueText = stringResource(
                    R.string.home_grams_progress,
                    uiState.carbsGrams,
                    uiState.carbsGoalGrams,
                ),
                progress = uiState.carbsGrams.toFloat() / uiState.carbsGoalGrams,
                color = MaterialTheme.colorScheme.secondary,
            )
        }
    }
}

@Composable
private fun ActivityCard(uiState: HomeUiState) {
    VoraCard {
        Row(modifier = Modifier.height(IntrinsicSize.Min)) {
            ActivityMetric(
                label = stringResource(R.string.home_steps),
                value = uiState.steps.grouped(),
                modifier = Modifier.weight(1f),
            )
            ActivityDivider()
            ActivityMetric(
                label = stringResource(R.string.home_distance),
                value = stringResource(
                    R.string.home_distance_value,
                    String.format(Locale.US, "%.1f", uiState.distanceKm),
                ),
                modifier = Modifier.weight(1f),
            )
            ActivityDivider()
            ActivityMetric(
                label = stringResource(R.string.home_active_kcal),
                value = stringResource(R.string.home_kcal_value, uiState.activeKcal.grouped()),
                modifier = Modifier.weight(1f),
            )
        }
    }
}

/** Figma: 16 between metrics, then a 1 dp left border and 12 of padding. */
@Composable
private fun ActivityDivider() {
    Spacer(modifier = Modifier.width(Spacing.s16))
    VerticalDivider(
        modifier = Modifier.fillMaxHeight(),
        thickness = 1.dp,
        color = MaterialTheme.colorScheme.outline,
    )
    Spacer(modifier = Modifier.width(Spacing.s12))
}

@Composable
private fun ActivityMetric(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(Spacing.s2)) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.voraColors.textMuted,
        )
        Text(
            text = value,
            // Figma: 16 Bold; closest theme size is 15 (titleSmall).
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
private fun RecommendedWorkoutCard(workout: RecommendedWorkout, onStartWorkout: () -> Unit) {
    VoraCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(Spacing.s6),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_home_calendar),
                    contentDescription = null, // decorative
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp),
                )
                Text(
                    text = stringResource(R.string.home_recommended_workout),
                    // Figma: 12 SemiBold; closest theme size is 13 (labelMedium).
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.voraColors.textSecondary,
                )
            }
            Text(
                text = stringResource(R.string.home_today),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = Spacing.s8, vertical = Spacing.s2),
            )
        }
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.s4)) {
            Text(
                text = workout.name,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = stringResource(
                    R.string.home_workout_summary,
                    workout.durationMinutes,
                    workout.exerciseCount,
                    workout.level,
                ),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.voraColors.textSecondary,
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Spacing.s4),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.home_estimated_volume, workout.estimatedVolumeKg.grouped()),
                // Figma: 12 Regular; closest theme size is 11 (bodySmall).
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.voraColors.textMuted,
            )
            VoraPrimaryButton(
                text = stringResource(R.string.home_start_workout),
                onClick = onStartWorkout,
            )
        }
    }
}

// The app copy is English only, so numbers follow the Figma format ("1,420") on any device locale.
private fun Int.grouped(): String = String.format(Locale.US, "%,d", this)

@Preview(showBackground = true, widthDp = 390)
@Composable
private fun HomeScreenPreview() {
    ExamenTP3Theme {
        HomeScreen(
            uiState = HomeSampleUiState,
            onNotificationsClick = {},
            onStartVoiceSession = {},
            onStartMyWorkout = {},
            onLogBreakfast = {},
            onTodaysOverviewClick = {},
            onStartWorkout = {},
        )
    }
}
