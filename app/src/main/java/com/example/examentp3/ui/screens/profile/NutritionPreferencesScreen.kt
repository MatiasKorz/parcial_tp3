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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.examentp3.R
import com.example.examentp3.model.DemoNutritionSelection
import com.example.examentp3.model.DietaryPreference
import com.example.examentp3.model.EnjoyedFood
import com.example.examentp3.model.NutritionPriority
import com.example.examentp3.ui.components.VoraButtonSize
import com.example.examentp3.ui.components.VoraPrimaryButton
import com.example.examentp3.ui.theme.ExamenTP3Theme
import com.example.examentp3.ui.theme.Spacing
import com.example.examentp3.ui.theme.VoraPillShape
import com.example.examentp3.ui.theme.voraColors
import com.example.examentp3.viewmodel.NutritionPreferencesUiState

/**
 * Profile / 04 — Nutrition Preferences. Demo screen: the selection is kept in the local profile;
 * the "Meal Preferences" rows show a demonstration notice (NAV03).
 */
@Composable
fun NutritionPreferencesScreen(
    uiState: NutritionPreferencesUiState,
    onBack: () -> Unit,
    onDietSelect: (DietaryPreference) -> Unit,
    onEnjoyedFoodToggle: (EnjoyedFood) -> Unit,
    onNewIngredientChange: (String) -> Unit,
    onIngredientAdd: () -> Unit,
    onAvoidedFoodRemove: (String) -> Unit,
    onPriorityToggle: (NutritionPriority) -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val draft = uiState.draft
    // Ephemeral UI state: whether the demo notice is open. The selection lives in the ViewModel.
    var showMealNotice by rememberSaveable { mutableStateOf(false) }

    // No bottom bar here, so the Scaffold adds no insets: this screen handles the system bars.
    // imePadding keeps the ingredient field above the keyboard (the activity uses adjustResize).
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .systemBarsPadding()
            .imePadding(),
    ) {
        NutritionPreferencesHeader(onBack = onBack)
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(start = Spacing.screenMargin, end = Spacing.screenMargin, top = Spacing.s12, bottom = Spacing.s24),
            verticalArrangement = Arrangement.spacedBy(Spacing.s20),
        ) {
            Text(
                text = stringResource(R.string.nutrition_preferences_intro),
                // Figma: 14 Regular, line height 20. No theme token matches (bodyLarge is 15), see BASE-03.
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp, lineHeight = 20.sp),
                color = MaterialTheme.voraColors.textSecondary,
            )
            NutritionSection(title = stringResource(R.string.nutrition_preferences_diet_label)) {
                FlowRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectableGroup(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.s8),
                    verticalArrangement = Arrangement.spacedBy(Spacing.s8),
                ) {
                    DietaryPreference.entries.forEach { diet ->
                        DietChip(
                            label = stringResource(diet.labelRes),
                            selected = diet == draft.diet,
                            onClick = { onDietSelect(diet) },
                        )
                    }
                }
            }
            NutritionSection(title = stringResource(R.string.nutrition_preferences_enjoy_label)) {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.s8),
                    verticalArrangement = Arrangement.spacedBy(Spacing.s8),
                ) {
                    EnjoyedFood.entries.forEach { food ->
                        EnjoyedFoodChip(
                            label = stringResource(food.labelRes),
                            selected = food in draft.enjoyedFoods,
                            onToggle = { onEnjoyedFoodToggle(food) },
                        )
                    }
                }
            }
            NutritionSection(title = stringResource(R.string.nutrition_preferences_avoid_label)) {
                AddIngredientField(
                    value = uiState.newIngredient,
                    onValueChange = onNewIngredientChange,
                    onAdd = onIngredientAdd,
                )
                if (draft.avoidedFoods.isNotEmpty()) {
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(Spacing.s8),
                        verticalArrangement = Arrangement.spacedBy(Spacing.s8),
                    ) {
                        draft.avoidedFoods.forEach { food ->
                            AvoidedFoodChip(label = food, onRemove = { onAvoidedFoodRemove(food) })
                        }
                    }
                }
            }
            NutritionSection(title = stringResource(R.string.nutrition_preferences_meals_label)) {
                MealPreferencesCard(onRowClick = { showMealNotice = true })
            }
            NutritionSection(title = stringResource(R.string.nutrition_preferences_priorities_label)) {
                NutritionPriority.entries.forEach { priority ->
                    PriorityCard(
                        title = stringResource(priority.titleRes),
                        selected = priority in draft.priorities,
                        onToggle = { onPriorityToggle(priority) },
                    )
                }
            }
            VoraPrimaryButton(
                text = stringResource(R.string.nutrition_preferences_save),
                onClick = onSave,
                size = VoraButtonSize.Large,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }

    if (showMealNotice) {
        AlertDialog(
            onDismissRequest = { showMealNotice = false },
            title = { Text(stringResource(R.string.nutrition_preferences_demo_title)) },
            text = { Text(stringResource(R.string.nutrition_preferences_demo_meals)) },
            confirmButton = {
                TextButton(onClick = { showMealNotice = false }) {
                    Text(stringResource(R.string.nutrition_preferences_ok))
                }
            },
        )
    }
}

