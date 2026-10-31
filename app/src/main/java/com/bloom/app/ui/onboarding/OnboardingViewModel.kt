package com.bloom.app.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bloom.app.data.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TrackingOption(
    val id: String,
    val title: String,
    val description: String,
    val iconEmoji: String,
    val enabled: Boolean = true
)

data class OnboardingUiState(
    val currentStep: Int = 1,
    val name: String = "",
    val trackingOptions: List<TrackingOption> = defaultTrackingOptions(),
    val selectedReminderPreset: String? = null,   // "morning" | "afternoon" | "evening"
    val customTime: String = "08:00 AM",
    val isComplete: Boolean = false
) {
    val isNameValid: Boolean get() = name.trim().isNotEmpty()
    val effectiveName: String get() = name.trim().ifEmpty { "Sofia" }
}

private fun defaultTrackingOptions() = listOf(
    TrackingOption("meals",     "Meals · 3 balanced meals",   "Steadier blood sugar helps keep PCOS symptoms in check.", "🍽"),
    TrackingOption("movement",  "Movement · 30 minutes",      "Supports insulin sensitivity, a key lever in PCOS.", "🚶"),
    TrackingOption("water",     "Water · 8 glasses",          "Helps with energy and bloating.", "💧"),
    TrackingOption("relax",     "Relaxation · 15 minutes",    "Lower stress means lower cortisol, which can worsen symptoms.", "🌿"),
    TrackingOption("sleep",     "Sleep · 8 hours",            "Consistent sleep helps balance hormones over time.", "🌙"),
    TrackingOption("cycle",     "Cycle · period dates",       "Spot your personal pattern and predict your next period.", "🩸"),
    TrackingOption("symptoms",  "Symptom check-in · bloating, skin, mood, sleep quality",
                                "Track how you feel day to day to notice patterns over time.", "😊")
)

class OnboardingViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _state = MutableStateFlow(OnboardingUiState())
    val state: StateFlow<OnboardingUiState> = _state.asStateFlow()

    fun onNameChanged(name: String) {
        _state.update { it.copy(name = name) }
    }

    fun onTrackingToggled(id: String, enabled: Boolean) {
        _state.update { s ->
            s.copy(trackingOptions = s.trackingOptions.map {
                if (it.id == id) it.copy(enabled = enabled) else it
            })
        }
    }

    fun onPresetReminderSelected(preset: String) {
        _state.update { it.copy(selectedReminderPreset = preset) }
    }

    fun onCustomTimeChanged(time: String) {
        _state.update { it.copy(customTime = time, selectedReminderPreset = null) }
    }

    fun nextStep() {
        _state.update { it.copy(currentStep = (it.currentStep + 1).coerceAtMost(4)) }
    }

    fun previousStep() {
        _state.update { it.copy(currentStep = (it.currentStep - 1).coerceAtLeast(1)) }
    }

    fun completeOnboarding() {
        viewModelScope.launch {
            val s = _state.value
            val reminderTime = when (s.selectedReminderPreset) {
                "morning"   -> "07:30 AM"
                "afternoon" -> "01:00 PM"
                "evening"   -> "08:00 PM"
                else        -> s.customTime
            }
            userRepository.completeOnboarding(s.effectiveName, reminderTime)
            _state.update { it.copy(isComplete = true) }
        }
    }
}
