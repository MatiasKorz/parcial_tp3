package com.example.examentp3.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.examentp3.ui.theme.ExamenTP3Theme
import com.example.examentp3.ui.theme.Spacing
import com.example.examentp3.ui.theme.VoraPillShape
import com.example.examentp3.ui.theme.voraColors

enum class VoraButtonSize { Compact, Large }

@Composable
private fun VoraPillButton(
    text: String,
    onClick: () -> Unit,
    containerColor: Color,
    contentColor: Color,
    textStyle: TextStyle,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
    border: BorderStroke? = null,
    leadingIcon: ImageVector? = null,
) {
    Row(
        modifier = modifier
            .clip(VoraPillShape)
            .background(containerColor)
            .then(if (border != null) Modifier.border(border, VoraPillShape) else Modifier)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(contentPadding),
        horizontalArrangement = Arrangement.spacedBy(Spacing.s8, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (leadingIcon != null) {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null, // decorative: the text already says it
                tint = contentColor,
                modifier = Modifier.size(20.dp), // estimated, confirm in Figma
            )
        }
        Text(text = text, style = textStyle, color = contentColor)
    }
}

/** Green main action. Compact = Home (13 SemiBold, 8/16); Large = Edit profile (15 Bold, 14/24). */
@Composable
fun VoraPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: VoraButtonSize = VoraButtonSize.Compact,
) {
    val isLarge = size == VoraButtonSize.Large
    VoraPillButton(
        text = text,
        onClick = onClick,
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        textStyle = if (isLarge) MaterialTheme.typography.titleSmall else MaterialTheme.typography.labelMedium,
        contentPadding = if (isLarge) {
            PaddingValues(horizontal = Spacing.s24, vertical = Spacing.s14)
        } else {
            PaddingValues(horizontal = Spacing.s16, vertical = Spacing.s8)
        },
        modifier = modifier,
    )
}

/** Lime CTA, used inside dark green panels. */
@Composable
fun VoraLimeButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null,
) {
    VoraPillButton(
        text = text,
        onClick = onClick,
        containerColor = MaterialTheme.colorScheme.tertiary,
        contentColor = MaterialTheme.colorScheme.onTertiary,
        textStyle = MaterialTheme.typography.labelLarge,
        contentPadding = PaddingValues(horizontal = Spacing.s16, vertical = Spacing.s8), // estimated, confirm in Figma
        modifier = modifier,
        leadingIcon = leadingIcon,
    )
}

/** Outlined button on dark panels ("Details"): translucent white border. */
@Composable
fun VoraOutlinedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    VoraPillButton(
        text = text,
        onClick = onClick,
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        textStyle = MaterialTheme.typography.labelLarge,
        contentPadding = PaddingValues(horizontal = Spacing.s16, vertical = Spacing.s8), // estimated, confirm in Figma
        modifier = modifier,
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.35f)), // alpha estimated, confirm in Figma
    )
}

/** White chip with border (voice prompts). */
@Composable
fun VoraPromptChip(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    VoraPillButton(
        text = text,
        onClick = onClick,
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        textStyle = MaterialTheme.typography.bodyMedium,
        contentPadding = PaddingValues(horizontal = Spacing.s12, vertical = Spacing.s8), // estimated, confirm in Figma
        modifier = modifier,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
    )
}

/** Text action: "View History" (green) or "Type instead" (muted, underlined). */
@Composable
fun VoraTextAction(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    muted: Boolean = false,
) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium.let {
            if (muted) it.copy(textDecoration = TextDecoration.Underline) else it
        },
        color = if (muted) MaterialTheme.voraColors.textSecondary else MaterialTheme.colorScheme.primary,
        modifier = modifier.clickable(role = Role.Button, onClick = onClick),
    )
}

@Preview(showBackground = true)
@Composable
private fun VoraButtonsPreview() {
    ExamenTP3Theme {
        Column(
            modifier = Modifier.padding(Spacing.s20),
            verticalArrangement = Arrangement.spacedBy(Spacing.s12),
        ) {
            VoraPrimaryButton(text = "Start workout", onClick = {})
            VoraPrimaryButton(
                text = "Save changes",
                onClick = {},
                size = VoraButtonSize.Large,
                modifier = Modifier.fillMaxWidth(),
            )
            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.s16)) {
                VoraPromptChip(text = "Start my workout", onClick = {})
                VoraTextAction(text = "View History", onClick = {})
                VoraTextAction(text = "Type instead", onClick = {}, muted = true)
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(Spacing.s20),
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(Spacing.s8)) {
                    VoraLimeButton(text = "Start Workout", onClick = {})
                    VoraOutlinedButton(text = "Details", onClick = {})
                }
            }
        }
    }
}