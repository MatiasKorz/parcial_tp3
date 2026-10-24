package com.example.examentp3.ui.screens.nutrition

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.examentp3.R
import com.example.examentp3.model.EstimatedIngredient
import com.example.examentp3.model.MealType
import com.example.examentp3.ui.components.VoraButtonSize
import com.example.examentp3.ui.components.VoraPrimaryButton
import com.example.examentp3.ui.components.VoraTextAction
import com.example.examentp3.ui.components.VoraTextField
import com.example.examentp3.ui.navigation.FoodConfirmationMode
import com.example.examentp3.ui.theme.ExamenTP3Theme
import com.example.examentp3.ui.theme.Spacing
import com.example.examentp3.ui.theme.VoraPillShape
import com.example.examentp3.ui.theme.voraColors
import com.example.examentp3.viewmodel.FoodConfirmationUiState
import com.example.examentp3.viewmodel.MealFieldError
import com.example.examentp3.viewmodel.MealFormDraft
import com.example.examentp3.viewmodel.MealFormErrors
import com.example.examentp3.viewmodel.MealTotals
import com.example.examentp3.viewmodel.foodConfirmationInitialState
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

// D9: long date in English, like the dates of the Nutrition dashboard, plus the year.
private val formDateFormatter = DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy", Locale.ENGLISH)

/**
 * "Nutrition / 07 — Food Confirmation". Manual mode: RF03 form (no Figma frame, it reuses the look
 * of the ingredient card). Estimate mode: the simulated analysis as drawn; adjusting ingredients
 * and "Save without logging" only show a demonstration notice (REF-05).
 */
@Composable
fun FoodConfirmationScreen(
    uiState: FoodConfirmationUiState,
    onBack: () -> Unit,
    onNameChange: (String) -> Unit,
    onDateSelect: (LocalDate) -> Unit,
    onTypeSelect: (MealType) -> Unit,
    onCaloriesChange: (String) -> Unit,
    onProteinChange: (String) -> Unit,
    onCarbsChange: (String) -> Unit,
    onFatChange: (String) -> Unit,
    onAddToMeal: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // Ephemeral UI state: which demonstration notice is open, if any, and the date picker.
    var notice by rememberSaveable { mutableStateOf<Int?>(null) }
    var showDatePicker by rememberSaveable { mutableStateOf(false) }
    val showAdjustNotice = { notice = R.string.food_confirmation_demo_adjust }
    val totals = uiState.totals

    // The bottom bar is shown here: the Scaffold already applies and consumes the bottom inset,
    // so imePadding only adds what the keyboard covers beyond the bar.
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .imePadding(),
    ) {
        FoodConfirmationHeader(
            onBack = onBack,
            modifier = Modifier.padding(
                start = Spacing.screenMargin,
                end = Spacing.screenMargin,
                top = Spacing.s12,
            ),
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(
                    start = Spacing.screenMargin,
                    end = Spacing.screenMargin,
                    top = Spacing.s20,
                    bottom = Spacing.s24,
                ),
            verticalArrangement = Arrangement.spacedBy(Spacing.s20),
        ) {
            val estimate = uiState.estimate
            if (estimate != null) {
                Column(verticalArrangement = Arrangement.spacedBy(Spacing.s12)) {
                    estimate.ingredients.forEach { ingredient ->
                        IngredientCard(
                            ingredient = ingredient,
                            onDecrease = showAdjustNotice,
                            onIncrease = showAdjustNotice,
                            onRemove = showAdjustNotice,
                        )
                    }
                }
                // Figma: 14 SemiBold; VoraTextAction is 13 SemiBold (deviation, BASE-03).
                VoraTextAction(
                    text = stringResource(R.string.food_confirmation_add_ingredient),
                    onClick = showAdjustNotice,
                )
            } else {
                MealFormCard(
                    draft = uiState.draft,
                    errors = uiState.errors,
                    onNameChange = onNameChange,
                    onDateClick = { showDatePicker = true },
                    onTypeSelect = onTypeSelect,
                    onCaloriesChange = onCaloriesChange,
                    onProteinChange = onProteinChange,
                    onCarbsChange = onCarbsChange,
                    onFatChange = onFatChange,
                )
            }
            MealTotalsPanel(totals = totals)
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.s10)) {
                if (uiState.saveFailed) {
                    Text(
                        text = stringResource(R.string.food_confirmation_save_error),
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                        color = MaterialTheme.colorScheme.error,
                    )
                }
                // Figma: 15 SemiBold; VoraPrimaryButton Large is 15 Bold (deviation, BASE-03).
                // While saving, the ViewModel ignores further taps.
                VoraPrimaryButton(
                    text = stringResource(
                        R.string.food_confirmation_add_to,
                        stringResource(uiState.draft.type.labelRes()),
                    ),
                    onClick = onAddToMeal,
                    size = VoraButtonSize.Large,
                    modifier = Modifier.fillMaxWidth(),
                )
                SaveWithoutLoggingButton(
                    onClick = { notice = R.string.food_confirmation_demo_save_without_logging },
                )
            }
        }
    }

    notice?.let { message ->
        AlertDialog(
            onDismissRequest = { notice = null },
            title = { Text(stringResource(R.string.food_confirmation_demo_title)) },
            text = { Text(stringResource(message)) },
            confirmButton = {
                TextButton(onClick = { notice = null }) {
                    Text(stringResource(R.string.food_confirmation_demo_ok))
                }
            },
        )
    }

    if (showDatePicker) {
        MealDatePickerDialog(
            selectedDate = uiState.draft.date,
            onConfirm = { date ->
                onDateSelect(date)
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false },
        )
    }
}

