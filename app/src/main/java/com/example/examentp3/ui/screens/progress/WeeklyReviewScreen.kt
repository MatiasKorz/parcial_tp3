package com.example.examentp3.ui.screens.progress

import android.app.Activity
import android.content.ContextWrapper
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import com.example.examentp3.R
import com.example.examentp3.ui.components.VoraLimeButton
import com.example.examentp3.ui.components.VoraOutlinedButton
import com.example.examentp3.ui.theme.ExamenTP3Theme
import com.example.examentp3.ui.theme.Spacing
import com.example.examentp3.ui.theme.VoraPillShape
import com.example.examentp3.ui.theme.voraColors

// Sizes measured in the Figma frame that have no token in the theme.
private val HeaderHeight = 48.dp
private val BackButtonSize = 36.dp
private val BackIconSize = 16.dp
private val OrbSize = 40.dp
private val SummaryCardPadding = 18.dp
private val CheckBadgeSize = 24.dp
private val CheckIconSize = 14.dp
private val AlertIconSize = 16.dp
private val ArrowIconSize = 14.dp
private val BulletSize = 6.dp
private val ActionMinHeight = 48.dp

// Figma uses translucent white over the dark green background.
private const val SurfaceAlpha = 0.07f
private const val BorderAlpha = 0.15f
private const val DividerAlpha = 0.12f
private const val SecondaryTextAlpha = 0.7f

private class SummaryStat(@param:StringRes val label: Int, @param:StringRes val value: Int)

private class Highlight(@param:StringRes val title: Int, @param:StringRes val text: Int)

// Demo data copied from the Figma frame (Progress / 06). The texts live in strings.xml.
private val summaryStats = listOf(
    SummaryStat(R.string.weekly_review_workouts_label, R.string.weekly_review_workouts_value),
    SummaryStat(R.string.weekly_review_steps_label, R.string.weekly_review_steps_value),
    SummaryStat(R.string.weekly_review_protein_label, R.string.weekly_review_protein_value),
)

private val highlights = listOf(
    Highlight(R.string.weekly_review_consistent_title, R.string.weekly_review_consistent_text),
    Highlight(R.string.weekly_review_protein_improved_title, R.string.weekly_review_protein_improved_text),
    Highlight(R.string.weekly_review_volume_title, R.string.weekly_review_volume_text),
)

private val recommendations = listOf(
    R.string.weekly_review_recommendation_schedule,
    R.string.weekly_review_recommendation_protein,
    R.string.weekly_review_recommendation_steps,
)

/** Demo screen: weekly review with fixed values from Figma (no ViewModel, no AI review). */
@Composable
fun WeeklyReviewScreen(
    onClose: () -> Unit,
    onViewNextWeekPlan: () -> Unit,
    onAskVora: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // Ephemeral UI state: "Get dinner ideas" only informs that this is a demonstration (NAV03).
    var showDinnerNotice by rememberSaveable { mutableStateOf(false) }

    WeeklyReviewSystemBars()
    // Not a tab destination: there is no bottom bar padding, so the screen handles the system bars itself.
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary)
            .verticalScroll(rememberScrollState())
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = Spacing.screenMargin)
            .padding(top = Spacing.s12, bottom = Spacing.s24),
        verticalArrangement = Arrangement.spacedBy(Spacing.s20),
    ) {
        Header(onBack = onClose)
        SummaryCard()
        WentWellSection()
        ImproveSection(onGetDinnerIdeas = { showDinnerNotice = true })
        RecommendationsCard(onViewNextWeekPlan = onViewNextWeekPlan)
        ReviewActions(onAskVora = onAskVora, onDone = onClose)
    }

    if (showDinnerNotice) {
        AlertDialog(
            onDismissRequest = { showDinnerNotice = false },
            title = { Text(stringResource(R.string.weekly_review_demo_title)) },
            text = { Text(stringResource(R.string.weekly_review_dinner_ideas_notice)) },
            confirmButton = {
                TextButton(onClick = { showDinnerNotice = false }) {
                    Text(stringResource(R.string.weekly_review_ok))
                }
            },
        )
    }
}

/** Translucent white container used over the dark background (summary and improve cards). */
private fun Modifier.translucentCard(shape: Shape, fill: Color, border: Color) = this
        .fillMaxWidth()
        .clip(shape)
        .background(fill)
        .border(1.dp, border, shape)

