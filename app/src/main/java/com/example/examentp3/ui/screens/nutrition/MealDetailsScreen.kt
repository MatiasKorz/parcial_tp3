package com.example.examentp3.ui.screens.nutrition

import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.examentp3.R
import com.example.examentp3.model.Meal
import com.example.examentp3.model.MealType
import com.example.examentp3.ui.components.VoraInfoMessage
import com.example.examentp3.ui.theme.ExamenTP3Theme
import com.example.examentp3.ui.theme.Spacing
import com.example.examentp3.ui.theme.VoraPillShape
import com.example.examentp3.ui.theme.voraColors
import com.example.examentp3.viewmodel.MealDetailsUiState
import java.time.LocalDate
import java.util.Locale

// Sizes measured in the Figma frame that have no token in the theme.
private val BackIconSize = 20.dp
private val BackTouchSize = 40.dp
private val RingSize = 64.dp
private val CaloriesCardShape = RoundedCornerShape(24.dp)

// The back touch area is wider than the 20 dp chevron: shift it so the chevron stays on the margin.
private val BackTouchInset = (BackTouchSize - BackIconSize) / 2

/** Dialogs of the screen. An enum fits in the Bundle, so rememberSaveable keeps it across rotation. */
private enum class MealDetailsDialog { None, Delete, EditDemo, DuplicateDemo }

/**
 * Nutrition / 08 — Meal Details. Shows a meal saved in Room and deletes it after a confirmation.
 * The ring, the tip and Edit/Duplicate are demonstration content (documented in the README).
 */
@Composable
fun MealDetailsScreen(
    uiState: MealDetailsUiState,
    onBack: () -> Unit,
    onDeleteConfirmed: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var dialog by rememberSaveable { mutableStateOf(MealDetailsDialog.None) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            // The bottom bar belongs to the Scaffold in AppNavigation, which already pads the content above it.
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(start = Spacing.screenMargin, end = Spacing.screenMargin, top = Spacing.s12, bottom = Spacing.s24),
        verticalArrangement = Arrangement.spacedBy(Spacing.s20),
    ) {
        when (uiState) {
            is MealDetailsUiState.Content -> MealDetailsContent(
                meal = uiState.meal,
                deleteFailed = uiState.deleteFailed,
                onBack = onBack,
                onEdit = { dialog = MealDetailsDialog.EditDemo },
                onDuplicate = { dialog = MealDetailsDialog.DuplicateDemo },
                onDelete = { dialog = MealDetailsDialog.Delete },
            )
            MealDetailsUiState.NotFound -> {
                MealDetailsHeader(title = null, date = null, onBack = onBack)
                Text(
                    text = stringResource(R.string.meal_details_not_found),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.voraColors.textSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(top = Spacing.s40),
                )
            }
            // Loading is short (one Room read) and Deleted is left right away: only the background.
            MealDetailsUiState.Loading, MealDetailsUiState.Deleted -> Unit
        }
    }

    when (dialog) {
        MealDetailsDialog.None -> Unit
        MealDetailsDialog.Delete -> {
            val meal = (uiState as? MealDetailsUiState.Content)?.meal
            if (meal != null) {
                DeleteMealDialog(
                    mealName = meal.name,
                    onConfirm = {
                        dialog = MealDetailsDialog.None
                        onDeleteConfirmed()
                    },
                    onDismiss = { dialog = MealDetailsDialog.None },
                )
            }
        }
        MealDetailsDialog.EditDemo -> DemoDialog(R.string.meal_details_edit_demo) { dialog = MealDetailsDialog.None }
        MealDetailsDialog.DuplicateDemo ->
            DemoDialog(R.string.meal_details_duplicate_demo) { dialog = MealDetailsDialog.None }
    }
}

@Composable
private fun MealDetailsContent(
    meal: Meal,
    deleteFailed: Boolean,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onDuplicate: () -> Unit,
    onDelete: () -> Unit,
) {
    // RF03 has no time of day: the Figma "13:12" slot shows the date of the meal instead.
    MealDetailsHeader(title = stringResource(meal.type.labelRes()), date = formatMealDate(meal.date), onBack = onBack)
    CaloriesCard(meal)
    // Demonstration copy from Figma: there is no daily protein target in the app yet.
    VoraInfoMessage(
        text = stringResource(R.string.meal_details_tip),
        icon = ImageVector.vectorResource(R.drawable.ic_meal_details_sparkles),
    )
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.s12)) {
        Text(
            text = stringResource(R.string.meal_details_ingredients),
            // Figma: 16 SemiBold; titleMedium is 18 SemiBold.
            style = MaterialTheme.typography.titleMedium.copy(fontSize = 16.sp),
        )
        // A saved meal has no ingredients (RF03): the whole meal is the only row, without grams.
        MealDetailsItemRow(name = meal.name, calories = meal.calories)
    }
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.s10)) {
        MealDetailsActionButton(stringResource(R.string.meal_details_edit), onEdit)
        MealDetailsActionButton(stringResource(R.string.meal_details_duplicate), onDuplicate)
        MealDetailsActionButton(stringResource(R.string.meal_details_delete), onDelete)
        if (deleteFailed) {
            Text(
                text = stringResource(R.string.meal_details_delete_error),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun MealDetailsHeader(title: String?, date: String?, onBack: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .offset(x = -BackTouchInset)
                    .size(BackTouchSize)
                    .clip(CircleShape)
                    .clickable(role = Role.Button, onClick = onBack),
                contentAlignment = Alignment.Center,
            ) {
                // Same chevron as Exercise Details (identical Figma vector).
                Icon(
                    painter = painterResource(R.drawable.ic_exercise_details_back),
                    contentDescription = stringResource(R.string.meal_details_back),
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(BackIconSize),
                )
            }
            if (title != null) {
                Text(
                    text = title,
                    // Figma: 20 SemiBold; titleLarge is 20 Bold.
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
                    // 8 dp after the chevron, compensating the shifted touch area.
                    modifier = Modifier.offset(x = Spacing.s8 - BackTouchInset * 2),
                )
            }
        }
        if (date != null) {
            Text(
                text = date,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.voraColors.textMuted,
            )
        }
    }
}

