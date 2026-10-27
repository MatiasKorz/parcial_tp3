package com.example.examentp3.ui.screens.nutrition

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.runtime.key
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.examentp3.R
import com.example.examentp3.model.Meal
import com.example.examentp3.model.MealType
import com.example.examentp3.ui.components.VoraCard
import com.example.examentp3.ui.components.VoraInfoMessage
import com.example.examentp3.ui.components.VoraMealRow
import com.example.examentp3.ui.components.VoraPanel
import com.example.examentp3.ui.components.VoraPrimaryButton
import com.example.examentp3.ui.components.VoraProgressRing
import com.example.examentp3.ui.components.VoraTextAction
import com.example.examentp3.ui.theme.ExamenTP3Theme
import com.example.examentp3.ui.theme.Spacing
import com.example.examentp3.ui.theme.VoraPillShape
import com.example.examentp3.ui.theme.voraColors
import com.example.examentp3.viewmodel.DashboardMealRow
import com.example.examentp3.viewmodel.NutritionDashboardUiState
import com.example.examentp3.viewmodel.nutritionDashboardUiState
import com.example.examentp3.viewmodel.progressFraction
import java.time.LocalDate
import java.util.Locale

// Sizes measured in the Figma frame (Nutrition / 01) that have no token in the theme.
private val CalendarButtonSize = 40.dp
private val CalendarIconSize = 20.dp
private val RingStrokeWidth = 5.4.dp // 72 ring, inner radius 30.6
private val MacroBarHeight = 4.dp
private val MacroBarShape = RoundedCornerShape(2.dp)
private val OrbSize = 28.dp

/**
 * Nutrition dashboard: the daily total and the meals of today come from Room (NUC-05); the goals and
 * the tip are sample values from Figma. Voice and Photo only navigate (no microphone or camera
 * permission, NAV05); Search opens the food search of EXT-05.
 */
@Composable
fun NutritionDashboardScreen(
    uiState: NutritionDashboardUiState,
    onDateSelected: (LocalDate) -> Unit,
    onViewHistory: () -> Unit,
    onMealClick: (Long) -> Unit,
    onAddMeal: (MealType) -> Unit,
    onVoice: () -> Unit,
    onPhoto: () -> Unit,
    onSearch: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // Ephemeral UI state: the ViewModel does not need to know a dialog is open.
    var showDatePicker by rememberSaveable { mutableStateOf(false) }

    // The bottom bar Scaffold already applies the navigation bar inset, so only the status bar is added.
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .statusBarsPadding()
            .padding(horizontal = Spacing.screenMargin)
            .padding(top = Spacing.s12, bottom = Spacing.s24),
        verticalArrangement = Arrangement.spacedBy(Spacing.s20),
    ) {
        DashboardHeader(date = uiState.date, onCalendarClick = { showDatePicker = true })
        // Room answers in milliseconds: while loading, skip the data blocks instead of flashing "0".
        if (!uiState.isLoading) {
            CalorieProgressCard(uiState = uiState)
            VoraInfoMessage(
                text = stringResource(R.string.nutrition_dashboard_tip),
                icon = ImageVector.vectorResource(R.drawable.ic_nutrition_dashboard_sparkles),
            )
            TodaysMealsSection(
                rows = uiState.mealRows,
                onViewHistory = onViewHistory,
                onMealClick = onMealClick,
                onAddMeal = onAddMeal,
            )
        }
        TellVoraPanel(onVoice = onVoice, onPhoto = onPhoto, onSearch = onSearch)
    }

    if (showDatePicker) {
        DashboardDatePicker(
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
private fun DashboardHeader(date: LocalDate, onCalendarClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(Spacing.s2),
        ) {
            Text(
                // Figma applies textCase UPPER to "Tuesday, 8 September".
                text = formatTimelineDate(date).uppercase(Locale.ENGLISH),
                // Figma: 11 SemiBold; bodySmall is 11 Regular.
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.voraColors.textSecondary,
            )
            Text(
                text = stringResource(R.string.nutrition_dashboard_title),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground,
            )
        }
        Box(
            modifier = Modifier
                .size(CalendarButtonSize)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surface)
                .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
                .clickable(role = Role.Button, onClick = onCalendarClick),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_nutrition_dashboard_calendar),
                contentDescription = stringResource(R.string.nutrition_dashboard_choose_date),
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(CalendarIconSize),
            )
        }
    }
}

