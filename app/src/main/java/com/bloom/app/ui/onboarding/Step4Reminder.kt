package com.bloom.app.ui.onboarding

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.bloom.app.ui.components.BloomPrimaryButton
import com.bloom.app.ui.components.BloomTextButton
import com.bloom.app.ui.theme.BloomTheme
import com.bloom.app.ui.theme.DarkText
import com.bloom.app.ui.theme.Dimens
import com.bloom.app.ui.theme.FieldShape
import com.bloom.app.ui.theme.LightPink
import com.bloom.app.ui.theme.MutedGrey
import com.bloom.app.ui.theme.PrimaryBrickRed
import com.bloom.app.ui.theme.SansFont
import com.bloom.app.ui.theme.White

private val presets = listOf(
    Triple("morning",   "Morning",   "7:30 AM — start the day on track"),
    Triple("afternoon", "Afternoon", "1:00 PM — a midday check-in"),
    Triple("evening",   "Evening",   "8:00 PM — wind down and reflect")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Step4Reminder(
    selectedPreset: String?,
    customTime: String,
    onPresetSelected: (String) -> Unit,
    onCustomTimeChanged: (String) -> Unit,
    onEnterBloom: () -> Unit,
    onSkip: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showTimePicker by remember { mutableStateOf(false) }
    val timePickerState = rememberTimePickerState(initialHour = 8, initialMinute = 0, is24Hour = false)

    Column(
        modifier            = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Dimens.ScreenPaddingH),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(Dimens.SpaceMd))

            // Clock icon
            Icon(
                imageVector        = Icons.Outlined.AccessTime,
                contentDescription = null,
                tint               = PrimaryBrickRed,
                modifier           = Modifier.size(36.dp)
            )

            Spacer(modifier = Modifier.height(Dimens.SpaceMd))

            Text(
                text      = "When should we remind you?",
                style     = androidx.compose.material3.MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(Dimens.SpaceSm))

            Text(
                text       = "We'll send one daily nudge to log how you're doing.",
                fontFamily = SansFont,
                color      = MutedGrey,
                textAlign  = TextAlign.Center,
                fontSize   = 14.sp
            )

            Spacer(modifier = Modifier.height(Dimens.SpaceLg))

            Text(
                text       = "Pick a time of day",
                fontFamily = SansFont,
                fontWeight = FontWeight.Medium,
                color      = MutedGrey,
                modifier   = Modifier.fillMaxWidth(),
                fontSize   = 13.sp
            )

            Spacer(modifier = Modifier.height(Dimens.SpaceSm))

            presets.forEach { (id, label, subtext) ->
                RadioTimeCard(
                    label     = label,
                    subtext   = subtext,
                    selected  = selectedPreset == id,
                    onClick   = { onPresetSelected(id) }
                )
                Spacer(modifier = Modifier.height(Dimens.SpaceSm))
            }

            Spacer(modifier = Modifier.height(Dimens.SpaceSm))

            Text(
                text       = "Or set a custom time",
                fontFamily = SansFont,
                fontWeight = FontWeight.Medium,
                color      = MutedGrey,
                modifier   = Modifier.fillMaxWidth(),
                fontSize   = 13.sp
            )

            Spacer(modifier = Modifier.height(Dimens.SpaceSm))

            // Custom time field
            OutlinedCard(
                onClick = { showTimePicker = true },
                shape   = FieldShape,
                border  = BorderStroke(1.dp, LightPink),
                colors  = CardDefaults.outlinedCardColors(containerColor = White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier            = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Dimens.SpaceLg, vertical = Dimens.SpaceMd),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment   = Alignment.CenterVertically
                ) {
                    Text(
                        text       = customTime,
                        fontFamily = SansFont,
                        color      = DarkText
                    )
                    Icon(
                        imageVector        = Icons.Outlined.AccessTime,
                        contentDescription = "Pick time",
                        tint               = MutedGrey,
                        modifier           = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(Dimens.SpaceLg))
        }

        // Bottom buttons
        Column(
            modifier            = Modifier.padding(horizontal = Dimens.ScreenPaddingH),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            BloomPrimaryButton(text = "Enter Bloom", onClick = onEnterBloom)
            BloomTextButton(
                text     = "I'll set this up later",
                onClick  = onSkip,
                color    = MutedGrey,
                modifier = Modifier.padding(vertical = Dimens.SpaceSm)
            )
        }

        Spacer(modifier = Modifier.height(Dimens.SpaceLg))
    }

    // Time Picker Dialog
    if (showTimePicker) {
        Dialog(onDismissRequest = { showTimePicker = false }) {
            Card(
                shape  = androidx.compose.foundation.shape.RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = White)
            ) {
                Column(
                    modifier            = Modifier.padding(Dimens.SpaceLg),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TimePicker(
                        state  = timePickerState,
                        colors = TimePickerDefaults.colors(
                            clockDialColor        = LightPink,
                            selectorColor         = PrimaryBrickRed,
                            periodSelectorSelectedContainerColor = PrimaryBrickRed,
                            timeSelectorSelectedContainerColor   = LightPink
                        )
                    )
                    Row(
                        modifier              = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        BloomTextButton("Cancel", onClick = { showTimePicker = false })
                        BloomTextButton("OK", onClick = {
                            val hour   = timePickerState.hour
                            val minute = timePickerState.minute
                            val amPm   = if (hour < 12) "AM" else "PM"
                            val h12    = when {
                                hour == 0  -> 12
                                hour > 12  -> hour - 12
                                else       -> hour
                            }
                            val formatted = "%02d:%02d %s".format(h12, minute, amPm)
                            onCustomTimeChanged(formatted)
                            showTimePicker = false
                        })
                    }
                }
            }
        }
    }
}

@Composable
private fun RadioTimeCard(
    label: String,
    subtext: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedCard(
        onClick = onClick,
        shape   = FieldShape,
        border  = BorderStroke(
            width = if (selected) 1.5.dp else 1.dp,
            color = if (selected) PrimaryBrickRed else LightPink
        ),
        colors  = CardDefaults.outlinedCardColors(containerColor = White),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier          = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimens.SpaceMd, vertical = Dimens.SpaceMd),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = selected,
                onClick  = onClick,
                colors   = RadioButtonDefaults.colors(selectedColor = PrimaryBrickRed)
            )
            Column {
                Text(
                    text       = label,
                    fontFamily = SansFont,
                    fontWeight = FontWeight.SemiBold,
                    color      = DarkText
                )
                Text(
                    text       = subtext,
                    fontFamily = SansFont,
                    color      = MutedGrey,
                    fontSize   = 13.sp
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFCFAF8, heightDp = 900)
@Composable
private fun Step4Preview() {
    BloomTheme {
        Step4Reminder(
            selectedPreset    = "morning",
            customTime        = "08:00 AM",
            onPresetSelected  = {},
            onCustomTimeChanged = {},
            onEnterBloom      = {},
            onSkip            = {}
        )
    }
}
