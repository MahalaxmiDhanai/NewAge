package com.bloom.app.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.bloom.app.ui.theme.BloomTheme
import com.bloom.app.ui.theme.DarkText
import com.bloom.app.ui.theme.Dimens
import com.bloom.app.ui.theme.DisabledButton
import com.bloom.app.ui.theme.PillShape
import com.bloom.app.ui.theme.PrimaryBrickRed
import com.bloom.app.ui.theme.SansFont
import com.bloom.app.ui.theme.White

@Composable
fun BloomPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Button(
        onClick  = onClick,
        enabled  = enabled,
        shape    = PillShape,
        colors   = ButtonDefaults.buttonColors(
            containerColor         = PrimaryBrickRed,
            contentColor           = White,
            disabledContainerColor = DisabledButton,
            disabledContentColor   = White
        ),
        modifier = modifier
            .fillMaxWidth()
            .height(Dimens.ButtonHeight)
    ) {
        Text(
            text       = text,
            fontFamily = SansFont,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun BloomTextButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = DarkText
) {
    TextButton(onClick = onClick, modifier = modifier) {
        Text(
            text       = text,
            color      = color,
            fontFamily = SansFont,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun BloomFilledButton(
    text: String,
    onClick: () -> Unit,
    containerColor: Color,
    contentColor: Color = White,
    modifier: Modifier = Modifier,
    leadingIcon: @Composable (() -> Unit)? = null
) {
    Button(
        onClick  = onClick,
        shape    = PillShape,
        colors   = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor   = contentColor
        ),
        modifier = modifier.height(Dimens.ButtonHeight)
    ) {
        if (leadingIcon != null) {
            leadingIcon()
        }
        Text(
            text       = text,
            fontFamily = SansFont,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFCFAF8)
@Composable
private fun BloomButtonsPreview() {
    BloomTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.SpaceLg),
            verticalArrangement = Arrangement.spacedBy(Dimens.SpaceMd)
        ) {
            BloomPrimaryButton("Get started", onClick = {})
            BloomPrimaryButton("Continue", onClick = {}, enabled = false)
            BloomTextButton("I'll set this up later", onClick = {})
        }
    }
}
