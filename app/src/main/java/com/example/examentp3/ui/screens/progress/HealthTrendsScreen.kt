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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.examentp3.R
import com.example.examentp3.ui.components.VoraCard
import com.example.examentp3.ui.theme.ExamenTP3Theme
import com.example.examentp3.ui.theme.Spacing
import com.example.examentp3.ui.theme.VoraRadius18
import com.example.examentp3.ui.theme.voraColors

// Sizes measured in the Figma frame that have no token in the theme.
private val HeaderHeight = 48.dp
private val HeaderButtonSize = 36.dp
private val BackIconSize = 16.dp
private val MoreIconSize = 18.dp
private val BarSlotHeight = 75.dp
private val BarSlotWidth = 26.dp
private val BarWidth = 18.dp
private val BarShape = RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)
private val OrbSize = 40.dp
private val HabitIconSize = 18.dp
private val TrendIconSize = 14.dp

private class StepBar(
    @param:StringRes val day: Int,
    val height: Dp,
    // Figma draws W and F in dark green without a legend (REF-05); no business meaning is assumed.
    val highlighted: Boolean = false,
)

private enum class HabitTrend(@param:StringRes val label: Int, @param:DrawableRes val icon: Int) {
    Improving(R.string.health_trends_trend_improving, R.drawable.ic_health_trends_trending_up),
    Consistent(R.string.health_trends_trend_consistent, R.drawable.ic_health_trends_activity),
}

private class HabitSignal(
    @param:StringRes val label: Int,
    @param:DrawableRes val icon: Int,
    val trend: HabitTrend,
)

// Demo data copied from the Figma frame (Progress / 05). Bar heights are the ones drawn in the
// frame; Figma gives no step values per day, so none are made up.
private val demoWeekSteps = listOf(
    StepBar(R.string.health_trends_day_monday, 45.dp),
    StepBar(R.string.health_trends_day_tuesday, 59.dp),
    StepBar(R.string.health_trends_day_wednesday, 61.dp, highlighted = true),
    StepBar(R.string.health_trends_day_thursday, 56.dp),
    StepBar(R.string.health_trends_day_friday, 71.dp, highlighted = true),
    StepBar(R.string.health_trends_day_saturday, 59.dp),
    StepBar(R.string.health_trends_day_sunday, 59.dp),
)

private val demoHabitSignals = listOf(
    HabitSignal(R.string.health_trends_habit_sleep_quality, R.drawable.ic_health_trends_bed, HabitTrend.Improving),
    HabitSignal(
        R.string.health_trends_habit_daily_movement,
        R.drawable.ic_health_trends_navigation,
        HabitTrend.Consistent,
    ),
    HabitSignal(
        R.string.health_trends_habit_workout_frequency,
        R.drawable.ic_health_trends_dumbbell,
        HabitTrend.Improving,
    ),
    HabitSignal(R.string.health_trends_habit_protein_intake, R.drawable.ic_health_trends_apple, HabitTrend.Improving),
)

/** Demo screen: health trends with fixed values from Figma (no ViewModel, no health permissions). */
@Composable
fun HealthTrendsScreen(
    onBack: () -> Unit,
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
        verticalArrangement = Arrangement.spacedBy(Spacing.s16),
    ) {
        Header(onBack = onBack)
        SummaryCard()
        StepsChartCard()
        InsightSnippet()
        HabitSignalsSection()
    }
}

@Composable
private fun Header(onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(HeaderHeight),
        horizontalArrangement = Arrangement.spacedBy(Spacing.s12),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        HeaderIconButton(
            icon = R.drawable.ic_health_trends_chevron_left,
            iconSize = BackIconSize,
            contentDescription = stringResource(R.string.health_trends_back),
            onClick = onBack,
        )
        // weight(1f) lets the subtitle wrap on narrow screens instead of pushing the "more" button out.
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(Spacing.s2),
        ) {
            Text(
                text = stringResource(R.string.health_trends_title),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Text(
                text = stringResource(R.string.health_trends_subtitle),
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp), // Figma: 12 Regular, no token
                color = MaterialTheme.voraColors.textSecondary,
            )
        }
        // Drawn as in Figma but without action: neither the card nor Figma give it a destination (REF-05).
        HeaderIconButton(
            icon = R.drawable.ic_health_trends_more,
            iconSize = MoreIconSize,
            contentDescription = null,
            onClick = null,
        )
    }
}

/** 36 dp round button with border; not clickable when [onClick] is null. */
@Composable
private fun HeaderIconButton(
    @DrawableRes icon: Int,
    iconSize: Dp,
    contentDescription: String?,
    onClick: (() -> Unit)?,
) {
    Box(
        modifier = Modifier
            .size(HeaderButtonSize)
            .clip(VoraRadius18)
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outline, VoraRadius18)
            .then(if (onClick != null) Modifier.clickable(role = Role.Button, onClick = onClick) else Modifier),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = contentDescription,
            modifier = Modifier.size(iconSize),
            tint = MaterialTheme.colorScheme.onBackground,
        )
    }
}

