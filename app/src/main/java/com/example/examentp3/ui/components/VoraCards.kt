package com.example.examentp3.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.examentp3.ui.theme.ExamenTP3Theme
import com.example.examentp3.ui.theme.Spacing
import com.example.examentp3.ui.theme.voraColors

/** White card for information and metrics: radius 16, padding 16, 12 between groups. */
@Composable
fun VoraCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outline, MaterialTheme.shapes.medium) // border estimated from the screenshots
            .padding(Spacing.s16),
        verticalArrangement = Arrangement.spacedBy(Spacing.s12),
        content = content,
    )
}

/** Green panel to highlight the workout or the coach: radius 20, padding 20, gap 16. */
@Composable
fun VoraPanel(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
            .background(MaterialTheme.colorScheme.primary)
            .padding(Spacing.s20),
        verticalArrangement = Arrangement.spacedBy(Spacing.s16),
        content = content,
    )
}

/** Meal row: radius 12, padding 12, 2 between texts. */
@Composable
fun VoraMealRow(
    title: String,
    description: String,
    trailing: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.small)
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outline, MaterialTheme.shapes.small)
            .padding(Spacing.s12),
        horizontalArrangement = Arrangement.spacedBy(Spacing.s12),
        verticalAlignment = Alignment.Top,
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(Spacing.s2),
        ) {
            Text(text = title, style = MaterialTheme.typography.labelMedium)
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.voraColors.textSecondary,
            )
        }
        Text(text = trailing, style = MaterialTheme.typography.labelMedium)
    }
}

@Preview(showBackground = true)
@Composable
private fun VoraCardsPreview() {
    ExamenTP3Theme {
        Column(
            modifier = Modifier.padding(Spacing.s20),
            verticalArrangement = Arrangement.spacedBy(Spacing.s16),
        ) {
            VoraPanel {
                Column(verticalArrangement = Arrangement.spacedBy(Spacing.s4)) {
                    Text(
                        text = "READY FOR TODAY?",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.secondary,
                    )
                    Text(
                        text = "Upper Body Strength",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onPrimary,
                    )
                    Text(
                        text = "42 min • 5 exercises • Focus: Chest & Back",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.7f), // preview only
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(Spacing.s8)) {
                    VoraLimeButton(text = "Start Workout", onClick = {})
                    VoraOutlinedButton(text = "Details", onClick = {})
                }
            }
            VoraCard {
                Text(text = "Upper Body Strength", style = MaterialTheme.typography.titleMedium)
                Text(
                    text = "42 min • 5 exercises • Intermediate",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.voraColors.textSecondary,
                )
            }
            VoraMealRow(
                title = "Breakfast",
                description = "Greek yoghurt · berries · granola",
                trailing = "420 kcal",
            )
        }
    }
}