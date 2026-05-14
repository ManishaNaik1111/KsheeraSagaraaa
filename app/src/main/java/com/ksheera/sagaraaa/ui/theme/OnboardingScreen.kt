package com.ksheera.sagaraaa.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ksheera.sagaraaa.AppStrings

data class OnboardingPage(
    val emoji: String,
    val title: String,
    val description: String,
    val bgColor1: Color,
    val bgColor2: Color
)

@Composable
fun OnboardingScreen(onFinished: () -> Unit) {
    val lang = AppStrings.currentLanguage

    val pages = listOf(
        OnboardingPage(
            emoji = "🥛",
            title = "Track Daily Milk",
            description = "Enter your daily milk slip data — Litres, Fat%, SNF% and Rate per litre. Never miss a day's income!",
            bgColor1 = Color(0xFF1B5E20),
            bgColor2 = Color(0xFF4CAF50)
        ),
        OnboardingPage(
            emoji = "💸",
            title = "Record Every Expense",
            description = "Log Fodder, Medicine, Labour and Electricity costs cow-wise. Know exactly where your money goes!",
            bgColor1 = Color(0xFF0D47A1),
            bgColor2 = Color(0xFF2196F3)
        ),
        OnboardingPage(
            emoji = "📊",
            title = "See Your Profit!",
            description = "Get instant Profit/Loss dashboard, Pie charts, Cow-wise analysis and share Monthly PDF reports!",
            bgColor1 = Color(0xFF4A148C),
            bgColor2 = Color(0xFF9C27B0)
        )
    )

    var currentPage by remember { mutableStateOf(0) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(pages[currentPage].bgColor1, pages[currentPage].bgColor2)
                )
            )
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            // Skip button
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = onFinished) {
                    Text("Skip", color = Color.White.copy(alpha = 0.7f))
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Animated page content
            AnimatedContent(
                targetState = currentPage,
                transitionSpec = {
                    slideInHorizontally(
                        animationSpec = tween(400)
                    ) { it } togetherWith slideOutHorizontally(
                        animationSpec = tween(400)
                    ) { -it }
                },
                label = "onboarding"
            ) { page ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = pages[page].emoji,
                        fontSize = 100.sp
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    Text(
                        text = pages[page].title,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = pages[page].description,
                        fontSize = 16.sp,
                        color = Color.White.copy(alpha = 0.85f),
                        textAlign = TextAlign.Center,
                        lineHeight = 24.sp
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Page indicator dots
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 32.dp)
            ) {
                pages.indices.forEach { index ->
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(
                                if (index == currentPage) Color.White
                                else Color.White.copy(alpha = 0.4f)
                            )
                            .size(if (index == currentPage) 12.dp else 8.dp)
                    )
                }
            }

            // Next / Get Started button
            Button(
                onClick = {
                    if (currentPage < pages.size - 1) {
                        currentPage++
                    } else {
                        onFinished()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White)
            ) {
                Text(
                    text = if (currentPage < pages.size - 1) "Next →" else "🚀 Get Started!",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = pages[currentPage].bgColor1
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}