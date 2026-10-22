package com.example.examentp3.ui.screens.nutrition

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.examentp3.R
import com.example.examentp3.model.Meal
import com.example.examentp3.model.MealType
import com.example.examentp3.ui.components.VoraPrimaryButton
import com.example.examentp3.ui.theme.ExamenTP3Theme
import com.example.examentp3.ui.theme.Spacing
import com.example.examentp3.ui.theme.voraColors
import com.example.examentp3.viewmodel.MealRoomCheckUiState
import java.time.LocalDate

/** Provisional Room check (DAT-02); NUC-04 and NUC-05 replace it. */
@Composable
fun MealRoomCheckSection(
    uiState: MealRoomCheckUiState,
    onInsertClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // Plain Column: it lives inside the placeholder's scrolling Column, where a lazy list would crash.
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(Spacing.s8),
    ) {
        VoraPrimaryButton(text = stringResource(R.string.nutrition_room_check_insert), onClick = onInsertClick)
        Text(
            text = stringResource(R.string.nutrition_room_check_count, uiState.meals.size),
            style = MaterialTheme.typography.bodyMedium,
        )
        if (uiState.saveFailed) {
            Text(
                text = stringResource(R.string.nutrition_room_check_error),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error,
            )
        }
        if (uiState.meals.isEmpty()) {
            Text(
                text = stringResource(R.string.nutrition_room_check_empty),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.voraColors.textSecondary,
            )
        }
        uiState.meals.forEach { meal ->
            Text(
                text = "${meal.name} · ${meal.date} · ${meal.calories} kcal",
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MealRoomCheckSectionEmptyPreview() {
    ExamenTP3Theme {
        MealRoomCheckSection(
            uiState = MealRoomCheckUiState(),
            onInsertClick = {},
            modifier = Modifier.padding(Spacing.s20),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun MealRoomCheckSectionWithDataPreview() {
    val meal = Meal(
        id = 1,
        name = "Chicken · rice · vegetables",
        date = LocalDate.of(2026, 10, 21),
        type = MealType.LUNCH,
        calories = 520,
        proteinGrams = 38.0,
        carbsGrams = 54.0,
        fatGrams = 14.0,
    )
    ExamenTP3Theme {
        MealRoomCheckSection(
            uiState = MealRoomCheckUiState(meals = listOf(meal, meal.copy(id = 2)), saveFailed = true),
            onInsertClick = {},
            modifier = Modifier.padding(Spacing.s20),
        )
    }
}