@Composable
private fun NutritionPreferencesHeader(onBack: () -> Unit) {
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
                // Same chevron as Profile / 02 and 03 (identical asset in Figma), so its drawable is reused.
                imageVector = ImageVector.vectorResource(R.drawable.ic_edit_profile_chevron_left),
                contentDescription = stringResource(R.string.nutrition_preferences_back_description),
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(14.dp),
            )
        }
        Text(
            text = stringResource(R.string.nutrition_preferences_title),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
        )
    }
}

@Composable
private fun NutritionSection(title: String, content: @Composable ColumnScope.() -> Unit) {
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

/** One chip of "Dietary Preference" (single choice). Same look as WorkoutChip of Profile / 03, see BASE-03. */
@Composable
private fun DietChip(label: String, selected: Boolean, onClick: () -> Unit) {
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

/**
 * One chip of "Foods You Enjoy" (several can be picked). Figma only draws the selected state
 * (mint border + check); the unselected one uses the neutral border without check (REF-05).
 */
@Composable
private fun EnjoyedFoodChip(label: String, selected: Boolean, onToggle: () -> Unit) {
    Row(
        modifier = Modifier
            .clip(VoraPillShape)
            .background(MaterialTheme.colorScheme.surface)
            .border(
                width = 1.dp,
                color = if (selected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.outline,
                shape = VoraPillShape,
            )
            .toggleable(value = selected, role = Role.Checkbox, onValueChange = { onToggle() })
            .padding(horizontal = Spacing.s14, vertical = Spacing.s8),
        horizontalArrangement = Arrangement.spacedBy(Spacing.s6),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            // Figma: 13 Medium. labelMedium is 13 SemiBold, see BASE-03.
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium),
            color = MaterialTheme.colorScheme.onSurface,
        )
        if (selected) {
            Icon(
                // Same path as the Profile / 03 check; Figma draws it in #123D35 (primary).
                imageVector = ImageVector.vectorResource(R.drawable.ic_goals_preferences_check),
                contentDescription = null, // the checked state is announced by toggleable
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(12.dp),
            )
        }
    }
}

/** "Add an ingredient..." field: the keyboard's Done action adds the typed ingredient. */
@Composable
private fun AddIngredientField(value: String, onValueChange: (String) -> Unit, onAdd: () -> Unit) {
    val shape = MaterialTheme.shapes.small
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        // Figma: 13 Regular.
        textStyle = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurface),
        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Sentences,
            imeAction = ImeAction.Done,
        ),
        keyboardActions = KeyboardActions(onDone = { onAdd() }),
        modifier = Modifier.fillMaxWidth(),
        decorationBox = { innerTextField ->
            // VoraTextField has a label on top, padding 16/12 and 15 sp text, so it does not fit (BASE-03).
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(shape)
                    .background(MaterialTheme.colorScheme.surface)
                    .border(1.dp, MaterialTheme.colorScheme.outline, shape)
                    .padding(horizontal = Spacing.s14, vertical = Spacing.s10),
                horizontalArrangement = Arrangement.spacedBy(Spacing.s8),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_nutrition_preferences_plus),
                    contentDescription = null,
                    tint = MaterialTheme.voraColors.textMuted,
                    modifier = Modifier.size(16.dp),
                )
                Box(modifier = Modifier.weight(1f)) {
                    if (value.isEmpty()) {
                        Text(
                            text = stringResource(R.string.nutrition_preferences_add_ingredient_hint),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.voraColors.textMuted,
                        )
                    }
                    innerTextField()
                }
            }
        },
    )
}

/** One chip of "Foods to Avoid". Tapping it removes the ingredient; the x-circle is the visual hint. */
@Composable
private fun AvoidedFoodChip(label: String, onRemove: () -> Unit) {
    Row(
        modifier = Modifier
            .clip(VoraPillShape)
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outline, VoraPillShape)
            .clickable(
                onClickLabel = stringResource(R.string.nutrition_preferences_remove_action),
                role = Role.Button,
                onClick = onRemove,
            )
            .padding(horizontal = Spacing.s14, vertical = Spacing.s8),
        horizontalArrangement = Arrangement.spacedBy(Spacing.s6),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            // Figma: 13 Medium. labelMedium is 13 SemiBold, see BASE-03.
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium),
            color = MaterialTheme.colorScheme.onSurface,
        )
        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.ic_nutrition_preferences_x_circle),
            contentDescription = null, // the action is announced by onClickLabel
            // Figma red #DC2626 is not a theme color (error is #D95D5D): the vector keeps it, see BASE-03.
            tint = Color.Unspecified,
            modifier = Modifier.size(12.dp),
        )
    }
}

