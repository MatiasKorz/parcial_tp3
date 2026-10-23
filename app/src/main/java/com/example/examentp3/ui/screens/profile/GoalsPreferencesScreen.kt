package com.example.examentp3.ui.screens.profile

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.examentp3.R
import com.example.examentp3.model.CoachingStyle
import com.example.examentp3.model.DemoGoalsSelection
import com.example.examentp3.model.PrimaryGoal
import com.example.examentp3.model.TrainingFrequency
import com.example.examentp3.model.WorkoutPreference
import com.example.examentp3.ui.components.VoraButtonSize
import com.example.examentp3.ui.components.VoraPrimaryButton
import com.example.examentp3.ui.theme.ExamenTP3Theme
import com.example.examentp3.ui.theme.Spacing
import com.example.examentp3.ui.theme.VoraPillShape
import com.example.examentp3.ui.theme.voraColors
import com.example.examentp3.viewmodel.GoalsPreferencesUiState

/** Profile / 03 — Goals & Preferences. Demo screen: the selection is kept in the local profile. */
@Composable
fun GoalsPreferencesScreen(
    uiState: GoalsPreferencesUiState,
    onBack: () -> Unit,
    onGoalToggle: (PrimaryGoal) -> Unit,
    onFrequencySelect: (TrainingFrequency) -> Unit,
    onWorkoutSelect: (WorkoutPreference) -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val draft = uiState.draft

    // No bottom bar here, so the Scaffold adds no insets: this screen handles the system bars.
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .systemBarsPadding(),
    ) {
        GoalsPreferencesHeader(onBack = onBack)
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(start = Spacing.screenMargin, end = Spacing.screenMargin, top = Spacing.s12, bottom = Spacing.s24),
            verticalArrangement = Arrangement.spacedBy(Spacing.s20),
        ) {
            Text(
                text = stringResource(R.string.goals_preferences_intro),
                // Figma: 14 Regular, line height 20. No theme token matches (bodyLarge is 15), see BASE-03.
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp, lineHeight = 20.sp),
                color = MaterialTheme.voraColors.textSecondary,
            )
            GoalsSection(title = stringResource(R.string.goals_preferences_primary_goal_label)) {
                PrimaryGoal.entries.forEach { goal ->
                    GoalOptionCard(
                        title = stringResource(goal.titleRes),
                        description = goal.descriptionRes?.let { stringResource(it) },
                        selected = goal in draft.goals,
                        onClick = { onGoalToggle(goal) },
                    )
                }
            }
            GoalsSection(title = stringResource(R.string.goals_preferences_frequency_label)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectableGroup(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.s8),
                ) {
                    TrainingFrequency.entries.forEach { frequency ->
                        FrequencyOption(
                            label = stringResource(frequency.labelRes),
                            selected = frequency == draft.frequency,
                            onClick = { onFrequencySelect(frequency) },
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }
            GoalsSection(title = stringResource(R.string.goals_preferences_workout_label)) {
                FlowRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectableGroup(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.s8),
                    verticalArrangement = Arrangement.spacedBy(Spacing.s8),
                ) {
                    WorkoutPreference.entries.forEach { workout ->
                        WorkoutChip(
                            label = stringResource(workout.labelRes),
                            selected = workout == draft.workout,
                            onClick = { onWorkoutSelect(workout) },
                        )
                    }
                }
            }
            GoalsSection(title = stringResource(R.string.goals_preferences_coaching_label)) {
                // Only "Balanced" is drawn, so it stays marked and is not clickable (open question in REF-05).
                GoalOptionCard(
                    title = stringResource(draft.coachingStyle.titleRes),
                    description = stringResource(draft.coachingStyle.descriptionRes),
                    selected = true,
                    onClick = null,
                )
            }
            VoraPrimaryButton(
                text = stringResource(R.string.goals_preferences_save),
                onClick = onSave,
                size = VoraButtonSize.Large,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun GoalsPreferencesHeader(onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.screenMargin, vertical = Spacing.s12),
        horizontalArrangement = Arrangement.spacedBy(Spacing.s12),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // 32 dp back button: no shared component has this size yet (see BASE-03).
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surface)
                .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
                .clickable(role = Role.Button, onClick = onBack),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                // Same chevron as Profile / 02 (identical asset in Figma), so its drawable is reused.
                imageVector = ImageVector.vectorResource(R.drawable.ic_edit_profile_chevron_left),
                contentDescription = stringResource(R.string.goals_preferences_back_description),
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(14.dp),
            )
        }
        Text(
            text = stringResource(R.string.goals_preferences_title),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
        )
    }
}

@Composable
private fun GoalsSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.s10)) {
        Text(
            // Figma applies textCase UPPER; the string keeps the original copy.
            text = title.uppercase(),
            // Figma: 13 Bold. labelMedium is 13 SemiBold, see BASE-03.
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.voraColors.textMuted,
        )
        content()
    }
}

