package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.model.Bolt
import kotlin.math.roundToInt

/**
 * Renders a screw bolt with metallic threaded rod, circular base pedestal,
 * stacked hexagonal nuts, and selection / completion effects.
 * Optimized for high performance and instant tap responsiveness.
 */
@Composable
fun BoltView(
    bolt: Bolt,
    isSelected: Boolean,
    isTargetHint: Boolean = false,
    isShakeTriggered: Boolean = false,
    onBoltClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    boltWidth: Dp = 68.dp,
    boltHeight: Dp = 180.dp
) {
    // Shake animation when invalid move is attempted
    val shakeOffset = remember { Animatable(0f) }
    LaunchedEffect(isShakeTriggered) {
        if (isShakeTriggered) {
            shakeOffset.animateTo(
                targetValue = 0f,
                animationSpec = keyframes {
                    durationMillis = 280
                    -10f at 40 using FastOutLinearInEasing
                    10f at 100 using FastOutLinearInEasing
                    -6f at 160 using FastOutLinearInEasing
                    6f at 220 using FastOutLinearInEasing
                    0f at 280
                }
            )
        }
    }

    Box(
        modifier = modifier
            .testTag("bolt_${bolt.id}")
            .size(width = boltWidth, height = boltHeight)
            .offset { IntOffset(shakeOffset.value.roundToInt(), 0) }
            .minimumInteractiveComponentSize()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = false, radius = 38.dp),
                onClick = { onBoltClick(bolt.id) }
            ),
        contentAlignment = Alignment.BottomCenter
    ) {
        // 1. Background Metallic Bolt (Threaded screw shaft + pedestal)
        Canvas(modifier = Modifier.size(width = boltWidth, height = boltHeight)) {
            drawMetallicBoltShaft(
                boltWidth = size.width,
                boltHeight = size.height,
                isCompleted = bolt.isCompleted
            )
        }

        // 2. Hint Ring indicator ONLY runs pulse if this bolt is actually a hint target
        if (isTargetHint) {
            HintTargetRing(boltWidth = boltWidth, boltHeight = boltHeight)
        }

        // 3. Completion Sparkles / Glow behind completed bolt
        if (bolt.isCompleted) {
            CompletedBoltGlow(
                width = boltWidth,
                height = boltHeight
            )
        }

        // 4. Nuts stacked on the bolt
        val nutHeight = 25.dp
        val nutSpacing = 24.dp
        val baseOffsetFromBottom = 16.dp

        Column(
            modifier = Modifier
                .width(boltWidth)
                .fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(1f))

            // Stack nuts from bottom to top
            Box(
                modifier = Modifier
                    .width(boltWidth)
                    .height(boltHeight),
                contentAlignment = Alignment.BottomCenter
            ) {
                bolt.nuts.forEachIndexed { index, nut ->
                    val isTopNut = (index == bolt.nuts.size - 1)
                    val isHoveringThis = isSelected && isTopNut
                    val yBottomOffset = baseOffsetFromBottom + (nutSpacing * index)

                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .offset(y = -yBottomOffset)
                    ) {
                        NutView(
                            nut = nut,
                            width = boltWidth - 6.dp,
                            height = nutHeight,
                            isHovering = isHoveringThis,
                            hoverOffsetY = (-42).dp
                        )
                    }
                }
            }
        }
    }
}

/**
 * Pulse indicator ring rendered ONLY when hint is active on this bolt.
 * Avoids any recomposition overhead during normal gameplay.
 */
@Composable
private fun HintTargetRing(boltWidth: Dp, boltHeight: Dp) {
    val infiniteTransition = rememberInfiniteTransition(label = "hintPulse")
    val hintAlpha by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(550, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "hintAlpha"
    )

    Canvas(modifier = Modifier.size(width = boltWidth, height = boltHeight)) {
        drawCircle(
            color = Color(0xFFFFEB3B).copy(alpha = hintAlpha),
            radius = size.width * 0.48f,
            center = Offset(size.width / 2f, size.height - 10.dp.toPx()),
            style = Stroke(width = 3.5.dp.toPx())
        )
    }
}

/**
 * Draws the silver threaded screw rod and base pedestal.
 */
