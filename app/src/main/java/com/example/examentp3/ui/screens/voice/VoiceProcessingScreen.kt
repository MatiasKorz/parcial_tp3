package com.example.examentp3.ui.screens.voice

import android.app.Activity
import android.content.ContextWrapper
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import com.example.examentp3.R
import com.example.examentp3.ui.theme.ExamenTP3Theme

/** Static Processing design; the destination owns the simulated transition. */
@Composable
fun VoiceProcessingScreen(
    onBack: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BackHandler(onBack = onBack)
    ProcessingSystemBars()
    val colors = MaterialTheme.colorScheme
    Column(
        modifier = modifier.fillMaxSize().background(colors.primary)
            .statusBarsPadding().navigationBarsPadding(),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier.size(32.dp).clip(CircleShape)
                    .background(colors.onPrimary.copy(alpha = 0.08f))
                    .border(1.dp, colors.onPrimary.copy(alpha = 0.12f), CircleShape)
                    .clickable(role = Role.Button, onClick = onBack),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painterResource(R.drawable.ic_voice_idle_chevron_left),
                    contentDescription = stringResource(R.string.voice_processing_back),
                    tint = colors.onPrimary, modifier = Modifier.size(14.dp),
                )
            }
            Text(
                stringResource(R.string.voice_processing_title),
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                color = colors.onPrimary,
            )
        }
        Column(
            modifier = Modifier.weight(1f).verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp).padding(top = 50.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            ProcessingOrb()
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    stringResource(R.string.voice_processing_thinking),
                    style = MaterialTheme.typography.labelLarge,
                    color = colors.secondary,
                )
                Text(
                    stringResource(R.string.voice_processing_transcript),
                    style = MaterialTheme.typography.bodyLarge.copy(lineHeight = 22.5.sp),
                    color = colors.onPrimary.copy(alpha = 0.9f),
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp))
                        .background(colors.onPrimary.copy(alpha = 0.04f))
                        .border(1.dp, colors.onPrimary.copy(alpha = 0.08f), RoundedCornerShape(16.dp))
                        .padding(18.dp),
                )
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    painterResource(R.drawable.ic_voice_processing_loader),
                    contentDescription = null,
                    tint = colors.secondary, modifier = Modifier.size(16.dp),
                )
                Text(
                    stringResource(R.string.voice_processing_checking),
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.onPrimary.copy(alpha = 0.6f),
                )
            }
        }
        Box(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)
                .padding(top = 16.dp, bottom = 24.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                stringResource(R.string.voice_processing_cancel),
                style = MaterialTheme.typography.labelLarge,
                color = colors.onPrimary.copy(alpha = 0.6f),
                modifier = Modifier.clickable(role = Role.Button, onClick = onCancel),
            )
        }
    }
}

@Composable
private fun ProcessingOrb() {
    Box(Modifier.size(150.dp), contentAlignment = Alignment.Center) {
        // The original export includes the core shadow below the 150 dp layout slot.
        Image(
            painterResource(R.drawable.voice_processing_orb), contentDescription = null,
            modifier = Modifier.requiredSize(161.dp, 164.dp).offset(y = 7.dp),
        )
    }
}

@Composable
private fun ProcessingSystemBars() {
    val view = LocalView.current
    DisposableEffect(view) {
        val activity = generateSequence(view.context) { (it as? ContextWrapper)?.baseContext }
            .filterIsInstance<Activity>().firstOrNull()
        val controller = if (!view.isInEditMode && activity != null) {
            WindowCompat.getInsetsController(activity.window, view)
        } else null
        val status = controller?.isAppearanceLightStatusBars
        val navigation = controller?.isAppearanceLightNavigationBars
        controller?.isAppearanceLightStatusBars = false
        controller?.isAppearanceLightNavigationBars = false
        onDispose {
            if (status != null) controller?.isAppearanceLightStatusBars = status
            if (navigation != null) controller?.isAppearanceLightNavigationBars = navigation
        }
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun ProcessingPreview() {
    ExamenTP3Theme { VoiceProcessingScreen({}, {}) }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 640, fontScale = 1.3f)
@Composable
private fun ProcessingCompactPreview() {
    ExamenTP3Theme { VoiceProcessingScreen({}, {}) }
}
