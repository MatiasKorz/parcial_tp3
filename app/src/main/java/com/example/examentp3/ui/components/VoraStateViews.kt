package com.example.examentp3.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.examentp3.ui.theme.ExamenTP3Theme
import com.example.examentp3.ui.theme.Spacing
import com.example.examentp3.ui.theme.voraColors

// RF05 states. Figma does not design them, so they reuse the card, typography and buttons of the system.

/** Waiting for a remote query: spinner and a short text. Compact, so it also fits above cached content. */
@Composable
fun VoraLoadingState(
    text: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth().padding(vertical = Spacing.s12),
        horizontalArrangement = Arrangement.spacedBy(Spacing.s12, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(20.dp),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.outline,
            strokeWidth = 2.dp,
        )
        Text(text, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.voraColors.textSecondary)
    }
}

/** A valid answer without results. Not an error, so it has no retry. */
@Composable
fun VoraEmptyState(
    title: String,
    message: String,
    modifier: Modifier = Modifier,
) {
    VoraCard(modifier) {
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.s4)) {
            Text(title, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurface)
            Text(message, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.voraColors.textSecondary)
        }
    }
}

/** A failed query with nothing to show instead: message and retry. */
@Composable
fun VoraErrorState(
    title: String,
    message: String,
    retryLabel: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    VoraCard(modifier) {
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.s4)) {
            Text(title, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.error)
            Text(message, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.voraColors.textSecondary)
        }
        VoraPrimaryButton(retryLabel, onRetry, modifier = Modifier.fillMaxWidth())
    }
}

/** A failed refresh while cached content stays visible: the error does not cover the content. */
@Composable
fun VoraErrorBanner(
    message: String,
    retryLabel: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.small)
            .background(MaterialTheme.voraColors.mintSoft)
            .padding(Spacing.s12),
        horizontalArrangement = Arrangement.spacedBy(Spacing.s12),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.weight(1f),
        )
        VoraTextAction(retryLabel, onRetry)
    }
}

@Preview(showBackground = true)
@Composable
private fun VoraStateViewsPreview() {
    ExamenTP3Theme {
        Column(
            modifier = Modifier.padding(Spacing.s20),
            verticalArrangement = Arrangement.spacedBy(Spacing.s16),
        ) {
            VoraLoadingState("Loading workouts…")
            VoraEmptyState("No workouts yet", "There are no workouts available right now.")
            VoraErrorState(
                "Couldn't load workouts",
                "The service isn't available right now. Try again in a moment.",
                "Retry",
                {},
            )
            VoraErrorBanner("Can't reach the server. Showing saved workouts.", "Retry", {})
        }
    }
}
