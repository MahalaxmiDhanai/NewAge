package com.bloom.app.ui.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.bloom.app.ui.theme.DarkText
import com.bloom.app.ui.theme.SerifFont

@Composable
fun SectionTitle(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text       = text,
        fontFamily = SerifFont,
        fontWeight = FontWeight.Bold,
        fontStyle  = FontStyle.Italic,
        fontSize   = 20.sp,
        color      = DarkText,
        modifier   = modifier
    )
}
