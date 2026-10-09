package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import kotlin.random.Random

private data class ConfettiParticle(
    val initialX: Float,
    val initialY: Float,
    val velocityX: Float,
    val velocityY: Float,
    val rotationSpeed: Float,
    val size: Float,
    val color: Color
)

/**
 * Celebratory full-screen confetti effect shown upon completing a level.
 */
@Composable
fun ConfettiCelebration(
    modifier: Modifier = Modifier,
    particleCount: Int = 80
) {
    val progress = remember { Animatable(0f) }

    val particles = remember {
        val colors = listOf(
            Color(0xFFFFD700), // Gold
            Color(0xFFFF3B30), // Red
            Color(0xFF00C7BE), // Cyan
            Color(0xFF34C759), // Green
            Color(0xFFFF9500), // Orange
            Color(0xFFAF52DE), // Purple
            Color(0xFF007AFF)  // Blue
        )
        List(particleCount) {
            ConfettiParticle(
                initialX = Random.nextFloat(),
                initialY = Random.nextFloat() * 0.3f, // start near top
                velocityX = (Random.nextFloat() - 0.5f) * 160f,
                velocityY = 300f + Random.nextFloat() * 400f,
                rotationSpeed = (Random.nextFloat() - 0.5f) * 720f,
                size = 8f + Random.nextFloat() * 10f,
                color = colors.random()
            )
        }
    }

    LaunchedEffect(Unit) {
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 2600, easing = LinearEasing)
        )
    }

    if (progress.value < 1f) {
        Canvas(modifier = modifier.fillMaxSize()) {
            val canvasW = size.width
            val canvasH = size.height
            val t = progress.value

            particles.forEach { p ->
                val x = (p.initialX * canvasW) + (p.velocityX * t)
                val y = (p.initialY * canvasH) + (p.velocityY * t) + (0.5f * 200f * t * t)
                val rot = p.rotationSpeed * t

                val alpha = (1f - (t * 0.9f)).coerceIn(0f, 1f)

                rotate(degrees = rot, pivot = Offset(x, y)) {
                    drawRect(
                        color = p.color.copy(alpha = alpha),
                        topLeft = Offset(x - p.size / 2f, y - p.size / 2f),
                        size = Size(p.size, p.size * 1.5f)
                    )
                }
            }
        }
    }
}