private fun DrawScope.drawMetallicBoltShaft(
    boltWidth: Float,
    boltHeight: Float,
    isCompleted: Boolean
) {
    val centerX = boltWidth / 2f
    val rodWidth = boltWidth * 0.28f
    val rodLeft = centerX - (rodWidth / 2f)
    val rodTop = boltHeight * 0.12f
    val baseHeight = boltHeight * 0.11f
    val baseBottom = boltHeight - 4f
    val baseTop = baseBottom - baseHeight

    // 1. Circular / Oval Pedestal Base
    val baseWidth = boltWidth * 0.90f
    val baseLeft = centerX - (baseWidth / 2f)

    // Base drop shadow
    drawOval(
        color = Color(0x77080C14),
        topLeft = Offset(baseLeft + 2f, baseBottom - 8f),
        size = Size(baseWidth - 4f, 14f)
    )

    // Base lower rim
    val baseRimPath = Path().apply {
        addOval(Rect(baseLeft, baseTop + 6f, baseLeft + baseWidth, baseBottom))
    }
    drawPath(
        path = baseRimPath,
        brush = Brush.verticalGradient(
            colors = listOf(Color(0xFF8E99A8), Color(0xFF47505E)),
            startY = baseTop,
            endY = baseBottom
        )
    )

    // Base top shiny platform
    val baseTopPath = Path().apply {
        addOval(Rect(baseLeft + 2f, baseTop, baseLeft + baseWidth - 2f, baseTop + 14f))
    }
    drawPath(
        path = baseTopPath,
        brush = Brush.horizontalGradient(
            colors = listOf(
                Color(0xFFBAC5D4),
                Color(0xFFFFFFFF),
                Color(0xFFDCE3ED),
                Color(0xFF7A8696)
            ),
            startX = baseLeft,
            endX = baseLeft + baseWidth
        )
    )

    // 2. Vertical Threaded Screw Rod
    val rodBrush = Brush.horizontalGradient(
        colors = listOf(
            Color(0xFFBAC4D1),
            Color(0xFFFFFFFF),
            Color(0xFFDFE6F0),
            Color(0xFF8692A3),
            Color(0xFF535D6E)
        ),
        startX = rodLeft,
        endX = rodLeft + rodWidth
    )

    drawRect(
        brush = rodBrush,
        topLeft = Offset(rodLeft, rodTop),
        size = Size(rodWidth, baseTop - rodTop + 8f)
    )

    // Top rounded screw cap
    drawOval(
        brush = Brush.horizontalGradient(
            colors = listOf(Color(0xFFBAC4D1), Color(0xFFFFFFFF), Color(0xFF7A8696)),
            startX = rodLeft,
            endX = rodLeft + rodWidth
        ),
        topLeft = Offset(rodLeft, rodTop - 4f),
        size = Size(rodWidth, 8f)
    )

    // Thread Grooves (Helical diagonal lines along the rod)
    val threadSpacing = 7f
    var currY = rodTop + 6f
    while (currY < baseTop) {
        drawLine(
            brush = Brush.horizontalGradient(
                colors = listOf(
                    Color(0x33404B5C),
                    Color(0xFF6B7787),
                    Color(0xFF384252)
                ),
                startX = rodLeft,
                endX = rodLeft + rodWidth
            ),
            start = Offset(rodLeft, currY),
            end = Offset(rodLeft + rodWidth, currY + 2.5f),
            strokeWidth = 2.0f
        )
        // Thread highlight reflection
        drawLine(
            color = Color(0x66FFFFFF),
            start = Offset(rodLeft + (rodWidth * 0.25f), currY + 1f),
            end = Offset(rodLeft + (rodWidth * 0.55f), currY + 1.8f),
            strokeWidth = 1.0f
        )
        currY += threadSpacing
    }
}

/**
 * Golden aura for completed bolts.
 */
@Composable
private fun CompletedBoltGlow(
    width: Dp,
    height: Dp
) {
    Canvas(modifier = Modifier.size(width = width, height = height)) {
        val centerX = size.width / 2f
        val centerY = size.height * 0.5f

        // Soft radial aura without endless recompositions
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0x55FFE082),
                    Color(0x22FFD54F),
                    Color(0x00FFD54F)
                ),
                center = Offset(centerX, centerY),
                radius = size.width * 0.82f
            ),
            center = Offset(centerX, centerY),
            radius = size.width * 0.82f
        )
    }
}
