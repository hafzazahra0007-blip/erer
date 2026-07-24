package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.data.model.Bottle
import com.example.data.model.BottleTheme
import com.example.data.model.LiquidColor
import kotlin.math.sin

/**
 * High-fidelity 3D-like realistic glass bottle custom Canvas view.
 * Features liquid block gradients, meniscus curves, glossy highlights, cork stopper, and selection lift.
 */
@Composable
fun GlassBottleView(
    bottle: Bottle,
    theme: BottleTheme = BottleTheme.CLASSIC_GLASS,
    tiltAngle: Float = 0f,
    liftOffset: Dp = 0.dp,
    translationX: Dp = 0.dp,
    translationY: Dp = 0.dp,
    pourFraction: Float = 0f,
    isPourSource: Boolean = false,
    isPourTarget: Boolean = false,
    pourColor: LiquidColor? = null,
    pourUnits: Int = 1,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    bottleWidth: Dp = 68.dp,
    bottleHeight: Dp = 180.dp
) {
    // Lift elevation transition when selected or pouring
    val targetLift = if (liftOffset != 0.dp) liftOffset else if (bottle.isSelected) (-28).dp else 0.dp
    val offsetY by animateDpAsState(
        targetValue = targetLift,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "bottleLift"
    )

    // Animated liquid wave phase
    val infiniteTransition = rememberInfiniteTransition(label = "wave")
    val wavePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wavePhase"
    )

    val totalOffsetY = offsetY + translationY

    Box(
        modifier = modifier
            .offset(x = translationX, y = totalOffsetY)
            .width(bottleWidth)
            .height(bottleHeight)
            .testTag("bottle_${bottle.id}")
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val pivotY = if (isPourSource || tiltAngle != 0f) size.height * 0.18f else size.height
            rotate(degrees = tiltAngle, pivot = Offset(size.width / 2f, pivotY)) {
                drawGlassBottle(
                    bottle = bottle,
                    theme = theme,
                    wavePhase = wavePhase,
                    pourFraction = pourFraction,
                    isPourSource = isPourSource,
                    isPourTarget = isPourTarget,
                    pourColor = pourColor,
                    pourUnits = pourUnits
                )
            }
        }
    }
}