/** "Meal Preferences": fixed Figma values. Every row opens the demonstration notice. */
@Composable
private fun MealPreferencesCard(onRowClick: () -> Unit) {
    val shape = MaterialTheme.shapes.medium
    // Figma: padding 12, gap 8. VoraCard uses 16/12, see BASE-03.
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outline, shape)
            .padding(Spacing.s12),
        verticalArrangement = Arrangement.spacedBy(Spacing.s8),
    ) {
        MealPreferenceRow(R.string.nutrition_preferences_meals_per_day, R.string.nutrition_preferences_meals_per_day_value, onRowClick)
        MealPreferenceRow(R.string.nutrition_preferences_breakfast, R.string.nutrition_preferences_breakfast_value, onRowClick)
        MealPreferenceRow(R.string.nutrition_preferences_dinner, R.string.nutrition_preferences_dinner_value, onRowClick)
    }
}

@Composable
private fun MealPreferenceRow(@StringRes label: Int, @StringRes value: Int, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(role = Role.Button, onClick = onClick),
        horizontalArrangement = Arrangement.spacedBy(Spacing.s8),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(label),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.voraColors.textSecondary,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = stringResource(value),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

/** Selectable card of "What matters most to you?". Same look as GoalOptionCard of Profile / 03, see BASE-03. */
@Composable
private fun PriorityCard(title: String, selected: Boolean, onToggle: () -> Unit) {
    val shape = MaterialTheme.shapes.medium
    Row(
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
            .toggleable(value = selected, role = Role.Checkbox, onValueChange = { onToggle() })
            .padding(Spacing.s16),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            // Figma: 16 SemiBold. titleMedium is 18, see BASE-03.
            style = MaterialTheme.typography.titleMedium.copy(fontSize = 16.sp),
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f),
        )
        SelectionIndicator(selected = selected)
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
                contentDescription = null, // the checked state is announced by the card
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

@get:StringRes
private val DietaryPreference.labelRes: Int
    get() = when (this) {
        DietaryPreference.Everything -> R.string.nutrition_preferences_diet_everything
        DietaryPreference.Vegetarian -> R.string.nutrition_preferences_diet_vegetarian
        DietaryPreference.Vegan -> R.string.nutrition_preferences_diet_vegan
        DietaryPreference.Pescatarian -> R.string.nutrition_preferences_diet_pescatarian
        DietaryPreference.Other -> R.string.nutrition_preferences_diet_other
    }

@get:StringRes
private val EnjoyedFood.labelRes: Int
    get() = when (this) {
        EnjoyedFood.Chicken -> R.string.nutrition_preferences_food_chicken
        EnjoyedFood.Eggs -> R.string.nutrition_preferences_food_eggs
        EnjoyedFood.Fish -> R.string.nutrition_preferences_food_fish
        EnjoyedFood.Rice -> R.string.nutrition_preferences_food_rice
        EnjoyedFood.Pasta -> R.string.nutrition_preferences_food_pasta
        EnjoyedFood.GreekYoghurt -> R.string.nutrition_preferences_food_greek_yoghurt
        EnjoyedFood.Vegetables -> R.string.nutrition_preferences_food_vegetables
        EnjoyedFood.Fruit -> R.string.nutrition_preferences_food_fruit
    }

@get:StringRes
private val NutritionPriority.titleRes: Int
    get() = when (this) {
        NutritionPriority.HitProteinTargets -> R.string.nutrition_preferences_priority_protein
        NutritionPriority.EatMoreConsistently -> R.string.nutrition_preferences_priority_consistency
    }

@Preview(showBackground = true, widthDp = 390, heightDp = 997)
@Composable
private fun NutritionPreferencesScreenPreview() {
    ExamenTP3Theme {
        NutritionPreferencesScreen(
            uiState = NutritionPreferencesUiState(draft = DemoNutritionSelection),
            onBack = {},
            onDietSelect = {},
            onEnjoyedFoodToggle = {},
            onNewIngredientChange = {},
            onIngredientAdd = {},
            onAvoidedFoodRemove = {},
            onPriorityToggle = {},
            onSave = {},
        )
    }
}