/** Selectable card of "Primary Goal" and "Coaching Style". Private until BASE-03 shares it with 7.4/7.5. */
@Composable
private fun GoalOptionCard(
    title: String,
    description: String?,
    selected: Boolean,
    onClick: (() -> Unit)?,
) {
    val shape = MaterialTheme.shapes.medium
    val interaction = if (onClick != null) {
        // Checkbox: "Primary Goal" allows several goals at once.
        Modifier.selectable(selected = selected, role = Role.Checkbox, onClick = onClick)
    } else {
        Modifier.semantics(mergeDescendants = true) { this.selected = selected }
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            // Figma: mint at 8 %. VoraMintSoft is 12 %, see BASE-03.
            .background(if (selected) MaterialTheme.colorScheme.secondary.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface)
            .border(
                width = if (selected) 2.dp else 1.dp,
                color = if (selected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.outline,
                shape = shape,
            )
            .then(interaction)
            .padding(Spacing.s16),
        verticalArrangement = Arrangement.spacedBy(Spacing.s6),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = title,
                // Figma: 16 SemiBold. titleMedium is 18, see BASE-03.
                style = MaterialTheme.typography.titleMedium.copy(fontSize = 16.sp),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f),
            )
            SelectionIndicator(selected = selected)
        }
        if (description != null) {
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.voraColors.textSecondary,
            )
        }
    }
}

@Composable
private fun SelectionIndicator(selected: Boolean) {
    if (selected) {
        Box(
            modifier = Modifier
                .size(20.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.ic_goals_preferences_check),
                contentDescription = null, // the selected state is announced by the card
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(12.dp),
            )
        }
    } else {
        Box(
            modifier = Modifier
                .size(20.dp)
                .border(2.dp, MaterialTheme.colorScheme.outline, CircleShape),
        )
    }
}

/** One option of "Training Frequency". Private until BASE-03 shares it with 7.4/7.5. */
@Composable
private fun FrequencyOption(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = MaterialTheme.shapes.small
    Box(
        modifier = modifier
            .clip(shape)
            .background(if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface)
            .border(1.dp, if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline, shape)
            .selectable(selected = selected, role = Role.RadioButton, onClick = onClick)
            .padding(horizontal = Spacing.s4, vertical = Spacing.s10),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
        )
    }
}

/** One chip of "Workout Preference". Private until BASE-03 shares it with 7.4/7.5. */
@Composable
private fun WorkoutChip(label: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(VoraPillShape)
            .background(if (selected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.surface)
            // Figma keeps the light border on the selected chip too.
            .border(1.dp, MaterialTheme.colorScheme.outline, VoraPillShape)
            .selectable(selected = selected, role = Role.RadioButton, onClick = onClick)
            .padding(horizontal = Spacing.s14, vertical = Spacing.s8),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@get:StringRes
private val PrimaryGoal.titleRes: Int
    get() = when (this) {
        PrimaryGoal.BuildStrength -> R.string.goals_preferences_goal_build_strength
        PrimaryGoal.ImproveFitness -> R.string.goals_preferences_goal_improve_fitness
        PrimaryGoal.LoseBodyFat -> R.string.goals_preferences_goal_lose_body_fat
        PrimaryGoal.BuildHealthyHabits -> R.string.goals_preferences_goal_healthy_habits
    }

// "Lose body fat" and "Build healthy habits" have no description in Figma (REF-05), so none is shown.
@get:StringRes
private val PrimaryGoal.descriptionRes: Int?
    get() = when (this) {
        PrimaryGoal.BuildStrength -> R.string.goals_preferences_goal_build_strength_description
        PrimaryGoal.ImproveFitness -> R.string.goals_preferences_goal_improve_fitness_description
        PrimaryGoal.LoseBodyFat, PrimaryGoal.BuildHealthyHabits -> null
    }

@get:StringRes
private val TrainingFrequency.labelRes: Int
    get() = when (this) {
        TrainingFrequency.ThreeDays -> R.string.goals_preferences_frequency_3
        TrainingFrequency.FourDays -> R.string.goals_preferences_frequency_4
        TrainingFrequency.FiveDays -> R.string.goals_preferences_frequency_5
        TrainingFrequency.SixPlusDays -> R.string.goals_preferences_frequency_6_plus
    }

@get:StringRes
private val WorkoutPreference.labelRes: Int
    get() = when (this) {
        WorkoutPreference.Strength -> R.string.goals_preferences_workout_strength
        WorkoutPreference.Cardio -> R.string.goals_preferences_workout_cardio
        WorkoutPreference.Hiit -> R.string.goals_preferences_workout_hiit
        WorkoutPreference.Mobility -> R.string.goals_preferences_workout_mobility
        WorkoutPreference.Mixed -> R.string.goals_preferences_workout_mixed
    }

@get:StringRes
private val CoachingStyle.titleRes: Int
    get() = when (this) {
        CoachingStyle.Balanced -> R.string.goals_preferences_coaching_balanced
    }

@get:StringRes
private val CoachingStyle.descriptionRes: Int
    get() = when (this) {
        CoachingStyle.Balanced -> R.string.goals_preferences_coaching_balanced_description
    }

@Preview(showBackground = true, heightDp = 947, widthDp = 390)
@Composable
private fun GoalsPreferencesScreenPreview() {
    ExamenTP3Theme {
        GoalsPreferencesScreen(
            uiState = GoalsPreferencesUiState(draft = DemoGoalsSelection),
            onBack = {},
            onGoalToggle = {},
            onFrequencySelect = {},
            onWorkoutSelect = {},
            onSave = {},
        )
    }
}
