package com.example.examentp3.ui.screens.nutrition

import androidx.annotation.DrawableRes
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.examentp3.R
import com.example.examentp3.ui.components.VoraLimeButton
import com.example.examentp3.ui.theme.ExamenTP3Theme
import com.example.examentp3.ui.theme.Spacing
import com.example.examentp3.ui.theme.voraColors

// Sizes measured in the Figma frame (Nutrition / 03) that have no token in the theme.
private val BackIconSize = 20.dp
private val OrbSlotSize = 44.dp
private val OrbDrawableSize = 51.dp // the ripple ring is wider than the 44 slot
private val TalkTextMaxWidth = 220.dp
private val LimeButtonMinHeight = 43.dp // Figma: 12 vertical padding + 14 text; VoraLimeButton pads 8
private val OptionCircleSize = 48.dp
private val OptionIconSize = 24.dp
private val OptionArrowSize = 20.dp

/**
 * Screen with no data (no ViewModel): the four ways to add food. "Search Database" opens the food
 * search of EXT-05 in Food Confirmation.
 */
@Composable
fun AddFoodScreen(
    onBack: () -> Unit,
    onStartVoiceLogging: () -> Unit,
    onSnapPhoto: () -> Unit,
    onSearchDatabase: () -> Unit,
    onLogManually: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // The bottom bar Scaffold already applies the navigation bar inset, so only the status bar is added.
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .statusBarsPadding()
            .padding(horizontal = Spacing.screenMargin)
            .padding(top = Spacing.s12, bottom = Spacing.s24),
        verticalArrangement = Arrangement.spacedBy(Spacing.s24),
    ) {
        AddFoodHeader(onBack = onBack)
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.s16)) {
            TalkToVoraPanel(onStartVoiceLogging = onStartVoiceLogging)
            AddFoodOption(
                icon = R.drawable.ic_add_food_camera,
                iconBackground = MaterialTheme.voraColors.mintSoft,
                title = R.string.add_food_photo_title,
                description = R.string.add_food_photo_description,
                onClick = onSnapPhoto,
            )
            AddFoodOption(
                icon = R.drawable.ic_add_food_search,
                iconBackground = MaterialTheme.colorScheme.background,
                title = R.string.add_food_search_title,
                description = R.string.add_food_search_description,
                onClick = onSearchDatabase,
            )
            AddFoodOption(
                icon = R.drawable.ic_add_food_pen,
                iconBackground = MaterialTheme.colorScheme.background,
                title = R.string.add_food_manual_title,
                description = R.string.add_food_manual_description,
                onClick = onLogManually,
            )
        }
    }
}

@Composable
private fun AddFoodHeader(onBack: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Spacing.s8),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_exercise_details_back), // same chevron as Workout / 06
            contentDescription = stringResource(R.string.add_food_back_description),
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .size(BackIconSize)
                .clickable(role = Role.Button, onClick = onBack),
        )
        Text(
            text = stringResource(R.string.add_food_title),
            // Figma: 20 SemiBold; titleLarge is 20 Bold.
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onBackground,
        )
    }
}

@Composable
private fun TalkToVoraPanel(onStartVoiceLogging: () -> Unit) {
    val shape = MaterialTheme.shapes.extraLarge
    Column(
        modifier = Modifier
            .fillMaxWidth()
            // Figma: drop shadow 0 8 8, green at 7 %.
            .dropShadow(
                shape = shape,
                shadow = Shadow(
                    radius = 8.dp,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.07f),
                    offset = DpOffset(0.dp, 8.dp),
                ),
            )
            .clip(shape)
            .background(MaterialTheme.colorScheme.primary)
            .padding(Spacing.s20),
        verticalArrangement = Arrangement.spacedBy(Spacing.s16),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top,
        ) {
            Column(
                modifier = Modifier
                    .weight(1f, fill = false)
                    .widthIn(max = TalkTextMaxWidth),
                verticalArrangement = Arrangement.spacedBy(Spacing.s4),
            ) {
                Text(
                    text = stringResource(R.string.add_food_talk_title),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimary,
                )
                Text(
                    text = stringResource(R.string.add_food_talk_example),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f),
                )
            }
            Box(modifier = Modifier.size(OrbSlotSize), contentAlignment = Alignment.Center) {
                Image(
                    painter = painterResource(R.drawable.ic_add_food_orb),
                    contentDescription = null, // decorative
                    modifier = Modifier.requiredSize(OrbDrawableSize),
                )
            }
        }
        VoraLimeButton(
            text = stringResource(R.string.add_food_start_voice),
            onClick = onStartVoiceLogging,
            leadingIcon = ImageVector.vectorResource(R.drawable.ic_add_food_mic),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = LimeButtonMinHeight),
        )
    }
}

@Composable
private fun AddFoodOption(
    @DrawableRes icon: Int,
    iconBackground: Color,
    @StringRes title: Int,
    @StringRes description: Int,
    onClick: () -> Unit,
) {
    val shape = MaterialTheme.shapes.large
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outline, shape)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(Spacing.s16),
        horizontalArrangement = Arrangement.spacedBy(Spacing.s16),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(OptionCircleSize)
                .clip(CircleShape)
                .background(iconBackground),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(icon),
                contentDescription = null, // decorative: the title already says it
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(OptionIconSize),
            )
        }
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(Spacing.s2),
        ) {
            Text(
                text = stringResource(title),
                // Figma: 15 SemiBold; titleSmall is 15 Bold.
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = stringResource(description),
                // Figma: 12 Regular; no token (bodySmall is 11).
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                color = MaterialTheme.voraColors.textSecondary,
            )
        }
        Icon(
            painter = painterResource(R.drawable.ic_add_food_arrow_right),
            contentDescription = null, // decorative: the whole card is the button
            tint = MaterialTheme.voraColors.textMuted,
            modifier = Modifier.size(OptionArrowSize),
        )
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun AddFoodScreenPreview() {
    ExamenTP3Theme {
        AddFoodScreen(onBack = {}, onStartVoiceLogging = {}, onSnapPhoto = {}, onSearchDatabase = {}, onLogManually = {})
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 640, fontScale = 1.3f)
@Composable
private fun AddFoodScreenCompactPreview() {
    ExamenTP3Theme {
        AddFoodScreen(onBack = {}, onStartVoiceLogging = {}, onSnapPhoto = {}, onSearchDatabase = {}, onLogManually = {})
    }
}
