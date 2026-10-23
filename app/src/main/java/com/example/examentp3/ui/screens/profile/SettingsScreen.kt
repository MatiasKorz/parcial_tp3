package com.example.examentp3.ui.screens.profile

import androidx.annotation.DrawableRes
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
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.examentp3.R
import com.example.examentp3.model.AppearanceMode
import com.example.examentp3.model.UnitSystem
import com.example.examentp3.ui.components.VoraCard
import com.example.examentp3.ui.theme.ExamenTP3Theme
import com.example.examentp3.ui.theme.Spacing
import com.example.examentp3.ui.theme.VoraPillShape
import com.example.examentp3.ui.theme.VoraRadius18
import com.example.examentp3.ui.theme.voraColors
import com.example.examentp3.viewmodel.SettingsUiState

/**
 * Profile / 06 — Settings. Demo screen: Notifications is a real on/off kept in the repository;
 * the other options without a Figma frame show a demonstration notice (NAV03).
 */
@Composable
fun SettingsScreen(
    uiState: SettingsUiState,
    onBack: () -> Unit,
    onAccountClick: () -> Unit,
    onNotificationsChange: (Boolean) -> Unit,
    onLogOut: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // Ephemeral UI state: which dialog is open. The chosen settings live in the ViewModel.
    var notice by rememberSaveable { mutableStateOf<Int?>(null) }
    var showNotifications by rememberSaveable { mutableStateOf(false) }

    // No bottom bar here, so the Scaffold adds no insets: the screen pads the system bars itself.
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .systemBarsPadding(),
    ) {
        SettingsHeader(onBack = onBack)
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(start = Spacing.s20, end = Spacing.s20, top = Spacing.s12, bottom = Spacing.s24),
            verticalArrangement = Arrangement.spacedBy(Spacing.s20),
        ) {
            SettingsSection(title = R.string.settings_section_account) {
                SettingsRow(
                    icon = R.drawable.ic_settings_user,
                    title = stringResource(R.string.settings_account_name),
                    subtitle = stringResource(R.string.settings_account_email),
                    onClick = onAccountClick,
                )
                SettingsRow(
                    icon = R.drawable.ic_settings_star,
                    title = stringResource(R.string.settings_premium_title),
                    subtitle = stringResource(R.string.settings_premium_subtitle),
                    onClick = { notice = R.string.settings_demo_premium },
                )
                SettingsRow(
                    icon = R.drawable.ic_settings_edit,
                    title = stringResource(R.string.settings_manage_account),
                    onClick = { notice = R.string.settings_demo_manage_account },
                )
            }
            SettingsSection(title = R.string.settings_section_app_preferences) {
                SettingsRow(
                    icon = R.drawable.ic_settings_bell,
                    title = stringResource(R.string.settings_notifications_title),
                    subtitle = stringResource(R.string.settings_notifications_subtitle),
                    onClick = { showNotifications = true },
                )
                SettingsRow(
                    icon = R.drawable.ic_settings_compass,
                    title = stringResource(R.string.settings_units_title),
                    subtitle = stringResource(uiState.units.label),
                    onClick = { notice = R.string.settings_demo_units },
                )
                SettingsRow(
                    icon = R.drawable.ic_settings_eye,
                    title = stringResource(R.string.settings_appearance_title),
                    subtitle = stringResource(uiState.appearance.label),
                    onClick = { notice = R.string.settings_demo_appearance },
                )
            }
            SettingsSection(title = R.string.settings_section_connected_data) {
                SettingsRow(
                    icon = R.drawable.ic_profile_connected_data, // same "activity" vector as Profile / 01
                    title = stringResource(R.string.settings_health_activity_title),
                    subtitle = stringResource(R.string.settings_health_activity_subtitle),
                    onClick = { notice = R.string.settings_demo_health_activity },
                )
            }
            SettingsSection(title = R.string.settings_section_privacy_security) {
                SettingsRow(
                    icon = R.drawable.ic_profile_account, // same "lock" vector as Profile / 01
                    title = stringResource(R.string.settings_privacy_policy),
                    onClick = { notice = R.string.settings_demo_privacy_policy },
                )
                SettingsRow(
                    icon = R.drawable.ic_settings_shield,
                    title = stringResource(R.string.settings_permissions),
                    onClick = { notice = R.string.settings_demo_permissions }, // asks for no permission (NAV05)
                )
            }
            SettingsSection(title = R.string.settings_section_support) {
                SettingsRow(
                    icon = R.drawable.ic_settings_help_circle,
                    title = stringResource(R.string.settings_help_centre),
                    onClick = { notice = R.string.settings_demo_help_centre },
                )
                SettingsRow(
                    icon = R.drawable.ic_settings_mail,
                    title = stringResource(R.string.settings_contact_support),
                    onClick = { notice = R.string.settings_demo_contact_support },
                )
            }
            Column(
                modifier = Modifier.padding(top = Spacing.s12),
                verticalArrangement = Arrangement.spacedBy(Spacing.s12),
            ) {
                LogOutButton(onClick = onLogOut)
                Text(
                    text = stringResource(R.string.settings_version),
                    // Figma: 12 Regular; the theme has no 12 sp slot (bodySmall is 11).
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                    color = MaterialTheme.voraColors.textMuted,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }

    notice?.let { message ->
        AlertDialog(
            onDismissRequest = { notice = null },
            title = { Text(stringResource(R.string.settings_demo_title)) },
            text = { Text(stringResource(message)) },
            confirmButton = {
                TextButton(onClick = { notice = null }) { Text(stringResource(R.string.settings_ok)) }
            },
        )
    }

    if (showNotifications) {
        NotificationsDialog(
            enabled = uiState.notificationsEnabled,
            onEnabledChange = onNotificationsChange,
            onDismiss = { showNotifications = false },
        )
    }
}

@get:StringRes
private val UnitSystem.label: Int
    get() = when (this) {
        UnitSystem.Metric -> R.string.settings_units_metric
    }

@get:StringRes
private val AppearanceMode.label: Int
    get() = when (this) {
        AppearanceMode.SystemDefault -> R.string.settings_appearance_system_default
    }

@Composable
private fun SettingsHeader(onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.s20, vertical = Spacing.s12),
        horizontalArrangement = Arrangement.spacedBy(Spacing.s12),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surface)
                .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
                .clickable(role = Role.Button, onClick = onBack),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_settings_chevron_left),
                contentDescription = stringResource(R.string.settings_back_description),
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(14.dp),
            )
        }
        Text(
            text = stringResource(R.string.settings_title),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
        )
    }
}

