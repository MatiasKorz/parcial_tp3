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
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.examentp3.R
import com.example.examentp3.ui.components.VoraCard
import com.example.examentp3.ui.theme.ExamenTP3Theme
import com.example.examentp3.ui.theme.Spacing
import com.example.examentp3.ui.theme.VoraPillShape
import com.example.examentp3.ui.theme.VoraRadius18
import com.example.examentp3.ui.theme.voraColors

// Sizes measured in the Figma frame that have no token in the theme.
private val HeaderHeight = 48.dp
private val HeaderButtonSize = 36.dp
private val BackIconSize = 16.dp
private val MoreIconSize = 18.dp
private val BarSlotHeight = 80.dp
private val BarSlotWidth = 32.dp
private val BarWidth = 24.dp
private val BarCornerRadius = 6.dp
private val BadgeSize = 40.dp
private val TrophySize = 20.dp
private val OrbSize = 40.dp

/** Period options of the selector. Only the selection changes: Figma has data for the week only. */
private enum class WorkoutProgressPeriod(@param:StringRes val label: Int) {
    Week(R.string.workout_progress_period_week),
    Month(R.string.workout_progress_period_month),
    ThreeMonths(R.string.workout_progress_period_three_months),
}

private class ChartBar(
    @param:StringRes val day: Int,
    val height: Dp,
    val isRest: Boolean = false,
)

private class ExerciseProgress(
    @param:StringRes val name: Int,
    @param:StringRes val detail: Int,
    @param:StringRes val change: Int,
    val improved: Boolean,
)

// Demo data from Figma (Progress / 03). Bar heights are the ones drawn in the frame.
private val ChartBars = listOf(
    ChartBar(R.string.workout_progress_day_mon, 52.dp),
    ChartBar(R.string.workout_progress_day_tue, 58.dp),
    ChartBar(R.string.workout_progress_day_wed, 6.dp, isRest = true),
    ChartBar(R.string.workout_progress_day_thu, 66.dp),
    ChartBar(R.string.workout_progress_day_fri, 72.dp),
)

private val Exercises = listOf(
    ExerciseProgress(
        R.string.workout_progress_exercise_bench_press_name,
        R.string.workout_progress_exercise_bench_press_detail,
        R.string.workout_progress_exercise_bench_press_change,
        improved = true,
    ),
    ExerciseProgress(
        R.string.workout_progress_exercise_cable_row_name,
        R.string.workout_progress_exercise_cable_row_detail,
        R.string.workout_progress_exercise_cable_row_change,
        improved = true,
    ),
    ExerciseProgress(
        R.string.workout_progress_exercise_shoulder_press_name,
        R.string.workout_progress_exercise_shoulder_press_detail,
        R.string.workout_progress_exercise_shoulder_press_change,
        improved = false,
    ),
    ExerciseProgress(
        R.string.workout_progress_exercise_lateral_raise_name,
        R.string.workout_progress_exercise_lateral_raise_detail,
        R.string.workout_progress_exercise_lateral_raise_change,
        improved = true,
    ),
)

/** Demo screen: workout progress with fixed values from Figma (no ViewModel). */
@Composable
fun WorkoutProgressScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var period by rememberSaveable { mutableStateOf(WorkoutProgressPeriod.Week) }

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
        MetricsCard()
        TrainingVolumeCard()
        PersonalBestCard()
        InsightSnippet()
        ExerciseProgressSection()
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
            HeaderIconButton(
                icon = R.drawable.ic_workout_progress_chevron_left,
                iconSize = BackIconSize,
                contentDescription = stringResource(R.string.workout_progress_back),
                onClick = onBack,
            )
            Text(
                text = stringResource(R.string.workout_progress_title),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
            )
        }
        // Drawn as in Figma but without action: neither the card nor Figma give it a destination (REF-05).
        HeaderIconButton(
            icon = R.drawable.ic_workout_progress_more,
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

@Composable
private fun PeriodSelector(
    selected: WorkoutProgressPeriod,
    onSelect: (WorkoutProgressPeriod) -> Unit,
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
        WorkoutProgressPeriod.entries.forEach { option ->
            val isSelected = option == selected
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(VoraPillShape)
                    .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent)
                    .selectable(selected = isSelected, role = Role.Tab, onClick = { onSelect(option) })
                    .padding(vertical = Spacing.s8),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = stringResource(option.label),
                    style = MaterialTheme.typography.labelMedium,
                    color = if (isSelected) {
                        MaterialTheme.colorScheme.onPrimary
                    } else {
                        MaterialTheme.voraColors.textSecondary
                    },
                )
            }
        }
    }
}

/** Figma uses padding 12 here; VoraCard has 16. */
@Composable
private fun MetricsCard() {
    VoraCard {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
        ) {
            MetricItem(
                label = stringResource(R.string.workout_progress_sessions_label),
                value = stringResource(R.string.workout_progress_sessions_value),
            )
            VerticalDivider(modifier = Modifier.fillMaxHeight(), color = MaterialTheme.colorScheme.outline)
            MetricItem(
                label = stringResource(R.string.workout_progress_volume_label),
                value = stringResource(R.string.workout_progress_volume_value),
            )
            VerticalDivider(modifier = Modifier.fillMaxHeight(), color = MaterialTheme.colorScheme.outline)
            MetricItem(
                label = stringResource(R.string.workout_progress_avg_time_label),
                value = stringResource(R.string.workout_progress_avg_time_value),
            )
        }
    }
}

