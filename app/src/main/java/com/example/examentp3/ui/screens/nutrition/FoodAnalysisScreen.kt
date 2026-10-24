package com.example.examentp3.ui.screens.nutrition

import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.examentp3.R
import com.example.examentp3.ui.theme.ExamenTP3Theme
import com.example.examentp3.ui.theme.Spacing
import com.example.examentp3.viewmodel.FoodAnalysisStepStatus
import com.example.examentp3.viewmodel.FoodAnalysisUiState

// Text styles of Nutrition / 05 without a theme token: .copy over the closest one (BASE-03).
private val analyzingTitleStyle: TextStyle
    @Composable get() = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold) // 20 SemiBold
private val capturedLabelStyle: TextStyle
    @Composable get() = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold) // 11 SemiBold
private val bodyStyle: TextStyle
    @Composable get() = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp, lineHeight = TextUnit.Unspecified) // 14 Regular

// Steps in the order drawn in Figma.
private val analysisSteps = listOf(
    R.string.food_analysis_step_identifying,
    R.string.food_analysis_step_estimating,
    R.string.food_analysis_step_calculating,
)

/**
 * Nutrition / 05 — Food Analysis. Demo processing state: the steps advance on their own (the
 * ViewModel runs the timer) and the NavHost opens the review when the analysis ends. The screen only
 * reports the cancel action; nothing is saved here.
 */
@Composable
fun FoodAnalysisScreen(
    uiState: FoodAnalysisUiState,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // Not a tab destination: there is no Scaffold padding here, so the screen handles the system bars itself.
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary)
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        FoodAnalysisHeader(onClose = onCancel)
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = Spacing.s40),
                verticalArrangement = Arrangement.spacedBy(Spacing.s24),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                FoodAnalysisOrb()
                Text(
                    text = stringResource(R.string.food_analysis_title),
                    style = analyzingTitleStyle,
                    color = MaterialTheme.colorScheme.onPrimary,
                    textAlign = TextAlign.Center,
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.s24),
                verticalArrangement = Arrangement.spacedBy(Spacing.s20),
            ) {
                CapturedInputCard()
                Column(verticalArrangement = Arrangement.spacedBy(Spacing.s12)) {
                    analysisSteps.forEachIndexed { index, textRes ->
                        AnalysisStepRow(textRes = textRes, status = uiState.statusOf(index))
                    }
                }
            }
            CancelAnalysisLink(onClick = onCancel)
        }
    }
}

@Composable
private fun FoodAnalysisHeader(onClose: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = Spacing.s20, end = Spacing.s20, top = Spacing.s12),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(R.string.food_analysis_brand),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f),
        )
        Image(
            painter = painterResource(R.drawable.ic_food_analysis_close),
            contentDescription = stringResource(R.string.food_analysis_cancel),
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .clickable(role = Role.Button, onClick = onClose),
        )
    }
}

@Composable
private fun FoodAnalysisOrb() {
    Box(modifier = Modifier.size(140.dp), contentAlignment = Alignment.Center) {
        // The ripple (161) goes past the 140 slot: requiredSize draws it without changing the layout.
        Image(
            painter = painterResource(R.drawable.ic_food_analysis_orb),
            contentDescription = null, // decorative
            modifier = Modifier.requiredSize(161.dp),
        )
        Icon(
            painter = painterResource(R.drawable.ic_voice_idle_waveform),
            contentDescription = null, // decorative
            tint = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.size(width = 28.dp, height = 20.dp),
        )
    }
}

@Composable
private fun CapturedInputCard() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.05f), MaterialTheme.shapes.medium)
            .padding(Spacing.s14),
        verticalArrangement = Arrangement.spacedBy(Spacing.s4),
    ) {
        Text(
            text = stringResource(R.string.food_analysis_captured_input_label),
            style = capturedLabelStyle,
            color = MaterialTheme.colorScheme.secondary,
        )
        Text(
            text = stringResource(R.string.food_analysis_captured_input),
            style = bodyStyle,
            color = MaterialTheme.colorScheme.onPrimary,
        )
    }
}

@Composable
private fun AnalysisStepRow(@StringRes textRes: Int, status: FoodAnalysisStepStatus) {
    val onPrimary = MaterialTheme.colorScheme.onPrimary
    val dotColor: Color = when (status) {
        FoodAnalysisStepStatus.Done -> MaterialTheme.colorScheme.secondary
        FoodAnalysisStepStatus.Active -> MaterialTheme.colorScheme.tertiary
        FoodAnalysisStepStatus.Pending -> onPrimary.copy(alpha = 0.12f)
    }
    val textStyle = if (status == FoodAnalysisStepStatus.Active) MaterialTheme.typography.labelLarge else bodyStyle
    val textColor = if (status == FoodAnalysisStepStatus.Pending) onPrimary.copy(alpha = 0.4f) else onPrimary
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .semantics(mergeDescendants = true) {},
        horizontalArrangement = Arrangement.spacedBy(Spacing.s12),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(modifier = Modifier.size(16.dp).background(dotColor, CircleShape))
        Text(text = stringResource(textRes), style = textStyle, color = textColor)
    }
}

@Composable
private fun CancelAnalysisLink(onClick: () -> Unit) {
    // VoraTextAction(muted) has this style but a fixed gray that is unreadable on the dark background (BASE-03).
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = Spacing.s24, end = Spacing.s24, top = Spacing.s16, bottom = Spacing.s24),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = stringResource(R.string.food_analysis_cancel),
            style = MaterialTheme.typography.labelMedium.copy(textDecoration = TextDecoration.Underline),
            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f),
            modifier = Modifier.clickable(role = Role.Button, onClick = onClick),
        )
    }
}

@Preview(widthDp = 390, heightDp = 844)
@Composable
private fun FoodAnalysisScreenPreview() {
    ExamenTP3Theme {
        // State drawn in Figma: first step done, second in progress.
        FoodAnalysisScreen(uiState = FoodAnalysisUiState(activeStep = 1), onCancel = {})
    }
}

@Preview(widthDp = 360, heightDp = 640, fontScale = 1.3f)
@Composable
private fun FoodAnalysisScreenSmallPreview() {
    ExamenTP3Theme {
        FoodAnalysisScreen(uiState = FoodAnalysisUiState(activeStep = 1), onCancel = {})
    }
}
