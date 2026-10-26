package com.example.examentp3.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.examentp3.R
import com.example.examentp3.ui.theme.ExamenTP3Theme
import com.example.examentp3.ui.theme.Spacing

/** Login-specific decoration preserves the existing BASE-02 field API. */
@Composable
internal fun VoraSignInField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    @DrawableRes icon: Int,
    keyboardOptions: KeyboardOptions,
    modifier: Modifier = Modifier,
    transformation: VisualTransformation = VisualTransformation.None,
    placeholder: String = "",
    trailing: @Composable (() -> Unit)? = null,
    minHeight: Dp = 55.dp,
    verticalPadding: Dp = 16.dp,
    leadingGap: Dp = 11.dp,
    fieldShape: Shape = RoundedCornerShape(16.dp),
    containerColor: Color = MaterialTheme.colorScheme.background,
    valueStyle: TextStyle = MaterialTheme.typography.bodyMedium,
    iconTint: Color = Color.Unspecified,
    iconSize: Dp = 20.dp,
    borderColor: Color = MaterialTheme.colorScheme.outline,
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        keyboardOptions = keyboardOptions,
        visualTransformation = transformation,
        textStyle = valueStyle.copy(color = MaterialTheme.colorScheme.onSurface),
        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
        modifier = modifier.fillMaxWidth().semantics { contentDescription = label },
        decorationBox = { field ->
            Row(
                modifier = Modifier
                    .heightIn(min = minHeight)
                    .background(containerColor, fieldShape)
                    .border(1.dp, borderColor, fieldShape)
                    .padding(start = 14.dp, end = if (trailing == null) 16.dp else 0.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(Modifier.size(20.dp), contentAlignment = Alignment.Center) {
                    Icon(painterResource(icon), contentDescription = null, tint = iconTint, modifier = Modifier.size(iconSize))
                }
                Spacer(Modifier.width(leadingGap))
                Box(Modifier.weight(1f).padding(vertical = verticalPadding)) {
                    if (value.isEmpty()) Text(placeholder, style = valueStyle, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    field()
                }
                trailing?.invoke()
            }
        },
    )
}

@Composable
internal fun VoraSignInService(
    @DrawableRes icon: Int,
    description: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    biometric: Boolean = false,
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.heightIn(min = 48.dp),
        shape = RoundedCornerShape(16.dp),
        contentPadding = PaddingValues(Spacing.s12),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = if (biometric) MaterialTheme.colorScheme.secondary.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surface,
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (biometric) MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outline,
        ),
    ) {
        Icon(painterResource(icon), contentDescription = description, tint = Color.Unspecified, modifier = Modifier.size(20.dp))
    }
}

/**
 * Form or Firebase Authentication message (EXT-01). The Figma frames draw no error state, so it uses
 * the theme's error color; the live region makes TalkBack read it when it appears.
 */
@Composable
internal fun VoraAuthErrorText(message: String, modifier: Modifier = Modifier) {
    Text(
        message,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.error,
        modifier = modifier.fillMaxWidth().semantics { liveRegion = LiveRegionMode.Polite },
    )
}

/** Replaces the label of the lime action button while Firebase answers. */
@Composable
internal fun VoraAuthProgress(modifier: Modifier = Modifier) {
    CircularProgressIndicator(
        modifier = modifier.size(20.dp),
        color = MaterialTheme.colorScheme.onTertiary,
        strokeWidth = 2.dp,
    )
}

@Preview(showBackground = true)
@Composable
private fun SignInComponentsPreview() {
    ExamenTP3Theme {
        Column(Modifier.padding(24.dp)) {
            VoraSignInField("alex.morgan@email.com", {}, "Email Address", R.drawable.ic_sign_in_mail, KeyboardOptions.Default)
            VoraSignInService(R.drawable.ic_sign_in_apple, "Apple", {})
            VoraAuthErrorText("Incorrect email or password.")
            VoraAuthProgress()
        }
    }
}
