package com.example.kokoro82m.utils

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.State

@Composable
fun DotMatrixAnimation(
    modifier: Modifier = Modifier,
    dotColor: Color = MaterialTheme.colorScheme.primary,
    backgroundColor: Color = Color.Transparent,
    rowCount: Int = 5,
    columnCount: Int = 10,
    animationDuration: Int = 1000
) {
    val infiniteTransition = rememberInfiniteTransition(label = "Infinite Dot matrix")

    val animatedProgressList = List(columnCount) { index ->
        infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = animationDuration,
                    easing = FastOutSlowInEasing,
                    delayMillis = index * (animationDuration / columnCount) / 2
                )
            ), label = "Dot animation $index"
        )
    }

    Box(modifier = modifier) {
        Canvas(modifier = Modifier.size(120.dp)) {
            drawDotMatrix(
                animatedProgressList = animatedProgressList,
                dotColor = dotColor,
                backgroundColor = backgroundColor,
                rowCount = rowCount,
                columnCount = columnCount,
            )
        }
    }
}

private fun DrawScope.drawDotMatrix(
    animatedProgressList: List<State<Float>>,
    dotColor: Color,
    backgroundColor: Color,
    rowCount: Int,
    columnCount: Int
) {
    drawRect(backgroundColor, Offset.Zero, Size(size.width, size.height))

    val dotSize = size.width / (columnCount * 2f)
    val dotSpacing = dotSize
    val totalWidth = (dotSize + dotSpacing) * columnCount - dotSpacing
    val totalHeight = (dotSize + dotSpacing) * rowCount - dotSpacing

    val startX = (size.width - totalWidth) / 2f
    val startY = (size.height - totalHeight) / 2f

    for (col in 0 until columnCount) {
        for (row in 0 until rowCount) {
            val x = startX + col * (dotSize + dotSpacing)
            val y = startY + row * (dotSize + dotSpacing)

            val animationProgress = animatedProgressList[col].value

            val isDotVisible =
                animationProgress > (0.5f + row.toFloat() / rowCount.toFloat() / 2) // Wave effect

            if (isDotVisible) {
                drawCircle(
                    color = dotColor,
                    radius = dotSize / 2f,
                    center = Offset(x + dotSize / 2f, y + dotSize / 2f)
                )
            }
        }
    }
}