@Composable
private fun CaloriesCard(meal: Meal) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(CaloriesCardShape)
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outline, CaloriesCardShape)
            .padding(Spacing.s20),
        verticalArrangement = Arrangement.spacedBy(Spacing.s16),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.s12),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(Spacing.s4)) {
                Text(
                    text = stringResource(R.string.meal_details_total_calories).uppercase(Locale.ENGLISH),
                    // Figma: 12 SemiBold; labelMedium is 13 SemiBold.
                    style = MaterialTheme.typography.labelMedium.copy(fontSize = 12.sp),
                    color = MaterialTheme.voraColors.textSecondary,
                )
                Text(
                    text = stringResource(R.string.meal_details_kcal, formatCalories(meal.calories)),
                    // Figma: 32 Bold; no token that large.
                    style = MaterialTheme.typography.headlineSmall.copy(fontSize = 32.sp, fontWeight = FontWeight.Bold),
                )
            }
            // Decorative static ring from Figma (75 % arc): there is no per-meal target to show.
            Image(
                painter = painterResource(R.drawable.ic_meal_details_ring),
                contentDescription = null,
                modifier = Modifier.size(RingSize),
            )
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.outline)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            MacroColumn(R.string.meal_details_protein, meal.proteinGrams)
            MacroColumn(R.string.meal_details_carbs, meal.carbsGrams)
            MacroColumn(R.string.meal_details_fat, meal.fatGrams)
        }
    }
}

@Composable
private fun MacroColumn(@StringRes label: Int, grams: Double) {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.s4)) {
        Text(
            text = stringResource(label),
            // Figma: 11 SemiBold; labelSmall is 10 SemiBold.
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
            color = MaterialTheme.voraColors.textSecondary,
        )
        Text(
            text = stringResource(R.string.meal_details_grams, formatGrams(grams)),
            // Figma: 16 Bold; titleSmall is 15 Bold.
            style = MaterialTheme.typography.titleSmall.copy(fontSize = 16.sp),
        )
    }
}

@Composable
private fun MealDetailsItemRow(name: String, calories: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.small)
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outline, MaterialTheme.shapes.small)
            .padding(Spacing.s12),
        horizontalArrangement = Arrangement.spacedBy(Spacing.s12),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = name, style = MaterialTheme.typography.labelMedium, modifier = Modifier.weight(1f))
        Text(
            text = stringResource(R.string.meal_details_kcal, formatCalories(calories)),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.voraColors.textSecondary,
        )
    }
}

/** White full-width pill with border (Figma "Edit Meal" / "Duplicate Meal"); no shared component fits. */
@Composable
private fun MealDetailsActionButton(text: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(VoraPillShape)
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outline, VoraPillShape)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = Spacing.s16, vertical = Spacing.s12),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = text, style = MaterialTheme.typography.labelLarge, textAlign = TextAlign.Center)
    }
}

@Composable
private fun DeleteMealDialog(mealName: String, onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.meal_details_delete_title)) },
        text = { Text(stringResource(R.string.meal_details_delete_message, mealName)) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(stringResource(R.string.meal_details_delete_confirm), color = MaterialTheme.colorScheme.error)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.meal_details_delete_cancel)) }
        },
    )
}

@Composable
private fun DemoDialog(@StringRes message: Int, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.meal_details_demo_title)) },
        text = { Text(stringResource(message)) },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.meal_details_demo_ok)) }
        },
    )
}

// Figma values; the meal name is an example because Figma only shows ingredients.
private val PreviewMeal = Meal(
    id = 1,
    name = "Grilled Chicken Breast",
    date = LocalDate.of(2026, 10, 23),
    type = MealType.LUNCH,
    calories = 520,
    proteinGrams = 38.0,
    carbsGrams = 54.0,
    fatGrams = 14.0,
)

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun MealDetailsContentPreview() {
    ExamenTP3Theme {
        MealDetailsScreen(uiState = MealDetailsUiState.Content(PreviewMeal), onBack = {}, onDeleteConfirmed = {})
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 640, fontScale = 1.3f)
@Composable
private fun MealDetailsDeleteFailedCompactPreview() {
    ExamenTP3Theme {
        MealDetailsScreen(
            uiState = MealDetailsUiState.Content(
                PreviewMeal.copy(name = "Grilled chicken bowl with basmati rice and steamed vegetables", proteinGrams = 12.5),
                deleteFailed = true,
            ),
            onBack = {},
            onDeleteConfirmed = {},
        )
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun MealDetailsNotFoundPreview() {
    ExamenTP3Theme {
        MealDetailsScreen(uiState = MealDetailsUiState.NotFound, onBack = {}, onDeleteConfirmed = {})
    }
}
