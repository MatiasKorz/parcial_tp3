package com.example.examentp3.ui.screens.nutrition

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.examentp3.R
import com.example.examentp3.model.DemoMealEstimate
import com.example.examentp3.model.EstimatedIngredient
import com.example.examentp3.model.MealType
import com.example.examentp3.ui.components.VoraButtonSize
import com.example.examentp3.ui.components.VoraPrimaryButton
import com.example.examentp3.ui.components.VoraTextAction
import com.example.examentp3.ui.theme.ExamenTP3Theme
import com.example.examentp3.ui.theme.Spacing
import com.example.examentp3.ui.theme.VoraPillShape
import com.example.examentp3.viewmodel.FoodConfirmationUiState
import kotlin.math.roundToInt

/**
 * "Nutrition / 07 — Food Confirmation": review of the simulated estimate before logging it.
 * Adjusting ingredients and "Save without logging" only show a demonstration notice (REF-05).
 */
@Composable
fun FoodConfirmationScreen(
    uiState: FoodConfirmationUiState,
    onBack: () -> Unit,
    onAddToMeal: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // Ephemeral UI state: which demonstration notice is open, if any.
    var notice by rememberSaveable { mutableStateOf<Int?>(null) }
    val showAdjustNotice = { notice = R.string.food_confirmation_demo_adjust }
    val estimate = uiState.estimate

    // The bottom bar is shown here: the Scaffold already applies the bottom inset.
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding(),
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
            MealTotalsPanel(
                calories = estimate.calories,
                proteinGrams = estimate.proteinGrams,
                carbsGrams = estimate.carbsGrams,
                fatGrams = estimate.fatGrams,
            )
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.s10)) {
                // Figma: 15 SemiBold; VoraPrimaryButton Large is 15 Bold (deviation, BASE-03).
                VoraPrimaryButton(
                    text = stringResource(
                        R.string.food_confirmation_add_to,
                        stringResource(uiState.mealType.labelRes()),
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

@Composable
private fun MealTotalsPanel(
    calories: Int,
    proteinGrams: Double,
    carbsGrams: Double,
    fatGrams: Double,
) {
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
                text = stringResource(R.string.food_confirmation_kcal, calories),
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
            MacroTotal(R.string.food_confirmation_protein, proteinGrams)
            MacroTotal(R.string.food_confirmation_carbs, carbsGrams)
            MacroTotal(R.string.food_confirmation_fat, fatGrams)
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
        // The Figma shows whole grams; NUC-04 may need decimals for typed values.
        Text(
            text = stringResource(R.string.food_confirmation_grams, grams.roundToInt()),
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

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun FoodConfirmationScreenPreview() {
    ExamenTP3Theme {
        FoodConfirmationScreen(
            uiState = FoodConfirmationUiState(estimate = DemoMealEstimate),
            onBack = {},
            onAddToMeal = {},
        )
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 640, fontScale = 1.3f)
@Composable
private fun FoodConfirmationScreenSmallPreview() {
    ExamenTP3Theme {
        FoodConfirmationScreen(
            uiState = FoodConfirmationUiState(estimate = DemoMealEstimate),
            onBack = {},
            onAddToMeal = {},
        )
    }
}
