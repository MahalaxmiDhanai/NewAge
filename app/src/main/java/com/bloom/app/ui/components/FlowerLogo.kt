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
 * Bloom flower logo — matches Figma exactly.
 *
 * The Figma logo (512×512) shows:
 *  - Top/bottom petals: tall vertical ovals, more elongated height-wise
 *  - Left/right petals: wide horizontal ovals, more elongated width-wise
 *  - Petals interlock at center — large overlap
 *  - Gold center circle ~22% of flower radius
 *  - Soft pink radial glow tightly hugging the petals
 *
 * [size] = diameter of the flower body (excluding glow).
 * [showGlow] = whether to draw the radial glow (splash + onboarding step 1).
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
        // Figma shows asymmetric petals:
        //   Vertical petals (top/bottom): halfW = 0.42r, halfH = 0.78r
        //   Horizontal petals (left/right): halfW = 0.78r, halfH = 0.42r
        //
        // Center offset (how far petal centre is from flower centre): 0.38r
        // This creates the interlocking overlap seen in the Figma.

        val vertHalfW = r * 0.42f
        val vertHalfH = r * 0.78f
        val horizHalfW = r * 0.78f
        val horizHalfH = r * 0.42f
        val petalOffset = r * 0.38f

        // Top petal
        drawOvalPetal(
            cx = cx, cy = cy - petalOffset,
            halfW = vertHalfW, halfH = vertHalfH,
            color = PrimaryBrickRed
        )
        // Bottom petal
        drawOvalPetal(
            cx = cx, cy = cy + petalOffset,
            halfW = vertHalfW, halfH = vertHalfH,
            color = PrimaryBrickRed
        )
        // Left petal
        drawOvalPetal(
            cx = cx - petalOffset, cy = cy,
            halfW = horizHalfW, halfH = horizHalfH,
            color = PrimaryBrickRed
        )
        // Right petal
        drawOvalPetal(
            cx = cx + petalOffset, cy = cy,
            halfW = horizHalfW, halfH = horizHalfH,
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
 * Draw an ellipse (oval petal) centered at (cx, cy) using a cubic bezier
 * approximation — gives the smooth rounded petal look from the Figma.
 * κ = 0.5523 is the standard circle-to-bezier constant.
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
