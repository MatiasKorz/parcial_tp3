package com.example.examentp3.ui.screens.nutrition

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layout
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.examentp3.R
import com.example.examentp3.model.DemoMealEstimate
import com.example.examentp3.model.MealEstimate
import com.example.examentp3.ui.components.VoraButtonSize
import com.example.examentp3.ui.components.VoraCard
import com.example.examentp3.ui.components.VoraPrimaryButton
import com.example.examentp3.ui.theme.ExamenTP3Theme
import com.example.examentp3.ui.theme.Spacing
import com.example.examentp3.ui.theme.VoraPillShape
import com.example.examentp3.ui.theme.voraColors

// Figma meal-photo-container: 350 x 220; tags at (40, 60) and (180, 110).
private val PhotoHeight = 220.dp
private const val FIGMA_PHOTO_WIDTH = 350f

/**
 * "Nutrition / 06 — Photo Analysis". Demonstration screen (SPEC p.10): there is no camera, the photo
 * and the estimate are the Figma sample. "Retake Photo" only shows a demonstration notice (NAV03),
 * so the app never asks for the camera permission (NAV05).
 */
@Composable
fun PhotoAnalysisScreen(
    estimate: MealEstimate,
    onBack: () -> Unit,
    onReviewAndAdd: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // Ephemeral UI state: whether the "Retake Photo" notice is open.
    var showRetakeNotice by rememberSaveable { mutableStateOf(false) }

    // The bottom bar is shown here: the Scaffold already applies and consumes the bottom inset.
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding(),
    ) {
        PhotoAnalysisHeader(
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
            PhotoAnalysisMealPhoto()
            PhotoAnalysisEstimateCard(estimate = estimate)
            // Figma: 12 Regular, no token (bodySmall is 11).
            Text(
                text = stringResource(R.string.photo_analysis_note),
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                color = MaterialTheme.voraColors.textSecondary,
                modifier = Modifier.fillMaxWidth(),
            )
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.s10)) {
                // Figma: 15 SemiBold; VoraPrimaryButton Large is 15 Bold (deviation, BASE-03).
                VoraPrimaryButton(
                    text = stringResource(R.string.photo_analysis_review),
                    onClick = onReviewAndAdd,
                    size = VoraButtonSize.Large,
                    modifier = Modifier.fillMaxWidth(),
                )
                PhotoAnalysisRetakeButton(onClick = { showRetakeNotice = true })
            }
        }
    }

    if (showRetakeNotice) {
        AlertDialog(
            onDismissRequest = { showRetakeNotice = false },
            title = { Text(stringResource(R.string.photo_analysis_demo_title)) },
            text = { Text(stringResource(R.string.photo_analysis_demo_retake)) },
            confirmButton = {
                TextButton(onClick = { showRetakeNotice = false }) {
                    Text(stringResource(R.string.photo_analysis_demo_ok))
                }
            },
        )
    }
}

@Composable
private fun PhotoAnalysisHeader(onBack: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Spacing.s8),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Same chevron as the Exercise Details header (Figma asset c775b).
        Icon(
            painter = painterResource(R.drawable.ic_exercise_details_back),
            contentDescription = stringResource(R.string.photo_analysis_back_description),
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .size(20.dp)
                .clickable(role = Role.Button, onClick = onBack),
        )
        // Figma: 20 SemiBold; titleLarge is 20 Bold.
        Text(
            text = stringResource(R.string.photo_analysis_title),
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onBackground,
        )
    }
}

/** Sample photo exported from the Figma frame, with its two tags placed as drawn. */
@Composable
private fun PhotoAnalysisMealPhoto() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(PhotoHeight)
            .clip(MaterialTheme.shapes.large),
    ) {
        // Decorative: the tags and the estimate card already say what the photo shows.
        Image(
            painter = painterResource(R.drawable.img_photo_analysis_meal),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize(),
        )
        // Tags cross the food in the Figma (chicken tag over the rice); kept as drawn.
        PhotoAnalysisTag(
            text = stringResource(R.string.photo_analysis_tag_chicken),
            modifier = Modifier.photoTagPosition(xFraction = 40f / FIGMA_PHOTO_WIDTH, top = 60.dp),
        )
        PhotoAnalysisTag(
            text = stringResource(R.string.photo_analysis_tag_rice),
            modifier = Modifier.photoTagPosition(xFraction = 180f / FIGMA_PHOTO_WIDTH, top = 110.dp),
        )
    }
}

