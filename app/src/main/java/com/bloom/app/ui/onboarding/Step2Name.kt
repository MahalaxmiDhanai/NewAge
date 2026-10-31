package com.bloom.app.ui.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.bloom.app.ui.components.BloomPrimaryButton
import com.bloom.app.ui.theme.BloomTheme
import com.bloom.app.ui.theme.DarkText
import com.bloom.app.ui.theme.Dimens
import com.bloom.app.ui.theme.FieldShape
import com.bloom.app.ui.theme.LightPink
import com.bloom.app.ui.theme.MutedGrey
import com.bloom.app.ui.theme.PrimaryBrickRed
import com.bloom.app.ui.theme.SansFont
import com.bloom.app.ui.theme.White

@Composable
fun Step2Name(
    name: String,
    isNameValid: Boolean,
    onNameChanged: (String) -> Unit,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current

    Column(
        modifier            = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(Dimens.Space2Xl))

        Text(
            text      = "A little about you",
            style     = androidx.compose.material3.MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
            modifier  = Modifier.padding(horizontal = Dimens.ScreenPaddingH)
        )

        Spacer(modifier = Modifier.height(Dimens.SpaceXl))

        OutlinedTextField(
            value    = name,
            onValueChange = onNameChanged,
            placeholder = {
                Text(
                    text       = "Your first name",
                    fontFamily = SansFont,
                    color      = MutedGrey
                )
            },
            shape  = FieldShape,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor   = PrimaryBrickRed,
                unfocusedBorderColor = LightPink,
                focusedContainerColor   = White,
                unfocusedContainerColor = White,
                focusedTextColor     = DarkText,
                unfocusedTextColor   = DarkText
            ),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
                imeAction      = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus()
                    if (isNameValid) onContinue()
                }
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimens.ScreenPaddingH)
        )

        Spacer(modifier = Modifier.weight(1f))

        BloomPrimaryButton(
            text    = "Continue",
            onClick = onContinue,
            enabled = isNameValid,
            modifier = Modifier.padding(horizontal = Dimens.ScreenPaddingH)
        )

        Spacer(modifier = Modifier.height(Dimens.Space2Xl))
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFCFAF8, heightDp = 800)
@Composable
private fun Step2Preview() {
    BloomTheme {
        Step2Name(
            name        = "",
            isNameValid = false,
            onNameChanged = {},
            onContinue  = {}
        )
    }
}
