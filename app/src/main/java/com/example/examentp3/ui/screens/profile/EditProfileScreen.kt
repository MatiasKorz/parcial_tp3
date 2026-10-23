package com.example.examentp3.ui.screens.profile

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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.examentp3.R
import com.example.examentp3.ui.components.VoraButtonSize
import com.example.examentp3.ui.components.VoraInfoMessage
import com.example.examentp3.ui.components.VoraPrimaryButton
import com.example.examentp3.ui.components.VoraTextAction
import com.example.examentp3.ui.components.VoraTextField
import com.example.examentp3.ui.theme.ExamenTP3Theme
import com.example.examentp3.ui.theme.Spacing
import com.example.examentp3.ui.theme.voraColors
import com.example.examentp3.viewmodel.EditProfileUiState

/** Profile / 02 — Edit Profile. Demo screen: the form edits the local profile (no network, no camera). */
@Composable
fun EditProfileScreen(
    uiState: EditProfileUiState,
    onBack: () -> Unit,
    onFirstNameChange: (String) -> Unit,
    onLastNameChange: (String) -> Unit,
    onDateOfBirthChange: (String) -> Unit,
    onHeightChange: (String) -> Unit,
    onWeightChange: (String) -> Unit,
    onGenderChange: (String) -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // Ephemeral UI state: the ViewModel does not need to know whether the notice is open.
    var showPhotoNotice by rememberSaveable { mutableStateOf(false) }

    // No bottom bar here, so the Scaffold adds no insets: this screen handles system bars and keyboard.
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .systemBarsPadding()
            .imePadding(),
    ) {
        EditProfileHeader(onBack = onBack)
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(start = Spacing.screenMargin, end = Spacing.screenMargin, top = Spacing.s12, bottom = Spacing.s24),
            verticalArrangement = Arrangement.spacedBy(Spacing.s20),
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(Spacing.s12),
            ) {
                Image(
                    painter = painterResource(R.drawable.img_profile_avatar),
                    contentDescription = null, // decorative: "Change photo" is right below
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape),
                )
                VoraTextAction(
                    text = stringResource(R.string.edit_profile_change_photo),
                    onClick = { showPhotoNotice = true },
                )
            }
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.s16)) {
                VoraTextField(
                    label = stringResource(R.string.edit_profile_first_name_label),
                    value = uiState.firstName,
                    onValueChange = onFirstNameChange,
                    modifier = Modifier.fillMaxWidth(),
                )
                VoraTextField(
                    label = stringResource(R.string.edit_profile_last_name_label),
                    value = uiState.lastName,
                    onValueChange = onLastNameChange,
                    modifier = Modifier.fillMaxWidth(),
                )
                VoraTextField(
                    label = stringResource(R.string.edit_profile_date_of_birth_label),
                    value = uiState.dateOfBirth,
                    onValueChange = onDateOfBirthChange,
                    modifier = Modifier.fillMaxWidth(),
                )
                // Figma uses 12 between Height and Weight, not the 16 between rows.
                Row(horizontalArrangement = Arrangement.spacedBy(Spacing.s12)) {
                    VoraTextField(
                        label = stringResource(R.string.edit_profile_height_label),
                        value = uiState.height,
                        onValueChange = onHeightChange,
                        modifier = Modifier.weight(1f),
                    )
                    VoraTextField(
                        label = stringResource(R.string.edit_profile_weight_label),
                        value = uiState.weight,
                        onValueChange = onWeightChange,
                        modifier = Modifier.weight(1f),
                    )
                }
                VoraTextField(
                    label = stringResource(R.string.edit_profile_gender_label),
                    value = uiState.gender,
                    onValueChange = onGenderChange,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            VoraInfoMessage(
                text = stringResource(R.string.edit_profile_gender_help),
                icon = ImageVector.vectorResource(R.drawable.ic_edit_profile_info),
            )
            Column(
                modifier = Modifier.padding(top = Spacing.s20),
                verticalArrangement = Arrangement.spacedBy(Spacing.s12),
            ) {
                VoraPrimaryButton(
                    text = stringResource(R.string.edit_profile_save),
                    onClick = onSave,
                    size = VoraButtonSize.Large,
                    modifier = Modifier.fillMaxWidth(),
                )
                Text(
                    text = stringResource(R.string.edit_profile_footer),
                    // Figma: 12 Regular, line height 16. No theme token matches (bodySmall is 11), see BASE-03.
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp, lineHeight = 16.sp),
                    color = MaterialTheme.voraColors.textSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }

    if (showPhotoNotice) {
        AlertDialog(
            onDismissRequest = { showPhotoNotice = false },
            title = { Text(stringResource(R.string.edit_profile_demo_title)) },
            text = { Text(stringResource(R.string.edit_profile_demo_change_photo)) },
            confirmButton = {
                TextButton(onClick = { showPhotoNotice = false }) {
                    Text(stringResource(R.string.edit_profile_demo_ok))
                }
            },
        )
    }
}

@Composable
private fun EditProfileHeader(onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.screenMargin, vertical = Spacing.s12),
        horizontalArrangement = Arrangement.spacedBy(Spacing.s12),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // 32 dp back button: no shared component has this size yet (see BASE-03).
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surface)
                .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
                .clickable(role = Role.Button, onClick = onBack),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.ic_edit_profile_chevron_left),
                contentDescription = stringResource(R.string.edit_profile_back_description),
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(14.dp),
            )
        }
        Text(
            text = stringResource(R.string.edit_profile_title),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
        )
    }
}

@Preview(showBackground = true, heightDp = 844, widthDp = 390)
@Composable
private fun EditProfileScreenPreview() {
    ExamenTP3Theme {
        EditProfileScreen(
            uiState = EditProfileUiState(
                firstName = "Alex",
                lastName = "Morgan",
                dateOfBirth = "14 March 1995",
                height = "178 cm",
                weight = "76 kg",
                gender = "Prefer not to say",
            ),
            onBack = {},
            onFirstNameChange = {},
            onLastNameChange = {},
            onDateOfBirthChange = {},
            onHeightChange = {},
            onWeightChange = {},
            onGenderChange = {},
            onSave = {},
        )
    }
}
