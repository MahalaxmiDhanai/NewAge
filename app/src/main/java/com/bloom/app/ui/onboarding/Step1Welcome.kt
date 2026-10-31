package com.bloom.app.ui.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bloom.app.ui.components.BloomPrimaryButton
import com.bloom.app.ui.components.FlowerLogo
import com.bloom.app.ui.theme.BloomTheme
import com.bloom.app.ui.theme.Dimens
import com.bloom.app.ui.theme.MutedGrey
import com.bloom.app.ui.theme.SansFont

@Composable
fun Step1Welcome(
    onGetStarted: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier            = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.weight(1f))

        FlowerLogo(size = 110.dp, showGlow = true)

        Spacer(modifier = Modifier.height(Dimens.Space2Xl))

        Text(
            text      = "Welcome to Bloom",
            style     = androidx.compose.material3.MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center,
            modifier  = Modifier.padding(horizontal = Dimens.SpaceLg)
        )

        Spacer(modifier = Modifier.height(Dimens.SpaceLg))

        Text(
            text       = "A simple daily companion for managing PCOS — track your cycle, meals, movement, water, relaxation and sleep.",
            fontFamily = SansFont,
            color      = MutedGrey,
            textAlign  = TextAlign.Center,
            modifier   = Modifier.padding(horizontal = Dimens.Space3Xl)
        )

        Spacer(modifier = Modifier.weight(1f))

        BloomPrimaryButton(
            text     = "Get started",
            onClick  = onGetStarted,
            modifier = Modifier.padding(horizontal = Dimens.ScreenPaddingH)
        )

        Spacer(modifier = Modifier.height(Dimens.Space2Xl))
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFCFAF8, heightDp = 800)
@Composable
private fun Step1Preview() {
    BloomTheme {
        Step1Welcome(onGetStarted = {})
    }
}
