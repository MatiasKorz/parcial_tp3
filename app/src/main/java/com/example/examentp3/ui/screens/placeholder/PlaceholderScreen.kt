package com.example.examentp3.ui.screens.placeholder

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.examentp3.R
import com.example.examentp3.ui.components.VoraPrimaryButton
import com.example.examentp3.ui.components.VoraTextAction
import com.example.examentp3.ui.theme.Spacing
import com.example.examentp3.ui.theme.voraColors

data class PlaceholderLink(val label: String, val onClick: () -> Unit)

/** Provisional screen: each PAN card replaces it when it builds its frame. */
@Composable
fun PlaceholderScreen(
    title: String,
    links: List<PlaceholderLink>,
    modifier: Modifier = Modifier,
    detail: String? = null,
    onBack: (() -> Unit)? = null,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .systemBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(Spacing.screenMargin),
        verticalArrangement = Arrangement.spacedBy(Spacing.s12),
    ) {
        Text(text = title, style = MaterialTheme.typography.titleLarge)
        Text(
            text = stringResource(R.string.placeholder_note),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.voraColors.textSecondary,
        )
        if (detail != null) {
            Text(text = detail, style = MaterialTheme.typography.bodyMedium)
        }
        links.forEach { link ->
            VoraPrimaryButton(text = link.label, onClick = link.onClick)
        }
        if (onBack != null) {
            VoraTextAction(text = stringResource(R.string.placeholder_back), onClick = onBack)
        }
    }
}