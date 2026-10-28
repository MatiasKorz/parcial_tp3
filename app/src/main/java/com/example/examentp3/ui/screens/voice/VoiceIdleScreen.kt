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
import com.example.examentp3.ui.components.VoraCard
import com.example.examentp3.ui.components.VoraPromptChip
import com.example.examentp3.ui.components.VoraStatusTag
import com.example.examentp3.ui.components.VoraTextAction
import com.example.examentp3.ui.theme.ExamenTP3Theme
import com.example.examentp3.ui.theme.Spacing
import com.example.examentp3.ui.theme.voraColors
import com.example.examentp3.viewmodel.VoiceIdleUiState

/**
 * Voice / 01 — Idle. The suggested questions are the Figma's: two navigate and two are asked to the coach by
 * Retrofit; "LAST CONVERSATION" comes from the Room history (EXT-04). Voice is still simulated: nothing is
 * recorded and no microphone permission is requested.
 */
@Composable
fun VoiceIdleScreen(
    uiState: VoiceIdleUiState,
    onBack: () -> Unit,
    onTapToTalk: () -> Unit,
    onTypeInstead: () -> Unit,
    onLastConversationClick: () -> Unit,
    onStartMyWorkoutClick: () -> Unit,
    onLogMyBreakfastClick: () -> Unit,
    onAskCoach: (String) -> Unit,
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
            SuggestedQuestions(onStartMyWorkoutClick, onLogMyBreakfastClick, onAskCoach)
            LastConversationCard(uiState.lastQuestion, onClick = onLastConversationClick)
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
    onStartMyWorkoutClick: () -> Unit,
    onLogMyBreakfastClick: () -> Unit,
    onAskCoach: (String) -> Unit,
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
        val whatToEat = stringResource(R.string.voice_idle_suggestion_what_to_eat)
        val howAmIDoing = stringResource(R.string.voice_idle_suggestion_how_am_i_doing)
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.s8, Alignment.CenterHorizontally),
            verticalArrangement = Arrangement.spacedBy(Spacing.s8),
        ) {
            VoraPromptChip(text = stringResource(R.string.voice_idle_suggestion_start_workout), onClick = onStartMyWorkoutClick)
            VoraPromptChip(text = stringResource(R.string.voice_idle_suggestion_log_breakfast), onClick = onLogMyBreakfastClick)
            // The two questions are asked to the coach (POST /v1/coach/messages, EXT-04).
            VoraPromptChip(text = whatToEat, onClick = { onAskCoach(whatToEat) })
            VoraPromptChip(text = howAmIDoing, onClick = { onAskCoach(howAmIDoing) })
        }
    }
}

@Composable
private fun LastConversationCard(lastQuestion: CoachMessage?, onClick: () -> Unit) {
    // Without history the card keeps the Figma example; then it shows the last question saved in Room.
    val text = lastQuestion?.text ?: stringResource(R.string.voice_idle_last_conversation_example)
    val time = lastQuestion?.let { DateUtils.getRelativeTimeSpanString(it.sentAtMillis).toString() }
        ?: stringResource(R.string.voice_idle_last_conversation_time)
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
                text = time,
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

// Figma content: the last question of the history.
private val PreviewState = VoiceIdleUiState(
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
            onStartMyWorkoutClick = {},
            onLogMyBreakfastClick = {},
            onAskCoach = {},
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
private fun VoiceIdleScreenNoHistoryPreview() = VoiceIdlePreviewContent(VoiceIdleUiState())
