package com.example.examentp3.ui.screens.nutrition

import android.app.Activity
import android.content.ContextWrapper
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import com.example.examentp3.R
import com.example.examentp3.ui.theme.ExamenTP3Theme
import com.example.examentp3.ui.theme.Spacing
import com.example.examentp3.ui.theme.VoraPillShape

// Figma alphas of white over the dark green background.
private const val CardFillAlpha = 0.05f
private const val CardBorderAlpha = 0.1f
private const val CloseButtonAlpha = 0.12f
private const val HintAlpha = 0.6f
private const val TypeInsteadAlpha = 0.8f

private val StatusDotSize = 8.dp
private val CloseButtonSize = 36.dp
private val OrbSlotSize = 160.dp
private val OrbDrawableSize = 184.dp // the ripple ring is wider than the 160 slot
private val WaveWidth = 28.dp
private val WaveHeight = 20.dp

/**
 * Nutrition / 04 — Voice Food Logging. Demo screen (NAV05): nothing is recorded, no microphone
 * permission is requested and the transcript is a fixed sample from the design.
 */
@Composable
fun VoiceFoodLoggingScreen(
    onClose: () -> Unit,
    onDoneSpeaking: () -> Unit,
    onTypeInstead: () -> Unit,
    modifier: Modifier = Modifier,
) {
    VoiceFoodLoggingSystemBars()
    // Not a tab destination: there is no Scaffold padding here, so the screen handles the system bars itself.
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary)
            .verticalScroll(rememberScrollState())
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        VoiceFoodLoggingHeader(onClose = onClose)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = Spacing.s40),
            verticalArrangement = Arrangement.spacedBy(Spacing.s32),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            VoiceFoodLoggingOrb()
            ListeningStatus()
        }
        TranscriptCard(modifier = Modifier.padding(horizontal = Spacing.s24))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = Spacing.s24, end = Spacing.s24, top = Spacing.s16, bottom = Spacing.s24),
            verticalArrangement = Arrangement.spacedBy(Spacing.s16),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            DoneSpeakingButton(onClick = onDoneSpeaking)
            TypeInsteadAction(onClick = onTypeInstead)
        }
    }
}

@Composable
private fun VoiceFoodLoggingHeader(onClose: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = Spacing.s20, end = Spacing.s20, top = Spacing.s12),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(Spacing.s8),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(StatusDotSize)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.secondary),
            )
            Text(
                text = stringResource(R.string.voice_food_logging_title),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onPrimary,
            )
        }
        Box(
            modifier = Modifier
                .size(CloseButtonSize)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.onPrimary.copy(alpha = CloseButtonAlpha))
                .clickable(role = Role.Button, onClick = onClose),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_voice_food_logging_close),
                contentDescription = stringResource(R.string.voice_food_logging_close_description),
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(CloseButtonSize),
            )
        }
    }
}

@Composable
private fun VoiceFoodLoggingOrb() {
    Box(modifier = Modifier.size(OrbSlotSize), contentAlignment = Alignment.Center) {
        Image(
            painter = painterResource(R.drawable.ic_voice_food_logging_orb),
            contentDescription = null, // decorative
            modifier = Modifier.requiredSize(OrbDrawableSize),
        )
        // Same waveform as Voice / 01 (identical asset in Figma).
        Image(
            painter = painterResource(R.drawable.ic_voice_idle_waveform),
            contentDescription = null, // decorative
            modifier = Modifier.size(width = WaveWidth, height = WaveHeight),
        )
    }
}

@Composable
private fun ListeningStatus() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Spacing.s8),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(R.string.voice_food_logging_listening),
            // Figma: 20 SemiBold; no token has it (BASE-03).
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.secondary,
            textAlign = TextAlign.Center,
        )
        Text(
            text = stringResource(R.string.voice_food_logging_hint),
            // Figma: 14 Regular; no token has it (BASE-03).
            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = HintAlpha),
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun TranscriptCard(modifier: Modifier = Modifier) {
    val shape = MaterialTheme.shapes.large
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(MaterialTheme.colorScheme.onPrimary.copy(alpha = CardFillAlpha))
            .border(1.dp, MaterialTheme.colorScheme.onPrimary.copy(alpha = CardBorderAlpha), shape)
            .padding(Spacing.s16),
    ) {
        Text(
            text = stringResource(R.string.voice_food_logging_transcript),
            // Figma: 15 Medium with 1.5 line height; no token has it (BASE-03).
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium, lineHeight = 22.5.sp),
            color = MaterialTheme.colorScheme.onPrimary,
        )
    }
}

/** Mint pill from the design; the shared buttons have no mint variant (BASE-03). */
@Composable
private fun DoneSpeakingButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(VoraPillShape)
            .background(MaterialTheme.colorScheme.secondary)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = Spacing.s32, vertical = Spacing.s14),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = stringResource(R.string.voice_food_logging_done),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSecondary,
        )
    }
}

/** VoraTextAction's muted color is meant for light screens; here the text is white 80 % (BASE-03). */
@Composable
private fun TypeInsteadAction(onClick: () -> Unit) {
    Text(
        text = stringResource(R.string.voice_food_logging_type_instead),
        style = MaterialTheme.typography.labelMedium.copy(textDecoration = TextDecoration.Underline),
        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = TypeInsteadAlpha),
        modifier = Modifier.clickable(role = Role.Button, onClick = onClick),
    )
}

/** Light system bar icons over the dark background, restored on leave (same pattern as PreWorkout). */
@Composable
private fun VoiceFoodLoggingSystemBars() {
    val view = LocalView.current
    DisposableEffect(view) {
        val activity = generateSequence(view.context) { (it as? ContextWrapper)?.baseContext }
            .filterIsInstance<Activity>().firstOrNull()
        val controller = if (!view.isInEditMode && activity != null) {
            WindowCompat.getInsetsController(activity.window, view)
        } else {
            null
        }
        val lightStatus = controller?.isAppearanceLightStatusBars
        val lightNavigation = controller?.isAppearanceLightNavigationBars
        controller?.isAppearanceLightStatusBars = false
        controller?.isAppearanceLightNavigationBars = false
        onDispose {
            if (lightStatus != null) controller?.isAppearanceLightStatusBars = lightStatus
            if (lightNavigation != null) controller?.isAppearanceLightNavigationBars = lightNavigation
        }
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun VoiceFoodLoggingScreenPreview() {
    ExamenTP3Theme {
        VoiceFoodLoggingScreen(onClose = {}, onDoneSpeaking = {}, onTypeInstead = {})
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
private fun VoiceFoodLoggingScreenSmallPreview() {
    ExamenTP3Theme {
        VoiceFoodLoggingScreen(onClose = {}, onDoneSpeaking = {}, onTypeInstead = {})
    }
}
