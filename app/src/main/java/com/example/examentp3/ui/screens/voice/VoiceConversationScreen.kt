package com.example.examentp3.ui.screens.voice

import android.app.Activity
import android.content.ContextWrapper
import androidx.activity.compose.BackHandler
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import com.example.examentp3.R
import com.example.examentp3.data.repository.COACH_MESSAGE_MAX_LENGTH
import com.example.examentp3.model.CoachMessage
import com.example.examentp3.model.CoachRole
import com.example.examentp3.ui.components.VoraErrorBanner
import com.example.examentp3.ui.components.VoraLoadingState
import com.example.examentp3.ui.theme.ExamenTP3Theme
import com.example.examentp3.ui.theme.voraColors
import com.example.examentp3.viewmodel.UiError
import com.example.examentp3.viewmodel.VoiceConversationUiState
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Voice / 05 — Conversation (EXT-04): the history saved in Room, with the coach's replies from
 * POST /v1/coach/messages. Voice ("Listen to response", microphone) is still simulated.
 */
@Composable
fun VoiceConversationScreen(
    uiState: VoiceConversationUiState,
    onSend: (String) -> Unit,
    onRetry: () -> Unit,
    onBack: () -> Unit,
    onMicrophone: () -> Unit,
    onListen: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BackHandler(onBack = onBack)
    ConversationSystemBars()
    // The draft is ephemeral UI state; what is sent lives in Room.
    var draft by rememberSaveable { mutableStateOf("") }
    var showAttachmentNotice by rememberSaveable { mutableStateOf(false) }
    val listState = rememberLazyListState()
    val send = {
        if (draft.isNotBlank() && !uiState.isSending) {
            onSend(draft)
            draft = ""
        }
    }
    val lastReplyId = uiState.messages.lastOrNull { it.role == CoachRole.Assistant }?.id
    // Keep the newest message, the reply in progress or the error in view.
    LaunchedEffect(uiState.messages.size, uiState.isSending, uiState.error) {
        val lastIndex = listState.layoutInfo.totalItemsCount - 1
        if (lastIndex > 0) listState.animateScrollToItem(lastIndex)
    }
    Column(
        modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
            .statusBarsPadding().imePadding(),
    ) {
        ConversationHeader(onBack)
        LazyColumn(
            state = listState,
            modifier = Modifier.weight(1f).fillMaxWidth(),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            uiState.messages.firstOrNull()?.let { first ->
                item(key = "date") {
                    Text(
                        conversationStart(first.sentAtMillis),
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.voraColors.textMuted, textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
            // Without history the screen keeps the Figma thread as an example; the first message sent
            // replaces it with the real conversation saved in Room (EXT-04).
            if (uiState.isEmpty) figmaThread(onListen)
            items(uiState.messages, key = { it.id }) { message ->
                val time = messageTime(message.sentAtMillis)
                when (message.role) {
                    CoachRole.User -> UserBubble(message.text, time)
                    CoachRole.Assistant -> CoachBubble(
                        message.text, time = time, isSimulated = message.isSimulated,
                        // The simulated audio (PAN-3.4) is offered for the latest reply only.
                        onListen = if (message.id == lastReplyId) onListen else null,
                    )
                }
            }
            if (uiState.isSending) {
                item(key = "sending") { VoraLoadingState(stringResource(R.string.voice_conversation_sending)) }
            }
            // The error does not cover the history (RF05): it goes after it, with retry.
            uiState.error?.let { error ->
                item(key = "error") {
                    VoraErrorBanner(stringResource(error.message), stringResource(R.string.state_retry), onRetry)
                }
            }
        }
        ConversationInput(
            value = draft, onValueChange = { draft = it.take(COACH_MESSAGE_MAX_LENGTH) }, onSend = send,
            onMicrophone = onMicrophone, onAdd = { showAttachmentNotice = true },
        )
    }
    if (showAttachmentNotice) {
        AlertDialog(
            onDismissRequest = { showAttachmentNotice = false },
            title = { Text(stringResource(R.string.voice_conversation_demo_title)) },
            text = { Text(stringResource(R.string.voice_conversation_attachments_notice)) },
            confirmButton = {
                TextButton(onClick = { showAttachmentNotice = false }) {
                    Text(stringResource(R.string.voice_conversation_ok))
                }
            },
        )
    }
}

private fun LazyListScope.figmaThread(onListen: () -> Unit) {
    item(key = "example_date") {
        Text(
            stringResource(R.string.voice_conversation_example_date),
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.voraColors.textMuted, textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
    }
    item(key = "example_user_dinner") {
        UserBubble(
            stringResource(R.string.voice_conversation_user_dinner),
            stringResource(R.string.voice_conversation_time_1241),
        )
    }
    item(key = "example_coach_dinner") {
        CoachBubble(stringResource(R.string.voice_conversation_coach_dinner), onListen = onListen)
    }
    item(key = "example_user_vegetarian") {
        UserBubble(
            stringResource(R.string.voice_conversation_user_vegetarian),
            stringResource(R.string.voice_conversation_time_1242),
        )
    }
    item(key = "example_coach_vegetarian") {
        CoachBubble(
            stringResource(R.string.voice_conversation_coach_vegetarian),
            time = stringResource(R.string.voice_conversation_time_1243),
        )
    }
}

@Composable
private fun ConversationHeader(onBack: () -> Unit) {
    Column(Modifier.background(MaterialTheme.colorScheme.surface)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                Modifier.size(32.dp).clip(CircleShape)
                    .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
                    .clickable(role = Role.Button, onClick = onBack),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painterResource(R.drawable.ic_voice_idle_chevron_left),
                    stringResource(R.string.voice_conversation_back),
                    tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(14.dp),
                )
            }
            Text(
                stringResource(R.string.voice_conversation_title),
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onBackground,
            )
            Box(Modifier.size(6.dp).background(MaterialTheme.colorScheme.secondary, CircleShape))
            Spacer(Modifier.weight(1f))
            ConversationOrb()
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.outline)
    }
}

@Composable
private fun ConversationOrb() {
    Box(Modifier.size(28.dp), contentAlignment = Alignment.Center) {
        // The original 69 px export extends 20.5 left/right and 12.5 above its 28 dp slot.
        Image(
            painterResource(R.drawable.voice_conversation_orb), contentDescription = null,
            modifier = Modifier.requiredSize(69.dp).offset(y = 8.dp),
        )
    }
}

@Composable
private fun UserBubble(text: String, time: String? = null) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
        Column(
            modifier = Modifier.widthIn(max = 280.dp).fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 4.dp))
                .background(MaterialTheme.colorScheme.primary).padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text, style = MaterialTheme.typography.bodyLarge.copy(fontSize = 14.sp, lineHeight = 19.sp),
                color = MaterialTheme.colorScheme.onPrimary,
            )
            if (time != null) Text(
                time, style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f),
                textAlign = TextAlign.End, modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