@Composable
private fun Header(onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(HeaderHeight),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(Spacing.s12),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            BackButton(onClick = onBack)
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.s2)) {
                Text(
                    text = stringResource(R.string.weekly_review_title),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimary,
                )
                Text(
                    text = stringResource(R.string.weekly_review_subtitle),
                    // Figma: 12 Regular; no 12 sp token yet (BASE-03).
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                    color = MaterialTheme.colorScheme.secondary,
                )
            }
        }
        Image(
            painter = painterResource(R.drawable.ic_profile_vora_orb), // same orb as Profile / 01
            contentDescription = null, // decorative
            modifier = Modifier.size(OrbSize),
        )
    }
}

@Composable
private fun BackButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(BackButtonSize)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.onPrimary.copy(alpha = DividerAlpha)) // Figma: white at 12%
            .clickable(role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.ic_weekly_review_chevron_left),
            contentDescription = stringResource(R.string.weekly_review_back),
            tint = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.size(BackIconSize),
        )
    }
}

@Composable
private fun SummaryCard() {
    val onPrimary = MaterialTheme.colorScheme.onPrimary
    Column(
        modifier = Modifier
            .translucentCard(
                shape = MaterialTheme.shapes.extraLarge,
                fill = onPrimary.copy(alpha = SurfaceAlpha), // Figma: white at 7%
                border = onPrimary.copy(alpha = BorderAlpha), // Figma: white at 15%
            )
            .padding(SummaryCardPadding),
        verticalArrangement = Arrangement.spacedBy(Spacing.s14),
    ) {
        Text(
            text = stringResource(R.string.weekly_review_summary_quote),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.tertiary,
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
            horizontalArrangement = Arrangement.spacedBy(Spacing.s8),
        ) {
            summaryStats.forEachIndexed { index, stat ->
                Row(modifier = Modifier.weight(1f)) {
                    if (index > 0) {
                        VerticalDivider(
                            modifier = Modifier.fillMaxHeight(),
                            color = onPrimary.copy(alpha = DividerAlpha), // Figma: white at 12%
                        )
                    }
                    SummaryStatItem(
                        stat = stat,
                        modifier = if (index > 0) Modifier.padding(start = Spacing.s12) else Modifier,
                    )
                }
            }
        }
    }
}

@Composable
private fun SummaryStatItem(stat: SummaryStat, modifier: Modifier = Modifier) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(Spacing.s2)) {
        Text(
            text = stringResource(stat.label),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = SecondaryTextAlpha), // Figma: white at 70%
        )
        Text(
            text = stringResource(stat.value),
            // Figma: 16 Bold; no 16 sp token yet (BASE-03).
            style = MaterialTheme.typography.titleSmall.copy(fontSize = 16.sp),
            color = MaterialTheme.colorScheme.onPrimary,
        )
    }
}

/** Section title with the Figma textCase UPPER applied in code. */
@Composable
private fun SectionTitle(@StringRes text: Int, color: Color) {
    Text(
        text = stringResource(text).uppercase(),
        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
        color = color,
    )
}

@Composable
private fun WentWellSection() {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.s12)) {
        SectionTitle(R.string.weekly_review_went_well_title, MaterialTheme.colorScheme.secondary)
        highlights.forEach { HighlightItem(it) }
    }
}

@Composable
private fun HighlightItem(highlight: Highlight) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Spacing.s12),
        verticalAlignment = Alignment.Top,
    ) {
        Box(
            modifier = Modifier
                .size(CheckBadgeSize)
                .clip(CircleShape)
                .background(MaterialTheme.voraColors.mintSoft),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.ic_weekly_review_check),
                contentDescription = null, // decorative
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(CheckIconSize),
            )
        }
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(Spacing.s2),
        ) {
            Text(
                text = stringResource(highlight.title),
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onPrimary,
            )
            Text(
                text = stringResource(highlight.text),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = SecondaryTextAlpha), // Figma: white at 70%
            )
        }
    }
}

@Composable
private fun ImproveSection(onGetDinnerIdeas: () -> Unit) {
    val onPrimary = MaterialTheme.colorScheme.onPrimary
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.s12)) {
        SectionTitle(R.string.weekly_review_improve_title, MaterialTheme.colorScheme.tertiary)
        Column(
            modifier = Modifier
                .translucentCard(
                    shape = MaterialTheme.shapes.medium,
                    fill = onPrimary.copy(alpha = SurfaceAlpha), // Figma: white at 7%
                    border = onPrimary.copy(alpha = BorderAlpha), // Figma: white at 15%
                )
                .padding(Spacing.s16),
            verticalArrangement = Arrangement.spacedBy(Spacing.s12),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(Spacing.s8),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_weekly_review_alert_circle),
                    contentDescription = null, // decorative
                    tint = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.size(AlertIconSize),
                )
                Text(
                    text = stringResource(R.string.weekly_review_dinner_title),
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                    color = onPrimary,
                    modifier = Modifier.weight(1f),
                )
            }
            Text(
                text = stringResource(R.string.weekly_review_dinner_text),
                style = MaterialTheme.typography.bodyMedium,
                color = onPrimary.copy(alpha = SecondaryTextAlpha), // Figma: white at 70%
            )
            TextLink(
                text = R.string.weekly_review_get_dinner_ideas,
                color = MaterialTheme.colorScheme.tertiary,
                onClick = onGetDinnerIdeas,
                modifier = Modifier.padding(top = Spacing.s4),
            )
        }
    }
}

