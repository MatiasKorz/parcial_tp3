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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.examentp3.R
import com.example.examentp3.ui.components.VoraPanel
import com.example.examentp3.ui.theme.ExamenTP3Theme
import com.example.examentp3.ui.theme.Spacing
import com.example.examentp3.ui.theme.VoraPillShape
import com.example.examentp3.ui.theme.voraColors

// Sizes measured in the Figma frame that have no token in the theme.
private val HeaderHeight = 48.dp
private val CircleButtonSize = 36.dp
private val BackIconSize = 16.dp
private val MoreIconSize = 18.dp
private val DayIndicatorSize = 32.dp
private val OrbSize = 40.dp
private val HabitCardShape = RoundedCornerShape(14.dp)

private enum class NutritionPeriod(@param:StringRes val label: Int) {
    Week(R.string.nutrition_progress_period_week),
    Month(R.string.nutrition_progress_period_month),
    ThreeMonths(R.string.nutrition_progress_period_three_months),
}

private enum class NutritionMetric(@param:StringRes val label: Int) {
    Calories(R.string.nutrition_progress_metric_calories),
    Protein(R.string.nutrition_progress_metric_protein),
    Carbs(R.string.nutrition_progress_metric_carbs),
    Fat(R.string.nutrition_progress_metric_fat),
}

private class DayResult(@param:StringRes val day: Int, val hit: Boolean)

private class Habit(@param:StringRes val label: Int, @param:StringRes val value: Int)

// Demo data copied from the Figma frame (Progress / 04). The texts live in strings.xml.
private val demoWeek = listOf(
    DayResult(R.string.nutrition_progress_day_monday, hit = true),
    DayResult(R.string.nutrition_progress_day_tuesday, hit = true),
    DayResult(R.string.nutrition_progress_day_wednesday, hit = false),
    DayResult(R.string.nutrition_progress_day_thursday, hit = true),
    DayResult(R.string.nutrition_progress_day_friday, hit = true),
    DayResult(R.string.nutrition_progress_day_saturday, hit = false),
    DayResult(R.string.nutrition_progress_day_sunday, hit = true),
)

private val demoHabits = listOf(
    Habit(R.string.nutrition_progress_habit_lunch_label, R.string.nutrition_progress_habit_lunch_value),
    Habit(
        R.string.nutrition_progress_habit_protein_source_label,
        R.string.nutrition_progress_habit_protein_source_value,
    ),
    Habit(R.string.nutrition_progress_habit_breakfast_label, R.string.nutrition_progress_habit_breakfast_value),
    Habit(R.string.nutrition_progress_habit_dinner_label, R.string.nutrition_progress_habit_dinner_value),
)

/**
 * Demo screen: nutrition progress with fixed values from Figma (no ViewModel).
 * The selectors only mark the chosen option; Figma designs just the Protein / Week view (REF-05).
 */
@Composable
fun NutritionProgressScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // Ephemeral UI state; enums are Serializable, so rememberSaveable keeps them across rotation.
    var period by rememberSaveable { mutableStateOf(NutritionPeriod.Week) }
    var metric by rememberSaveable { mutableStateOf(NutritionMetric.Protein) }

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
        PeriodSelector(selected = period, onSelect = { period = it })
        AveragesCard()
        ChartCard(selected = metric, onSelect = { metric = it })
        AnalysisPanel()
        HabitsSection()
    }
}

@Composable
private fun Header(onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(HeaderHeight),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(Spacing.s12),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CircleIconButton(
                icon = R.drawable.ic_nutrition_progress_chevron_left,
                iconSize = BackIconSize,
                contentDescription = stringResource(R.string.nutrition_progress_back),
                onClick = onBack,
            )
            Text(
                text = stringResource(R.string.nutrition_progress_title),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
            )
        }
        // Figma draws a "more actions" button with no menu or prototype link: decorative (REF-05).
        CircleIconButton(
            icon = R.drawable.ic_nutrition_progress_more,
            iconSize = MoreIconSize,
            contentDescription = null,
            onClick = null,
        )
    }
}

