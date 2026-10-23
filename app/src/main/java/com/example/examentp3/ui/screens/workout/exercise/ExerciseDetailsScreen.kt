package com.example.examentp3.ui.screens.workout.exercise

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.examentp3.R
import com.example.examentp3.ui.components.VoraPrimaryButton
import com.example.examentp3.ui.theme.*

/** Figma-only Seated Cable Row guide. Exercise-specific data/identity remains pending NUC-02. */
@Composable
fun ExerciseDetailsScreen(onBack: () -> Unit, modifier: Modifier = Modifier) {
    var notice by rememberSaveable { mutableStateOf<Int?>(null) }
    LazyColumn(
        modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
            .statusBarsPadding().navigationBarsPadding(),
        contentPadding = PaddingValues(
            start = Spacing.screenMargin, end = Spacing.screenMargin,
            top = Spacing.s12, bottom = Spacing.s24,
        ),
        verticalArrangement = Arrangement.spacedBy(Spacing.s16),
    ) {
        item(key = "header") {
            GuideHeader(onBack) { notice = R.string.exercise_details_help_notice }
        }
        item(key = "exercise") {
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.s4)) {
                Text(stringResource(R.string.exercise_details_title), style = MaterialTheme.typography.exerciseDetailsTitle)
                Text(
                    stringResource(R.string.exercise_details_target),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.voraColors.textSecondary,
                )
            }
        }
        item(key = "demonstration_image") {
            Image(
                painterResource(R.drawable.exercise_details_demo_area),
                contentDescription = stringResource(R.string.exercise_details_image_description),
                modifier = Modifier.fillMaxWidth().aspectRatio(350f / 160f).clip(MaterialTheme.shapes.medium),
                contentScale = ContentScale.Fit,
            )
        }
        item(key = "steps") { PerformanceSteps() }
        item(key = "cues") { FormCues() }
        item(key = "ask_vora") { AskVora { notice = R.string.exercise_details_coach_notice } }
        item(key = "back") {
            MaterialTheme(
                typography = MaterialTheme.typography.copy(labelMedium = MaterialTheme.typography.exerciseDetailsAction),
            ) {
                VoraPrimaryButton(
                    stringResource(R.string.exercise_details_back_to_workout),
                    onBack,
                    modifier = Modifier.fillMaxWidth().heightIn(min = 44.dp),
                )
            }
        }
    }
    notice?.let { message ->
        AlertDialog(
            onDismissRequest = { notice = null },
            title = { Text(stringResource(R.string.exercise_details_demo_title)) },
            text = { Text(stringResource(message)) },
            confirmButton = {
                TextButton(onClick = { notice = null }) { Text(stringResource(R.string.exercise_details_ok)) }
            },
        )
    }
}

@Composable
private fun GuideHeader(onBack: () -> Unit, onHelp: () -> Unit) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Icon(
            painterResource(R.drawable.ic_exercise_details_back),
            contentDescription = stringResource(R.string.exercise_details_back),
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp).clickable(role = Role.Button, onClick = onBack),
        )
        Text(
            stringResource(R.string.exercise_details_guide),
            style = MaterialTheme.typography.exerciseDetailsHeader,
            modifier = Modifier.weight(1f),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
        )
        Icon(
            painterResource(R.drawable.ic_exercise_details_help),
            contentDescription = stringResource(R.string.exercise_details_help),
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp).clickable(role = Role.Button, onClick = onHelp),
        )
    }
}

@Composable
private fun PerformanceSteps() {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.s10)) {
        Text(stringResource(R.string.exercise_details_steps), style = MaterialTheme.typography.exerciseDetailsSectionTitle)
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.s6)) {
            listOf(
                R.string.exercise_details_step_one,
                R.string.exercise_details_step_two,
                R.string.exercise_details_step_three,
                R.string.exercise_details_step_four,
                R.string.exercise_details_step_five,
            ).forEach { step ->
                Text(stringResource(step), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.voraColors.textSecondary)
            }
        }
    }
}

@Composable
private fun FormCues() {
    Column(
        modifier = Modifier.fillMaxWidth().clip(MaterialTheme.shapes.small)
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outline, MaterialTheme.shapes.small)
            .padding(Spacing.s12),
        verticalArrangement = Arrangement.spacedBy(Spacing.s8),
    ) {
        Text(
            stringResource(R.string.exercise_details_cues),
            style = MaterialTheme.typography.exerciseDetailsCueLabel, color = MaterialTheme.colorScheme.primary,
        )
        Text(stringResource(R.string.exercise_details_cue_one), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.voraColors.textSecondary)
        Text(stringResource(R.string.exercise_details_cue_two), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.voraColors.textSecondary)
    }
}

@Composable
private fun AskVora(onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clip(MaterialTheme.shapes.small)
            .background(MaterialTheme.voraColors.mintSoft)
            .clickable(role = Role.Button, onClick = onClick).padding(Spacing.s12),
        horizontalArrangement = Arrangement.spacedBy(Spacing.s10),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(painterResource(R.drawable.ic_home_orb), contentDescription = null, modifier = Modifier.size(24.dp))
        Text(
            stringResource(R.string.exercise_details_ask),
            style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.weight(1f),
        )
        Icon(
            painterResource(R.drawable.ic_workout_details_chevron), contentDescription = null,
            tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp),
        )
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun ExerciseDetailsPreview() {
    ExamenTP3Theme { ExerciseDetailsScreen({}) }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 640, fontScale = 1.3f)
@Composable
private fun ExerciseDetailsCompactPreview() {
    ExamenTP3Theme { ExerciseDetailsScreen({}) }
}
