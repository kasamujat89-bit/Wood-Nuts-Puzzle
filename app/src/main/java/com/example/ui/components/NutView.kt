package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Nut

/**
 * Renders a high-fidelity 3D hexagonal nut with realistic chamfers, bevels,
 * central screw hole, and mystery challenge mode.
 * Snappy 130ms lifting transition for instantaneous finger responsiveness.
 */
@Composable
fun NutView(
    nut: Nut,
    modifier: Modifier = Modifier,
    width: Dp = 64.dp,
    height: Dp = 26.dp,
    isHovering: Boolean = false,
    hoverOffsetY: Dp = (-42).dp
) {
    val animatedOffset by animateDpAsState(
        targetValue = if (isHovering) hoverOffsetY else 0.dp,
        animationSpec = tween(
            durationMillis = 130,
            easing = FastOutSlowInEasing
        ),
        label = "nutLift"
    )

    Box(
        modifier = modifier
            .size(width = width, height = height)
            .offset(y = animatedOffset),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(width = width, height = height)) {
            draw3DHexNut(
                nut = nut,
                width = size.width,
                height = size.height,
                isHovering = isHovering
            )
        }

        // Question mark for Mystery Nut
        if (!nut.isVisible) {
            Text(
                text = "?",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Black,
                modifier = Modifier.offset(y = 1.dp)
            )
        }
    }
}

private fun DrawScope.draw3DHexNut(
    nut: Nut,
    width: Float,
    height: Float,
    isHovering: Boolean
) {
    val isMystery = !nut.isVisible

    val topFaceHeight = height * 0.45f
    val frontWallHeight = height * 0.55f

    // Color definitions
    val primaryColor = if (isMystery) Color(0xFF38404E) else nut.color.primaryColor
    val highlightColor = if (isMystery) Color(0xFF6B7588) else nut.color.lightHighlight
    val shadowColor = if (isMystery) Color(0xFF1E232B) else nut.color.darkShadow
    val strokeColor = if (isMystery) Color(0xFF14181F) else nut.color.borderColor

    // Geometry of the 3D hexagon:
    val leftX = width * 0.05f
    val rightX = width * 0.95f
    val centerX = width * 0.5f

    val quarterLeftX = width * 0.24f
    val quarterRightX = width * 0.76f

    val topY = 2f
    val midY = topFaceHeight
    val bottomY = height - 2f

    // 1. Drop shadow below nut
    val shadowPath = Path().apply {
        moveTo(quarterLeftX, bottomY + 1f)
        lineTo(centerX, bottomY + 3f)
        lineTo(quarterRightX, bottomY + 1f)
        lineTo(rightX, midY + frontWallHeight)
        lineTo(leftX, midY + frontWallHeight)
        close()
    }
    drawPath(shadowPath, color = Color(0x44000000))

    // 2. Front Facets (3 vertical walls of the hexagonal nut)
    // Left front wall
    val leftWallPath = Path().apply {
        moveTo(leftX, midY)
        lineTo(quarterLeftX, midY + 4f)
        lineTo(quarterLeftX, bottomY - 1f)
        lineTo(leftX, bottomY - 3f)
        close()
    }
    drawPath(
        path = leftWallPath,
        brush = Brush.verticalGradient(
            colors = listOf(primaryColor, shadowColor),
            startY = midY,
            endY = bottomY
        )
    )

    // Center front wall (receives direct light)
    val centerWallPath = Path().apply {
        moveTo(quarterLeftX, midY + 4f)
        lineTo(quarterRightX, midY + 4f)
        lineTo(quarterRightX, bottomY)
        lineTo(quarterLeftX, bottomY)
        close()
    }
    drawPath(
        path = centerWallPath,
        brush = Brush.verticalGradient(
            colors = listOf(highlightColor, primaryColor),
            startY = midY,
            endY = bottomY
        )
    )

    // Right front wall (shadow side)
    val rightWallPath = Path().apply {
        moveTo(quarterRightX, midY + 4f)
        lineTo(rightX, midY)
        lineTo(rightX, bottomY - 3f)
        lineTo(quarterRightX, bottomY - 1f)
        close()
    }
    drawPath(
        path = rightWallPath,
        brush = Brush.verticalGradient(
            colors = listOf(shadowColor, strokeColor),
            startY = midY,
            endY = bottomY
        )
    )

    // 3. Top Face of Hex Nut (Hexagonal chamfer)
    val topFacePath = Path().apply {
        moveTo(centerX, topY)
        lineTo(quarterRightX, topY + 2f)
        lineTo(rightX, midY)
        lineTo(quarterRightX, midY + 4f)
        lineTo(quarterLeftX, midY + 4f)
        lineTo(leftX, midY)
        lineTo(quarterLeftX, topY + 2f)
        close()
    }
    drawPath(
        path = topFacePath,
        brush = Brush.verticalGradient(
            colors = listOf(highlightColor, primaryColor),
            startY = topY,
            endY = midY + 4f
        )
    )

    // Inner Hole (Screw threading view)
    val holeWidth = width * 0.32f
    val holeHeight = topFaceHeight * 0.70f
    val holeLeft = (width - holeWidth) / 2f
    val holeTop = topY + (topFaceHeight - holeHeight) / 2f + 2f

    val holePath = Path().apply {
        addOval(Rect(holeLeft, holeTop, holeLeft + holeWidth, holeTop + holeHeight))
    }

    // Inner hole cavity
    drawPath(
        path = holePath,
        brush = Brush.verticalGradient(
            colors = listOf(Color(0xFF262C38), Color(0xFF6B7485)),
            startY = holeTop,
            endY = holeTop + holeHeight
        )
    )

    // Inner hole thread ridge line
    drawOval(
        brush = Brush.horizontalGradient(
            colors = listOf(Color(0xFF4A5568), Color(0xFFBAC5D5), Color(0xFF3B4454))
        ),
        topLeft = Offset(holeLeft + 2f, holeTop + 1f),
        size = Size(holeWidth - 4f, holeHeight - 2f),
        style = Stroke(width = 1.2f)
    )

    // Top rim highlight line
    drawPath(
        path = topFacePath,
        color = Color(0x66FFFFFF),
        style = Stroke(width = 1.2f)
    )

    // Subtle facet divider grooves
    drawLine(
        color = Color(0x33000000),
        start = Offset(quarterLeftX, midY + 4f),
        end = Offset(quarterLeftX, bottomY),
        strokeWidth = 1.2f
    )
    drawLine(
        color = Color(0x33000000),
        start = Offset(quarterRightX, midY + 4f),
        end = Offset(quarterRightX, bottomY),
        strokeWidth = 1.2f
    )

    // Glowing halo if hovering
    if (isHovering) {
        val glowColor = if (isMystery) Color(0x88FFFFFF) else nut.color.glowColor.copy(alpha = 0.7f)
        drawPath(
            path = topFacePath,
            color = glowColor,
            style = Stroke(width = 3.5f)
        )
        drawPath(
            path = centerWallPath,
            color = glowColor,
            style = Stroke(width = 2.5f)
        )
    }
}
