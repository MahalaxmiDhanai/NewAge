package com.bloom.app.ui.onboarding

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bloom.app.data.UserRepository
import com.bloom.app.ui.components.OnboardingProgressBars
import com.bloom.app.ui.theme.BackgroundCream
import com.bloom.app.ui.theme.BloomTheme
import com.bloom.app.ui.theme.Dimens
import com.bloom.app.ui.theme.LightPink

@Composable
fun OnboardingScreen(
    userRepository: UserRepository,
    onFinished: () -> Unit
) {
    val viewModel: OnboardingViewModel = viewModel(
        factory = object : androidx.lifecycle.ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return OnboardingViewModel(userRepository) as T
            }
        }
    )
    val state by viewModel.state.collectAsStateWithLifecycle()

    // Navigate when onboarding completes
    LaunchedEffect(state.isComplete) {
        if (state.isComplete) onFinished()
    }

    // System back to previous step (or no-op on step 1)
    BackHandler(enabled = state.currentStep > 1) {
        viewModel.previousStep()
    }

    OnboardingContent(
        state    = state,
        onBack   = { viewModel.previousStep() },
        onGetStarted = { viewModel.nextStep() },
        onNameChanged = viewModel::onNameChanged,
        onContinueName = { viewModel.nextStep() },
        onTrackingToggled = viewModel::onTrackingToggled,
        onContinueTracking = { viewModel.nextStep() },
        onPresetSelected   = viewModel::onPresetReminderSelected,
        onCustomTimeChanged = viewModel::onCustomTimeChanged,
        onEnterBloom = { viewModel.completeOnboarding() },
        onSkip       = { viewModel.completeOnboarding() }
    )
}

@Composable
private fun OnboardingContent(
    state: OnboardingUiState,
    onBack: () -> Unit,
    onGetStarted: () -> Unit,
    onNameChanged: (String) -> Unit,
    onContinueName: () -> Unit,
    onTrackingToggled: (String, Boolean) -> Unit,
    onContinueTracking: () -> Unit,
    onPresetSelected: (String) -> Unit,
    onCustomTimeChanged: (String) -> Unit,
    onEnterBloom: () -> Unit,
    onSkip: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundCream)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // Top row: back button + progress bars
        Row(
            modifier          = Modifier.padding(
                start  = Dimens.SpaceLg,
                end    = Dimens.SpaceLg,
                top    = Dimens.SpaceLg,
                bottom = Dimens.SpaceSm
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (state.currentStep > 1) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(LightPink)
                ) {
                    Icon(
                        imageVector        = Icons.Filled.ChevronLeft,
                        contentDescription = "Back",
                        tint               = androidx.compose.ui.graphics.Color(0xFF8A5A5A)
                    )
                }
                Spacer(modifier = Modifier.width(Dimens.SpaceSm))
                OnboardingProgressBars(
                    currentStep = state.currentStep,
                    modifier    = Modifier.weight(1f)
                )
            } else {
                // Step 1: bars fill the full row, no back button
                OnboardingProgressBars(
                    currentStep = state.currentStep,
                    modifier    = Modifier.fillMaxWidth()
                )
            }
        }

        // Step content
        Box(modifier = Modifier.weight(1f)) {
            when (state.currentStep) {
                1 -> Step1Welcome(onGetStarted = onGetStarted, modifier = Modifier.fillMaxSize())
                2 -> Step2Name(
                    name         = state.name,
                    isNameValid  = state.isNameValid,
                    onNameChanged = onNameChanged,
                    onContinue   = onContinueName,
                    modifier     = Modifier.fillMaxSize()
                )
                3 -> Step3Tracking(
                    trackingOptions = state.trackingOptions,
                    onToggle        = onTrackingToggled,
                    onContinue      = onContinueTracking,
                    modifier        = Modifier.fillMaxSize()
                )
                4 -> Step4Reminder(
                    selectedPreset    = state.selectedReminderPreset,
                    customTime        = state.customTime,
                    onPresetSelected  = onPresetSelected,
                    onCustomTimeChanged = onCustomTimeChanged,
                    onEnterBloom      = onEnterBloom,
                    onSkip            = onSkip,
                    modifier          = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Preview(showBackground = true, heightDp = 800)
@Composable
private fun OnboardingPreview() {
    BloomTheme {
        OnboardingContent(
            state               = OnboardingUiState(currentStep = 1),
            onBack              = {},
            onGetStarted        = {},
            onNameChanged       = {},
            onContinueName      = {},
            onTrackingToggled   = { _, _ -> },
            onContinueTracking  = {},
            onPresetSelected    = {},
            onCustomTimeChanged = {},
            onEnterBloom        = {},
            onSkip              = {}
        )
    }
}
