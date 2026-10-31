package com.bloom.app.ui.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bloom.app.ui.components.BloomPrimaryButton
import com.bloom.app.ui.theme.BloomTheme
import com.bloom.app.ui.theme.CardShape
import com.bloom.app.ui.theme.DarkText
import com.bloom.app.ui.theme.Dimens
import com.bloom.app.ui.theme.LightPink
import com.bloom.app.ui.theme.MutedGrey
import com.bloom.app.ui.theme.PrimaryBrickRed
import com.bloom.app.ui.theme.SansFont
import com.bloom.app.ui.theme.SerifFont
import com.bloom.app.ui.theme.White

@Composable
fun Step3Tracking(
    trackingOptions: List<TrackingOption>,
    onToggle: (String, Boolean) -> Unit,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier
) {
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

            // Star icon
            Text(
                text      = "✳",
                fontSize  = 28.sp,
                color     = PrimaryBrickRed
            )

            Spacer(modifier = Modifier.height(Dimens.SpaceMd))

            Text(
                text      = "What would you like to track daily?",
                style     = androidx.compose.material3.MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(Dimens.SpaceSm))

            Text(
                text       = "Pick the goals that matter to you — only these will show up on your dashboard. Partial progress still counts, and your flower fills in as you go.",
                fontFamily = SansFont,
                color      = MutedGrey,
                textAlign  = TextAlign.Center,
                fontSize   = 14.sp
            )

            Spacer(modifier = Modifier.height(Dimens.SpaceLg))

            // Tracking options card
            Card(
                shape     = CardShape,
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                colors    = CardDefaults.cardColors(containerColor = White),
                modifier  = Modifier.fillMaxWidth()
            ) {
                trackingOptions.forEachIndexed { index, option ->
                    TrackingRow(
                        option  = option,
                        onToggle = { enabled -> onToggle(option.id, enabled) }
                    )
                    if (index < trackingOptions.lastIndex) {
                        HorizontalDivider(
                            modifier  = Modifier.padding(horizontal = Dimens.SpaceLg),
                            thickness = 0.5.dp,
                            color     = LightPink.copy(alpha = 0.5f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(Dimens.SpaceLg))

            Text(
                text       = "Targets and what you track can be adjusted any time in Settings.",
                fontFamily = SansFont,
                color      = MutedGrey,
                textAlign  = TextAlign.Center,
                fontSize   = 12.sp
            )

            Spacer(modifier = Modifier.height(Dimens.SpaceLg))
        }

        BloomPrimaryButton(
            text     = "Continue",
            onClick  = onContinue,
            modifier = Modifier.padding(
                horizontal = Dimens.ScreenPaddingH,
                vertical   = Dimens.SpaceLg
            )
        )
    }
}

@Composable
private fun TrackingRow(
    option: TrackingOption,
    onToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier            = modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.SpaceLg, vertical = Dimens.SpaceMd),
        verticalAlignment   = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Dimens.SpaceMd)
    ) {
        // Icon chip
        androidx.compose.foundation.layout.Box(
            modifier        = Modifier
                .background(color = LightPink, shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp))
                .padding(Dimens.SpaceSm),
            contentAlignment = Alignment.Center
        ) {
            Text(text = option.iconEmoji, fontSize = 18.sp)
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text       = option.title,
                fontFamily = SansFont,
                fontWeight = FontWeight.SemiBold,
                color      = DarkText,
                fontSize   = 14.sp
            )
            Text(
                text       = option.description,
                fontFamily = SansFont,
                color      = MutedGrey,
                fontSize   = 12.sp
            )
        }

        Switch(
            checked         = option.enabled,
            onCheckedChange = onToggle,
            colors          = SwitchDefaults.colors(
                checkedThumbColor       = White,
                checkedTrackColor       = PrimaryBrickRed,
                uncheckedThumbColor     = White,
                uncheckedTrackColor     = LightPink
            )
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFCFAF8, heightDp = 900)
@Composable
private fun Step3Preview() {
    BloomTheme {
        Step3Tracking(
            trackingOptions = OnboardingUiState().trackingOptions,
            onToggle        = { _, _ -> },
            onContinue      = {}
        )
    }
}
