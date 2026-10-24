package com.example.examentp3.ui.screens.voice

import android.app.Activity
import android.content.ContextWrapper
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import com.example.examentp3.R
import com.example.examentp3.ui.components.VoraListeningOrb
import com.example.examentp3.ui.theme.ExamenTP3Theme

/** Fixed Figma transcript; actions simulate the voice flow without recording audio. */
@Composable
fun VoiceListeningScreen(
    onBack: () -> Unit,
    onProcess: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BackHandler(onBack = onBack)
    ListeningSystemBars()
    val colors = MaterialTheme.colorScheme
    Column(
        modifier = modifier.fillMaxSize().background(colors.primary)
            .statusBarsPadding().navigationBarsPadding(),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                modifier = Modifier.weight(1f),
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
                        contentDescription = stringResource(R.string.voice_listening_back),
                        tint = colors.onPrimary, modifier = Modifier.size(14.dp),
                    )
                }
                Text(
                    stringResource(R.string.voice_listening_title),
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                    color = colors.onPrimary,
                )
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(Modifier.size(8.dp).background(colors.secondary, CircleShape))
                Text(
                    stringResource(R.string.voice_listening_live_audio),
                    style = MaterialTheme.typography.labelMedium.copy(fontSize = 12.sp),
                    color = colors.secondary,
                )
            }
        }
        Column(
            modifier = Modifier.weight(1f).verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp).padding(top = 40.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            VoraListeningOrb()
            Column(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(24.dp))
                    .background(colors.onPrimary.copy(alpha = 0.06f))
                    .border(1.dp, colors.onPrimary.copy(alpha = 0.1f), RoundedCornerShape(24.dp))
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(
                    stringResource(R.string.voice_listening_transcript_label),
                    style = MaterialTheme.typography.labelMedium.copy(fontSize = 12.sp),
                    color = colors.secondary,
                )
                Text(
                    stringResource(R.string.voice_listening_transcript),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Medium, lineHeight = 27.sp,
                    ),
                    color = colors.onPrimary,
                )
            }
            Text(
                stringResource(R.string.voice_listening_hint),
                style = MaterialTheme.typography.bodyMedium,
                color = colors.onPrimary.copy(alpha = 0.6f),
                textAlign = TextAlign.Center,
            )
        }
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)
                .padding(top = 16.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier.size(80.dp).clip(CircleShape).background(colors.secondary)
                    .clickable(role = Role.Button, onClick = onProcess),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painterResource(R.drawable.ic_voice_idle_mic),
                    contentDescription = stringResource(R.string.voice_listening_process),
                    tint = colors.onSecondary, modifier = Modifier.size(28.dp),
                )
            }
            Text(
                stringResource(R.string.voice_listening_cancel),
                style = MaterialTheme.typography.labelLarge,
                color = colors.secondary,
                modifier = Modifier.clickable(role = Role.Button, onClick = onBack),
            )
        }
    }
}

@Composable
private fun ListeningSystemBars() {
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
private fun ListeningPreview() {
    ExamenTP3Theme { VoiceListeningScreen({}, {}) }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 640, fontScale = 1.3f)
@Composable
private fun ListeningCompactPreview() {
    ExamenTP3Theme { VoiceListeningScreen({}, {}) }
}
