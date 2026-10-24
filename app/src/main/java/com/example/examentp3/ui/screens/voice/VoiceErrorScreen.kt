package com.example.examentp3.ui.screens.voice

import android.app.Activity
import android.content.ContextWrapper
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import com.example.examentp3.ui.components.VoraTextAction
import com.example.examentp3.ui.theme.ExamenTP3Theme
import com.example.examentp3.ui.theme.VoraPillShape
import com.example.examentp3.ui.theme.voiceErrorPermissionBackground
import com.example.examentp3.ui.theme.voraColors
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/** Demonstration only: this screen never checks or requests microphone access. */
@Composable
fun VoiceErrorScreen(
    onBack: () -> Unit,
    onRetry: () -> Unit,
    onTypeInstead: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BackHandler(onBack = onBack)
    ErrorSystemBars()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var accessNoticeJob by remember { mutableStateOf<Job?>(null) }
    val accessNotice = stringResource(R.string.voice_error_access_notice)
    val showAccessNotice = {
        // Restart the notice on every tap instead of queueing stale messages.
        accessNoticeJob?.cancel()
        accessNoticeJob = scope.launch {
            snackbarHostState.showSnackbar(accessNotice, duration = SnackbarDuration.Short)
        }
    }
    val colors = MaterialTheme.colorScheme
    Box(modifier = modifier.fillMaxSize()) {
    Column(
        modifier = Modifier.fillMaxSize().background(colors.background)
            .statusBarsPadding().navigationBarsPadding(),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier.size(32.dp).clip(CircleShape).background(colors.surface)
                    .border(1.dp, colors.outline, CircleShape)
                    .clickable(role = Role.Button, onClick = onBack),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painterResource(R.drawable.ic_voice_idle_chevron_left),
                    contentDescription = stringResource(R.string.voice_error_back),
                    tint = colors.primary, modifier = Modifier.size(14.dp),
                )
            }
            Text(
                stringResource(R.string.voice_error_title),
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                color = colors.onBackground,
            )
        }
        Column(
            modifier = Modifier.weight(1f).verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp).padding(top = 30.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                painterResource(R.drawable.voice_error_orb), contentDescription = null,
                modifier = Modifier.size(120.dp),
            )
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    stringResource(R.string.voice_error_heading),
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = colors.onBackground, textAlign = TextAlign.Center,
                )
                Text(
                    stringResource(R.string.voice_error_description),
                    style = MaterialTheme.typography.bodyLarge.copy(fontSize = 14.sp, lineHeight = 19.sp),
                    color = MaterialTheme.voraColors.textSecondary, textAlign = TextAlign.Center,
                )
            }
            PermissionCard(onAllow = showAccessNotice, onNotNow = onBack)
        }
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)
                .padding(top = 16.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                stringResource(R.string.voice_error_retry),
                style = MaterialTheme.typography.titleSmall,
                color = colors.onSecondary, textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().clip(VoraPillShape).background(colors.secondary)
                    .clickable(role = Role.Button, onClick = onRetry)
                    .padding(horizontal = 32.dp, vertical = 14.dp),
            )
            VoraTextAction(
                text = stringResource(R.string.voice_error_type_instead),
                onClick = onTypeInstead, muted = true,
            )
        }
    }
    SnackbarHost(
        hostState = snackbarHostState,
        modifier = Modifier.align(Alignment.BottomCenter).navigationBarsPadding()
            .padding(bottom = 120.dp),
    )
    }
}

@Composable
private fun PermissionCard(onAllow: () -> Unit, onNotNow: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(20.dp)
    Column(
        modifier = Modifier.fillMaxWidth().clip(shape).background(colors.surface)
            .border(1.dp, colors.outline, shape).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                Modifier.size(36.dp).background(colors.voiceErrorPermissionBackground, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painterResource(R.drawable.ic_voice_conversation_mic), contentDescription = null,
                    tint = colors.error, modifier = Modifier.size(18.dp),
                )
            }
            Text(
                stringResource(R.string.voice_error_permission_title),
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                color = colors.onSurface,
            )
        }
        Text(
            stringResource(R.string.voice_error_permission_description),
            style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 18.2.sp),
            color = MaterialTheme.voraColors.textSecondary,
        )
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            PermissionAction(
                stringResource(R.string.voice_error_allow), true, onAllow, Modifier.weight(1f),
            )
            PermissionAction(
                stringResource(R.string.voice_error_not_now), false, onNotNow, Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun PermissionAction(text: String, filled: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val colors = MaterialTheme.colorScheme
    Text(
        text,
        style = MaterialTheme.typography.labelMedium.copy(
            fontWeight = if (filled) FontWeight.Bold else FontWeight.SemiBold,
        ),
        color = if (filled) colors.onPrimary else MaterialTheme.voraColors.textSecondary,
        textAlign = TextAlign.Center,
        modifier = modifier.clip(VoraPillShape)
            .background(if (filled) colors.primary else Color.Transparent)
            .then(if (!filled) Modifier.border(1.dp, colors.outline, VoraPillShape) else Modifier)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp),
    )
}

@Composable
private fun ErrorSystemBars() {
    val view = LocalView.current
    LaunchedEffect(view) {
        val activity = generateSequence(view.context) { (it as? ContextWrapper)?.baseContext }
            .filterIsInstance<Activity>().firstOrNull()
        if (!view.isInEditMode && activity != null) {
            WindowCompat.getInsetsController(activity.window, view).apply {
                isAppearanceLightStatusBars = true
                isAppearanceLightNavigationBars = true
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun ErrorPreview() {
    ExamenTP3Theme { VoiceErrorScreen({}, {}, {}) }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 640, fontScale = 1.3f)
@Composable
private fun ErrorCompactPreview() {
    ExamenTP3Theme { VoiceErrorScreen({}, {}, {}) }
}
