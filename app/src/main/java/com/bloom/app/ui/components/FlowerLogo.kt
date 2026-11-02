package com.bloom.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
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
 * Bloom flower logo from Figma (512×512 design).
 *
 * Design: 4 elongated oval petals forming a cross
 *  - Top/bottom petals: tall vertical ovals (0.40r wide × 0.80r tall)
 *  - Left/right petals: wide horizontal ovals (0.80r wide × 0.40r tall)
 *  - Petals offset 0.50r from center (14px gap in Figma)
 *  - Gold center circle at 26% of radius
 *
 * @param size Flower diameter (excluding glow)
 * @param showGlow Show radial halo (used on splash/onboarding)
 */
@Composable
fun FlowerLogo(
    size: Dp = 120.dp,
    showGlow: Boolean = true,
    modifier: Modifier = Modifier
) {
    // Give the canvas extra room for the glow halo
    val glowPadding = if (showGlow) size * 0.65f else 0.dp
    val canvasSize  = size + glowPadding * 2f

    Canvas(modifier = modifier.size(canvasSize)) {
        val cx = this.size.width  / 2f
        val cy = this.size.height / 2f
        val r  = size.toPx() / 2f   // flower radius

        // ── Radial glow — tight pink halo around the flower ──────────────
        if (showGlow) {
            val glowR = r * 1.55f
            drawCircle(
                brush = Brush.radialGradient(
                    colorStops = arrayOf(
                        0.0f to LightPink.copy(alpha = 0.45f),
                        0.55f to LightPink.copy(alpha = 0.20f),
                        1.0f to LightPink.copy(alpha = 0.0f)
                    ),
                    center = Offset(cx, cy),
                    radius = glowR
                ),
                radius = glowR,
                center = Offset(cx, cy)
            )
        }

        // ── Petal geometry ────────────────────────────────────────────────
        //
        // Figma 512×512 logo: 4 elongated oval petals forming a cross
        // Top/bottom petals are TALL (vertical ovals)
        // Left/right petals are WIDE (horizontal ovals)
        // Gap of 14px between petals
        //
        // Petal dimensions (measured from Figma):
        // - Vertical petals (top/bottom): ~100px wide × 200px tall
        // - Horizontal petals (left/right): ~200px wide × 100px tall
        // - In relative units: 0.40r × 0.80r for vertical, swapped for horizontal

        val vertPetalHalfW = r * 0.40f   // Narrow width for vertical petals
        val vertPetalHalfH = r * 0.80f   // Tall height for vertical petals
        val horizPetalHalfW = r * 0.80f  // Wide width for horizontal petals
        val horizPetalHalfH = r * 0.40f  // Short height for horizontal petals
        val petalOffset = r * 0.50f      // Distance from center

        // Top petal (tall vertical oval)
        drawOvalPetal(
            cx = cx, cy = cy - petalOffset,
            halfW = vertPetalHalfW, halfH = vertPetalHalfH,
            color = PrimaryBrickRed
        )
        // Bottom petal (tall vertical oval)
        drawOvalPetal(
            cx = cx, cy = cy + petalOffset,
            halfW = vertPetalHalfW, halfH = vertPetalHalfH,
            color = PrimaryBrickRed
        )
        // Left petal (wide horizontal oval)
        drawOvalPetal(
            cx = cx - petalOffset, cy = cy,
            halfW = horizPetalHalfW, halfH = horizPetalHalfH,
            color = PrimaryBrickRed
        )
        // Right petal (wide horizontal oval)
        drawOvalPetal(
            cx = cx + petalOffset, cy = cy,
            halfW = horizPetalHalfW, halfH = horizPetalHalfH,
            color = PrimaryBrickRed
        )

        // ── Gold center circle ────────────────────────────────────────────
        drawCircle(
            color  = Gold,
            radius = r * 0.26f,
            center = Offset(cx, cy)
        )
    }
}

/**
 * Draw an oval petal centered at (cx, cy) using cubic bezier curves.
 * κ = 0.5523 is the standard circle-to-bezier constant for smooth curves.
 */
private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawOvalPetal(
    cx: Float, cy: Float,
    halfW: Float, halfH: Float,
    color: Color
) {
    val k = 0.5523f
    val kw = halfW * k
    val kh = halfH * k

    val path = Path().apply {
        moveTo(cx, cy - halfH)
        cubicTo(cx + kw, cy - halfH,   cx + halfW, cy - kh,   cx + halfW, cy)
        cubicTo(cx + halfW, cy + kh,   cx + kw, cy + halfH,   cx, cy + halfH)
        cubicTo(cx - kw, cy + halfH,   cx - halfW, cy + kh,   cx - halfW, cy)
        cubicTo(cx - halfW, cy - kh,   cx - kw, cy - halfH,   cx, cy - halfH)
        close()
    }
    drawPath(path = path, color = color)
}

// ── 4-point sparkle ──────────────────────────────────────────────────────────

@Composable
fun FourPointSparkle(
    size: Dp     = 12.dp,
    color: Color = Gold,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.size(size)) {
        val cx    = this.size.width  / 2f
        val cy    = this.size.height / 2f
        val outer = this.size.minDimension / 2f
        val inner = outer * 0.18f

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
        FlowerLogo(size = 130.dp, showGlow = true)
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFCFAF8)
@Composable
private fun FlowerLogoNoGlowPreview() {
    Box(modifier = Modifier.size(200.dp), contentAlignment = Alignment.Center) {
        FlowerLogo(size = 130.dp, showGlow = false)
    }
}
