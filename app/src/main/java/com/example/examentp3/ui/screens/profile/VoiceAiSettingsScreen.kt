package com.example.examentp3.ui.screens.profile

import androidx.annotation.StringRes
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.examentp3.R
import com.example.examentp3.model.CoachStyle
import com.example.examentp3.model.ResponseStyle
import com.example.examentp3.model.VoiceCoachSettings
import com.example.examentp3.ui.components.VoraCard
import com.example.examentp3.ui.theme.ExamenTP3Theme
import com.example.examentp3.ui.theme.Spacing
import com.example.examentp3.ui.theme.VoraPillShape
import com.example.examentp3.ui.theme.voraColors
import com.example.examentp3.viewmodel.VoiceAiSettingsUiState
import com.example.examentp3.viewmodel.VoiceToggle
import com.example.examentp3.viewmodel.isOn

/**
 * Profile / 05 — Voice & AI Settings. Demo screen: the choices are kept in the local settings,
 * "Preview" shows a demonstration notice and nothing asks for the microphone (NAV03/NAV05).
 */
@Composable
fun VoiceAiSettingsScreen(
    uiState: VoiceAiSettingsUiState,
    onBack: () -> Unit,
    onCoachStyleSelect: (CoachStyle) -> Unit,
    onResponseStyleSelect: (ResponseStyle) -> Unit,
    onToggleChange: (VoiceToggle, Boolean) -> Unit,
    onPrivacyClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val settings = uiState.settings
    // Ephemeral UI state: whether the "Preview" notice is open. The choices live in the ViewModel.
    var showPreviewNotice by rememberSaveable { mutableStateOf(false) }

    // No bottom bar here, so the Scaffold adds no insets: the screen pads the system bars itself.
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .systemBarsPadding(),
    ) {
        VoiceAiSettingsHeader(onBack = onBack)
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(start = Spacing.screenMargin, end = Spacing.screenMargin, top = Spacing.s12, bottom = Spacing.s24),
            verticalArrangement = Arrangement.spacedBy(Spacing.s20),
        ) {
            Text(
                text = stringResource(R.string.voice_ai_settings_intro),
                // Figma: 14 Regular, line height 20. No theme token matches, see BASE-03.
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp, lineHeight = 20.sp),
                color = MaterialTheme.voraColors.textSecondary,
            )
            VoiceSection(title = R.string.voice_ai_settings_coaching_style) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectableGroup(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.s8),
                ) {
                    CoachStyle.entries.forEach { style ->
                        CoachStyleOption(
                            label = stringResource(style.labelRes),
                            selected = style == settings.coachStyle,
                            onClick = { onCoachStyleSelect(style) },
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }
            ActiveVoiceCard(onPreviewClick = { showPreviewNotice = true })
            VoiceSection(title = R.string.voice_ai_settings_speaking_behaviour) {
                // Figma: padding 12 in this card; VoraCard has 16 (approved, see BASE-03).
                VoraCard {
                    ToggleRows(
                        toggles = listOf(
                            VoiceToggle.SpeakResponsesAloud,
                            VoiceToggle.AutoPlayCoaching,
                            VoiceToggle.AllowInterruptions,
                        ),
                        settings = settings,
                        onToggleChange = onToggleChange,
                    )
                    Text(
                        text = stringResource(R.string.voice_ai_settings_interruptions_hint),
                        // Figma: 12 Regular; the theme has no 12 sp slot (bodySmall is 11).
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                        color = MaterialTheme.voraColors.textSecondary,
                    )
                }
            }
            VoiceSection(title = R.string.voice_ai_settings_response_style) {
                Column(
                    modifier = Modifier.selectableGroup(),
                    verticalArrangement = Arrangement.spacedBy(Spacing.s8),
                ) {
                    ResponseStyle.entries.forEach { style ->
                        ResponseStyleCard(
                            title = stringResource(style.titleRes),
                            description = stringResource(style.descriptionRes),
                            selected = style == settings.responseStyle,
                            onClick = { onResponseStyleSelect(style) },
                        )
                    }
                }
            }
            VoraCard {
                ToggleRows(
                    toggles = listOf(VoiceToggle.VoiceFeedback, VoiceToggle.HapticFeedback),
                    settings = settings,
                    onToggleChange = onToggleChange,
                )
            }
            ControlNoticeCard(onPrivacyClick = onPrivacyClick)
        }
    }

    if (showPreviewNotice) {
        AlertDialog(
            onDismissRequest = { showPreviewNotice = false },
            title = { Text(stringResource(R.string.voice_ai_settings_demo_title)) },
            text = { Text(stringResource(R.string.voice_ai_settings_preview_notice)) },
            confirmButton = {
                TextButton(onClick = { showPreviewNotice = false }) {
                    Text(stringResource(R.string.voice_ai_settings_ok))
                }
            },
        )
    }
}

