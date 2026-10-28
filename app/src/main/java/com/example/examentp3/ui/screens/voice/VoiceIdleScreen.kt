package com.example.examentp3.ui.screens.voice

import android.text.format.DateUtils
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
import androidx.compose.foundation.layout.navigationBarsPadding
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.examentp3.R
import com.example.examentp3.model.CoachMessage
import com.example.examentp3.model.CoachRole
import com.example.examentp3.model.CoachSuggestion
import com.example.examentp3.ui.components.VoraCard
import com.example.examentp3.ui.components.VoraEmptyState
import com.example.examentp3.ui.components.VoraErrorBanner
import com.example.examentp3.ui.components.VoraErrorState
import com.example.examentp3.ui.components.VoraLoadingState
import com.example.examentp3.ui.components.VoraPromptChip
import com.example.examentp3.ui.components.VoraStatusTag
import com.example.examentp3.ui.components.VoraTextAction
import com.example.examentp3.ui.theme.ExamenTP3Theme
import com.example.examentp3.ui.theme.Spacing
import com.example.examentp3.ui.theme.voraColors
import com.example.examentp3.viewmodel.UiError
import com.example.examentp3.viewmodel.VoiceIdleUiState

/**
 * Voice / 01 — Idle. The suggested questions come from GET /v1/coach/suggestions and "LAST CONVERSATION"
 * from the Room history (EXT-04). Voice is still simulated: nothing is recorded and no microphone
 * permission is requested.
 */
@Composable
fun VoiceIdleScreen(
    uiState: VoiceIdleUiState,
    onBack: () -> Unit,
    onTapToTalk: () -> Unit,
    onTypeInstead: () -> Unit,
    onLastConversationClick: () -> Unit,
    onSuggestionClick: (String) -> Unit,
    onRetrySuggestions: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // Not a tab destination: there is no Scaffold padding here, so the screen handles the system bars itself.
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        VoiceIdleHeader(onBack = onBack)
        // Only the content scrolls, so "TAP TO TALK" stays visible on small screens.
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(top = Spacing.s20, start = Spacing.s24, end = Spacing.s24),
            verticalArrangement = Arrangement.spacedBy(Spacing.s32),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            VoiceIdleOrb()
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(Spacing.s8),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(R.string.voice_idle_heading),
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center,
                )
                Text(
                    text = stringResource(R.string.voice_idle_subtitle),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.voraColors.textSecondary,
                    textAlign = TextAlign.Center,
                )
            }
            SuggestedQuestions(uiState, onSuggestionClick, onRetrySuggestions)
            // Inferred (EXT-04): the card shows the last question in the history, so it waits for one.
            uiState.lastQuestion?.let { last ->
                LastConversationCard(last.text, last.sentAtMillis, onClick = onLastConversationClick)
            }
        }
        VoiceIdleMicBlock(onTapToTalk = onTapToTalk, onTypeInstead = onTypeInstead)
    }
}

@Composable
private fun VoiceIdleHeader(onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.s20, vertical = Spacing.s12),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(Spacing.s10),
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
                    painter = painterResource(R.drawable.ic_voice_idle_chevron_left),
                    contentDescription = stringResource(R.string.voice_idle_back_description),
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(14.dp),
                )
            }
            Text(
                text = stringResource(R.string.voice_idle_title),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onBackground,
            )
        }
        VoraStatusTag(text = stringResource(R.string.voice_idle_status))
    }
}

@Composable
private fun VoiceIdleOrb() {
    Box(modifier = Modifier.size(140.dp), contentAlignment = Alignment.Center) {
        Image(
            painter = painterResource(R.drawable.ic_voice_idle_orb),
            contentDescription = null, // decorative
            modifier = Modifier.size(140.dp),
        )
        Image(
            painter = painterResource(R.drawable.ic_voice_idle_waveform),
            contentDescription = null, // decorative
            modifier = Modifier.size(width = 28.dp, height = 20.dp),
        )
    }
}

