package com.aaryan.simdash.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.dp

@Composable
fun DataRing(
    percent: Float,
    ringColor: Color,
    modifier: Modifier = Modifier
) {
    val animatedPercent = remember { Animatable(0f) }

    LaunchedEffect(percent) {
        animatedPercent.animateTo(
            targetValue = percent,
            animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing)
        )
    }

    val trackColor = MaterialTheme.colorScheme.surfaceVariant

    Box(
        modifier = modifier.aspectRatio(1f),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            val strokeWidth = size.width * 0.1f

            // Draw Track
            drawArc(
                color = trackColor,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            // Draw Progress with premium sweep gradient
            val gradientBrush = androidx.compose.ui.graphics.Brush.sweepGradient(
                0.0f to ringColor.copy(alpha = 0.3f),
                (animatedPercent.value.coerceIn(0f, 1f)) to ringColor
            )
            
            // Re-rotate the canvas so the gradient starts exactly at the beginning of the stroke
            rotate(270f, center) {
                drawArc(
                    brush = gradientBrush,
                    startAngle = 0f,
                    sweepAngle = (animatedPercent.value.coerceIn(0f, 1f)) * 360f,
                    useCenter = false,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )
            }
        }
        
        Text(
            text = "${(percent * 100).toInt()}%",
            style = MaterialTheme.typography.displayMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
