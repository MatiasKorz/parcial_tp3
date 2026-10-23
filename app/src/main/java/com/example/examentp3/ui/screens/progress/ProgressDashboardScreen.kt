package com.example.examentp3.ui.screens.progress

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.examentp3.R
import com.example.examentp3.ui.components.VoraCard
import com.example.examentp3.ui.components.VoraPanel
import com.example.examentp3.ui.theme.ExamenTP3Theme
import com.example.examentp3.ui.theme.Spacing
import com.example.examentp3.ui.theme.VoraPillShape
import com.example.examentp3.ui.theme.voraColors

// Sizes measured in the Figma frame that have no token in the theme.
private val IconBadgeSize = 40.dp
private val AreaIconSize = 20.dp
private val SmallIconSize = 14.dp
private val GoalIconSize = 16.dp
private val OrbSize = 40.dp

/** Period options of the selector. Figma only has data for the week, so all of them show it. */
private enum class ProgressPeriod(@param:StringRes val label: Int) {
    Week(R.string.progress_dashboard_period_week),
    Month(R.string.progress_dashboard_period_month),
    ThreeMonths(R.string.progress_dashboard_period_three_months),
}

private class OverviewStat(
    @param:StringRes val label: Int,
    @param:StringRes val value: Int,
    @param:StringRes val delta: Int,
)

// Demo data copied from the Figma frame (Progress / 02). The texts live in strings.xml.
private val overviewStats = listOf(
    OverviewStat(
        R.string.progress_dashboard_workouts_label,
        R.string.progress_dashboard_workouts_value,
        R.string.progress_dashboard_workouts_delta,
    ),
    OverviewStat(
        R.string.progress_dashboard_steps_label,
        R.string.progress_dashboard_steps_value,
        R.string.progress_dashboard_steps_delta,
    ),
    OverviewStat(
        R.string.progress_dashboard_protein_label,
        R.string.progress_dashboard_protein_value,
        R.string.progress_dashboard_protein_delta,
    ),
    OverviewStat(
        R.string.progress_dashboard_streak_label,
        R.string.progress_dashboard_streak_value,
        R.string.progress_dashboard_streak_delta,
    ),
)

/** Demo screen: weekly progress with fixed values from Figma (no ViewModel yet). */
@Composable
fun ProgressDashboardScreen(
    onSeeWeeklyReview: () -> Unit,
    onWorkoutClick: () -> Unit,
    onNutritionClick: () -> Unit,
    onActivityClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // Ephemeral UI state: it only marks the option, the content stays the weekly one (REF-05).
    var selectedPeriod by rememberSaveable { mutableStateOf(ProgressPeriod.Week) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .statusBarsPadding()
            .padding(horizontal = Spacing.screenMargin)
            .padding(top = Spacing.s12, bottom = Spacing.s24),
        verticalArrangement = Arrangement.spacedBy(Spacing.s16),
    ) {
        Header()
        PeriodSelector(
            selected = selectedPeriod,
            onSelect = { selectedPeriod = it },
        )
        OverviewCard()
        InsightPanel(onSeeWeeklyReview = onSeeWeeklyReview)
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.s8)) {
            AreaRow(
                icon = R.drawable.ic_progress_dashboard_dumbbell,
                title = R.string.progress_dashboard_area_workout_title,
                subtitle = R.string.progress_dashboard_area_workout_subtitle,
                onClick = onWorkoutClick,
            )
            AreaRow(
                icon = R.drawable.ic_progress_dashboard_apple,
                title = R.string.progress_dashboard_area_nutrition_title,
                subtitle = R.string.progress_dashboard_area_nutrition_subtitle,
                onClick = onNutritionClick,
            )
            AreaRow(
                icon = R.drawable.ic_progress_dashboard_chart_line,
                title = R.string.progress_dashboard_area_activity_title,
                subtitle = R.string.progress_dashboard_area_activity_subtitle,
                onClick = onActivityClick,
            )
        }
        ActiveGoalCard()
    }
}

@Composable
private fun Header() {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.s4)) {
        Text(
            text = stringResource(R.string.progress_dashboard_title),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold, // Figma: 24 Bold, the token is SemiBold
            color = MaterialTheme.colorScheme.onBackground,
        )
        Text(
            text = stringResource(R.string.progress_dashboard_subtitle),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Normal, // Figma: 14 Regular, the token is SemiBold
            color = MaterialTheme.voraColors.textSecondary,
        )
    }
}

/** Pill segmented selector: there is no shared component for it yet. */
@Composable
private fun PeriodSelector(
    selected: ProgressPeriod,
    onSelect: (ProgressPeriod) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(VoraPillShape)
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outline, VoraPillShape)
            .padding(Spacing.s4)
            .selectableGroup(),
        horizontalArrangement = Arrangement.spacedBy(Spacing.s2),
    ) {
        ProgressPeriod.entries.forEach { period ->
            val isSelected = period == selected
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(VoraPillShape)
                    .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent)
                    .selectable(
                        selected = isSelected,
                        onClick = { onSelect(period) },
                        role = Role.Tab,
                    )
                    .padding(vertical = Spacing.s8),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = stringResource(period.label),
                    style = MaterialTheme.typography.labelMedium,
                    color = if (isSelected) {
                        MaterialTheme.colorScheme.onPrimary
                    } else {
                        MaterialTheme.voraColors.textSecondary
                    },
                    maxLines = 1,
                )
            }
        }
    }
}

