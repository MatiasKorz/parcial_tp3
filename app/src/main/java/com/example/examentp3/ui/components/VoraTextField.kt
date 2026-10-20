package com.example.examentp3.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.examentp3.ui.theme.ExamenTP3Theme
import com.example.examentp3.ui.theme.Spacing
import com.example.examentp3.ui.theme.voraColors

/** Form field: uppercase label, white box, soft border, radius 12, padding 12/16, label gap 6. */
@Composable
fun VoraTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(Spacing.s6),
    ) {
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelSmall, // size and weight estimated, confirm in Figma
            color = MaterialTheme.voraColors.textSecondary,
        )
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = singleLine,
            textStyle = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurface),
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            modifier = Modifier.fillMaxWidth(),
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(MaterialTheme.shapes.small)
                        .background(MaterialTheme.colorScheme.surface)
                        .border(1.dp, MaterialTheme.colorScheme.outline, MaterialTheme.shapes.small)
                        .padding(horizontal = Spacing.s16, vertical = Spacing.s12),
                ) {
                    innerTextField()
                }
            },
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun VoraTextFieldPreview() {
    ExamenTP3Theme {
        Column(
            modifier = Modifier.padding(Spacing.s20),
            verticalArrangement = Arrangement.spacedBy(Spacing.s16),
        ) {
            VoraTextField(label = "First Name", value = "Alex", onValueChange = {})
            VoraTextField(label = "Date of Birth", value = "14 March 1995", onValueChange = {})
            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.s16)) {
                VoraTextField(label = "Height", value = "178 cm", onValueChange = {}, modifier = Modifier.weight(1f))
                VoraTextField(label = "Weight", value = "76 kg", onValueChange = {}, modifier = Modifier.weight(1f))
            }
        }
    }
}