/**
 * Places a tag at the Figma top offset and at a horizontal position proportional to the photo width,
 * so it stays inside the photo on narrow screens (UI03). On a 350 dp photo it matches the Figma.
 */
private fun Modifier.photoTagPosition(xFraction: Float, top: Dp): Modifier =
    layout { measurable, constraints ->
        val placeable = measurable.measure(constraints.copy(minWidth = 0, minHeight = 0))
        val width = constraints.maxWidth
        val height = constraints.maxHeight
        val x = (width * xFraction).toInt().coerceAtMost(width - placeable.width).coerceAtLeast(0)
        val y = top.roundToPx().coerceAtMost(height - placeable.height).coerceAtLeast(0)
        layout(width, height) { placeable.place(x, y) }
    }

@Composable
private fun PhotoAnalysisTag(text: String, modifier: Modifier = Modifier) {
    // Figma: radius 6, no shape token (deviation, BASE-03). Text 10 SemiBold = labelSmall.
    Box(modifier = modifier) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(6.dp))
                .padding(horizontal = Spacing.s8, vertical = Spacing.s4),
        )
    }
}

/** "VORA ESTIMATE" card: VoraCard matches the Figma (radius 16, padding 16, gap 12). */
@Composable
private fun PhotoAnalysisEstimateCard(estimate: MealEstimate) {
    VoraCard {
        // FlowRow: one line as in Figma; wraps instead of cutting text on small screens (UI03).
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalArrangement = Arrangement.spacedBy(Spacing.s4),
            itemVerticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.photo_analysis_estimate_title),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.voraColors.textSecondary,
            )
            Text(
                text = stringResource(R.string.photo_analysis_estimate_hint),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.voraColors.textMuted,
            )
        }
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalArrangement = Arrangement.spacedBy(Spacing.s8),
            itemVerticalAlignment = Alignment.CenterVertically,
        ) {
            // Figma: 28 Bold, no token (headlineSmall is 24 SemiBold).
            Text(
                text = stringResource(R.string.photo_analysis_kcal, estimate.calories),
                style = MaterialTheme.typography.headlineSmall.copy(fontSize = 28.sp, fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.s8)) {
                PhotoAnalysisMacroChip(
                    text = stringResource(R.string.photo_analysis_protein, formatGrams(estimate.proteinGrams)),
                    container = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                )
                PhotoAnalysisMacroChip(
                    text = stringResource(R.string.photo_analysis_carbs, formatGrams(estimate.carbsGrams)),
                    container = MaterialTheme.voraColors.mintSoft,
                )
                PhotoAnalysisMacroChip(
                    text = stringResource(R.string.photo_analysis_fat, formatGrams(estimate.fatGrams)),
                    container = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.12f),
                )
            }
        }
    }
}

@Composable
private fun PhotoAnalysisMacroChip(text: String, container: Color) {
    // Figma: radius 4 and 11 SemiBold, no tokens; green 8 % and lime 12 % have no color token either.
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
        color = MaterialTheme.colorScheme.primary,
        maxLines = 1,
        modifier = Modifier
            .background(container, RoundedCornerShape(4.dp))
            .padding(horizontal = Spacing.s8, vertical = Spacing.s4),
    )
}

/** White pill with border, same look as "Save without logging" in 5.7; no shared component yet. */
@Composable
private fun PhotoAnalysisRetakeButton(onClick: () -> Unit) {
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
            text = stringResource(R.string.photo_analysis_retake),
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun PhotoAnalysisScreenPreview() {
    ExamenTP3Theme {
        PhotoAnalysisScreen(estimate = DemoMealEstimate, onBack = {}, onReviewAndAdd = {})
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 640, fontScale = 1.3f)
@Composable
private fun PhotoAnalysisScreenCompactPreview() {
    ExamenTP3Theme {
        PhotoAnalysisScreen(estimate = DemoMealEstimate, onBack = {}, onReviewAndAdd = {})
    }
}
