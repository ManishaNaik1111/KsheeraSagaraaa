package com.ksheera.sagaraaa.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ksheera.sagaraaa.AppStrings

data class PieSlice(val label: String, val value: Float, val color: Color)

@Composable
fun PieChartView(slices: List<PieSlice>, modifier: Modifier = Modifier) {
    val lang = AppStrings.currentLanguage
    val total = slices.sumOf { it.value.toDouble() }.toFloat()

    if (total == 0f) {
        Box(modifier = modifier, contentAlignment = Alignment.Center) {
            Text("No expense data yet", color = Color.Gray)
        }
        return
    }
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(contentAlignment = Alignment.Center) {
            Canvas(modifier = Modifier.size(200.dp)) {
                var startAngle = -90f
                val strokeWidth = 60f
                val radius = size.minDimension / 2f
                val topLeft = Offset(strokeWidth / 2, strokeWidth / 2)
                val arcSize = Size(
                    size.width - strokeWidth,
                    size.height - strokeWidth
                )
                slices.forEach { slice ->
                    val sweepAngle = (slice.value / total) * 360f
                    drawArc(
                        color = slice.color,
                        startAngle = startAngle,
                        sweepAngle = sweepAngle - 1f,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(width = strokeWidth)
                    )
                    startAngle += sweepAngle
                }
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "Total",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                Text(
                    "₹${"%.0f".format(total)}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1B5E20)
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        slices.chunked(2).forEach { rowSlices ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                rowSlices.forEach { slice ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(4.dp)
                    ) {
                        Canvas(modifier = Modifier.size(10.dp)) {
                            drawCircle(color = slice.color)
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${slice.label}\n₹${"%.0f".format(slice.value)}",
                            fontSize = 11.sp,
                            color = Color.DarkGray
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    color: Color
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = title, fontSize = 12.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontWeight = FontWeight.Bold,
                color = color,
                fontSize = 16.sp
            )
        }
    }
}