private fun DrawScope.drawGlassBottle(
    bottle: Bottle,
    theme: BottleTheme,
    wavePhase: Float,
    pourFraction: Float = 0f,
    isPourSource: Boolean = false,
    isPourTarget: Boolean = false,
    pourColor: LiquidColor? = null,
    pourUnits: Int = 1
) {
    val w = size.width
    val h = size.height

    // Dimensions
    val neckHeight = h * 0.15f
    val neckWidth = w * 0.55f
    val bodyTop = neckHeight
    val bodyWidth = w * 0.85f
    val bodyLeft = (w - bodyWidth) / 2f
    val bodyRight = bodyLeft + bodyWidth
    val bodyBottom = h - 12f
    val cornerRadius = 24f

    val capacity = bottle.capacity
    val layers = bottle.layers
    val liquidHeightUnit = (bodyBottom - bodyTop - 8f) / capacity

    // 1. Draw Glass Background Fill
    val neckLeft = (w - neckWidth) / 2f
    val neckRight = neckLeft + neckWidth

    val bottlePath = Path().apply {
        when (theme.shapeType) {
            BottleTheme.ShapeType.CYLINDER -> {
                moveTo(neckLeft, 8f)
                lineTo(neckRight, 8f)
                lineTo(neckRight, bodyTop)
                lineTo(bodyRight, bodyTop)
                lineTo(bodyRight, bodyBottom - cornerRadius)
                arcTo(
                    rect = Rect(bodyRight - cornerRadius * 2, bodyBottom - cornerRadius * 2, bodyRight, bodyBottom),
                    startAngleDegrees = 0f, sweepAngleDegrees = 90f, forceMoveTo = false
                )
                lineTo(bodyLeft + cornerRadius, bodyBottom)
                arcTo(
                    rect = Rect(bodyLeft, bodyBottom - cornerRadius * 2, bodyLeft + cornerRadius * 2, bodyBottom),
                    startAngleDegrees = 90f, sweepAngleDegrees = 90f, forceMoveTo = false
                )
                lineTo(bodyLeft, bodyTop)
                lineTo(neckLeft, bodyTop)
                close()
            }
            BottleTheme.ShapeType.ROUND_BOTTOM -> {
                moveTo(neckLeft, 8f)
                lineTo(neckRight, 8f)
                lineTo(neckRight, bodyTop)
                lineTo(bodyRight, bodyTop)
                lineTo(bodyRight, bodyBottom - bodyWidth * 0.5f)
                arcTo(
                    rect = Rect(bodyLeft, bodyBottom - bodyWidth, bodyRight, bodyBottom),
                    startAngleDegrees = 0f, sweepAngleDegrees = 180f, forceMoveTo = false
                )
                lineTo(bodyLeft, bodyTop)
                lineTo(neckLeft, bodyTop)
                close()
            }
            BottleTheme.ShapeType.FLASK -> {
                moveTo(neckLeft, 8f)
                lineTo(neckRight, 8f)
                lineTo(neckRight, bodyTop + 10f)
                lineTo(bodyRight + 8f, bodyBottom - 16f)
                lineTo(bodyRight - 8f, bodyBottom)
                lineTo(bodyLeft + 8f, bodyBottom)
                lineTo(bodyLeft - 8f, bodyBottom - 16f)
                lineTo(neckLeft, bodyTop + 10f)
                close()
            }
            BottleTheme.ShapeType.HEXAGONAL -> {
                moveTo(neckLeft, 8f)
                lineTo(neckRight, 8f)
                lineTo(neckRight, bodyTop)
                lineTo(bodyRight, bodyTop + 20f)
                lineTo(bodyRight, bodyBottom - 20f)
                lineTo(bodyRight - 16f, bodyBottom)
                lineTo(bodyLeft + 16f, bodyBottom)
                lineTo(bodyLeft, bodyBottom - 20f)
                lineTo(bodyLeft, bodyTop + 20f)
                lineTo(neckLeft, bodyTop)
                close()
            }
            BottleTheme.ShapeType.DIAMOND -> {
                moveTo(neckLeft, 8f)
                lineTo(neckRight, 8f)
                lineTo(neckRight + 4f, bodyTop + 12f)
                lineTo(bodyRight + 6f, bodyTop + (bodyBottom - bodyTop) * 0.4f)
                lineTo(w / 2f + 16f, bodyBottom)
                lineTo(w / 2f - 16f, bodyBottom)
                lineTo(bodyLeft - 6f, bodyTop + (bodyBottom - bodyTop) * 0.4f)
                lineTo(neckLeft - 4f, bodyTop + 12f)
                close()
            }
            BottleTheme.ShapeType.POTION_VIAL -> {
                moveTo(neckLeft, 8f)
                lineTo(neckRight, 8f)
                lineTo(neckRight - 2f, bodyTop)
                cubicTo(
                    bodyRight + 12f, bodyTop + 10f,
                    bodyRight + 12f, bodyBottom - 10f,
                    w / 2f, bodyBottom
                )
                cubicTo(
                    bodyLeft - 12f, bodyBottom - 10f,
                    bodyLeft - 12f, bodyTop + 10f,
                    neckLeft + 2f, bodyTop
                )
                close()
            }
            BottleTheme.ShapeType.GOBLET -> {
                moveTo(neckLeft - 4f, 8f)
                lineTo(neckRight + 4f, 8f)
                lineTo(neckRight + 2f, bodyTop)
                lineTo(bodyRight, bodyTop + (bodyBottom - bodyTop) * 0.6f)
                lineTo(w / 2f + 6f, bodyBottom - 24f)
                lineTo(w / 2f + 6f, bodyBottom - 8f)
                lineTo(bodyRight - 8f, bodyBottom)
                lineTo(bodyLeft + 8f, bodyBottom)
                lineTo(w / 2f - 6f, bodyBottom - 8f)
                lineTo(w / 2f - 6f, bodyBottom - 24f)
                lineTo(bodyLeft, bodyTop + (bodyBottom - bodyTop) * 0.6f)
                lineTo(neckLeft - 2f, bodyTop)
                close()
            }
            BottleTheme.ShapeType.OCTAGONAL -> {
                moveTo(neckLeft, 8f)
                lineTo(neckRight, 8f)
                lineTo(neckRight, bodyTop)
                lineTo(bodyRight - 8f, bodyTop + 16f)
                lineTo(bodyRight, bodyTop + (bodyBottom - bodyTop) * 0.5f)
                lineTo(bodyRight - 8f, bodyBottom)
                lineTo(bodyLeft + 8f, bodyBottom)
                lineTo(bodyLeft, bodyTop + (bodyBottom - bodyTop) * 0.5f)
                lineTo(bodyLeft + 8f, bodyTop + 16f)
                lineTo(neckLeft, bodyTop)
                close()
            }
            BottleTheme.ShapeType.BEAKER -> {
                moveTo(neckLeft - 6f, 8f)
                lineTo(neckRight + 6f, 8f)
                lineTo(neckRight, bodyTop)
                lineTo(bodyRight + 4f, bodyBottom - 8f)
                lineTo(bodyRight - 8f, bodyBottom)
                lineTo(bodyLeft + 8f, bodyBottom)
                lineTo(bodyLeft - 4f, bodyBottom - 8f)
                lineTo(neckLeft, bodyTop)
                close()
            }
            BottleTheme.ShapeType.HOURGLASS -> {
                val midY = bodyTop + (bodyBottom - bodyTop) * 0.5f
                moveTo(neckLeft, 8f)
                lineTo(neckRight, 8f)
                lineTo(neckRight, bodyTop)
                lineTo(bodyRight - 2f, bodyTop + 10f)
                cubicTo(
                    w / 2f + 4f, midY,
                    bodyRight + 4f, bodyBottom - 20f,
                    bodyRight - 4f, bodyBottom
                )
                lineTo(bodyLeft + 4f, bodyBottom)
                cubicTo(
                    bodyLeft - 4f, bodyBottom - 20f,
                    w / 2f - 4f, midY,
                    bodyLeft + 2f, bodyTop + 10f
                )
                lineTo(neckLeft, bodyTop)
                close()
            }
        }
    }

    // Clip to inner glass region for drawing liquid
    clipPath(bottlePath) {
        // Fill glass background tint
        drawPath(
            path = bottlePath,
            brush = Brush.verticalGradient(
                colors = listOf(
                    theme.glassColor,
                    theme.glassColor.copy(alpha = theme.glassColor.alpha * 0.6f)
                )
            )
        )

        // 2. Draw Liquid Layers
        var currentY = bodyBottom
        for (i in layers.indices) {
            val color = layers[i]

            // Calculate height for this layer
            val layerHeight = if (isPourSource && pourFraction > 0f && i >= layers.size - pourUnits) {
                liquidHeightUnit * (1f - pourFraction)
            } else {
                liquidHeightUnit
            }

            if (layerHeight > 0.5f) {
                val layerTopY = currentY - layerHeight
                val isTopLayer = (i == layers.lastIndex)

                val liquidPath = Path().apply {
                    moveTo(bodyLeft - 4f, currentY + 4f)
                    lineTo(bodyRight + 4f, currentY + 4f)
                    lineTo(bodyRight + 4f, layerTopY)

                    if (isTopLayer) {
                        // Draw meniscus fluid wave on top
                        val steps = 20
                        val stepWidth = bodyWidth / steps
                        val waveAmp = if (isPourSource) 4f else 3f
                        for (step in 0..steps) {
                            val x = bodyRight - step * stepWidth
                            val waveY = layerTopY + sin(wavePhase * 2.5f + step * 0.4f) * waveAmp
                            lineTo(x, waveY)
                        }
                    } else {
                        lineTo(bodyLeft - 4f, layerTopY)
                    }

                    close()
                }

                // Draw liquid layer gradient
                drawPath(
                    path = liquidPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(color.topColor, color.primaryColor, color.bottomColor),
                        startY = layerTopY,
                        endY = currentY
                    )
                )

                // Draw subtle surface highlight line for top layer
                if (isTopLayer) {
                    drawPath(
                        path = Path().apply {
                            moveTo(bodyLeft, layerTopY)
                            lineTo(bodyRight, layerTopY)
                        },
                        color = Color.White.copy(alpha = 0.4f),
                        style = Stroke(width = 3f)
                    )
                }

                currentY = layerTopY
            }
        }

        // Draw incoming liquid filling target bottle
        if (isPourTarget && pourFraction > 0f && pourColor != null) {
            val fillHeight = liquidHeightUnit * pourUnits * pourFraction
            if (fillHeight > 0.5f) {
                val layerTopY = currentY - fillHeight
                val color = pourColor

                val targetPath = Path().apply {
                    moveTo(bodyLeft - 4f, currentY + 4f)
                    lineTo(bodyRight + 4f, currentY + 4f)
                    lineTo(bodyRight + 4f, layerTopY)

                    // Active surface splashing waves as liquid fills
                    val steps = 20
                    val stepWidth = bodyWidth / steps
                    for (step in 0..steps) {
                        val x = bodyRight - step * stepWidth
                        val splashWave = sin(wavePhase * 4f + step * 0.5f) * (3f + 4f * pourFraction)
                        lineTo(x, layerTopY + splashWave)
                    }

                    close()
                }

                drawPath(
                    path = targetPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(color.topColor, color.primaryColor, color.bottomColor),
                        startY = layerTopY,
                        endY = currentY
                    )
                )

                // Surface foam/highlight
                drawPath(
                    path = Path().apply {
                        moveTo(bodyLeft, layerTopY)
                        lineTo(bodyRight, layerTopY)
                    },
                    color = Color.White.copy(alpha = 0.6f),
                    style = Stroke(width = 3.5f)
                )
            }
        }
    }

    // 3. Draw Cork Stopper if Completed
    if (bottle.isCompleted) {
        val neckLeft = (w - neckWidth) / 2f
        val corkPath = Path().apply {
            addRoundRect(
                RoundRect(
                    rect = Rect(neckLeft + 4f, -4f, neckLeft + neckWidth - 4f, 16f),
                    cornerRadius = CornerRadius(8f, 8f)
                )
            )
        }
        drawPath(
            path = corkPath,
            brush = Brush.verticalGradient(
                colors = listOf(theme.capColor, theme.capColor.copy(alpha = 0.8f))
            )
        )
    }

    // 4. Draw Outer Glass Outline
    drawPath(
        path = bottlePath,
        color = if (bottle.isSelected) theme.highlightColor else theme.outlineColor,
        style = Stroke(width = if (bottle.isSelected) 5f else 3f)
    )

    // 5. Draw Glossy Glass Reflection Highlight Streak
    val glossPath = Path().apply {
        moveTo(bodyLeft + 8f, bodyTop + 8f)
        lineTo(bodyLeft + 16f, bodyTop + 8f)
        lineTo(bodyLeft + 12f, bodyBottom - 16f)
        lineTo(bodyLeft + 6f, bodyBottom - 16f)
        close()
    }
    drawPath(
        path = glossPath,
        brush = Brush.linearGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.45f),
                Color.White.copy(alpha = 0.05f)
            ),
            start = Offset(bodyLeft, bodyTop),
            end = Offset(bodyLeft + 20f, bodyBottom)
        )
    )

    // Neck Rim Outer Lip Ring
    drawRoundRect(
        color = theme.outlineColor,
        topLeft = Offset(neckLeft - 2f, 4f),
        size = Size(neckWidth + 4f, 6f),
        cornerRadius = CornerRadius(3f, 3f),
        style = Stroke(width = 2.5f)
    )
}
