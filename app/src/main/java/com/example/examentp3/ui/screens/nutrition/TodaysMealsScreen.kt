package com.example.examentp3.ui.screens.nutrition

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.examentp3.R
import com.example.examentp3.model.Meal
import com.example.examentp3.model.MealSection
import com.example.examentp3.model.MealType
import com.example.examentp3.ui.components.VoraButtonSize
import com.example.examentp3.ui.components.VoraCard
import com.example.examentp3.ui.components.VoraPrimaryButton
import com.example.examentp3.ui.theme.ExamenTP3Theme
import com.example.examentp3.ui.theme.Spacing
import com.example.examentp3.ui.theme.VoraPillShape
import com.example.examentp3.ui.theme.voraColors
import com.example.examentp3.viewmodel.TodaysMealsUiState
import com.example.examentp3.viewmodel.todaysMealsUiState
import java.time.LocalDate
import java.util.Locale

// Sizes measured in the Figma frame that have no token in the theme.
private val ChevronSize = 20.dp
private val ChevronTouchSize = 24.dp
private val DashLength = 4.dp
private const val TOTAL_PILL_ALPHA = 0.08f

/**
 * Nutrition / 02 — Today's Meals: the meals saved in Room for one date, grouped by type, with the
 * subtotal of each type and the total of the day (RF04). The arrows and the date open other days
 * (not in Figma, pending REF-05).
 */
@Composable
fun TodaysMealsScreen(
    uiState: TodaysMealsUiState,
    onPreviousDay: () -> Unit,
    onNextDay: () -> Unit,
    onDateSelected: (LocalDate) -> Unit,
    onMealClick: (Long) -> Unit,
    onAddMeal: (MealType) -> Unit,
    modifier: Modifier = Modifier,
) {
    // Ephemeral UI state: the ViewModel does not need to know whether the picker is open.
    var showDatePicker by rememberSaveable { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding(),
        contentPadding = PaddingValues(
            start = Spacing.screenMargin,
            end = Spacing.screenMargin,
            top = Spacing.s12,
            bottom = Spacing.s24,
        ),
        verticalArrangement = Arrangement.spacedBy(Spacing.s16),
    ) {
        item(key = "header") {
            TodaysMealsHeader(
                date = uiState.date,
                // Hidden while Room answers so "0 kcal" does not flash before the real total.
                totalCalories = if (uiState.isLoading) null else uiState.totals.calories,
                onPreviousDay = onPreviousDay,
                onNextDay = onNextDay,
                onDateClick = { showDatePicker = true },
                // Figma: 20 between the header and the sections, 16 between sections.
                modifier = Modifier.padding(bottom = Spacing.s4),
            )
        }
        if (!uiState.isLoading) {
            items(uiState.sections, key = { it.type.name }) { section ->
                if (section.meals.isEmpty()) {
                    MealTypeEmptyCard(type = section.type, onAddMeal = { onAddMeal(section.type) })
                } else {
                    MealSectionCard(section = section, onMealClick = onMealClick)
                }
            }
        }
    }

    if (showDatePicker) {
        TodaysMealsDatePicker(
            date = uiState.date,
            onConfirm = { date ->
                showDatePicker = false
                onDateSelected(date)
            },
            onDismiss = { showDatePicker = false },
        )
    }
}

@Composable
private fun TodaysMealsHeader(
    date: LocalDate,
    totalCalories: Int?,
    onPreviousDay: () -> Unit,
    onNextDay: () -> Unit,
    onDateClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // The date line goes on its own row: with a long date it would not fit next to the pill.
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(Spacing.s2)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            ChevronButton(
                icon = R.drawable.ic_todays_meals_chevron_left,
                contentDescription = stringResource(R.string.todays_meals_previous_day),
                onClick = onPreviousDay,
            )
            Text(
                text = stringResource(
                    R.string.todays_meals_timeline_date,
                    formatTimelineDate(date),
                ).uppercase(Locale.ENGLISH), // Figma: textCase UPPER
                // Figma 11 SemiBold; bodySmall is 11 Regular.
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.voraColors.textSecondary,
                modifier = Modifier
                    .clip(VoraPillShape)
                    .clickable(
                        role = Role.Button,
                        onClickLabel = stringResource(R.string.todays_meals_pick_date),
                        onClick = onDateClick,
                    )
                    .padding(horizontal = Spacing.s4, vertical = Spacing.s2),
            )
            ChevronButton(
                icon = R.drawable.ic_todays_meals_chevron_right,
                contentDescription = stringResource(R.string.todays_meals_next_day),
                onClick = onNextDay,
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.s12),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.todays_meals_title),
                style = MaterialTheme.typography.headlineSmall, // 24 SemiBold, as in Figma
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f),
            )
            if (totalCalories != null) TotalPill(calories = totalCalories)
        }
    }
}

@Composable
private fun ChevronButton(
    @DrawableRes icon: Int,
    contentDescription: String,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(ChevronTouchSize)
            .clip(CircleShape)
            .clickable(role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = contentDescription,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(ChevronSize),
        )
    }
}

/** Total of the day: #123D35 at 8 %, padding 12/6, 13 SemiBold. No shared component has this look. */
@Composable
private fun TotalPill(calories: Int) {
    Text(
        text = stringResource(R.string.todays_meals_kcal, formatCalories(calories)),
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier
            .clip(VoraPillShape)
            .background(MaterialTheme.colorScheme.primary.copy(alpha = TOTAL_PILL_ALPHA))
            .padding(horizontal = Spacing.s12, vertical = Spacing.s6),
    )
}