/** White 36 dp circle with a border; only clickable when [onClick] is given. */
@Composable
private fun CircleIconButton(
    @DrawableRes icon: Int,
    iconSize: Dp,
    contentDescription: String?,
    onClick: (() -> Unit)?,
) {
    Box(
        modifier = Modifier
            .size(CircleButtonSize)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
            .then(if (onClick != null) Modifier.clickable(role = Role.Button, onClick = onClick) else Modifier),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(icon),
            contentDescription = contentDescription,
            tint = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.size(iconSize),
        )
    }
}

@Composable
private fun PeriodSelector(
    selected: NutritionPeriod,
    onSelect: (NutritionPeriod) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(VoraPillShape)
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outline, VoraPillShape)
            .selectableGroup()
            .padding(Spacing.s4),
        horizontalArrangement = Arrangement.spacedBy(Spacing.s2),
    ) {
        NutritionPeriod.entries.forEach { period ->
            val isSelected = period == selected
            Text(
                text = stringResource(period.label),
                style = MaterialTheme.typography.labelMedium,
                color = if (isSelected) {
                    MaterialTheme.colorScheme.onPrimary
                } else {
                    MaterialTheme.voraColors.textSecondary
                },
                textAlign = TextAlign.Center,
                maxLines = 1,
                modifier = Modifier
                    .weight(1f)
                    .clip(VoraPillShape)
                    .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent)
                    .selectable(selected = isSelected, role = Role.Tab, onClick = { onSelect(period) })
                    .padding(vertical = Spacing.s8),
            )
        }
    }
}

/** Figma uses padding 12 here, unlike VoraCard (16). */
@Composable
private fun AveragesCard() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outline, MaterialTheme.shapes.medium)
            .padding(Spacing.s12),
    ) {
        AverageItem(
            label = stringResource(R.string.nutrition_progress_avg_calories_label),
            value = stringResource(R.string.nutrition_progress_avg_calories_value),
        )
        VerticalDivider(modifier = Modifier.fillMaxHeight(), color = MaterialTheme.colorScheme.outline)
        AverageItem(
            label = stringResource(R.string.nutrition_progress_avg_protein_label),
            value = stringResource(R.string.nutrition_progress_avg_protein_value),
        )
        VerticalDivider(modifier = Modifier.fillMaxHeight(), color = MaterialTheme.colorScheme.outline)
        AverageItem(
            label = stringResource(R.string.nutrition_progress_target_hit_label),
            value = stringResource(R.string.nutrition_progress_target_hit_value),
        )
    }
}

@Composable
private fun RowScope.AverageItem(label: String, value: String) {
    Column(
        modifier = Modifier.weight(1f),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.s2),
    ) {
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.voraColors.textMuted,
            textAlign = TextAlign.Center,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
        )
    }
}

/** Figma uses radius 20 here, unlike VoraCard (16). */
@Composable
private fun ChartCard(
    selected: NutritionMetric,
    onSelect: (NutritionMetric) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outline, MaterialTheme.shapes.large)
            .padding(Spacing.s16),
        verticalArrangement = Arrangement.spacedBy(Spacing.s12),
    ) {
        MetricSelector(selected = selected, onSelect = onSelect)
        Column(
            modifier = Modifier.padding(top = Spacing.s12),
            verticalArrangement = Arrangement.spacedBy(Spacing.s4),
        ) {
            Text(
                text = stringResource(R.string.nutrition_progress_chart_title),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.voraColors.textSecondary,
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Spacing.s8),
                horizontalArrangement = Arrangement.spacedBy(Spacing.s8),
            ) {
                demoWeek.forEach { result -> DayResultItem(result) }
            }
        }
    }
}