@Composable
private fun FoodConfirmationHeader(onBack: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Spacing.s8),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Same chevron as the Exercise Details header (Figma asset c775b).
        Icon(
            painter = painterResource(R.drawable.ic_exercise_details_back),
            contentDescription = stringResource(R.string.food_confirmation_back_description),
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .size(20.dp)
                .clickable(role = Role.Button, onClick = onBack),
        )
        // Figma: 20 SemiBold; titleLarge is 20 Bold.
        Text(
            text = stringResource(R.string.food_confirmation_title),
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onBackground,
        )
    }
}

@Composable
private fun IngredientCard(
    ingredient: EstimatedIngredient,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit,
    onRemove: () -> Unit,
) {
    // Figma: radius 16, padding 12, gap 10 (VoraCard is padding 16, gap 12).
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outline, MaterialTheme.shapes.medium)
            .padding(Spacing.s12),
        verticalArrangement = Arrangement.spacedBy(Spacing.s10),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.s8),
        ) {
            Text(
                text = ingredient.name,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f),
            )
            // Figma: 14 Bold, no token.
            Text(
                text = stringResource(R.string.food_confirmation_kcal, ingredient.calories),
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(Spacing.s8),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                QuantityButton(
                    sign = stringResource(R.string.food_confirmation_decrease),
                    onClick = onDecrease,
                )
                Text(
                    text = stringResource(R.string.food_confirmation_grams, ingredient.grams),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                QuantityButton(
                    sign = stringResource(R.string.food_confirmation_increase),
                    onClick = onIncrease,
                )
            }
            // Figma: 12 Regular, no token (bodySmall is 11).
            Text(
                text = stringResource(R.string.food_confirmation_remove),
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.clickable(role = Role.Button, onClick = onRemove),
            )
        }
    }
}

