package com.example.examentp3.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.examentp3.ui.theme.ExamenTP3Theme
import com.example.examentp3.ui.theme.Spacing
import com.example.examentp3.ui.theme.VoraPillShape
import com.example.examentp3.ui.theme.voraColors

/** Progress ring (72 dp) with the value in the center. */
@Composable
fun VoraProgressRing(
    progress: Float,
    centerText: String,
    modifier: Modifier = Modifier,
    progressColor: Color = MaterialTheme.colorScheme.primary,
    trackColor: Color = MaterialTheme.colorScheme.outline,
    strokeWidth: Dp = 6.dp, // estimated, confirm in Figma
) {
    Box(modifier = modifier.size(72.dp), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokePx = strokeWidth.toPx()
            val stroke = Stroke(width = strokePx, cap = StrokeCap.Round)
            val topLeft = Offset(strokePx / 2, strokePx / 2)
            val arcSize = Size(size.width - strokePx, size.height - strokePx)
            drawArc(
                color = trackColor,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = stroke,
            )
            if (progress > 0f) {
                drawArc(
                    color = progressColor,
                    startAngle = -90f,
                    sweepAngle = 360f * progress.coerceIn(0f, 1f),
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = stroke,
                )
            }
        }
        Text(text = centerText, style = MaterialTheme.typography.titleSmall)
    }
}

/** Macro bar (4 dp): label on the left, value on the right, the color is chosen by the caller. */
@Composable
fun VoraMacroBar(
    label: String,
    valueText: String,
    progress: Float,
    color: Color,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Spacing.s4), // estimated, confirm in Figma
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.voraColors.textSecondary,
            )
            Text(
                text = valueText,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(VoraPillShape)
                .background(MaterialTheme.colorScheme.outline),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress.coerceIn(0f, 1f))
                    .fillMaxHeight()
                    .background(color),
            )
        }
    }
}

/** Compact availability tag ("ONLINE"): pill with soft mint and green text. */
@Composable
fun VoraStatusTag(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier
            .clip(VoraPillShape)
            .background(MaterialTheme.voraColors.mintSoft)
            .padding(horizontal = Spacing.s8, vertical = Spacing.s4), // estimated, confirm in Figma
    )
}

/** Status signal: 8 dp dot + title + value. The number always goes with the color. */
@Composable
fun VoraStatusSignal(
    title: String,
    status: String,
    signalColor: Color,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(Spacing.s8),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(signalColor),
        )
        Text(text = title, style = MaterialTheme.typography.labelMedium)
        Text(
            text = status,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.voraColors.textSecondary,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun VoraIndicatorsPreview() {
    ExamenTP3Theme {
        Column(
            modifier = Modifier.padding(Spacing.s20),
            verticalArrangement = Arrangement.spacedBy(Spacing.s16),
        ) {
            VoraCard {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(Spacing.s16),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    VoraProgressRing(progress = 0.67f, centerText = "67%")
                    Text(
                        text = "Goal: 2,100 kcal",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.voraColors.textSecondary,
                    )
                }
            }
            VoraMacroBar("Protein", "104g / 140g", 104f / 140f, MaterialTheme.colorScheme.primary)
            VoraMacroBar("Carbs", "152g / 220g", 152f / 220f, MaterialTheme.colorScheme.secondary)
            VoraMacroBar("Fat", "48g / 65g", 48f / 65f, MaterialTheme.colorScheme.tertiary)
            Row(
                horizontalArrangement = Arrangement.spacedBy(Spacing.s16),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                VoraStatusTag(text = "ONLINE")
                VoraStatusSignal(
                    title = "Workout Progress",
                    status = "Not started 0%",
                    signalColor = MaterialTheme.colorScheme.error,
                )
            }
        }
    }
}