package com.example.examentp3.ui.screens.voice

import android.app.Activity
import android.content.ContextWrapper
import androidx.activity.compose.BackHandler
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.graphics.Color
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
import com.example.examentp3.ui.theme.VoraPillShape

/** Fixed response and static playback illustration; no audio is played. */
@Composable
fun VoiceSpeakingScreen(
    onBack: () -> Unit,
    onInterrupt: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BackHandler(onBack = onBack)
    SpeakingSystemBars()
    val colors = MaterialTheme.colorScheme
    Column(
        modifier = modifier.fillMaxSize().background(colors.primary)
            .statusBarsPadding().navigationBarsPadding().verticalScroll(rememberScrollState()),
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
                    contentDescription = stringResource(R.string.voice_speaking_back),
                    tint = colors.onPrimary, modifier = Modifier.size(14.dp),
                )
            }
            Text(
                stringResource(R.string.voice_speaking_title),
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                color = colors.onPrimary, modifier = Modifier.weight(1f),
            )
            Text(
                stringResource(R.string.voice_speaking_status),
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                color = colors.secondary,
                modifier = Modifier.clip(VoraPillShape)
                    .background(colors.secondary.copy(alpha = 0.15f))
                    .padding(horizontal = 10.dp, vertical = 4.dp),
            )
        }
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp).padding(top = 30.dp),
            verticalArrangement = Arrangement.spacedBy(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            SpeakingOrb()
            Text(
                stringResource(R.string.voice_speaking_response),
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 16.sp, fontWeight = FontWeight.Medium, lineHeight = 24.sp,
                ),
                color = colors.onPrimary,
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp))
                    .background(colors.onPrimary.copy(alpha = 0.05f))
                    .border(1.dp, colors.onPrimary.copy(alpha = 0.1f), RoundedCornerShape(20.dp))
                    .padding(20.dp),
            )
            StaticPlayback()
        }
        Box(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)
                .padding(top = 16.dp, bottom = 24.dp),
        ) {
            Text(
                stringResource(R.string.voice_speaking_interrupt),
                style = MaterialTheme.typography.titleSmall,
                color = colors.onTertiary,
                modifier = Modifier.fillMaxWidth().clip(VoraPillShape).background(colors.tertiary)
                    .clickable(role = Role.Button, onClick = onInterrupt)
                    .padding(horizontal = 32.dp, vertical = 14.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            )
        }
    }
}

@Composable
private fun SpeakingOrb() {
    Box(Modifier.size(140.dp), contentAlignment = Alignment.Center) {
        // Export bounds include the 161 dp ripple and the core shadow below it.
        Image(
            painterResource(R.drawable.voice_speaking_orb), contentDescription = null,
            modifier = Modifier.requiredSize(161.dp, 165.dp).offset(y = 2.dp),
        )
    }
}

@Composable
private fun StaticPlayback() {
    val colors = MaterialTheme.colorScheme
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Figma has a fixed 180 dp fill in a 342 dp track; preserve that visual ratio.
        Box(
            Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp))
                .background(colors.onPrimary.copy(alpha = 0.15f)),
        ) {
            Box(
                Modifier.fillMaxWidth(180f / 342f).fillMaxHeight()
                    .clip(RoundedCornerShape(2.dp)).background(colors.secondary),
            )
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(
                stringResource(R.string.voice_speaking_elapsed),
                style = MaterialTheme.typography.bodySmall,
                color = colors.onPrimary.copy(alpha = 0.6f),
            )
            Text(
                stringResource(R.string.voice_speaking_remaining),
                style = MaterialTheme.typography.bodySmall,
                color = colors.onPrimary.copy(alpha = 0.6f),
            )
        }
        // Visual controls belong to the static playback illustration, not interactive buttons.
        Row(horizontalArrangement = Arrangement.spacedBy(24.dp), verticalAlignment = Alignment.CenterVertically) {
            PlaybackIcon(R.drawable.ic_voice_speaking_repeat, false, colors.onPrimary)
            PlaybackIcon(R.drawable.ic_voice_speaking_pause, true, colors.onSecondary)
            PlaybackIcon(R.drawable.ic_voice_speaking_stop, false, colors.onPrimary)
        }
    }
}

@Composable
private fun PlaybackIcon(@DrawableRes icon: Int, primary: Boolean, tint: Color) {
    Box(
        modifier = Modifier.size(if (primary) 56.dp else 40.dp)
            .background(
                if (primary) MaterialTheme.colorScheme.secondary
                else MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.08f),
                CircleShape,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painterResource(icon), contentDescription = null,
            tint = tint, modifier = Modifier.size(if (primary) 20.dp else 18.dp),
        )
    }
}

@Composable
private fun SpeakingSystemBars() {
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
private fun SpeakingPreview() {
    ExamenTP3Theme { VoiceSpeakingScreen({}, {}) }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 640, fontScale = 1.3f)
@Composable
private fun SpeakingCompactPreview() {
    ExamenTP3Theme { VoiceSpeakingScreen({}, {}) }
}