@Composable
private fun MetricSelector(
    selected: NutritionMetric,
    onSelect: (NutritionMetric) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .selectableGroup(),
        horizontalArrangement = Arrangement.spacedBy(Spacing.s4),
    ) {
        NutritionMetric.entries.forEach { metric ->
            val isSelected = metric == selected
            Text(
                text = stringResource(metric.label),
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold,
                color = if (isSelected) {
                    MaterialTheme.colorScheme.onPrimary
                } else {
                    MaterialTheme.voraColors.textSecondary
                },
                textAlign = TextAlign.Center,
                maxLines = 1,
                modifier = Modifier
                    .weight(1f)
                    .clip(MaterialTheme.shapes.extraSmall)
                    .background(
                        if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.background,
                    )
                    .selectable(selected = isSelected, role = Role.Tab, onClick = { onSelect(metric) })
                    .padding(horizontal = Spacing.s10, vertical = Spacing.s6),
            )
        }
    }
}

@Composable
private fun RowScope.DayResultItem(result: DayResult) {
    Column(
        modifier = Modifier.weight(1f),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.s6),
    ) {
        Box(
            modifier = Modifier
                .size(DayIndicatorSize)
                .clip(CircleShape)
                .background(
                    if (result.hit) MaterialTheme.voraColors.mintSoft else MaterialTheme.colorScheme.background,
                )
                .border(
                    1.dp,
                    if (result.hit) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.outline,
                    CircleShape,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = stringResource(result.day),
                style = MaterialTheme.typography.bodySmall, // Figma: 12 Bold, no token yet
                fontWeight = FontWeight.Bold,
                color = if (result.hit) MaterialTheme.colorScheme.primary else MaterialTheme.voraColors.textMuted,
            )
        }
        Text(
            text = stringResource(if (result.hit) R.string.nutrition_progress_hit else R.string.nutrition_progress_miss),
            style = MaterialTheme.typography.labelSmall,
            color = if (result.hit) MaterialTheme.colorScheme.secondary else MaterialTheme.voraColors.textMuted,
        )
    }
}

/** VoraPanel keeps its padding 20 / gap 16; Figma uses 16 / 10 here. */
@Composable
private fun AnalysisPanel() {
    VoraPanel {
        Row(
            horizontalArrangement = Arrangement.spacedBy(Spacing.s8),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painter = painterResource(R.drawable.ic_profile_vora_orb),
                contentDescription = null, // decorative
                modifier = Modifier.size(OrbSize),
            )
            Text(
                text = stringResource(R.string.nutrition_progress_analysis_title),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.secondary,
            )
        }
        Text(
            text = stringResource(R.string.nutrition_progress_analysis_text),
            style = MaterialTheme.typography.labelLarge, // Figma: 14 Regular, no token yet
            fontWeight = FontWeight.Normal,
            color = MaterialTheme.colorScheme.onPrimary,
        )
    }
}

@Composable
private fun HabitsSection() {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.s8)) {
        Text(
            text = stringResource(R.string.nutrition_progress_habits_title).uppercase(),
            style = MaterialTheme.typography.labelLarge, // Figma: 14 Bold, no token yet
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.voraColors.textMuted,
        )
        // Figma uses fixed 162 dp cards; weight(1f) keeps two columns on narrower screens (UI03).
        demoHabits.chunked(2).forEach { rowHabits ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min),
                horizontalArrangement = Arrangement.spacedBy(Spacing.s8),
            ) {
                rowHabits.forEach { habit -> HabitCard(habit) }
            }
        }
    }
}

@Composable
private fun RowScope.HabitCard(habit: Habit) {
    Column(
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
            .clip(HabitCardShape)
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outline, HabitCardShape)
            .padding(Spacing.s12),
        verticalArrangement = Arrangement.spacedBy(Spacing.s4),
    ) {
        Text(
            text = stringResource(habit.label),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.voraColors.textSecondary,
        )
        Text(
            text = stringResource(habit.value),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 912)
@Composable
private fun NutritionProgressScreenPreview() {
    ExamenTP3Theme {
        NutritionProgressScreen(onBack = {})
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
private fun NutritionProgressScreenSmallPreview() {
    ExamenTP3Theme {
        NutritionProgressScreen(onBack = {})
    }
}
