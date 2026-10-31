package com.bloom.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.bloom.app.ui.theme.Gold
import com.bloom.app.ui.theme.LightPink
import com.bloom.app.ui.theme.PrimaryBrickRed
import kotlin.math.cos
import kotlin.math.sin

/**
 * Bloom flower drawn entirely on Compose Canvas.
 * 4 rounded petals (brick-red) + gold center circle.
 * [showGlow] adds a soft pink radial glow behind the flower.
 */
@Composable
fun FlowerLogo(
    size: Dp       = 120.dp,
    showGlow: Boolean = true,
    modifier: Modifier = Modifier
) {
    val glowMultiplier = if (showGlow) 2.2f else 1f
    val canvasSize     = size * glowMultiplier

    Canvas(modifier = modifier.size(canvasSize)) {
        val cx = this.size.width  / 2f
        val cy = this.size.height / 2f
        val flowerR = (size.toPx()) / 2f

        // ── Radial glow ────────────────────────────────────────────────────
        if (showGlow) {
            drawCircle(
                brush  = Brush.radialGradient(
                    colors  = listOf(LightPink.copy(alpha = 0.55f), LightPink.copy(alpha = 0f)),
                    center  = Offset(cx, cy),
                    radius  = this.size.minDimension / 2f
                ),
                radius = this.size.minDimension / 2f,
                center = Offset(cx, cy)
            )
        }

        // ── 4 petals ───────────────────────────────────────────────────────
        val petalW = flowerR * 0.52f
        val petalH = flowerR * 0.90f
        val offset = petalH * 0.48f

        for (i in 0 until 4) {
            val angle = Math.toRadians((i * 90.0)).toFloat()
            drawPetal(
                cx = cx + sin(angle) * offset,
                cy = cy - cos(angle) * offset,
                halfW = petalW,
                halfH = petalH,
                color = PrimaryBrickRed
            )
        }

        // ── Gold center ────────────────────────────────────────────────────
        val centerR = flowerR * 0.28f
        drawCircle(
            color  = Gold,
            radius = centerR,
            center = Offset(cx, cy)
        )
    }
}

/**
 * Draw a single rounded petal centred at (cx, cy) using a cubic bezier path.
 * The petal is axis-aligned (vertical). Rotate the caller's coordinate frame
 * externally if needed.
 */
private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawPetal(
    cx: Float, cy: Float,
    halfW: Float, halfH: Float,
    color: Color
) {
    val cp = halfW * 1.35f      // control point spread
    val path = Path().apply {
        moveTo(cx, cy + halfH)
        cubicTo(cx + cp, cy + halfH, cx + halfW, cy, cx, cy - halfH)
        cubicTo(cx - halfW, cy, cx - cp, cy + halfH, cx, cy + halfH)
        close()
    }
    drawPath(path = path, color = color)
}

// ── 4-point sparkle ─────────────────────────────────────────────────────────

@Composable
fun FourPointSparkle(
    size: Dp       = 12.dp,
    color: Color   = Gold,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.size(size)) {
        val cx    = this.size.width  / 2f
        val cy    = this.size.height / 2f
        val outer = this.size.minDimension / 2f
        val inner = outer * 0.15f

        val path = Path()
        for (i in 0 until 8) {
            val angle  = Math.toRadians((i * 45.0) - 90.0)
            val radius = if (i % 2 == 0) outer else inner
            val x = cx + (radius * cos(angle)).toFloat()
            val y = cy + (radius * sin(angle)).toFloat()
            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        path.close()
        drawPath(path = path, color = color)
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFCFAF8)
@Composable
private fun FlowerLogoPreview() {
    Box(modifier = Modifier.size(300.dp), contentAlignment = Alignment.Center) {
        FlowerLogo(size = 120.dp, showGlow = true)
    }
}
