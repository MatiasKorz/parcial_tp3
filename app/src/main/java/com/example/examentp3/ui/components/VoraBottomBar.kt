package com.example.examentp3.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.examentp3.ui.theme.ExamenTP3Theme
import com.example.examentp3.ui.theme.Spacing
import com.example.examentp3.ui.theme.voraColors
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.path

data class VoraBottomBarItem(val label: String, val icon: ImageVector)

/** Bottom bar: icon + 11 sp label, active in green (600), inactive muted (500), 1 px top border. */
@Composable
fun VoraBottomBar(
    items: List<VoraBottomBarItem>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface),
    ) {
        HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outline)
        Row(
            modifier = Modifier
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(vertical = Spacing.s10), // estimated, confirm in Figma
        ) {
            items.forEachIndexed { index, item ->
                val selected = index == selectedIndex
                val color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.voraColors.textMuted
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .selectable(
                            selected = selected,
                            role = Role.Tab,
                            onClick = { onSelect(index) },
                        ),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(Spacing.s4),
                ) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = null, // the label already says it
                        tint = color,
                        modifier = Modifier.size(20.dp),
                    )
                    Text(
                        text = item.label,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
                        ),
                        color = color,
                    )
                }
            }
        }
    }
}
// Placeholder icon for the preview only; the real ones come from the Figma export.
private val PreviewIcon: ImageVector = ImageVector.Builder(
    defaultWidth = 20.dp,
    defaultHeight = 20.dp,
    viewportWidth = 20f,
    viewportHeight = 20f,
).apply {
    path(fill = SolidColor(Color.Black)) {
        moveTo(4f, 4f)
        lineTo(16f, 4f)
        lineTo(16f, 16f)
        lineTo(4f, 16f)
        close()
    }
}.build()

@Preview(showBackground = true)
@Composable
private fun VoraBottomBarPreview() {
    ExamenTP3Theme {
        VoraBottomBar(
            items = listOf(
                VoraBottomBarItem("Home", PreviewIcon),
                VoraBottomBarItem("Workout", PreviewIcon),
                VoraBottomBarItem("Nutrition", PreviewIcon),
                VoraBottomBarItem("Progress", PreviewIcon),
                VoraBottomBarItem("Profile", PreviewIcon),
            ),
            selectedIndex = 2,
            onSelect = {},
        )
    }
}