private val BubbleTime = DateTimeFormatter.ofPattern("h:mm", Locale.US)
private val StartTime = DateTimeFormatter.ofPattern("h:mm a", Locale.US)
private val StartDate = DateTimeFormatter.ofPattern("MMM d", Locale.US)

private fun Long.toLocalDateTime(): LocalDateTime =
    LocalDateTime.ofInstant(Instant.ofEpochMilli(this), ZoneId.systemDefault())

/** "12:41" in Figma. */
private fun messageTime(millis: Long): String = millis.toLocalDateTime().format(BubbleTime)

/** "TODAY AT 12:40 PM" in Figma; older conversations show their date instead of "today". */
@Composable
private fun conversationStart(millis: Long): String {
    val start = millis.toLocalDateTime()
    val time = start.format(StartTime)
    return if (start.toLocalDate() == LocalDate.now()) {
        stringResource(R.string.voice_conversation_date_today, time)
    } else {
        stringResource(R.string.voice_conversation_date, start.format(StartDate), time).uppercase()
    }
}

@Composable
private fun CoachBubble(
    text: String,
    time: String? = null,
    isSimulated: Boolean = false,
    onListen: (() -> Unit)? = null,
) {
    val shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 4.dp, bottomEnd = 16.dp)
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        ConversationOrb()
        Column(
            modifier = Modifier.widthIn(max = 270.dp).weight(1f, fill = false).clip(shape)
                .background(MaterialTheme.colorScheme.surface)
                .border(1.dp, MaterialTheme.colorScheme.outline, shape).padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(if (onListen != null) 10.dp else 4.dp),
        ) {
            Text(
                text, style = MaterialTheme.typography.bodyLarge.copy(fontSize = 14.sp, lineHeight = 19.6.sp),
                color = MaterialTheme.colorScheme.onSurface,
            )
            if (onListen != null) {
                HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                Row(
                    modifier = Modifier.fillMaxWidth().clickable(role = Role.Button, onClick = onListen),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        Modifier.size(24.dp).background(MaterialTheme.colorScheme.background, CircleShape),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            painterResource(R.drawable.ic_voice_conversation_play), contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(10.dp),
                        )
                    }
                    Text(
                        stringResource(R.string.voice_conversation_listen),
                        style = MaterialTheme.typography.labelMedium.copy(fontSize = 12.sp),
                        color = MaterialTheme.voraColors.textSecondary,
                    )
                }
            }
            // isSimulated from the mock (SPEC p.4): the reply says it is not real coaching.
            val footer = listOfNotNull(
                time,
                if (isSimulated) stringResource(R.string.voice_conversation_simulated) else null,
            )
            if (footer.isNotEmpty()) Text(
                footer.joinToString(" · "), style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                color = MaterialTheme.voraColors.textMuted,
            )
        }
    }
}

