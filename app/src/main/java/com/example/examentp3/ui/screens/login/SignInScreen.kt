package com.example.examentp3.ui.screens.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.examentp3.R
import com.example.examentp3.ui.components.*
import com.example.examentp3.ui.theme.ExamenTP3Theme
import com.example.examentp3.ui.theme.signInSubtitle
import com.example.examentp3.ui.theme.signInDescription
import com.example.examentp3.viewmodel.AuthError
import com.example.examentp3.viewmodel.SignInUiState

// Scoped to this screen: the ticket defines this background without changing BASE-01.
private val SignInBackground = Color(0xFFF3F9F5)

@Composable
fun SignInScreen(
    uiState: SignInUiState,
    onSignIn: (email: String, password: String) -> Unit,
    onSignUp: () -> Unit,
    onInputChange: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val exampleEmail = stringResource(R.string.sign_in_example_email)
    var email by rememberSaveable { mutableStateOf(exampleEmail) }
    // Sent to Firebase Authentication only when the build has it (EXT-01); kept out of the saved
    // instance state on purpose, so it is not written to the Bundle.
    var password by remember { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    var notice by remember { mutableStateOf<Int?>(null) }

    Box(
        modifier = modifier.fillMaxSize().background(SignInBackground).safeDrawingPadding().imePadding(),
        contentAlignment = Alignment.TopCenter,
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 448.dp)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .background(Brush.verticalGradient(listOf(MaterialTheme.colorScheme.secondary.copy(alpha = 0.10f), SignInBackground), endY = 600f))
                .padding(horizontal = 24.dp)
                .padding(top = 8.dp, bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = { notice = R.string.sign_in_demo_back },
                    modifier = Modifier.size(48.dp).background(MaterialTheme.colorScheme.surface, CircleShape),
                ) {
                    Icon(painterResource(R.drawable.ic_sign_in_back), stringResource(R.string.sign_in_back), tint = Color.Unspecified, modifier = Modifier.size(20.dp))
                }
                OutlinedButton(
                    onClick = { notice = R.string.sign_in_demo_language },
                    contentPadding = PaddingValues(horizontal = 15.dp),
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                ) {
                    Text(stringResource(R.string.sign_in_language), style = MaterialTheme.typography.labelMedium)
                    Spacer(Modifier.width(6.dp))
                    Icon(painterResource(R.drawable.ic_sign_in_chevron), null, tint = Color.Unspecified, modifier = Modifier.size(14.dp))
                }
            }
            Spacer(Modifier.height(46.dp))
            Image(painterResource(R.drawable.sign_in_logo), stringResource(R.string.sign_in_logo), modifier = Modifier.size(72.dp))
            Spacer(Modifier.height(12.dp))
            Text(stringResource(R.string.sign_in_title), style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(4.dp))
            Text(
                stringResource(R.string.sign_in_subtitle),
                style = MaterialTheme.typography.signInSubtitle,
                color = MaterialTheme.colorScheme.primary,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                stringResource(R.string.sign_in_description),
                style = MaterialTheme.typography.signInDescription,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.widthIn(max = 280.dp),
            )
            Spacer(Modifier.height(28.dp))
            // BASE-02 card reused under a local shape override; global tokens stay intact.
            MaterialTheme(shapes = MaterialTheme.shapes.copy(medium = RoundedCornerShape(24.dp))) {
                VoraCard {
                    Column(Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(stringResource(R.string.sign_in_email), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                            val emailLabel = stringResource(R.string.sign_in_email)
                            VoraSignInField(
                                email, { email = it; onInputChange() }, emailLabel, R.drawable.ic_sign_in_mail,
                                KeyboardOptions(keyboardType = KeyboardType.Email),
                                modifier = Modifier.semantics { contentDescription = emailLabel },
                            )
                        }
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Text(stringResource(R.string.sign_in_password), modifier = Modifier.weight(1f), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                                VoraTextAction(
                                    stringResource(R.string.sign_in_forgot),
                                    { notice = R.string.sign_in_demo_recovery },
                                    modifier = Modifier.weight(2f),
                                )
                            }
                            val passwordLabel = stringResource(R.string.sign_in_password)
                            VoraSignInField(
                                password, { password = it; onInputChange() }, passwordLabel, R.drawable.ic_sign_in_lock,
                                KeyboardOptions(keyboardType = KeyboardType.Password),
                                modifier = Modifier.semantics { contentDescription = passwordLabel },
                                placeholder = stringResource(R.string.sign_in_password_placeholder),
                                transformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                trailing = {
                                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                        Icon(
                                            painterResource(R.drawable.ic_sign_in_eye),
                                            stringResource(if (passwordVisible) R.string.sign_in_hide_password else R.string.sign_in_show_password),
                                            tint = Color.Unspecified, modifier = Modifier.size(20.dp),
                                        )
                                    }
                                },
                            )
                        }
                        uiState.error?.let { error -> VoraAuthErrorText(stringResource(error.message)) }
                        Box {
                            val actionLabel = stringResource(R.string.sign_in_action)
                            // Reuse the BASE-02 surface and click behavior; custom content matches the trailing icon.
                            VoraLimeButton(
                                "", { onSignIn(email, password) },
                                modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp)
                                    .semantics { contentDescription = actionLabel },
                            )
                            if (uiState.isLoading) {
                                VoraAuthProgress(Modifier.align(Alignment.Center))
                            } else {
                                Row(
                                    modifier = Modifier.align(Alignment.Center).padding(horizontal = 16.dp)
                                        .clearAndSetSemantics {},
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                ) {
                                    Text(actionLabel, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onTertiary)
                                    Icon(painterResource(R.drawable.ic_sign_in_arrow), null, tint = Color.Unspecified, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            HorizontalDivider(Modifier.weight(1f), color = MaterialTheme.colorScheme.outline)
                            Text(
                                stringResource(R.string.sign_in_continue),
                                modifier = Modifier.padding(horizontal = 12.dp),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            HorizontalDivider(Modifier.weight(1f), color = MaterialTheme.colorScheme.outline)
                        }
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            VoraSignInService(R.drawable.ic_sign_in_apple, stringResource(R.string.sign_in_apple), { notice = R.string.sign_in_demo_apple }, Modifier.weight(1f))
                            VoraSignInService(R.drawable.ic_sign_in_google, stringResource(R.string.sign_in_google), { notice = R.string.sign_in_demo_google }, Modifier.weight(1f))
                            VoraSignInService(R.drawable.ic_sign_in_face_id, stringResource(R.string.sign_in_face_id), { notice = R.string.sign_in_demo_face_id }, Modifier.weight(1f), biometric = true)
                        }
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(painterResource(R.drawable.ic_sign_in_shield), null, tint = Color.Unspecified, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(8.dp))
                Text(stringResource(R.string.sign_in_security), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(Modifier.height(40.dp))
            FlowRow(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(stringResource(R.string.sign_in_no_account), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.width(4.dp))
                VoraTextAction(stringResource(R.string.sign_in_sign_up), onSignUp, muted = true)
            }
        }
    }
    notice?.let { message ->
        AlertDialog(
            onDismissRequest = { notice = null },
            title = { Text(stringResource(R.string.sign_in_demo_title)) },
            text = { Text(stringResource(message)) },
            confirmButton = { TextButton(onClick = { notice = null }) { Text(stringResource(R.string.sign_in_notice_ok)) } },
        )
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 894)
@Composable
private fun SignInPreview() {
    ExamenTP3Theme { SignInScreen(SignInUiState(), { _, _ -> }, {}, {}) }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 640, fontScale = 1.3f)
@Composable
private fun SignInCompactPreview() {
    ExamenTP3Theme { SignInScreen(SignInUiState(), { _, _ -> }, {}, {}) }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 894)
@Composable
private fun SignInErrorPreview() {
    ExamenTP3Theme { SignInScreen(SignInUiState(error = AuthError.InvalidCredentials), { _, _ -> }, {}, {}) }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 894)
@Composable
private fun SignInLoadingPreview() {
    ExamenTP3Theme { SignInScreen(SignInUiState(isLoading = true), { _, _ -> }, {}, {}) }
}