/** VoraCard matches Figma here (radius 16, padding 16). Items are start-aligned, unlike 6.3/6.4. */
@Composable
private fun SummaryCard() {
    VoraCard {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
        ) {
            StatItem(
                label = stringResource(R.string.health_trends_avg_steps_label),
                value = stringResource(R.string.health_trends_avg_steps_value),
            )
            Spacer(Modifier.width(Spacing.s12))
            VerticalDivider(modifier = Modifier.fillMaxHeight(), color = MaterialTheme.colorScheme.outline)
            StatItem(
                label = stringResource(R.string.health_trends_avg_distance_label),
                value = stringResource(R.string.health_trends_avg_distance_value),
                modifier = Modifier.padding(horizontal = Spacing.s12),
            )
            VerticalDivider(modifier = Modifier.fillMaxHeight(), color = MaterialTheme.colorScheme.outline)
            Spacer(Modifier.width(Spacing.s12))
            StatItem(
                label = stringResource(R.string.health_trends_active_kcal_label),
                value = stringResource(R.string.health_trends_active_kcal_value),
                modifier = Modifier.padding(start = Spacing.s12),
            )
        }
    }
}

@Composable
private fun RowScope.StatItem(label: String, value: String, modifier: Modifier = Modifier) {
    Column(
        modifier = Modifier
            .weight(1f)
            .then(modifier),
        verticalArrangement = Arrangement.spacedBy(Spacing.s2),
    ) {
        // Figma applies textCase UPPER; the string keeps the original copy. May wrap on narrow screens.
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.voraColors.textMuted,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium.copy(fontSize = 16.sp, fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

/** Figma uses radius 20 here, unlike VoraCard (16). */
@Composable
private fun StepsChartCard() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outline, MaterialTheme.shapes.large)
            .semantics(mergeDescendants = true) {}
            .padding(Spacing.s16),
        verticalArrangement = Arrangement.spacedBy(Spacing.s12),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.health_trends_chart_title),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = stringResource(R.string.health_trends_chart_goal),
                style = MaterialTheme.typography.labelMedium.copy(fontSize = 12.sp), // Figma: 12 SemiBold, no token
                color = MaterialTheme.colorScheme.secondary,
            )
        }
        // Static chart. Figma's 100-high container (top padding 12) is shorter than its content,
        // so the height is left to the content to avoid clipping the day labels.
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Spacing.s12),
            horizontalArrangement = Arrangement.spacedBy(Spacing.s8),
            verticalAlignment = Alignment.Bottom,
        ) {
            demoWeekSteps.forEach { bar -> StepBarColumn(bar) }
        }
    }
}

@Composable
private fun RowScope.StepBarColumn(bar: StepBar) {
    Column(
        modifier = Modifier.weight(1f),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.s6),
    ) {
        Box(
            modifier = Modifier
                .width(BarSlotWidth)
                .height(BarSlotHeight),
            contentAlignment = Alignment.BottomCenter,
        ) {
            Box(
                modifier = Modifier
                    .width(BarWidth)
                    .height(bar.height)
                    .clip(BarShape)
                    .background(
                        if (bar.highlighted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
                    ),
            )
        }
        Text(
            text = stringResource(bar.day),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.voraColors.textSecondary,
        )
    }
}

/** VoraInfoMessage tints its 16 dp icon and uses gap 8; the multicolor 40 dp orb needs its own row. */
@Composable
private fun InsightSnippet() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.small)
            .background(MaterialTheme.voraColors.mintSoft)
            .border(1.dp, MaterialTheme.colorScheme.secondary, MaterialTheme.shapes.small)
            .padding(Spacing.s12),
        horizontalArrangement = Arrangement.spacedBy(Spacing.s12),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = painterResource(R.drawable.ic_profile_vora_orb),
            contentDescription = null, // decorative
            modifier = Modifier.size(OrbSize),
        )
        Text(
            text = stringResource(R.string.health_trends_insight),
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun HabitSignalsSection() {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.s8)) {
        Text(
            text = stringResource(R.string.health_trends_habits_title).uppercase(),
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold), // Figma: 14 Bold
            color = MaterialTheme.voraColors.textMuted,
        )
        demoHabitSignals.forEach { signal -> HabitRow(signal) }
    }
}

@Composable
private fun HabitRow(signal: HabitSignal) {
    val trendColor: Color = when (signal.trend) {
        HabitTrend.Improving -> MaterialTheme.voraColors.success
        HabitTrend.Consistent -> MaterialTheme.voraColors.textSecondary
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.small)
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outline, MaterialTheme.shapes.small)
            .padding(Spacing.s14),
        horizontalArrangement = Arrangement.spacedBy(Spacing.s12),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = painterResource(signal.icon),
            contentDescription = null, // decorative: the label says it
            modifier = Modifier.size(HabitIconSize),
            tint = MaterialTheme.colorScheme.primary,
        )
        Text(
            text = stringResource(signal.label),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f),
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(Spacing.s6),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(signal.trend.label),
                style = MaterialTheme.typography.labelMedium,
                color = trendColor,
            )
            Icon(
                painter = painterResource(signal.trend.icon),
                contentDescription = null, // decorative: the status text says it
                modifier = Modifier.size(TrendIconSize),
                tint = trendColor,
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun HealthTrendsScreenPreview() {
    ExamenTP3Theme {
        HealthTrendsScreen(onBack = {})
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
private fun HealthTrendsScreenSmallPreview() {
    ExamenTP3Theme {
        HealthTrendsScreen(onBack = {})
    }
}