@Composable
private fun ConversationInput(
    value: String,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit,
    onMicrophone: () -> Unit,
    onAdd: () -> Unit,
) {
    val colors = MaterialTheme.colorScheme
    Column(Modifier.fillMaxWidth().background(colors.surface).navigationBarsPadding()) {
        HorizontalDivider(color = colors.outline)
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ComposerButton(
                R.drawable.ic_voice_conversation_plus,
                stringResource(R.string.voice_conversation_add), colors.background,
                MaterialTheme.voraColors.textSecondary, onAdd,
            )
            Row(
                modifier = Modifier.weight(1f).heightIn(min = 44.dp)
                    .background(colors.background, RoundedCornerShape(22.dp))
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                val placeholder = stringResource(R.string.voice_conversation_placeholder)
                BasicTextField(
                    value = value, onValueChange = onValueChange,
                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = 14.sp, lineHeight = 20.sp, color = colors.onSurface,
                    ),
                    cursorBrush = SolidColor(colors.primary), maxLines = 3,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(onSend = { onSend() }),
                    modifier = Modifier.weight(1f).semantics { contentDescription = placeholder },
                    decorationBox = { inner ->
                        Box {
                            if (value.isEmpty()) Text(
                                placeholder, style = MaterialTheme.typography.bodyLarge.copy(fontSize = 14.sp),
                                color = MaterialTheme.voraColors.textSecondary,
                            )
                            inner()
                        }
                    },
                )
                Icon(
                    painterResource(R.drawable.ic_voice_conversation_mic),
                    stringResource(R.string.voice_conversation_microphone),
                    tint = MaterialTheme.voraColors.textMuted,
                    modifier = Modifier.size(18.dp).clickable(role = Role.Button, onClick = onMicrophone),
                )
            }
            ComposerButton(
                R.drawable.ic_voice_conversation_send,
                stringResource(R.string.voice_conversation_send), colors.primary, colors.onPrimary, onSend,
            )
        }
    }
}

@Composable
private fun ComposerButton(
    @DrawableRes icon: Int,
    description: String,
    background: Color,
    tint: Color,
    onClick: () -> Unit,
) {
    Box(
        Modifier.size(40.dp).clip(CircleShape).background(background)
            .clickable(role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painterResource(icon), description, tint = tint,
            modifier = Modifier.size(if (icon == R.drawable.ic_voice_conversation_plus) 20.dp else 18.dp),
        )
    }
}

@Composable
private fun ConversationSystemBars() {
    val view = LocalView.current
    LaunchedEffect(view) {
        val activity = generateSequence(view.context) { (it as? ContextWrapper)?.baseContext }
            .filterIsInstance<Activity>().firstOrNull()
        val controller = if (!view.isInEditMode && activity != null) {
            WindowCompat.getInsetsController(activity.window, view)
        } else null
        controller?.isAppearanceLightStatusBars = true
        controller?.isAppearanceLightNavigationBars = true
        // The next destination owns its appearance; do not overwrite dark Voice screens on disposal.
    }
}

// Figma thread, as it would come from Room.
private val PreviewMessages = listOf(
    CoachMessage(1, CoachRole.User, "What should I eat for dinner?", 0),
    CoachMessage(
        2, CoachRole.Assistant,
        "You're currently 36g short of your protein target today. I'd highly recommend grilled chicken " +
            "breast with roasted seasonal vegetables and a side of Greek yoghurt.",
        60_000, isSimulated = true,
    ),
    CoachMessage(3, CoachRole.User, "Can you make that vegetarian?", 120_000),
    CoachMessage(
        4, CoachRole.Assistant,
        "Absolutely. Try grilled organic tofu with roasted vegetables, quinoa and Greek yoghurt.",
        180_000, isSimulated = true,
    ),
)

@Composable
private fun ConversationPreviewContent(uiState: VoiceConversationUiState) {
    ExamenTP3Theme { VoiceConversationScreen(uiState, {}, {}, {}, {}, {}) }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun ConversationPreview() =
    ConversationPreviewContent(VoiceConversationUiState(messages = PreviewMessages))

@Preview(showBackground = true, widthDp = 360, heightDp = 640, fontScale = 1.3f)
@Composable
private fun ConversationCompactPreview() =
    ConversationPreviewContent(VoiceConversationUiState(messages = PreviewMessages, isSending = true))

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun ConversationErrorPreview() = ConversationPreviewContent(
    VoiceConversationUiState(messages = PreviewMessages.take(3), error = UiError.NoConnection),
)

@Preview(showBackground = true, widthDp = 390, heightDp = 640)
@Composable
private fun ConversationEmptyPreview() = ConversationPreviewContent(VoiceConversationUiState())
