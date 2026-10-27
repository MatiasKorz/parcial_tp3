package com.example.examentp3.ui.screens.profile

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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.examentp3.R
import com.example.examentp3.ui.components.VoraCard
import com.example.examentp3.ui.components.VoraLimeButton
import com.example.examentp3.ui.components.VoraPanel
import com.example.examentp3.ui.theme.ExamenTP3Theme
import com.example.examentp3.ui.theme.Spacing
import com.example.examentp3.ui.theme.VoraPillShape
import com.example.examentp3.ui.theme.VoraRadius18
import com.example.examentp3.ui.theme.voraColors
import com.example.examentp3.viewmodel.ProfileDashboardUiState

/**
 * Profile / 01 — Dashboard. The name comes from the saved profile (EXT-02); the rest of the
 * cards are sample data from strings.xml. Every control just reports the click to the NavHost.
 */
@Composable
fun ProfileDashboardScreen(
    uiState: ProfileDashboardUiState,
    onSettingsClick: () -> Unit,
    onEditProfileClick: () -> Unit,
    onReviewPreferencesClick: () -> Unit,
    onGoalsClick: () -> Unit,
    onPreferencesClick: () -> Unit,
    onConnectedDataClick: () -> Unit,
    onAccountClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // The bottom bar belongs to the Scaffold in AppNavigation, which already pads the content above it.
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .statusBarsPadding()
            .padding(horizontal = Spacing.screenMargin),
    ) {
        ProfileHeader(onSettingsClick = onSettingsClick)
        Spacer(modifier = Modifier.height(Spacing.s12))
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.s20)) {
            PersonalCard(fullName = uiState.fullName, onEditProfileClick = onEditProfileClick)
            SnapshotCard()
            PersonalisationPanel(onReviewPreferencesClick = onReviewPreferencesClick)
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.s8)) {
                ProfileLinkRow(
                    icon = R.drawable.ic_profile_goals,
                    title = R.string.profile_dashboard_goals_title,
                    subtitle = R.string.profile_dashboard_goals_subtitle,
                    onClick = onGoalsClick,
                )
                ProfileLinkRow(
                    icon = R.drawable.ic_profile_preferences,
                    title = R.string.profile_dashboard_preferences_title,
                    subtitle = R.string.profile_dashboard_preferences_subtitle,
                    onClick = onPreferencesClick,
                )
                ProfileLinkRow(
                    icon = R.drawable.ic_profile_connected_data,
                    title = R.string.profile_dashboard_connected_data_title,
                    subtitle = R.string.profile_dashboard_connected_data_subtitle,
                    onClick = onConnectedDataClick,
                    status = R.string.profile_dashboard_connected_data_status,
                )
                ProfileLinkRow(
                    icon = R.drawable.ic_profile_account,
                    title = R.string.profile_dashboard_account_title,
                    subtitle = R.string.profile_dashboard_account_subtitle,
                    onClick = onAccountClick,
                )
            }
        }
        Spacer(modifier = Modifier.height(Spacing.s24))
    }
}

@Composable
private fun ProfileHeader(onSettingsClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Spacing.s12),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(R.string.profile_dashboard_title),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(VoraRadius18)
                .background(MaterialTheme.colorScheme.surface)
                .border(1.dp, MaterialTheme.colorScheme.outline, VoraRadius18)
                .clickable(role = Role.Button, onClick = onSettingsClick),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_profile_settings),
                contentDescription = stringResource(R.string.profile_dashboard_settings_description),
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(18.dp),
            )
        }
    }
}

@Composable
private fun PersonalCard(fullName: String, onEditProfileClick: () -> Unit) {
    VoraCard {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Spacing.s16),
        ) {
            Image(
                painter = painterResource(R.drawable.img_profile_avatar),
                contentDescription = null, // decorative: the name is right below
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape),
            )
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(Spacing.s4),
            ) {
                Text(
                    text = fullName,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = stringResource(R.string.profile_dashboard_tagline),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.voraColors.textSecondary,
                    textAlign = TextAlign.Center,
                )
            }
            // Soft pill on the card, not the green VoraPrimaryButton: no shared component matches it.
            Text(
                text = stringResource(R.string.profile_dashboard_edit_profile),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .clip(VoraPillShape)
                    .background(MaterialTheme.colorScheme.background)
                    .clickable(role = Role.Button, onClick = onEditProfileClick)
                    .padding(horizontal = Spacing.s16, vertical = Spacing.s6),
            )
        }
    }
}

