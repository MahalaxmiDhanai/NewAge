package com.bloom.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Circular ring icon that partially fills based on [progress] (0f..1f).
 * When progress == 1f the ring is fully solid (filled circle + stroke).
 */
@Composable
fun RingIcon(
    color: Color,
    progress: Float,            // 0f to 1f
    size: Dp = 40.dp,
    strokeWidth: Dp = 3.dp,
    modifier: Modifier = Modifier
) {
    val clampedProgress = progress.coerceIn(0f, 1f)
    Canvas(modifier = modifier.size(size)) {
        val stroke = strokeWidth.toPx()
        val inset  = stroke / 2f
        val arcSize = Size(
            this.size.width  - stroke,
            this.size.height - stroke
        )
        val topLeft = Offset(inset, inset)

        // Background ring (always drawn)
        drawArc(
            color       = color.copy(alpha = 0.15f),
            startAngle  = -90f,
            sweepAngle  = 360f,
            useCenter   = false,
            topLeft     = topLeft,
            size        = arcSize,
            style       = Stroke(width = stroke, cap = StrokeCap.Round)
        )

        // Progress arc
        if (clampedProgress > 0f) {
            drawArc(
                color       = color,
                startAngle  = -90f,
                sweepAngle  = 360f * clampedProgress,
                useCenter   = false,
                topLeft     = topLeft,
                size        = arcSize,
                style       = Stroke(width = stroke, cap = StrokeCap.Round)
            )
        }
    }
}
