package com.example.examentp3.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.examentp3.R
import com.example.examentp3.ui.theme.ExamenTP3Theme

/** Original Figma orb, including the ripple extending beyond its 170 dp layout slot. */
@Composable
fun VoraListeningOrb(modifier: Modifier = Modifier) {
    Box(modifier = modifier.size(170.dp), contentAlignment = Alignment.Center) {
        Image(
            painter = painterResource(R.drawable.voice_listening_orb),
            contentDescription = null,
            modifier = Modifier.requiredSize(195.5.dp),
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF123D35)
@Composable
private fun VoraListeningOrbPreview() {
    ExamenTP3Theme { VoraListeningOrb() }
}
