package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import com.example.data.model.LiquidColor

/**
 * Animated liquid pouring stream connecting the source bottle mouth to target bottle mouth.
 */
@Composable
fun PourStreamCanvas(
    sourcePos: Offset,
    targetPos: Offset,
    color: LiquidColor,
    progress: Float,
    modifier: Modifier = Modifier
) {
    if (progress <= 0f) return

    Canvas(modifier = modifier.fillMaxSize()) {
        val startX = sourcePos.x
        val startY = sourcePos.y
        val endX = targetPos.x
        val endY = targetPos.y

        // Natural gravity curve: stream falls slightly inward towards target
        val controlX = startX * 0.35f + endX * 0.65f
        val controlY = startY * 0.5f + endY * 0.5f

        val path = Path().apply {
            moveTo(startX, startY)
            quadraticTo(controlX, controlY, endX, endY)
        }

        val strokeWidth = 13f * progress

        // 1. Draw outer glowing aura
        drawPath(
            path = path,
            color = color.glowColor.copy(alpha = 0.5f * progress),
            style = Stroke(width = strokeWidth + 6f)
        )

        // 2. Draw main liquid stream gradient
        drawPath(
            path = path,
            brush = Brush.verticalGradient(
                colors = listOf(color.topColor, color.primaryColor, color.bottomColor),
                startY = startY,
                endY = endY
            ),
            style = Stroke(width = strokeWidth)
        )

        // 3. Draw glossy specular highlight line
        drawPath(
            path = path,
            color = Color.White.copy(alpha = 0.65f * progress),
            style = Stroke(width = 3f)
        )

        // 4. Impact splash ripple ring at target bottle opening
        drawCircle(
            color = Color.White.copy(alpha = 0.7f * progress),
            radius = (strokeWidth * 0.6f) + 3f,
            center = Offset(endX, endY),
            style = Stroke(width = 2f)
        )
        drawCircle(
            color = color.primaryColor.copy(alpha = 0.8f * progress),
            radius = strokeWidth * 0.8f,
            center = Offset(endX, endY)
        )
    }
}
