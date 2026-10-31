package com.bloom.app.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val BloomShapes = Shapes(
    // Small: chips, small badges
    small  = RoundedCornerShape(8.dp),
    // Medium: text fields, radio cards
    medium = RoundedCornerShape(16.dp),
    // Large: cards
    large  = RoundedCornerShape(20.dp),
    // ExtraLarge: pill buttons
    extraLarge = RoundedCornerShape(28.dp)
)

// Named aliases used directly in composables
val PillShape   = RoundedCornerShape(28.dp)
val CardShape   = RoundedCornerShape(20.dp)
val ChipShape   = RoundedCornerShape(10.dp)
val FieldShape  = RoundedCornerShape(16.dp)
