package com.example.examentp3.ui.screens.progress

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.examentp3.R
import com.example.examentp3.ui.components.VoraAdviceCard
import com.example.examentp3.ui.components.VoraCard
import com.example.examentp3.ui.components.VoraPrimaryButton
import com.example.examentp3.ui.components.VoraProgressRing
import com.example.examentp3.ui.theme.ExamenTP3Theme
import com.example.examentp3.ui.theme.Spacing
import com.example.examentp3.ui.theme.voraColors

// Demo data copied from the Figma frame (Progress / 01). The texts live in strings.xml.
private object TodaysOverviewDemo {
    const val DAILY_COMPLETION = 0.72f
}

// Sizes measured in the Figma frame that have no token in the theme.
private val CompletionRingSize = 60.dp
private val CompletionRingStroke = 4.5.dp
private val SignalDotSize = 8.dp
private val StartWorkoutHeight = 38.dp

private class CompletionRow(
    @param:StringRes val label: Int,
    @param:StringRes val value: Int,
    @param:StringRes val percent: Int,
    val dotColor: Color,
)

/** Demo screen: today's summary with fixed values from Figma (no ViewModel yet). */
@Composable
fun TodaysOverviewScreen(
    onStartWorkout: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .statusBarsPadding()
            .padding(horizontal = Spacing.screenMargin)
            .padding(top = Spacing.s12, bottom = Spacing.s24),
        verticalArrangement = Arrangement.spacedBy(Spacing.s20),
    ) {
        Header()
        CompletionCard()
        VoraAdviceCard(
            title = stringResource(R.string.todays_overview_advice_title),
            text = stringResource(R.string.todays_overview_advice_text),
            icon = ImageVector.vectorResource(R.drawable.ic_todays_overview_sparkles),
        )
        CategoryBreakdowns(onStartWorkout = onStartWorkout)
    }
}

@Composable
private fun Header() {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.s2)) {
        Text(
            text = stringResource(R.string.todays_overview_date),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.voraColors.textSecondary,
        )
        Text(
            text = stringResource(R.string.todays_overview_title),
            style = MaterialTheme.typography.headlineSmall, // Figma: 28 SemiBold, no token yet
            color = MaterialTheme.colorScheme.onBackground,
        )
    }
}

/** Figma uses radius 20 / padding 20 / gap 16 here, unlike VoraCard (16 / 16 / 12). */
@Composable
private fun CompletionCard() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outline, MaterialTheme.shapes.large)
            .padding(Spacing.s20),
        verticalArrangement = Arrangement.spacedBy(Spacing.s16),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.s4)) {
                Text(
                    text = stringResource(R.string.todays_overview_daily_completion),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.voraColors.textSecondary,
                )
                Text(
                    text = stringResource(R.string.todays_overview_daily_completion_value),
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
            // The outer size wins over the component's 72 dp, so the ring matches the 60 dp of Figma.
            VoraProgressRing(
                progress = TodaysOverviewDemo.DAILY_COMPLETION,
                centerText = "",
                modifier = Modifier.size(CompletionRingSize),
                trackColor = MaterialTheme.colorScheme.background,
                strokeWidth = CompletionRingStroke,
            )
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.outline)
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.s12)) {
            completionRows().forEach { row -> CompletionRowItem(row) }
        }
    }
}

@Composable
private fun completionRows(): List<CompletionRow> = listOf(
    CompletionRow(
        R.string.todays_overview_workout_label,
        R.string.todays_overview_workout_value,
        R.string.todays_overview_workout_percent,
        MaterialTheme.colorScheme.error, // attention red, not an error state
    ),
    CompletionRow(
        R.string.todays_overview_nutrition_label,
        R.string.todays_overview_nutrition_value,
        R.string.todays_overview_nutrition_percent,
        MaterialTheme.colorScheme.secondary,
    ),
    CompletionRow(
        R.string.todays_overview_activity_label,
        R.string.todays_overview_activity_value,
        R.string.todays_overview_activity_percent,
        MaterialTheme.colorScheme.primary,
    ),
    CompletionRow(
        R.string.todays_overview_hydration_label,
        R.string.todays_overview_hydration_value,
        R.string.todays_overview_hydration_percent,
        MaterialTheme.colorScheme.tertiary,
    ),
)

@Composable
private fun CompletionRowItem(row: CompletionRow) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Spacing.s8),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(SignalDotSize)
                .clip(CircleShape)
                .background(row.dotColor),
        )
        Text(
            text = stringResource(row.label),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f),
        )
        val value = stringResource(row.value)
        val percent = stringResource(row.percent)
        val emphasis = SpanStyle(fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
        Text(
            text = buildAnnotatedString {
                append("$value ")
                withStyle(emphasis) { append(percent) }
            },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.voraColors.textSecondary,
        )
    }
}

@Composable
private fun CategoryBreakdowns(onStartWorkout: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.s12)) {
        Text(
            text = stringResource(R.string.todays_overview_category_breakdowns),
            style = MaterialTheme.typography.titleMedium, // Figma: 16 SemiBold, no token yet
            color = MaterialTheme.colorScheme.onBackground,
        )
        WorkoutCard(onStartWorkout = onStartWorkout)
        GoalCard()
    }
}

@Composable
private fun WorkoutCard(onStartWorkout: () -> Unit) {
    VoraCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = stringResource(R.string.todays_overview_workout_category),
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.voraColors.textMuted,
            )
            NotStartedLabel()
        }
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.s4)) {
            Text(
                text = stringResource(R.string.todays_overview_workout_name),
                style = MaterialTheme.typography.titleMedium, // Figma: 16 SemiBold, no token yet
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = stringResource(R.string.todays_overview_workout_meta),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.voraColors.textSecondary,
            )
        }
        VoraPrimaryButton(
            text = stringResource(R.string.todays_overview_start_workout),
            onClick = onStartWorkout,
            modifier = Modifier
                .fillMaxWidth()
                .height(StartWorkoutHeight),
        )
    }
}

/** Plain red text in Figma (no pill), so VoraStatusTag does not apply. */
@Composable
private fun NotStartedLabel() {
    Text(
        text = stringResource(R.string.todays_overview_workout_status),
        style = MaterialTheme.typography.bodySmall, // Figma: 12 SemiBold, no token yet
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.error, // attention red, not an error state
    )
}

@Composable
private fun GoalCard() {
    VoraCard {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
            horizontalArrangement = Arrangement.spacedBy(Spacing.s16),
        ) {
            GoalItem(
                label = stringResource(R.string.todays_overview_current_goal),
                value = stringResource(R.string.todays_overview_current_goal_value),
            )
            VerticalDivider(
                modifier = Modifier.fillMaxHeight(),
                color = MaterialTheme.colorScheme.outline,
            )
            GoalItem(
                label = stringResource(R.string.todays_overview_weekly_target),
                value = stringResource(R.string.todays_overview_weekly_target_value),
            )
        }
    }
}

@Composable
private fun RowScope.GoalItem(label: String, value: String) {
    Column(
        modifier = Modifier.weight(1f),
        verticalArrangement = Arrangement.spacedBy(Spacing.s4),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.voraColors.textMuted,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 930)
@Composable
private fun TodaysOverviewScreenPreview() {
    ExamenTP3Theme {
        TodaysOverviewScreen(onStartWorkout = {})
    }
}