@Composable
private fun SuggestedQuestions(
    uiState: VoiceIdleUiState,
    onSuggestionClick: (String) -> Unit,
    onRetry: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Spacing.s12),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Figma applies uppercase as a text style (textCase), the copy itself is "Suggested Questions".
        Text(
            text = stringResource(R.string.voice_idle_suggestions_title).uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.voraColors.textMuted,
        )
        // Suggestions already shown stay visible; an error then goes in a banner next to them (RF05).
        if (uiState.suggestions.isNotEmpty()) {
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.s8, Alignment.CenterHorizontally),
                verticalArrangement = Arrangement.spacedBy(Spacing.s8),
            ) {
                uiState.suggestions.forEach { suggestion ->
                    VoraPromptChip(text = suggestion.text, onClick = { onSuggestionClick(suggestion.text) })
                }
            }
            uiState.suggestionsError?.let { error ->
                VoraErrorBanner(stringResource(error.message), stringResource(R.string.state_retry), onRetry)
            }
            return@Column
        }
        when {
            uiState.isLoadingSuggestions ->
                VoraLoadingState(stringResource(R.string.voice_idle_suggestions_loading))
            uiState.suggestionsError != null -> VoraErrorState(
                stringResource(R.string.voice_idle_suggestions_error_title),
                stringResource(uiState.suggestionsError.message),
                stringResource(R.string.state_retry),
                onRetry,
            )
            uiState.hasNoSuggestions -> VoraEmptyState(
                stringResource(R.string.voice_idle_suggestions_empty_title),
                stringResource(R.string.voice_idle_suggestions_empty_message),
            )
        }
    }
}

@Composable
private fun LastConversationCard(text: String, sentAtMillis: Long, onClick: () -> Unit) {
    // Clip before clickable so the ripple follows the card's rounded corners.
    VoraCard(
        modifier = Modifier
            .clip(MaterialTheme.shapes.medium)
            .clickable(role = Role.Button, onClick = onClick),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = stringResource(R.string.voice_idle_last_conversation_label),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.voraColors.textMuted,
            )
            Text(
                // "2 hours ago" in Figma: relative to now, in the device language.
                text = DateUtils.getRelativeTimeSpanString(sentAtMillis).toString(),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.voraColors.textMuted,
            )
        }
        Text(
            text = stringResource(R.string.voice_idle_last_conversation_text, text),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun VoiceIdleMicBlock(onTapToTalk: () -> Unit, onTypeInstead: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = Spacing.s16, bottom = Spacing.s24, start = Spacing.s24, end = Spacing.s24),
        verticalArrangement = Arrangement.spacedBy(Spacing.s16),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Circle and label are one control: the label already describes it, so the icon is decorative.
        Column(
            modifier = Modifier.clickable(role = Role.Button, onClick = onTapToTalk),
            verticalArrangement = Arrangement.spacedBy(Spacing.s8),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_voice_idle_mic),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(28.dp),
                )
            }
            Text(
                text = stringResource(R.string.voice_idle_tap_to_talk),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
            )
        }
        VoraTextAction(
            text = stringResource(R.string.voice_idle_type_instead),
            onClick = onTypeInstead,
            muted = true,
        )
    }
}

// Figma content: the four suggestions and the last question.
private val PreviewState = VoiceIdleUiState(
    suggestions = listOf("Start my workout", "Log my breakfast", "What should I eat tonight?", "How am I doing?")
        .mapIndexed { index, text -> CoachSuggestion("s$index", text) },
    lastQuestion = CoachMessage(
        role = CoachRole.User,
        text = "Create a 30-minute workout for me.",
        sentAtMillis = System.currentTimeMillis() - 2 * 60 * 60 * 1000,
    ),
)

@Composable
private fun VoiceIdlePreviewContent(uiState: VoiceIdleUiState) {
    ExamenTP3Theme {
        VoiceIdleScreen(
            uiState = uiState,
            onBack = {},
            onTapToTalk = {},
            onTypeInstead = {},
            onLastConversationClick = {},
            onSuggestionClick = {},
            onRetrySuggestions = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun VoiceIdleScreenPreview() = VoiceIdlePreviewContent(PreviewState)

@Preview(showBackground = true, heightDp = 640)
@Composable
private fun VoiceIdleScreenSmallPreview() = VoiceIdlePreviewContent(PreviewState)

@Preview(showBackground = true)
@Composable
private fun VoiceIdleScreenErrorPreview() =
    VoiceIdlePreviewContent(VoiceIdleUiState(suggestionsError = UiError.Server))

@Preview(showBackground = true)
@Composable
private fun VoiceIdleScreenEmptyPreview() = VoiceIdlePreviewContent(VoiceIdleUiState())
