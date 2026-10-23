package com.example.examentp3.ui.screens.notifications

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.examentp3.R
import com.example.examentp3.ui.components.VoraCard
import com.example.examentp3.ui.theme.ExamenTP3Theme
import com.example.examentp3.ui.theme.Spacing
import com.example.examentp3.ui.theme.voraColors

// Sizes measured in the Figma frame that have no token in the theme.
private val IconBoxSize = 36.dp
private val IconSize = 16.dp

// The Figma card has padding 14 and the dot sits 11 from the top-right corner.
// VoraCard pads 16, so the dot is pushed 5 dp out of the content area.
private val UnreadDotOffset = 5.dp

// Demo data copied from the Figma frame (Home / 02). The texts live in strings.xml.
private data class NotificationSample(
    val id: String,
    @param:DrawableRes val icon: Int,
    @param:StringRes val source: Int,
    @param:StringRes val time: Int,
    @param:StringRes val title: Int,
    @param:StringRes val message: Int,
    val unread: Boolean,
)

private val todayNotifications = listOf(
    NotificationSample(
        id = "coach",
        icon = R.drawable.ic_notifications_sparkles,
        source = R.string.notifications_coach_source,
        time = R.string.notifications_coach_time,
        title = R.string.notifications_coach_title,
        message = R.string.notifications_coach_message,
        unread = true,
    ),
    NotificationSample(
        id = "workout",
        icon = R.drawable.ic_notifications_dumbbell,
        source = R.string.notifications_workout_source,
        time = R.string.notifications_workout_time,
        title = R.string.notifications_workout_title,
        message = R.string.notifications_workout_message,
        unread = true,
    ),
    NotificationSample(
        id = "nutrition",
        icon = R.drawable.ic_notifications_apple,
        source = R.string.notifications_nutrition_source,
        time = R.string.notifications_nutrition_time,
        title = R.string.notifications_nutrition_title,
        message = R.string.notifications_nutrition_message,
        unread = false,
    ),
)

private val yesterdayNotifications = listOf(
    NotificationSample(
        id = "milestone",
        icon = R.drawable.ic_notifications_flame,
        source = R.string.notifications_milestone_source,
        time = R.string.notifications_milestone_time,
        title = R.string.notifications_milestone_title,
        message = R.string.notifications_milestone_message,
        unread = false,
    ),
)

@Composable
fun NotificationsScreen(modifier: Modifier = Modifier) {
    // Ephemeral UI state: survives rotation, is not persisted (out of scope for the card).
    var allRead by rememberSaveable { mutableStateOf(false) }
    NotificationsContent(
        allRead = allRead,
        onMarkAllAsReadClick = { allRead = true },
        modifier = modifier,
    )
}

@Composable
private fun NotificationsContent(
    allRead: Boolean,
    onMarkAllAsReadClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding(),
        contentPadding = PaddingValues(
            start = Spacing.screenMargin,
            top = Spacing.s12,
            end = Spacing.screenMargin,
            bottom = Spacing.s24,
        ),
    ) {
        item(key = "header") {
            NotificationsHeader(
                onMarkAllAsReadClick = onMarkAllAsReadClick,
                modifier = Modifier.padding(bottom = Spacing.s20),
            )
        }
        item(key = "today") {
            SectionLabel(
                text = stringResource(R.string.notifications_section_today),
                modifier = Modifier.padding(bottom = Spacing.s12),
            )
        }
        items(todayNotifications, key = { it.id }) { notification ->
            NotificationCard(
                notification = notification,
                showUnread = notification.unread && !allRead,
                modifier = Modifier.padding(
                    bottom = if (notification == todayNotifications.last()) Spacing.s20 else Spacing.s10,
                ),
            )
        }
        item(key = "yesterday") {
            SectionLabel(
                text = stringResource(R.string.notifications_section_yesterday),
                modifier = Modifier.padding(bottom = Spacing.s12),
            )
        }
        items(yesterdayNotifications, key = { it.id }) { notification ->
            NotificationCard(
                notification = notification,
                showUnread = notification.unread && !allRead,
                modifier = Modifier.padding(
                    bottom = if (notification == yesterdayNotifications.last()) 0.dp else Spacing.s10,
                ),
            )
        }
    }
}

@Composable
private fun NotificationsHeader(
    onMarkAllAsReadClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(R.string.notifications_title),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
            text = stringResource(R.string.notifications_mark_all_as_read),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.clickable(role = Role.Button, onClick = onMarkAllAsReadClick),
        )
    }
}

@Composable
private fun SectionLabel(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.voraColors.textSecondary,
        modifier = modifier,
    )
}

/** Same card for every notification (UI02). Not clickable: Figma does not connect it. */
@Composable
private fun NotificationCard(
    notification: NotificationSample,
    showUnread: Boolean,
    modifier: Modifier = Modifier,
) {
    VoraCard(modifier = modifier) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(Spacing.s12),
                verticalAlignment = Alignment.Top,
            ) {
                Box(
                    modifier = Modifier.size(IconBoxSize),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        painter = painterResource(notification.icon),
                        contentDescription = null, // decorative: the source text names it
                        modifier = Modifier.size(IconSize),
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(Spacing.s4),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = stringResource(notification.source),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.voraColors.textSecondary,
                        )
                        Text(
                            text = stringResource(notification.time),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.voraColors.textMuted,
                        )
                    }
                    Text(
                        text = stringResource(notification.title),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        text = stringResource(notification.message),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.voraColors.textSecondary,
                    )
                }
            }
            if (showUnread) {
                // Decorative unread dot: Figma has no text for it.
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = UnreadDotOffset, y = -UnreadDotOffset)
                        .size(Spacing.s6)
                        .background(MaterialTheme.colorScheme.primary, CircleShape),
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun NotificationsContentPreview() {
    ExamenTP3Theme {
        NotificationsContent(allRead = false, onMarkAllAsReadClick = {})
    }
}

@Preview(showBackground = true)
@Composable
private fun NotificationsContentAllReadPreview() {
    ExamenTP3Theme {
        NotificationsContent(allRead = true, onMarkAllAsReadClick = {})
    }
}