@Composable
private fun VoiceAiSettingsHeader(onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.screenMargin, vertical = Spacing.s12),
        horizontalArrangement = Arrangement.spacedBy(Spacing.s12),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // 32 dp back button: no shared component has this size yet (see BASE-03).
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
                // Same chevron asset as Profile / 06 in Figma, so its drawable is reused.
                painter = painterResource(R.drawable.ic_settings_chevron_left),
                contentDescription = stringResource(R.string.voice_ai_settings_back_description),
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(14.dp),
            )
        }
        Text(
            text = stringResource(R.string.voice_ai_settings_title),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
        )
    }
}

@Composable
private fun SectionTitle(@StringRes title: Int) {
    Text(
        // Figma applies textCase UPPER; the string keeps the original copy.
        text = stringResource(title).uppercase(),
        // Figma: 13 Bold. labelMedium is 13 SemiBold, see BASE-03.
        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.voraColors.textMuted,
    )
}

@Composable
private fun VoiceSection(@StringRes title: Int, content: @Composable ColumnScope.() -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.s10)) {
        SectionTitle(title)
        content()
    }
}

/** One option of "Coaching Style". Same look as "Training Frequency" (Profile / 03); private until BASE-03. */
@Composable
private fun CoachStyleOption(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = MaterialTheme.shapes.small
    Box(
        modifier = modifier
            .clip(shape)
            .background(if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface)
            .border(1.dp, if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline, shape)
            .selectable(selected = selected, role = Role.RadioButton, onClick = onClick)
            .padding(horizontal = Spacing.s4, vertical = Spacing.s10),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun ActiveVoiceCard(onPreviewClick: () -> Unit) {
    VoraCard {
        // In Figma this title sits inside the card, unlike the other section titles.
        SectionTitle(R.string.voice_ai_settings_active_voice)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.s12),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_voice_ai_settings_volume),
                    contentDescription = null, // decorative: the voice name already says it
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(16.dp),
                )
            }
            Text(
                text = stringResource(R.string.voice_ai_settings_voice_name),
                // Figma: 15 SemiBold. bodyLarge is 15 Regular, see BASE-03.
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f),
            )
            PreviewButton(onClick = onPreviewClick)
        }
    }
}

/** Outlined pill with a waveform icon. No shared component matches it (see BASE-03). */
@Composable
private fun PreviewButton(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .clip(VoraPillShape)
            .border(1.dp, MaterialTheme.colorScheme.outline, VoraPillShape)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = Spacing.s12, vertical = Spacing.s6),
        horizontalArrangement = Arrangement.spacedBy(Spacing.s6),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_voice_ai_settings_waveform),
            contentDescription = null, // decorative: the label already says it
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(12.dp),
        )
        Text(
            text = stringResource(R.string.voice_ai_settings_preview),
            // Figma: 12 SemiBold. labelMedium is 13 SemiBold, see BASE-03.
            style = MaterialTheme.typography.labelMedium.copy(fontSize = 12.sp),
            color = MaterialTheme.colorScheme.primary,
        )
    }
}

@Composable
private fun ToggleRows(
    toggles: List<VoiceToggle>,
    settings: VoiceCoachSettings,
    onToggleChange: (VoiceToggle, Boolean) -> Unit,
) {
    toggles.forEach { toggle ->
        ToggleRow(
            label = stringResource(toggle.labelRes),
            checked = settings.isOn(toggle),
            onCheckedChange = { onToggleChange(toggle, it) },
        )
    }
}

/** Whole row is the touch target, so TalkBack reads "switch, on/off" with its label. */
@Composable
private fun ToggleRow(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .toggleable(value = checked, role = Role.Switch, onValueChange = onCheckedChange),
        horizontalArrangement = Arrangement.spacedBy(Spacing.s12),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            // Figma: 14 Medium. labelLarge is 14 SemiBold, see BASE-03.
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Medium),
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f),
        )
        VoraSwitchVisual(checked = checked)
    }
}

/**
 * 44x24 switch as drawn in Figma (mint track, white knob on the right). The off state is not
 * drawn: grey track and knob on the left are inferred (approved, open question in REF-05).
 */
@Composable
private fun VoraSwitchVisual(checked: Boolean) {
    val knobOffset by animateDpAsState(targetValue = if (checked) 22.dp else 2.dp, label = "knobOffset")
    Box(
        modifier = Modifier
            .size(width = 44.dp, height = 24.dp)
            .clip(VoraPillShape)
            .background(if (checked) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.outline),
        contentAlignment = Alignment.CenterStart,
    ) {
        Box(
            modifier = Modifier
                .offset(x = knobOffset)
                .size(20.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surface),
        )
    }
}

