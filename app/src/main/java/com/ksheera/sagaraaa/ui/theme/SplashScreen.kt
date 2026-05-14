package com.ksheera.sagaraaa.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ksheera.sagaraaa.AppStrings
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onSplashDone: () -> Unit) {

    val scale = remember { Animatable(0f) }
    val alpha = remember { Animatable(0f) }
    val textAlpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        scale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
        alpha.animateTo(1f, animationSpec = tween(500))
        textAlpha.animateTo(1f, animationSpec = tween(800))
        delay(1800)
        onSplashDone()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1B5E20),
                        Color(0xFF2E7D32),
                        Color(0xFF4CAF50)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Animated cow emoji
            Text(
                text = "\uD83D\uDC04",
                fontSize = 100.sp,
                modifier = Modifier.scale(scale.value)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // App name in English
            Text(
                text = "KsheeraSagara",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.alpha(textAlpha.value)
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Kannada name — using AppStrings so it uses correct spelling
            Text(
                text = "\u0C95\u0CCD\u0CB7\u0CC0\u0CB0\u0CB8\u0CBE\u0C97\u0CB0",
                fontSize = 22.sp,
                color = Color(0xFFB9F6CA),
                modifier = Modifier.alpha(textAlpha.value)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Dairy Profit Calculator",
                fontSize = 14.sp,
                color = Color(0xFFB9F6CA),
                modifier = Modifier.alpha(textAlpha.value)
            )

            Spacer(modifier = Modifier.height(50.dp))

            LoadingDots(modifier = Modifier.alpha(alpha.value))
        }
    }
}

@Composable
fun LoadingDots(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "dots")

    val dot1 by infiniteTransition.animateFloat(
        initialValue = 0.3f, targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600),
            repeatMode = RepeatMode.Reverse,
            initialStartOffset = StartOffset(0)
        ), label = "dot1"
    )
    val dot2 by infiniteTransition.animateFloat(
        initialValue = 0.3f, targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600),
            repeatMode = RepeatMode.Reverse,
            initialStartOffset = StartOffset(200)
        ), label = "dot2"
    )
    val dot3 by infiniteTransition.animateFloat(
        initialValue = 0.3f, targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600),
            repeatMode = RepeatMode.Reverse,
            initialStartOffset = StartOffset(400)
        ), label = "dot3"
    )

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        listOf(dot1, dot2, dot3).forEach { alphaVal ->
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .alpha(alphaVal)
                    .background(Color.White, shape = CircleShape)
            )
        }
    }
}