@Composable
private fun QuantityButton(sign: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(MaterialTheme.shapes.extraSmall)
            .background(MaterialTheme.colorScheme.background)
            .clickable(role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = sign,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

/** RF03 form. No Figma frame (D3): same card as the ingredients (white, border, radius 16, padding 12). */
@Composable
private fun MealFormCard(
    draft: MealFormDraft,
    errors: MealFormErrors,
    onNameChange: (String) -> Unit,
    onDateClick: () -> Unit,
    onTypeSelect: (MealType) -> Unit,
    onCaloriesChange: (String) -> Unit,
    onProteinChange: (String) -> Unit,
    onCarbsChange: (String) -> Unit,
    onFatChange: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outline, MaterialTheme.shapes.medium)
            .padding(Spacing.s12),
        verticalArrangement = Arrangement.spacedBy(Spacing.s16),
    ) {
        // VoraTextField has no keyboardOptions nor error state yet (deviation, BASE-03): numbers use
        // the text keyboard and errors are shown below each field.
        FormField(errors.name) {
            VoraTextField(
                label = stringResource(R.string.food_confirmation_name_label),
                value = draft.name,
                onValueChange = onNameChange,
            )
        }
        MealDateField(date = draft.date, onClick = onDateClick)
        MealTypeSelector(selected = draft.type, onSelect = onTypeSelect)
        FormField(errors.calories) {
            VoraTextField(
                label = stringResource(R.string.food_confirmation_calories_label),
                value = draft.calories,
                onValueChange = onCaloriesChange,
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(Spacing.s12)) {
            FormField(errors.protein, Modifier.weight(1f)) {
                VoraTextField(
                    label = stringResource(R.string.food_confirmation_protein_label),
                    value = draft.protein,
                    onValueChange = onProteinChange,
                )
            }
            FormField(errors.carbs, Modifier.weight(1f)) {
                VoraTextField(
                    label = stringResource(R.string.food_confirmation_carbs_label),
                    value = draft.carbs,
                    onValueChange = onCarbsChange,
                )
            }
            FormField(errors.fat, Modifier.weight(1f)) {
                VoraTextField(
                    label = stringResource(R.string.food_confirmation_fat_label),
                    value = draft.fat,
                    onValueChange = onFatChange,
                )
            }
        }
    }
}

/** A field with its validation message below (D11: 12 Regular, error red). */
@Composable
private fun FormField(
    error: MealFieldError?,
    modifier: Modifier = Modifier,
    field: @Composable () -> Unit,
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(Spacing.s4)) {
        field()
        if (error != null) {
            Text(
                text = stringResource(error.message),
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                color = MaterialTheme.colorScheme.error,
            )
        }
    }
}

/** Read-only field with the look of VoraTextField; tapping it opens the date picker. */
@Composable
private fun MealDateField(date: LocalDate, onClick: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.s6)) {
        Text(
            text = stringResource(R.string.food_confirmation_date_label).uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.voraColors.textSecondary,
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.small)
                .background(MaterialTheme.colorScheme.surface)
                .border(1.dp, MaterialTheme.colorScheme.outline, MaterialTheme.shapes.small)
                .clickable(
                    onClickLabel = stringResource(R.string.food_confirmation_date_description),
                    role = Role.Button,
                    onClick = onClick,
                )
                .padding(horizontal = Spacing.s16, vertical = Spacing.s12),
        ) {
            Text(
                text = formDateFormatter.format(date),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

/** Breakfast / Lunch / Snack / Dinner (D8). No component nor frame: private pills that wrap if needed. */
@Composable
private fun MealTypeSelector(selected: MealType, onSelect: (MealType) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.s6)) {
        Text(
            text = stringResource(R.string.food_confirmation_type_label).uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.voraColors.textSecondary,
        )
        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .selectableGroup(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.s8),
            verticalArrangement = Arrangement.spacedBy(Spacing.s8),
        ) {
            MealType.entries.forEach { type ->
                MealTypeChip(
                    label = stringResource(type.labelRes()),
                    selected = type == selected,
                    onClick = { onSelect(type) },
                )
            }
        }
    }
}

@Composable
private fun MealTypeChip(label: String, selected: Boolean, onClick: () -> Unit) {
    val container = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.background
    val content = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
    Box(
        modifier = Modifier
            .clip(VoraPillShape)
            .background(container)
            .border(1.dp, if (selected) container else MaterialTheme.colorScheme.outline, VoraPillShape)
            .selectable(selected = selected, role = Role.RadioButton, onClick = onClick)
            .padding(horizontal = Spacing.s14, vertical = Spacing.s8),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = label, style = MaterialTheme.typography.labelMedium, color = content)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MealDatePickerDialog(
    selectedDate: LocalDate,
    onConfirm: (LocalDate) -> Unit,
    onDismiss: () -> Unit,
) {
    // D9: no future dates. The picker works in UTC milliseconds (see NutritionFormat.kt).
    val todayMillis = remember { LocalDate.now().toUtcMillis() }
    val selectableDates = remember(todayMillis) {
        object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean = utcTimeMillis <= todayMillis
            override fun isSelectableYear(year: Int): Boolean = year <= utcMillisToLocalDate(todayMillis).year
        }
    }
    val state = rememberDatePickerState(
        initialSelectedDateMillis = selectedDate.toUtcMillis(),
        selectableDates = selectableDates,
    )
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = { state.selectedDateMillis?.let { onConfirm(utcMillisToLocalDate(it)) } ?: onDismiss() },
            ) {
                Text(stringResource(R.string.food_confirmation_date_confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.food_confirmation_date_cancel))
            }
        },
    ) {
        DatePicker(state = state)
    }
}

