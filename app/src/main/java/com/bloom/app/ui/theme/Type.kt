package com.bloom.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.sp
import com.bloom.app.R

private val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage   = "com.google.android.gms",
    certificates      = R.array.com_google_android_gms_fonts_certs
)

val SerifFont: FontFamily = FontFamily(
    Font(
        googleFont     = GoogleFont("Fraunces"),
        fontProvider   = provider,
        weight         = FontWeight.Normal,
        style          = FontStyle.Italic
    ),
    Font(
        googleFont     = GoogleFont("Fraunces"),
        fontProvider   = provider,
        weight         = FontWeight.Bold,
        style          = FontStyle.Italic
    )
)

val SansFont: FontFamily = FontFamily(
    Font(
        googleFont   = GoogleFont("Plus Jakarta Sans"),
        fontProvider = provider,
        weight       = FontWeight.Normal
    ),
    Font(
        googleFont   = GoogleFont("Plus Jakarta Sans"),
        fontProvider = provider,
        weight       = FontWeight.Medium
    ),
    Font(
        googleFont   = GoogleFont("Plus Jakarta Sans"),
        fontProvider = provider,
        weight       = FontWeight.SemiBold
    ),
    Font(
        googleFont   = GoogleFont("Plus Jakarta Sans"),
        fontProvider = provider,
        weight       = FontWeight.Bold
    )
)

val BloomTypography = Typography(
    headlineLarge = TextStyle(
        fontFamily = SerifFont,
        fontWeight = FontWeight.Bold,
        fontStyle  = FontStyle.Italic,
        fontSize   = 28.sp,
        lineHeight = 34.sp,
        color      = DarkText
    ),
    headlineMedium = TextStyle(
        fontFamily = SerifFont,
        fontWeight = FontWeight.Bold,
        fontStyle  = FontStyle.Italic,
        fontSize   = 24.sp,
        lineHeight = 30.sp,
        color      = DarkText
    ),
    headlineSmall = TextStyle(
        fontFamily = SerifFont,
        fontWeight = FontWeight.Bold,
        fontStyle  = FontStyle.Italic,
        fontSize   = 20.sp,
        lineHeight = 26.sp,
        color      = DarkText
    ),
    bodyLarge = TextStyle(
        fontFamily = SansFont,
        fontWeight = FontWeight.Normal,
        fontSize   = 16.sp,
        lineHeight = 24.sp,
        color      = DarkText
    ),
    bodyMedium = TextStyle(
        fontFamily = SansFont,
        fontWeight = FontWeight.Normal,
        fontSize   = 14.sp,
        lineHeight = 20.sp,
        color      = DarkText
    ),
    bodySmall = TextStyle(
        fontFamily = SansFont,
        fontWeight = FontWeight.Normal,
        fontSize   = 12.sp,
        lineHeight = 18.sp,
        color      = MutedGrey
    ),
    labelLarge = TextStyle(
        fontFamily = SansFont,
        fontWeight = FontWeight.SemiBold,
        fontSize   = 16.sp,
        lineHeight = 20.sp
    ),
    labelMedium = TextStyle(
        fontFamily = SansFont,
        fontWeight = FontWeight.Medium,
        fontSize   = 14.sp,
        lineHeight = 18.sp
    ),
    labelSmall = TextStyle(
        fontFamily = SansFont,
        fontWeight = FontWeight.Medium,
        fontSize   = 11.sp,
        lineHeight = 16.sp
    ),
    titleLarge = TextStyle(
        fontFamily = SerifFont,
        fontWeight = FontWeight.Bold,
        fontStyle  = FontStyle.Italic,
        fontSize   = 22.sp,
        lineHeight = 28.sp,
        color      = DarkText
    ),
    titleMedium = TextStyle(
        fontFamily = SansFont,
        fontWeight = FontWeight.SemiBold,
        fontSize   = 16.sp,
        lineHeight = 22.sp,
        color      = DarkText
    ),
    titleSmall = TextStyle(
        fontFamily = SansFont,
        fontWeight = FontWeight.Medium,
        fontSize   = 14.sp,
        lineHeight = 20.sp,
        color      = DarkText
    )
)
