package com.example.examentp3.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.examentp3.ui.theme.ExamenTP3Theme
import com.example.examentp3.ui.theme.Spacing
import com.example.examentp3.ui.theme.voraColors

/** Short recommendation: soft mint, mint border, green text, radius 12, padding 12. */
@Composable
fun VoraInfoMessage(
    text: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.small)
            .background(MaterialTheme.voraColors.mintSoft)
            .border(1.dp, MaterialTheme.colorScheme.secondary, MaterialTheme.shapes.small) // alpha estimated, compare with Figma
            .padding(Spacing.s12),
        horizontalArrangement = Arrangement.spacedBy(Spacing.s8),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null, // decorative: the text already says it
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(16.dp), // estimated, confirm in Figma
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary,
        )
    }
}

/** Featured advice: green panel with white content, radius 16, padding 16. */
@Composable
fun VoraAdviceCard(
    title: String,
    text: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.primary)
            .padding(Spacing.s16),
        verticalArrangement = Arrangement.spacedBy(Spacing.s8), // estimated, confirm in Figma
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(Spacing.s8),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(16.dp), // estimated, confirm in Figma
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall, // size estimated, confirm in Figma
                color = MaterialTheme.colorScheme.secondary,
            )
        }
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimary,
        )
    }
}

// Placeholder icon for the preview only; the real ones come from the Figma export.
private val PreviewIcon: ImageVector = ImageVector.Builder(
    defaultWidth = 16.dp,
    defaultHeight = 16.dp,
    viewportWidth = 16f,
    viewportHeight = 16f,
).apply {
    path(fill = SolidColor(Color.Black)) {
        moveTo(3f, 3f)
        lineTo(13f, 3f)
        lineTo(13f, 13f)
        lineTo(3f, 13f)
        close()
    }
}.build()

@Preview(showBackground = true)
@Composable
private fun VoraMessagesPreview() {
    ExamenTP3Theme {
        Column(
            modifier = Modifier.padding(Spacing.s20),
            verticalArrangement = Arrangement.spacedBy(Spacing.s12),
        ) {
            VoraInfoMessage(
                text = "Used to personalise your recommendations.",
                icon = PreviewIcon,
            )
            VoraInfoMessage(
                text = "You're on track today. A protein-rich dinner would help you reach your target.",
                icon = PreviewIcon,
            )
            VoraAdviceCard(
                title = "VORA ADVICE",
                text = "\"Completing today's intermediate workout and a protein-rich dinner will secure your optimal target recovery.\"",
                icon = PreviewIcon,
            )
        }
    }
}