@Composable
private fun RowScope.MetricItem(label: String, value: String) {
    Column(
        modifier = Modifier.weight(1f),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.s2),
    ) {
        // Figma applies textCase UPPER; the string keeps the original copy.
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.voraColors.textMuted,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium, // Figma: 16 Bold, no token yet
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

/** Figma uses radius 20 / padding 16 / gap 16 here, unlike VoraCard (16 / 16 / 12). */
@Composable
private fun TrainingVolumeCard() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outline, MaterialTheme.shapes.large)
            .semantics(mergeDescendants = true) {}
            .padding(Spacing.s16),
        verticalArrangement = Arrangement.spacedBy(Spacing.s16),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.s2)) {
                Text(
                    text = stringResource(R.string.workout_progress_chart_title),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = stringResource(R.string.workout_progress_chart_subtitle),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.voraColors.textSecondary,
                )
            }
            Text(
                text = stringResource(R.string.workout_progress_chart_change),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
            )
        }
        // Static chart. Figma's 100-high container (top padding 12) is shorter than its content,
        // so the height is left to the content to avoid clipping the day labels.
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.s12),
            verticalAlignment = Alignment.Bottom,
        ) {
            ChartBars.forEach { bar -> BarColumn(bar) }
        }
    }
}

@Composable
private fun RowScope.BarColumn(bar: ChartBar) {
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
                    .clip(RoundedCornerShape(topStart = BarCornerRadius, topEnd = BarCornerRadius))
                    .background(
                        // A rest day is drawn as a short grey bar in Figma.
                        if (bar.isRest) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.primary,
                    ),
            )
        }
        Text(
            text = stringResource(bar.day),
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.voraColors.textSecondary,
        )
    }
}

/** Figma background #FAFDF9 and badge #EAF9EE have no token: approximated with surface and mintSoft. */
@Composable
private fun PersonalBestCard() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.secondary, MaterialTheme.shapes.medium)
            .padding(Spacing.s16),
        horizontalArrangement = Arrangement.spacedBy(Spacing.s12),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(BadgeSize)
                .clip(CircleShape)
                .background(MaterialTheme.voraColors.mintSoft),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_workout_progress_trophy),
                contentDescription = null, // decorative
                modifier = Modifier.size(TrophySize),
                tint = MaterialTheme.voraColors.success,
            )
        }
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(Spacing.s2),
        ) {
            Text(
                text = stringResource(R.string.workout_progress_best_label).uppercase(),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.voraColors.success,
            )
            Text(
                text = stringResource(R.string.workout_progress_best_exercise),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
            )
            val value = stringResource(R.string.workout_progress_best_value)
            val change = stringResource(R.string.workout_progress_best_change)
            val emphasis = SpanStyle(fontWeight = FontWeight.SemiBold, color = MaterialTheme.voraColors.success)
            Text(
                text = buildAnnotatedString {
                    append("$value ")
                    withStyle(emphasis) { append(change) }
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.voraColors.textSecondary,
            )
        }
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
            text = stringResource(R.string.workout_progress_insight),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun ExerciseProgressSection() {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.s8)) {
        Text(
            text = stringResource(R.string.workout_progress_exercises_title).uppercase(),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.voraColors.textMuted,
        )
        Exercises.forEach { exercise -> ExerciseRow(exercise) }
    }
}

@Composable
private fun ExerciseRow(exercise: ExerciseProgress) {
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
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(Spacing.s2),
        ) {
            Text(
                text = stringResource(exercise.name),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = stringResource(exercise.detail),
                style = MaterialTheme.typography.bodySmall, // Figma: 12 Regular, no token yet
                color = MaterialTheme.voraColors.textSecondary,
            )
        }
        ChangeBadge(text = stringResource(exercise.change), improved = exercise.improved)
    }
}

/** Mint with green text for an improvement, grey for a stable value (Figma). */
@Composable
private fun ChangeBadge(text: String, improved: Boolean) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodySmall, // Figma: 12 SemiBold, no token yet
        fontWeight = FontWeight.SemiBold,
        color = if (improved) MaterialTheme.colorScheme.primary else MaterialTheme.voraColors.textSecondary,
        modifier = Modifier
            .clip(VoraPillShape)
            .background(if (improved) MaterialTheme.voraColors.mintSoft else MaterialTheme.colorScheme.background)
            .padding(horizontal = Spacing.s8, vertical = Spacing.s4),
    )
}

@Preview(showBackground = true, widthDp = 390, heightDp = 1087)
@Composable
private fun WorkoutProgressScreenPreview() {
    ExamenTP3Theme {
        WorkoutProgressScreen(onBack = {})
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
private fun WorkoutProgressScreenSmallPreview() {
    ExamenTP3Theme {
        WorkoutProgressScreen(onBack = {})
    }
}