@Composable
private fun MealSectionCard(
    section: MealSection,
    onMealClick: (Long) -> Unit,
) {
    // Figma uses padding 12 and gap 8 here; VoraCard has 16 / 12. Reused anyway (UI02; BASE-03).
    VoraCard {
        // Figma 14 Bold; labelLarge is 14 SemiBold.
        val headerStyle = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.s8),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(section.type.labelRes()),
                style = headerStyle,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = stringResource(R.string.todays_meals_kcal, formatCalories(section.calories)),
                style = headerStyle,
                color = MaterialTheme.colorScheme.primary,
            )
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.outline)
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.s6)) {
            section.meals.forEach { meal ->
                MealRow(meal = meal, onClick = { onMealClick(meal.id) })
            }
        }
    }
}

/** One saved meal. Room has no grams per meal (RF03), so the grey second line of Figma is omitted. */
@Composable
private fun MealRow(meal: Meal, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(role = Role.Button, onClick = onClick),
        horizontalArrangement = Arrangement.spacedBy(Spacing.s8),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = meal.name,
            // Figma 13 Medium; bodyMedium is 13 Regular.
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = stringResource(R.string.todays_meals_kcal, formatCalories(meal.calories)),
            // Figma 12 SemiBold; labelMedium is 13 SemiBold.
            style = MaterialTheme.typography.labelMedium.copy(fontSize = 12.sp),
            color = MaterialTheme.voraColors.textSecondary,
        )
    }
}

/** Type without meals: dashed border, generalizing Figma's "Dinner is not logged yet". */
@Composable
private fun MealTypeEmptyCard(type: MealType, onAddMeal: () -> Unit) {
    val typeLabel = stringResource(type.labelRes())
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .dashedBorder(color = MaterialTheme.colorScheme.outline, shape = MaterialTheme.shapes.medium)
            .padding(Spacing.s16),
        verticalArrangement = Arrangement.spacedBy(Spacing.s10),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(R.string.todays_meals_not_logged, typeLabel),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.voraColors.textSecondary,
        )
        VoraPrimaryButton(
            text = stringResource(R.string.todays_meals_add, typeLabel),
            onClick = onAddMeal,
            size = VoraButtonSize.Compact,
        )
    }
}

/** 1 dp dashed border (Compose's border() cannot dash), drawn inside the bounds like border(). */
private fun Modifier.dashedBorder(color: Color, shape: CornerBasedShape): Modifier = drawBehind {
    val strokeWidth = 1.dp.toPx()
    val dash = DashLength.toPx()
    drawRoundRect(
        color = color,
        topLeft = Offset(strokeWidth / 2, strokeWidth / 2),
        size = Size(size.width - strokeWidth, size.height - strokeWidth),
        cornerRadius = CornerRadius(shape.topStart.toPx(size, this)),
        style = Stroke(width = strokeWidth, pathEffect = PathEffect.dashPathEffect(floatArrayOf(dash, dash))),
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TodaysMealsDatePicker(
    date: LocalDate,
    onConfirm: (LocalDate) -> Unit,
    onDismiss: () -> Unit,
) {
    // DatePicker works in UTC milliseconds; NutritionFormat converts both ways in UTC.
    val state = rememberDatePickerState(initialSelectedDateMillis = date.toUtcMillis())
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = { onConfirm(state.selectedDateMillis?.let(::utcMillisToLocalDate) ?: date) },
            ) {
                Text(stringResource(R.string.todays_meals_date_picker_ok))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.todays_meals_date_picker_cancel))
            }
        },
    ) {
        DatePicker(state = state)
    }
}

// Preview data: the Figma values (names and kcal; Room has no grams).
private val previewDate: LocalDate = LocalDate.of(2026, 10, 23)

private val previewMeals = listOf(
    previewMeal(1, "Greek Yoghurt (0% Fat)", MealType.BREAKFAST, 150),
    previewMeal(2, "Fresh Blueberries", MealType.BREAKFAST, 60),
    previewMeal(3, "Organic Honey Granola", MealType.BREAKFAST, 210),
    previewMeal(4, "Grilled Chicken Breast", MealType.LUNCH, 280),
    previewMeal(5, "Basmati Rice", MealType.LUNCH, 190),
    previewMeal(6, "Mixed Steamed Vegetables", MealType.LUNCH, 50),
    previewMeal(7, "Whey Protein Shake", MealType.SNACK, 240),
)

private fun previewMeal(id: Long, name: String, type: MealType, calories: Int) = Meal(
    id = id,
    name = name,
    date = previewDate,
    type = type,
    calories = calories,
    proteinGrams = 0.0,
    carbsGrams = 0.0,
    fatGrams = 0.0,
)

@Preview(showBackground = true, heightDp = 818)
@Composable
private fun TodaysMealsScreenPreview() {
    ExamenTP3Theme {
        TodaysMealsScreen(
            uiState = todaysMealsUiState(previewDate, previewMeals, today = previewDate),
            onPreviousDay = {},
            onNextDay = {},
            onDateSelected = {},
            onMealClick = {},
            onAddMeal = {},
        )
    }
}

@Preview(showBackground = true, heightDp = 818)
@Composable
private fun TodaysMealsScreenEmptyPreview() {
    ExamenTP3Theme {
        TodaysMealsScreen(
            uiState = todaysMealsUiState(previewDate, emptyList(), today = previewDate),
            onPreviousDay = {},
            onNextDay = {},
            onDateSelected = {},
            onMealClick = {},
            onAddMeal = {},
        )
    }
}