@Composable
private fun MealTotalsPanel(totals: MealTotals) {
    // Figma: radius 20, padding 16, gap 12 (VoraPanel is padding 20, gap 16).
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
            .background(MaterialTheme.colorScheme.primary)
            .padding(Spacing.s16),
        verticalArrangement = Arrangement.spacedBy(Spacing.s12),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.food_confirmation_totals),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.secondary,
            )
            // Figma: 18 Bold, no token (titleMedium is 18 SemiBold).
            Text(
                text = stringResource(R.string.food_confirmation_kcal, totals.calories),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onPrimary,
            )
        }
        HorizontalDivider(
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.12f),
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            MacroTotal(R.string.food_confirmation_protein, totals.proteinGrams)
            MacroTotal(R.string.food_confirmation_carbs, totals.carbsGrams)
            MacroTotal(R.string.food_confirmation_fat, totals.fatGrams)
        }
    }
}

@Composable
private fun MacroTotal(@StringRes label: Int, grams: Double) {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.s2)) {
        Text(
            text = stringResource(label),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.secondary,
        )
        // Whole grams as in Figma ("38g"); typed decimals keep one ("12.5g").
        Text(
            text = stringResource(R.string.food_confirmation_macro_grams, formatGrams(grams)),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onPrimary,
        )
    }
}

/** White pill with border; no shared component has this look. */
@Composable
private fun SaveWithoutLoggingButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(VoraPillShape)
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outline, VoraPillShape)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(vertical = Spacing.s14),
        contentAlignment = Alignment.Center,
    ) {
        // Figma: 15 SemiBold; titleSmall is 15 Bold.
        Text(
            text = stringResource(R.string.food_confirmation_save_without_logging),
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
private fun FoodConfirmationPreviewContent(uiState: FoodConfirmationUiState) {
    ExamenTP3Theme {
        FoodConfirmationScreen(
            uiState = uiState,
            onBack = {},
            onNameChange = {},
            onDateSelect = {},
            onTypeSelect = {},
            onCaloriesChange = {},
            onProteinChange = {},
            onCarbsChange = {},
            onFatChange = {},
            onAddToMeal = {},
        )
    }
}

private val previewDate = LocalDate.of(2026, 10, 23)

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun FoodConfirmationEstimatePreview() {
    FoodConfirmationPreviewContent(foodConfirmationInitialState(FoodConfirmationMode.Estimate, previewDate))
}

@Preview(showBackground = true, widthDp = 360, heightDp = 640, fontScale = 1.3f)
@Composable
private fun FoodConfirmationEstimateSmallPreview() {
    FoodConfirmationPreviewContent(foodConfirmationInitialState(FoodConfirmationMode.Estimate, previewDate))
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun FoodConfirmationManualEmptyPreview() {
    FoodConfirmationPreviewContent(foodConfirmationInitialState(FoodConfirmationMode.Manual, previewDate))
}

@Preview(showBackground = true, widthDp = 360, heightDp = 900)
@Composable
private fun FoodConfirmationManualErrorsPreview() {
    val initial = foodConfirmationInitialState(FoodConfirmationMode.Manual, previewDate)
    FoodConfirmationPreviewContent(
        initial.copy(
            draft = initial.draft.copy(calories = "12.5", protein = "-3", carbs = "abc", fat = "4,5", type = MealType.DINNER),
            errors = MealFormErrors(
                name = MealFieldError.NameRequired,
                calories = MealFieldError.NotAWholeNumber,
                protein = MealFieldError.Negative,
                carbs = MealFieldError.NotANumber,
            ),
            saveFailed = true,
        ),
    )
}