@Composable
private fun SettingsSection(
    @StringRes title: Int,
    rows: @Composable () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.s10)) {
        // Figma applies textCase UPPER; the string keeps the original copy.
        Text(
            text = stringResource(title).uppercase(),
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.voraColors.textMuted,
        )
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.s8)) {
            rows()
        }
    }
}

@Composable
private fun SettingsRow(
    @DrawableRes icon: Int,
    title: String,
    onClick: () -> Unit,
    subtitle: String? = null,
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
                    text = title,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface,
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.voraColors.textSecondary,
                    )
                }
            }
            Icon(
                painter = painterResource(R.drawable.ic_profile_chevron_right),
                contentDescription = null,
                tint = MaterialTheme.voraColors.textMuted,
                modifier = Modifier.size(16.dp),
            )
        }
    }
}

/** White pill with red border. Figma uses #DC2626, which the theme lacks: it uses colorScheme.error. */
@Composable
private fun LogOutButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(VoraPillShape)
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.error, VoraPillShape)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = Spacing.s24, vertical = Spacing.s14),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = stringResource(R.string.settings_log_out),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.error,
        )
    }
}

/** On/off for reminders. It only stores the choice: no real notifications, no permission request. */
@Composable
private fun NotificationsDialog(
    enabled: Boolean,
    onEnabledChange: (Boolean) -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.settings_notifications_title)) },
        text = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.s12),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.settings_notifications_subtitle),
                    modifier = Modifier.weight(1f),
                )
                Switch(checked = enabled, onCheckedChange = onEnabledChange)
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.settings_ok)) }
        },
    )
}

@Preview(showBackground = true, widthDp = 390, heightDp = 1309)
@Composable
private fun SettingsScreenPreview() {
    ExamenTP3Theme {
        SettingsScreen(
            uiState = SettingsUiState(),
            onBack = {},
            onAccountClick = {},
            onNotificationsChange = {},
            onLogOut = {},
        )
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 640, fontScale = 1.3f)
@Composable
private fun SettingsScreenSmallPreview() {
    ExamenTP3Theme {
        SettingsScreen(
            uiState = SettingsUiState(notificationsEnabled = false),
            onBack = {},
            onAccountClick = {},
            onNotificationsChange = {},
            onLogOut = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun NotificationsDialogPreview() {
    ExamenTP3Theme {
        NotificationsDialog(enabled = true, onEnabledChange = {}, onDismiss = {})
    }
}