/** Text action followed by an arrow ("Get dinner ideas", "View next week's plan"). */
@Composable
private fun TextLink(
    @StringRes text: Int,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.clickable(role = Role.Button, onClick = onClick),
        horizontalArrangement = Arrangement.spacedBy(Spacing.s6),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = stringResource(text), style = MaterialTheme.typography.labelMedium, color = color)
        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.ic_weekly_review_arrow_right),
            contentDescription = null, // decorative: the text already says it
            tint = color,
            modifier = Modifier.size(ArrowIconSize),
        )
    }
}

/** White card over the dark background; VoraCard has radius 16 and a border, Figma uses radius 20 without one. */
@Composable
private fun RecommendationsCard(onViewNextWeekPlan: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
            .background(MaterialTheme.colorScheme.surface)
            .padding(Spacing.s16),
        verticalArrangement = Arrangement.spacedBy(Spacing.s12),
    ) {
        SectionTitle(R.string.weekly_review_recommendations_title, MaterialTheme.voraColors.textMuted)
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.s10)) {
            recommendations.forEach { RecommendationItem(it) }
        }
        Column {
            HorizontalDivider(color = MaterialTheme.colorScheme.outline)
            // The whole row is clickable: the text alone would be a small touch target.
            TextLink(
                text = R.string.weekly_review_view_next_week_plan,
                color = MaterialTheme.colorScheme.primary,
                onClick = onViewNextWeekPlan,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Spacing.s12),
            )
        }
    }
}

@Composable
private fun RecommendationItem(@StringRes text: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Spacing.s10),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Plain 6 dp dot, same as the Figma ellipse.
        Box(
            modifier = Modifier
                .size(BulletSize)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary),
        )
        Text(
            text = stringResource(text),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun ReviewActions(onAskVora: () -> Unit, onDone: () -> Unit) {
    Column(
        modifier = Modifier.padding(top = Spacing.s8),
        verticalArrangement = Arrangement.spacedBy(Spacing.s12),
    ) {
        // BASE-02 buttons: their padding is 16/8, so the Figma height (padding 14) comes from heightIn.
        VoraLimeButton(
            text = stringResource(R.string.weekly_review_ask_vora),
            onClick = onAskVora,
            leadingIcon = ImageVector.vectorResource(R.drawable.ic_weekly_review_mic),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = ActionMinHeight),
        )
        VoraOutlinedButton(
            text = stringResource(R.string.weekly_review_done),
            onClick = onDone,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = ActionMinHeight)
                .background(
                    MaterialTheme.colorScheme.onPrimary.copy(alpha = SurfaceAlpha), // Figma: white at 7%
                    VoraPillShape,
                ),
        )
    }
}

/** Light status and navigation bar icons over the dark background; restores the previous ones on exit. */
@Composable
private fun WeeklyReviewSystemBars() {
    val view = LocalView.current
    DisposableEffect(view) {
        val activity = generateSequence(view.context) { (it as? ContextWrapper)?.baseContext }
            .filterIsInstance<Activity>()
            .firstOrNull()
        val controller = if (!view.isInEditMode && activity != null) {
            WindowCompat.getInsetsController(activity.window, view)
        } else {
            null
        }
        val status = controller?.isAppearanceLightStatusBars
        val navigation = controller?.isAppearanceLightNavigationBars
        controller?.isAppearanceLightStatusBars = false
        controller?.isAppearanceLightNavigationBars = false
        onDispose {
            if (status != null) controller?.isAppearanceLightStatusBars = status
            if (navigation != null) controller?.isAppearanceLightNavigationBars = navigation
        }
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 1015)
@Composable
private fun WeeklyReviewScreenPreview() {
    ExamenTP3Theme {
        WeeklyReviewScreen(onClose = {}, onViewNextWeekPlan = {}, onAskVora = {})
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
private fun WeeklyReviewScreenSmallPreview() {
    ExamenTP3Theme {
        WeeklyReviewScreen(onClose = {}, onViewNextWeekPlan = {}, onAskVora = {})
    }
}
