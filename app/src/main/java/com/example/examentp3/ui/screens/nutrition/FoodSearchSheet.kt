package com.example.examentp3.ui.screens.nutrition

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.examentp3.R
import com.example.examentp3.model.Food
import com.example.examentp3.ui.components.VoraEmptyState
import com.example.examentp3.ui.components.VoraErrorBanner
import com.example.examentp3.ui.components.VoraErrorState
import com.example.examentp3.ui.components.VoraLoadingState
import com.example.examentp3.ui.components.VoraTextAction
import com.example.examentp3.ui.components.VoraTextField
import com.example.examentp3.ui.theme.ExamenTP3Theme
import com.example.examentp3.ui.theme.Spacing
import com.example.examentp3.ui.theme.voraColors
import com.example.examentp3.viewmodel.FoodSearchUiState
import com.example.examentp3.viewmodel.UiError
import kotlin.math.roundToInt

/**
 * Food search of EXT-05, opened from "+ Add Ingredient" of Food Confirmation. Figma has no frame for
 * it, so it reuses the field, card and state views of the design system.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun FoodSearchSheet(
    uiState: FoodSearchUiState,
    onQueryChange: (String) -> Unit,
    onFoodSelect: (Food) -> Unit,
    onLoadMore: () -> Unit,
    onRetry: () -> Unit,
    onDismiss: () -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.background,
    ) {
        FoodSearchContent(uiState, onQueryChange, onFoodSelect, onLoadMore, onRetry)
    }
}

@Composable
private fun FoodSearchContent(
    uiState: FoodSearchUiState,
    onQueryChange: (String) -> Unit,
    onFoodSelect: (Food) -> Unit,
    onLoadMore: () -> Unit,
    onRetry: () -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(start = Spacing.screenMargin, end = Spacing.screenMargin, bottom = Spacing.s24),
        verticalArrangement = Arrangement.spacedBy(Spacing.s12),
    ) {
        item(key = "title") {
            Text(
                text = stringResource(R.string.food_search_title),
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onBackground,
            )
        }
        item(key = "query") {
            VoraTextField(
                label = stringResource(R.string.food_search_label),
                value = uiState.query,
                onValueChange = onQueryChange,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        items(uiState.results, key = { it.id }) { food ->
            FoodResultRow(food = food, onClick = { onFoodSelect(food) })
        }
        // Results already shown stay; loading, the error or "Load more" go after them (RF05).
        when {
            uiState.isLoading -> item(key = "loading") {
                VoraLoadingState(stringResource(R.string.food_search_loading))
            }
            uiState.error != null && uiState.results.isNotEmpty() -> item(key = "error") {
                VoraErrorBanner(stringResource(uiState.error.message), stringResource(R.string.state_retry), onRetry)
            }
            uiState.error != null -> item(key = "error") {
                VoraErrorState(
                    stringResource(R.string.food_search_error_title),
                    stringResource(uiState.error.message),
                    stringResource(R.string.state_retry),
                    onRetry,
                )
            }
            uiState.isEmpty -> item(key = "empty") {
                VoraEmptyState(
                    stringResource(R.string.food_search_empty_title),
                    stringResource(R.string.food_search_empty_message),
                )
            }
            uiState.hasMore -> item(key = "more") {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    VoraTextAction(stringResource(R.string.food_search_load_more), onLoadMore)
                }
            }
        }
    }
}

@Composable
private fun FoodResultRow(food: Food, onClick: () -> Unit) {
    // Same card as the ingredients of Food Confirmation (white, border, radius 16, padding 12).
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outline, MaterialTheme.shapes.medium)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(Spacing.s12),
        horizontalArrangement = Arrangement.spacedBy(Spacing.s8),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(Spacing.s2)) {
            Text(food.name, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurface)
            Text(
                text = stringResource(
                    R.string.food_search_macros,
                    formatGrams(food.proteinPer100g),
                    formatGrams(food.carbsPer100g),
                    formatGrams(food.fatPer100g),
                ),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.voraColors.textSecondary,
            )
        }
        Text(
            text = stringResource(R.string.food_search_kcal_per_100g, food.kcalPer100g.roundToInt()),
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

private val PreviewFoods = listOf(
    Food("f01", "Grilled chicken breast", 165.0, 31.0, 0.0, 3.6),
    Food("f02", "Basmati rice", 130.0, 2.7, 28.0, 0.3),
)

@Composable
private fun FoodSearchPreviewContent(uiState: FoodSearchUiState) {
    ExamenTP3Theme {
        Column(Modifier.background(MaterialTheme.colorScheme.background).padding(top = Spacing.s16)) {
            FoodSearchContent(uiState, {}, {}, {}, {})
        }
    }
}

@Preview(showBackground = true, widthDp = 390)
@Composable
private fun FoodSearchResultsPreview() =
    FoodSearchPreviewContent(FoodSearchUiState(results = PreviewFoods, page = 1, hasMore = true))

@Preview(showBackground = true, widthDp = 390)
@Composable
private fun FoodSearchEmptyPreview() = FoodSearchPreviewContent(FoodSearchUiState(query = "pizza", page = 1))

@Preview(showBackground = true, widthDp = 390)
@Composable
private fun FoodSearchErrorPreview() = FoodSearchPreviewContent(FoodSearchUiState(error = UiError.Server))