@Composable
private fun CalorieProgressCard(uiState: NutritionDashboardUiState) {
    val totals = uiState.totals
    val goals = uiState.goals
    VoraCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(Spacing.s4),
            ) {
                Text(
                    // Figma applies textCase UPPER to "Calories Consumed".
                    text = stringResource(R.string.nutrition_dashboard_calories_consumed).uppercase(Locale.ENGLISH),
                    // Figma: 12 SemiBold; labelMedium is 13 SemiBold.
                    style = MaterialTheme.typography.labelMedium.copy(fontSize = 12.sp),
                    color = MaterialTheme.voraColors.textSecondary,
                )
                Text(
                    text = formatCalories(totals.calories),
                    // Figma: 32 Bold; no token (headlineSmall is 24 SemiBold).
                    style = MaterialTheme.typography.headlineSmall.copy(fontSize = 32.sp, fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = stringResource(R.string.nutrition_dashboard_calories_goal, formatCalories(goals.calories)),
                    // Figma: 12 Regular; bodySmall is 11.
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                    color = MaterialTheme.voraColors.textMuted,
                )
            }
            VoraProgressRing(
                progress = progressFraction(totals.calories.toDouble(), goals.calories),
                centerText = stringResource(R.string.nutrition_dashboard_percent, uiState.caloriesPercent),
                // Figma: the track is the background color and the stroke is 5.4.
                trackColor = MaterialTheme.colorScheme.background,
                strokeWidth = RingStrokeWidth,
            )
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.outline)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.s12),
        ) {
            DashboardMacro(
                label = stringResource(R.string.nutrition_dashboard_protein),
                consumed = totals.proteinGrams,
                goal = goals.proteinGrams,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.weight(1f),
            )
            DashboardMacro(
                label = stringResource(R.string.nutrition_dashboard_carbs),
                consumed = totals.carbsGrams,
                goal = goals.carbsGrams,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.weight(1f),
            )
            DashboardMacro(
                label = stringResource(R.string.nutrition_dashboard_fat),
                consumed = totals.fatGrams,
                goal = goals.fatGrams,
                color = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

/**
 * Macro of the dashboard: label above the value, as in Figma. VoraMacroBar puts label and value on
 * one row, which does not fit in a third of the card (BASE-03).
 */
@Composable
private fun DashboardMacro(
    label: String,
    consumed: Double,
    goal: Int,
    color: Color,
    modifier: Modifier = Modifier,
) {
    val value = stringResource(R.string.nutrition_dashboard_grams, formatGrams(consumed))
    val goalText = stringResource(R.string.nutrition_dashboard_goal_grams, goal.toString())
    val mutedColor = MaterialTheme.voraColors.textMuted
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(Spacing.s6),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.s2)) {
            Text(
                text = label,
                // Figma: 12 SemiBold; labelMedium is 13 SemiBold.
                style = MaterialTheme.typography.labelMedium.copy(fontSize = 12.sp),
                color = MaterialTheme.voraColors.textSecondary,
            )
            Text(
                text = buildAnnotatedString {
                    append(value)
                    // Figma: the goal goes in 10 Bold, muted.
                    withStyle(SpanStyle(fontSize = 10.sp, color = mutedColor)) { append(goalText) }
                },
                // Figma: 12 Bold; labelMedium is 13 SemiBold.
                style = MaterialTheme.typography.labelMedium.copy(fontSize = 12.sp, fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(MacroBarHeight)
                .clip(MacroBarShape)
                .background(MaterialTheme.colorScheme.outline),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progressFraction(consumed, goal))
                    .fillMaxHeight()
                    .clip(MacroBarShape)
                    .background(color),
            )
        }
    }
}

@Composable
private fun TodaysMealsSection(
    rows: List<DashboardMealRow>,
    onViewHistory: () -> Unit,
    onMealClick: (Long) -> Unit,
    onAddMeal: (MealType) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.s10)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.nutrition_dashboard_todays_meals),
                // Figma: 16 SemiBold; titleSmall is 15 Bold.
                style = MaterialTheme.typography.titleSmall.copy(fontSize = 16.sp, fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onBackground,
            )
            VoraTextAction(
                text = stringResource(R.string.nutrition_dashboard_view_history),
                onClick = onViewHistory,
            )
        }
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.s8)) {
            rows.forEach { row ->
                when (row) {
                    is DashboardMealRow.Logged -> key(row.meal.id) {
                        VoraMealRow(
                            title = stringResource(row.meal.type.labelRes()),
                            description = row.meal.name,
                            trailing = stringResource(R.string.nutrition_dashboard_kcal, formatCalories(row.meal.calories)),
                            // Clip first so the ripple follows the rounded row.
                            modifier = Modifier
                                .clip(MaterialTheme.shapes.small)
                                .clickable(role = Role.Button) { onMealClick(row.meal.id) },
                        )
                    }
                    is DashboardMealRow.NotLogged -> key(row.type) {
                        NotLoggedMealRow(type = row.type, onAdd = { onAddMeal(row.type) })
                    }
                }
            }
        }
    }
}

/** Same frame as VoraMealRow, with the "+ Add" button that VoraMealRow cannot hold. */
@Composable
private fun NotLoggedMealRow(type: MealType, onAdd: () -> Unit) {
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
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(Spacing.s2),
        ) {
            Text(text = stringResource(type.labelRes()), style = MaterialTheme.typography.labelMedium)
            Text(
                text = stringResource(R.string.nutrition_dashboard_not_logged),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.voraColors.textMuted,
            )
        }
        VoraPrimaryButton(text = stringResource(R.string.nutrition_dashboard_add), onClick = onAdd)
    }
}

