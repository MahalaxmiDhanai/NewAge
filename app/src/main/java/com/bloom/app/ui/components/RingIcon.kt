package com.bloom.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Circular progress ring with centered icon (matches Figma Goal Progress design).
 *
 * - Background ring at 15% opacity
 * - Progress arc starting from top
 * - Icon centered inside, same color as ring
 */
@Composable
fun RingIcon(
    color: Color,
    progress: Float,
    icon: ImageVector,
    size: Dp        = 42.dp,
    strokeWidth: Dp = 3.dp,
    modifier: Modifier = Modifier
) {
    val clamped = progress.coerceIn(0f, 1f)

    Box(
        modifier         = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        // Ring arc
        Canvas(modifier = Modifier.size(size)) {
            val stroke   = strokeWidth.toPx()
            val inset    = stroke / 2f
            val arcSize  = Size(this.size.width - stroke, this.size.height - stroke)
            val topLeft  = Offset(inset, inset)

            // Faint background full ring
            drawArc(
                color      = color.copy(alpha = 0.15f),
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter  = false,
                topLeft    = topLeft,
                size       = arcSize,
                style      = Stroke(width = stroke, cap = StrokeCap.Round)
            )
            // Filled progress arc
            if (clamped > 0f) {
                drawArc(
                    color      = color,
                    startAngle = -90f,
                    sweepAngle = 360f * clamped,
                    useCenter  = false,
                    topLeft    = topLeft,
                    size       = arcSize,
                    style      = Stroke(width = stroke, cap = StrokeCap.Round)
                )
            }
        }

        // Centered icon in ring color
        Icon(
            imageVector        = icon,
            contentDescription = null,
            tint               = color,
            modifier           = Modifier.size(size * 0.44f)
        )
    }
}