/** Option card of "Response Style". Same look as "Primary Goal" (Profile / 03); private until BASE-03. */
@Composable
private fun ResponseStyleCard(
    title: String,
    description: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val shape = MaterialTheme.shapes.medium
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            // Figma: mint at 8 %. VoraMintSoft is 12 %, see BASE-03.
            .background(if (selected) MaterialTheme.colorScheme.secondary.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface)
            .border(
                width = if (selected) 2.dp else 1.dp,
                color = if (selected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.outline,
                shape = shape,
            )
            .selectable(selected = selected, role = Role.RadioButton, onClick = onClick)
            .padding(Spacing.s16),
        verticalArrangement = Arrangement.spacedBy(Spacing.s6),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = title,
                // Figma: 16 SemiBold. titleMedium is 18, see BASE-03.
                style = MaterialTheme.typography.titleMedium.copy(fontSize = 16.sp),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f),
            )
            SelectionIndicator(selected = selected)
        }
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.voraColors.textSecondary,
        )
    }
}

@Composable
private fun SelectionIndicator(selected: Boolean) {
    if (selected) {
        Box(
            modifier = Modifier
                .size(20.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                // Same check asset as Profile / 03 in Figma, so its drawable is reused.
                painter = painterResource(R.drawable.ic_goals_preferences_check),
                contentDescription = null, // the selected state is announced by the card
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(12.dp),
            )
        }
    } else {
        Box(
            modifier = Modifier
                .size(20.dp)
                .border(2.dp, MaterialTheme.colorScheme.outline, CircleShape),
        )
    }
}

/** Mint notice with the link to Settings. VoraInfoMessage differs (radius 12, padding 12), see BASE-03. */
@Composable
private fun ControlNoticeCard(onPrivacyClick: () -> Unit) {
    val shape = MaterialTheme.shapes.medium
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(MaterialTheme.voraColors.mintSoft)
            // Figma: mint border at 25 %.
            .border(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.25f), shape)
            .padding(Spacing.s16),
        verticalArrangement = Arrangement.spacedBy(Spacing.s10),
    ) {
        Text(
            text = stringResource(R.string.voice_ai_settings_control_title),
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.primary,
        )
        Text(
            text = stringResource(R.string.voice_ai_settings_control_body),
            style = MaterialTheme.typography.bodyMedium,
            // Figma: 90 % opacity.
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.9f),
        )
        Text(
            text = stringResource(R.string.voice_ai_settings_privacy_link),
            // Figma: 13 Bold underlined. VoraTextAction is SemiBold and only underlines the grey variant.
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                textDecoration = TextDecoration.Underline,
            ),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.clickable(role = Role.Button, onClick = onPrivacyClick),
        )
    }
}

@get:StringRes
private val CoachStyle.labelRes: Int
    get() = when (this) {
        CoachStyle.Supportive -> R.string.voice_ai_settings_coaching_supportive
        CoachStyle.Balanced -> R.string.voice_ai_settings_coaching_balanced
        CoachStyle.Direct -> R.string.voice_ai_settings_coaching_direct
    }

@get:StringRes
private val ResponseStyle.titleRes: Int
    get() = when (this) {
        ResponseStyle.Concise -> R.string.voice_ai_settings_response_concise
        ResponseStyle.Balanced -> R.string.voice_ai_settings_response_balanced
        ResponseStyle.Detailed -> R.string.voice_ai_settings_response_detailed
    }

@get:StringRes
private val ResponseStyle.descriptionRes: Int
    get() = when (this) {
        ResponseStyle.Concise -> R.string.voice_ai_settings_response_concise_description
        ResponseStyle.Balanced -> R.string.voice_ai_settings_response_balanced_description
        ResponseStyle.Detailed -> R.string.voice_ai_settings_response_detailed_description
    }

@get:StringRes
private val VoiceToggle.labelRes: Int
    get() = when (this) {
        VoiceToggle.SpeakResponsesAloud -> R.string.voice_ai_settings_speak_aloud
        VoiceToggle.AutoPlayCoaching -> R.string.voice_ai_settings_auto_play
        VoiceToggle.AllowInterruptions -> R.string.voice_ai_settings_allow_interruptions
        VoiceToggle.VoiceFeedback -> R.string.voice_ai_settings_voice_feedback
        VoiceToggle.HapticFeedback -> R.string.voice_ai_settings_haptic_feedback
    }

@Preview(showBackground = true, widthDp = 390, heightDp = 1125)
@Composable
private fun VoiceAiSettingsScreenPreview() {
    ExamenTP3Theme {
        VoiceAiSettingsScreen(
            uiState = VoiceAiSettingsUiState(),
            onBack = {},
            onCoachStyleSelect = {},
            onResponseStyleSelect = {},
            onToggleChange = { _, _ -> },
            onPrivacyClick = {},
        )
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 640, fontScale = 1.3f)
@Composable
private fun VoiceAiSettingsScreenSmallPreview() {
    ExamenTP3Theme {
        VoiceAiSettingsScreen(
            uiState = VoiceAiSettingsUiState(
                settings = VoiceCoachSettings(
                    coachStyle = CoachStyle.Direct,
                    responseStyle = ResponseStyle.Detailed,
                    autoPlayCoaching = false,
                    hapticFeedback = false,
                ),
            ),
            onBack = {},
            onCoachStyleSelect = {},
            onResponseStyleSelect = {},
            onToggleChange = { _, _ -> },
            onPrivacyClick = {},
        )
    }
}
