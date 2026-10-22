package com.example.examentp3.ui.screens.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.examentp3.R
import com.example.examentp3.ui.components.VoraCard
import com.example.examentp3.ui.components.VoraLimeButton
import com.example.examentp3.ui.components.VoraSignInField
import com.example.examentp3.ui.components.VoraSignInService
import com.example.examentp3.ui.components.VoraTextAction
import com.example.examentp3.ui.theme.ExamenTP3Theme
import com.example.examentp3.ui.theme.SignUpTheme

@Composable
fun SignUpScreen(onBack: () -> Unit, modifier: Modifier = Modifier) {
    val exampleName = stringResource(R.string.sign_up_example_name)
    val exampleEmail = stringResource(R.string.sign_up_example_email)
    var name by rememberSaveable { mutableStateOf(exampleName) }
    var email by rememberSaveable { mutableStateOf(exampleEmail) }
    var password by remember { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    var notice by remember { mutableStateOf<Int?>(null) }

    SignUpTheme {
        Box(
            modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
                .safeDrawingPadding().imePadding(),
            contentAlignment = Alignment.TopCenter,
        ) {
            Column(
                modifier = Modifier.widthIn(max = 448.dp).fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .background(Brush.verticalGradient(listOf(
                        MaterialTheme.colorScheme.surfaceVariant,
                        MaterialTheme.colorScheme.background,
                    )))
                    .padding(horizontal = 24.dp).padding(top = 8.dp, bottom = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onBack, Modifier.size(48.dp).background(MaterialTheme.colorScheme.surface, CircleShape)) {
                        Icon(painterResource(R.drawable.ic_sign_in_back), stringResource(R.string.sign_up_back), tint = Color.Unspecified, modifier = Modifier.size(20.dp))
                    }
                    OutlinedButton(
                        onClick = { notice = R.string.sign_up_demo_language },
                        contentPadding = PaddingValues(horizontal = 15.dp),
                        border = null,
                        colors = ButtonDefaults.outlinedButtonColors(containerColor = MaterialTheme.colorScheme.surface),
                    ) {
                        Text(stringResource(R.string.sign_up_language), style = MaterialTheme.typography.labelMedium)
                        Spacer(Modifier.width(6.dp))
                        Icon(painterResource(R.drawable.ic_sign_in_chevron), null, tint = Color.Unspecified, modifier = Modifier.size(14.dp))
                    }
                }
                Spacer(Modifier.height(12.dp))
                Box(Modifier.size(56.dp), contentAlignment = Alignment.Center) {
                    // Export includes ambient effects outside the original 56 dp orb.
                    Image(painterResource(R.drawable.sign_up_logo), stringResource(R.string.sign_up_logo),
                        Modifier.requiredSize(width = 96.dp, height = 98.dp))
                }
                Spacer(Modifier.height(6.dp))
                Text(stringResource(R.string.sign_up_title), style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.height(5.dp))
                Text(stringResource(R.string.sign_up_subtitle), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.height(24.dp))
                VoraCard {
                    Column(Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        SignUpField(
                            stringResource(R.string.sign_up_name), name, { name = it },
                            R.drawable.ic_sign_up_person, KeyboardOptions(keyboardType = KeyboardType.Text),
                        )
                        SignUpField(
                            stringResource(R.string.sign_up_email), email, { email = it },
                            R.drawable.ic_sign_in_mail, KeyboardOptions(keyboardType = KeyboardType.Email),
                        )
                        SignUpField(
                            stringResource(R.string.sign_up_password), password, { password = it },
                            R.drawable.ic_sign_in_lock, KeyboardOptions(keyboardType = KeyboardType.Password),
                            placeholder = stringResource(R.string.sign_up_password_placeholder),
                            transformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            trailing = {
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(painterResource(R.drawable.ic_sign_in_eye),
                                        stringResource(if (passwordVisible) R.string.sign_up_hide_password else R.string.sign_up_show_password),
                                        tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                                }
                            },
                        )
                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            Row(Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                                Icon(painterResource(R.drawable.ic_sign_up_security_check), null, tint = Color.Unspecified, modifier = Modifier.size(12.5.dp))
                                Spacer(Modifier.width(6.dp))
                                Text(stringResource(R.string.sign_up_secure), style = MaterialTheme.typography.bodySmall)
                            }
                            TextButton(onClick = { notice = R.string.sign_up_demo_help }, contentPadding = PaddingValues(0.dp)) {
                                Text(stringResource(R.string.sign_up_help), style = MaterialTheme.typography.bodySmall)
                            }
                        }
                        Box {
                            val action = stringResource(R.string.sign_up_action)
                            VoraLimeButton("", { notice = R.string.sign_up_demo_register },
                                modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp).semantics { contentDescription = action })
                            Row(Modifier.align(Alignment.Center).padding(horizontal = 16.dp).clearAndSetSemantics {},
                                horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                Text(action, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onTertiary)
                                Icon(painterResource(R.drawable.ic_sign_in_arrow), null, tint = Color.Unspecified, modifier = Modifier.size(14.dp))
                            }
                        }
                        Text(stringResource(R.string.sign_up_divider), style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.align(Alignment.CenterHorizontally))
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            VoraSignInService(R.drawable.ic_sign_in_apple, stringResource(R.string.sign_up_apple),
                                { notice = R.string.sign_up_demo_apple }, Modifier.weight(1f))
                            VoraSignInService(R.drawable.ic_sign_in_google, stringResource(R.string.sign_up_google),
                                { notice = R.string.sign_up_demo_google }, Modifier.weight(1f))
                            VoraSignInService(R.drawable.ic_sign_in_face_id, stringResource(R.string.sign_up_face_id),
                                { notice = R.string.sign_up_demo_face_id }, Modifier.weight(1f), biometric = true)
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))
                Row(Modifier.padding(horizontal = 12.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(painterResource(R.drawable.ic_sign_in_shield), null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(12.5.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(stringResource(R.string.sign_up_biometric), style = MaterialTheme.typography.bodyLarge)
                }
                Spacer(Modifier.height(10.dp))
                FlowRow(horizontalArrangement = Arrangement.Center, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(stringResource(R.string.sign_up_issues), style = MaterialTheme.typography.bodyLarge)
                    Spacer(Modifier.width(4.dp))
                    VoraTextAction(stringResource(R.string.sign_up_recover), { notice = R.string.sign_up_demo_recover })
                }
                Spacer(Modifier.height(24.dp))
                FlowRow(horizontalArrangement = Arrangement.Center, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(stringResource(R.string.sign_up_existing), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.width(4.dp))
                    VoraTextAction(stringResource(R.string.sign_up_sign_in), onBack)
                }
            }
        }
        notice?.let { message ->
            AlertDialog(
                onDismissRequest = { notice = null },
                title = { Text(stringResource(R.string.sign_up_demo_title)) },
                text = { Text(stringResource(message)) },
                confirmButton = { TextButton(onClick = { notice = null }) { Text(stringResource(R.string.sign_up_ok)) } },
            )
        }
    }
}

@Composable
private fun SignUpField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    icon: Int,
    keyboard: KeyboardOptions,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    transformation: VisualTransformation = VisualTransformation.None,
    trailing: @Composable (() -> Unit)? = null,
) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(label, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.primary)
        VoraSignInField(
            value, onValueChange, label, icon, keyboard,
            transformation = transformation, placeholder = placeholder, trailing = trailing,
            minHeight = 52.dp, verticalPadding = 14.dp, leadingGap = 7.dp,
            fieldShape = MaterialTheme.shapes.small,
            containerColor = MaterialTheme.colorScheme.surface,
            valueStyle = MaterialTheme.typography.bodyLarge,
            iconTint = MaterialTheme.colorScheme.primary,
            borderColor = MaterialTheme.colorScheme.outlineVariant,
            iconSize = if (icon == R.drawable.ic_sign_up_person) 13.3333.dp else 20.dp,
        )
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 921)
@Composable
private fun SignUpPreview() {
    ExamenTP3Theme { SignUpScreen({}) }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 640, fontScale = 1.3f)
@Composable
private fun SignUpCompactPreview() {
    ExamenTP3Theme { SignUpScreen({}) }
}
