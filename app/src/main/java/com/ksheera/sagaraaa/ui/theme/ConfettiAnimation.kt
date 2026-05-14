package com.ksheera.sagaraaa.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import kotlin.math.sin
import kotlin.random.Random
import com.ksheera.sagaraaa.AppStrings

data class ConfettiParticle(
    val x: Float,
    val y: Float,
    val color: Color,
    val size: Float,
    val speed: Float,
    val angle: Float,
    val rotation: Float
)

@Composable
fun ConfettiAnimation(modifier: Modifier = Modifier) {
    val lang = AppStrings.currentLanguage
    val colors = listOf(
        Color(0xFF4CAF50), Color(0xFFFFC107), Color(0xFF2196F3),
        Color(0xFFE91E63), Color(0xFFFF5722), Color(0xFF9C27B0),
        Color(0xFF00BCD4), Color(0xFFFFEB3B)
    )

    val particles = remember {
        List(80) {
            ConfettiParticle(
                x = Random.nextFloat(),
                y = Random.nextFloat() * -1f,
                color = colors.random(),
                size = Random.nextFloat() * 12f + 6f,
                speed = Random.nextFloat() * 0.003f + 0.002f,
                angle = Random.nextFloat() * 360f,
                rotation = Random.nextFloat() * 10f - 5f
            )
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "confetti")
    val progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "progress"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        particles.forEach { particle ->
            val yPos = ((particle.y + progress * particle.speed * 200f) % 1.2f) * size.height
            val xPos = (particle.x + sin(progress * 6.28f + particle.angle) * 0.05f) * size.width

            drawCircle(
                color = particle.color,
                radius = particle.size,
                center = Offset(xPos, yPos)
            )
        }
    }
}