@Composable
private fun SnapshotCard() {
    VoraCard {
        // Figma applies textCase UPPER to "My Snapshot"; the string keeps the original copy.
        Text(
            text = stringResource(R.string.profile_dashboard_snapshot_title).uppercase(),
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.voraColors.textMuted,
        )
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.s10)) {
            SnapshotItem(
                label = R.string.profile_dashboard_snapshot_goal_label,
                value = R.string.profile_dashboard_snapshot_goal_value,
            )
            SnapshotItem(
                label = R.string.profile_dashboard_snapshot_experience_label,
                value = R.string.profile_dashboard_snapshot_experience_value,
            )
            SnapshotItem(
                label = R.string.profile_dashboard_snapshot_training_label,
                value = R.string.profile_dashboard_snapshot_training_value,
            )
            SnapshotItem(
                label = R.string.profile_dashboard_snapshot_workout_label,
                value = R.string.profile_dashboard_snapshot_workout_value,
            )
        }
    }
}

@Composable
private fun SnapshotItem(@StringRes label: Int, @StringRes value: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Spacing.s12),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(label),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.voraColors.textSecondary,
        )
        Text(
            text = stringResource(value),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun PersonalisationPanel(onReviewPreferencesClick: () -> Unit) {
    VoraPanel {
        Row(
            horizontalArrangement = Arrangement.spacedBy(Spacing.s8),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painter = painterResource(R.drawable.ic_profile_vora_orb),
                contentDescription = null, // decorative
                modifier = Modifier.size(40.dp),
            )
            Text(
                text = stringResource(R.string.profile_dashboard_vora_label),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.secondary,
            )
        }
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.s4)) {
            Text(
                text = stringResource(R.string.profile_dashboard_vora_title),
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onPrimary,
            )
            Text(
                text = stringResource(R.string.profile_dashboard_vora_body),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f), // Figma: opacity 80%
            )
        }
        VoraLimeButton(
            text = stringResource(R.string.profile_dashboard_review_preferences),
            onClick = onReviewPreferencesClick,
        )
    }
}

@Composable
private fun ProfileLinkRow(
    @DrawableRes icon: Int,
    @StringRes title: Int,
    @StringRes subtitle: Int,
    onClick: () -> Unit,
    @StringRes status: Int? = null,
) {
    VoraCard(
        modifier = Modifier
            .clip(MaterialTheme.shapes.medium)
            .clickable(role = Role.Button, onClick = onClick),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.s12),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(VoraRadius18)
                    .background(MaterialTheme.colorScheme.background),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(icon),
                    contentDescription = null, // decorative: the title already says it
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp),
                )
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(Spacing.s2),
            ) {
                Text(
                    text = stringResource(title),
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = stringResource(subtitle),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.voraColors.textSecondary,
                )
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(Spacing.s8),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (status != null) SyncedTag(text = stringResource(status))
                Icon(
                    painter = painterResource(R.drawable.ic_profile_chevron_right),
                    contentDescription = null,
                    tint = MaterialTheme.voraColors.textMuted,
                    modifier = Modifier.size(16.dp),
                )
            }
        }
    }
}

/** "Synced": green pill with white text. VoraStatusTag is soft mint with green text, so it does not match. */
@Composable
private fun SyncedTag(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
        color = MaterialTheme.colorScheme.onPrimary,
        modifier = Modifier
            .clip(VoraPillShape)
            .background(MaterialTheme.colorScheme.primary)
            .padding(horizontal = Spacing.s8, vertical = Spacing.s2),
    )
}

@Preview(showBackground = true, widthDp = 390)
@Composable
private fun ProfileDashboardScreenPreview() {
    ExamenTP3Theme {
        ProfileDashboardScreen(
            uiState = ProfileDashboardUiState(fullName = stringResource(R.string.profile_dashboard_name)),
            onSettingsClick = {},
            onEditProfileClick = {},
            onReviewPreferencesClick = {},
            onGoalsClick = {},
            onPreferencesClick = {},
            onConnectedDataClick = {},
            onAccountClick = {},
        )
    }
}
