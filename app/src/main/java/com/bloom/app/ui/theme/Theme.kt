package com.bloom.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

// App is LIGHT ONLY - always use light scheme regardless of system dark mode
private val BloomColorScheme = lightColorScheme(
    primary          = PrimaryBrickRed,
    onPrimary        = White,
    primaryContainer = LightPink,
    onPrimaryContainer = DarkText,
    secondary        = Gold,
    onSecondary      = White,
    background       = BackgroundCream,
    onBackground     = DarkText,
    surface          = White,
    onSurface        = DarkText,
    surfaceVariant   = QuickActionCardBg,
    onSurfaceVariant = DarkText,
    outline          = LightPink,
    error            = PrimaryBrickRed,
    onError          = White
)

@Composable
fun BloomTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = BloomColorScheme,
        typography  = BloomTypography,
        shapes      = BloomShapes,
        content     = content
    )
}