/** Figma uses radius 20 / padding 16 / gap 14 here, unlike VoraCard (16 / 16 / 12). */
@Composable
private fun OverviewCard() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outline, MaterialTheme.shapes.large)
            .padding(Spacing.s16),
        verticalArrangement = Arrangement.spacedBy(Spacing.s14),
    ) {
        Text(
            text = stringResource(R.string.progress_dashboard_overview_title).uppercase(),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.voraColors.textMuted,
        )
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.s8)) {
            overviewStats.chunked(2).forEach { rowStats ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Min),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.s8),
                ) {
                    rowStats.forEach { stat -> StatItem(stat) }
                }
            }
        }
    }
}

@Composable
private fun RowScope.StatItem(stat: OverviewStat) {
    Column(
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
            .clip(MaterialTheme.shapes.small)
            .background(MaterialTheme.colorScheme.background)
            .padding(Spacing.s12),
        verticalArrangement = Arrangement.spacedBy(Spacing.s4),
    ) {
        Text(
            text = stringResource(stat.label),
            style = MaterialTheme.typography.bodySmall, // Figma: 12 Regular, no token yet
            color = MaterialTheme.voraColors.textSecondary,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(Spacing.s6)) {
            Text(
                text = stringResource(stat.value),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.alignByBaseline(),
            )
            Text(
                text = stringResource(stat.delta),
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.alignByBaseline(),
            )
        }
    }
}

@Composable
private fun InsightPanel(onSeeWeeklyReview: () -> Unit) {
    // VoraPanel uses padding 20 / gap 16; Figma uses 16 / 12 here.
    VoraPanel {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(Spacing.s8),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_profile_vora_orb), // same orb as Profile / 01
                    contentDescription = null, // decorative
                    modifier = Modifier.size(OrbSize),
                )
                Text(
                    text = stringResource(R.string.progress_dashboard_insight_title),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.secondary,
                )
            }
            ConsistentChip()
        }
        Text(
            text = stringResource(R.string.progress_dashboard_insight_text),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onPrimary,
        )
        Row(
            modifier = Modifier
                .padding(top = Spacing.s4)
                .clickable(role = Role.Button, onClick = onSeeWeeklyReview),
            horizontalArrangement = Arrangement.spacedBy(Spacing.s6),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.progress_dashboard_see_weekly_review),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.tertiary,
            )
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.ic_progress_dashboard_arrow_right),
                contentDescription = null, // decorative: the text already says it
                tint = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.size(SmallIconSize),
            )
        }
    }
}

/** Translucent white chip over the green panel; VoraStatusTag is a mint tag, so it does not apply. */
@Composable
private fun ConsistentChip() {
    Text(
        text = stringResource(R.string.progress_dashboard_insight_status),
        style = MaterialTheme.typography.bodySmall,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.tertiary,
        modifier = Modifier
            .clip(VoraPillShape)
            .background(MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.12f)) // Figma: white at 12%
            .padding(horizontal = Spacing.s8, vertical = Spacing.s4),
    )
}

/** The whole row is clickable: "View" alone would be a touch target far below 48 dp. */
@Composable
private fun AreaRow(
    @DrawableRes icon: Int,
    @StringRes title: Int,
    @StringRes subtitle: Int,
    onClick: () -> Unit,
) {
    VoraCard(
        modifier = Modifier
            .clip(MaterialTheme.shapes.medium)
            .clickable(role = Role.Button, onClick = onClick),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.s12),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(IconBadgeSize)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.background),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(icon),
                    contentDescription = null, // decorative: the title already says it
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(AreaIconSize),
                )
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(Spacing.s2),
            ) {
                Text(
                    text = stringResource(title),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = stringResource(subtitle),
                    style = MaterialTheme.typography.bodySmall, // Figma: 12 Regular, no token yet
                    color = MaterialTheme.voraColors.textSecondary,
                )
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(Spacing.s4),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.progress_dashboard_view),
                    style = MaterialTheme.typography.bodySmall, // Figma: 12 SemiBold, no token yet
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary,
                )
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_progress_dashboard_chevron_right),
                    contentDescription = null, // decorative
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(SmallIconSize),
                )
            }
        }
    }
}

/** Mint card with a mint border: VoraInfoMessage is a radius-12 row, so it does not apply. */
@Composable
private fun ActiveGoalCard() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.voraColors.mintSoft)
            .border(1.dp, MaterialTheme.colorScheme.secondary, MaterialTheme.shapes.medium)
            .padding(Spacing.s16),
        verticalArrangement = Arrangement.spacedBy(Spacing.s12),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(Spacing.s8),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_progress_dashboard_target),
                    contentDescription = null, // decorative
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(GoalIconSize),
                )
                Text(
                    text = stringResource(R.string.progress_dashboard_goal_label).uppercase(),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
            Text(
                text = stringResource(R.string.progress_dashboard_goal_progress),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
            )
        }
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.s4)) {
            Text(
                text = stringResource(R.string.progress_dashboard_goal_title),
                style = MaterialTheme.typography.titleMedium, // Figma: 16 SemiBold, no token yet
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = stringResource(R.string.progress_dashboard_goal_body),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.voraColors.textSecondary,
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 1123)
@Composable
private fun ProgressDashboardScreenPreview() {
    ExamenTP3Theme {
        ProgressDashboardScreen(
            onSeeWeeklyReview = {},
            onWorkoutClick = {},
            onNutritionClick = {},
            onActivityClick = {},
        )
    }
}