@Composable
private fun TellVoraPanel(onVoice: () -> Unit, onPhoto: () -> Unit, onSearch: () -> Unit) {
    val onPanel = MaterialTheme.colorScheme.onPrimary
    VoraPanel {
        Row(
            horizontalArrangement = Arrangement.spacedBy(Spacing.s8),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painter = painterResource(R.drawable.ic_nutrition_dashboard_orb),
                contentDescription = null, // decorative
                modifier = Modifier.size(OrbSize),
            )
            Text(
                text = stringResource(R.string.nutrition_dashboard_tell_vora),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.secondary,
            )
        }
        Text(
            text = stringResource(R.string.nutrition_dashboard_tell_vora_description),
            style = MaterialTheme.typography.bodyMedium,
            color = onPanel.copy(alpha = 0.6f),
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.s8),
        ) {
            PanelActionButton(
                text = stringResource(R.string.nutrition_dashboard_voice),
                icon = R.drawable.ic_nutrition_dashboard_mic,
                containerColor = MaterialTheme.colorScheme.tertiary,
                contentColor = MaterialTheme.colorScheme.onTertiary,
                onClick = onVoice,
                modifier = Modifier.weight(1f),
            )
            PanelActionButton(
                text = stringResource(R.string.nutrition_dashboard_photo),
                icon = R.drawable.ic_nutrition_dashboard_camera,
                containerColor = onPanel.copy(alpha = 0.12f),
                contentColor = onPanel,
                onClick = onPhoto,
                modifier = Modifier.weight(1f),
            )
            PanelActionButton(
                text = stringResource(R.string.nutrition_dashboard_search),
                icon = R.drawable.ic_nutrition_dashboard_search,
                containerColor = onPanel.copy(alpha = 0.12f),
                contentColor = onPanel,
                onClick = onSearch,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

/**
 * Pill of the "Tell Vora" panel (Figma: 10 vertical padding, 14 icon, 6 gap, 13 SemiBold). Private
 * because VoraLimeButton (20 icon, 16 horizontal padding) does not fit three in a row on a 360 dp
 * screen, and no shared button has the 12 % white fill of "Photo" and "Search" (BASE-03).
 */
@Composable
private fun PanelActionButton(
    text: String,
    @DrawableRes icon: Int,
    containerColor: Color,
    contentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .clip(VoraPillShape)
            .background(containerColor)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = Spacing.s4, vertical = Spacing.s10),
        horizontalArrangement = Arrangement.spacedBy(Spacing.s6, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = null, // decorative: the text already says it
            tint = contentColor,
        )
        Text(text = text, style = MaterialTheme.typography.labelMedium, color = contentColor, maxLines = 1)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DashboardDatePicker(
    date: LocalDate,
    onConfirm: (LocalDate) -> Unit,
    onDismiss: () -> Unit,
) {
    // The Material 3 DatePicker works in UTC milliseconds (see NutritionFormat.kt).
    val state = rememberDatePickerState(initialSelectedDateMillis = date.toUtcMillis())
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = { onConfirm(state.selectedDateMillis?.let(::utcMillisToLocalDate) ?: date) },
            ) {
                Text(stringResource(R.string.nutrition_dashboard_date_picker_ok))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.nutrition_dashboard_date_picker_cancel))
            }
        },
    ) {
        DatePicker(state = state)
    }
}

// Preview data: the Figma values (Dinner not logged yet).
private val previewDate: LocalDate = LocalDate.of(2026, 9, 8)

private fun previewMeal(id: Long, name: String, type: MealType, calories: Int, protein: Double, carbs: Double, fat: Double) =
    Meal(
        id = id,
        name = name,
        date = previewDate,
        type = type,
        calories = calories,
        proteinGrams = protein,
        carbsGrams = carbs,
        fatGrams = fat,
    )

private val previewMeals = listOf(
    previewMeal(1, "Greek yoghurt · berries · granola", MealType.BREAKFAST, 420, 30.0, 52.0, 12.0),
    previewMeal(2, "Chicken · rice · vegetables", MealType.LUNCH, 520, 38.0, 54.0, 14.0),
    previewMeal(3, "Protein shake", MealType.SNACK, 240, 36.0, 46.0, 22.0),
)

@Preview(showBackground = true, widthDp = 390, heightDp = 1013)
@Composable
private fun NutritionDashboardScreenPreview() {
    ExamenTP3Theme {
        NutritionDashboardScreen(
            uiState = nutritionDashboardUiState(previewDate, previewMeals),
            onDateSelected = {},
            onViewHistory = {},
            onMealClick = {},
            onAddMeal = {},
            onVoice = {},
            onPhoto = {},
            onSearch = {},
        )
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
private fun NutritionDashboardScreenEmptyPreview() {
    ExamenTP3Theme {
        NutritionDashboardScreen(
            uiState = nutritionDashboardUiState(previewDate, emptyList()),
            onDateSelected = {},
            onViewHistory = {},
            onMealClick = {},
            onAddMeal = {},
            onVoice = {},
            onPhoto = {},
            onSearch = {},
        )
    }
}
