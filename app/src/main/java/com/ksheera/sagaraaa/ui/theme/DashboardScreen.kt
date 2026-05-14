package com.ksheera.sagaraaa.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ksheera.sagaraaa.AppStrings
import com.ksheera.sagaraaa.viewmodel.FarmViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DashboardScreen(viewModel: FarmViewModel, username: String = "") {
    @Suppress("UNUSED_VARIABLE")
    val lang = AppStrings.currentLanguage

    val income by viewModel.monthlyIncome.collectAsState()
    val expense by viewModel.monthlyExpense.collectAsState()
    val litres by viewModel.monthlyLitres.collectAsState()
    val expenses by viewModel.monthlyExpenses.collectAsState()

    val incomeVal = income ?: 0.0
    val expenseVal = expense ?: 0.0
    val litresVal = litres ?: 0.0
    val netProfit = incomeVal - expenseVal
    val profitPerLitre = if (litresVal > 0) netProfit / litresVal else 0.0
    val isProfit = netProfit >= 0
    val currentMonth = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(Date())

    val fodder = expenses.filter { it.category == "Fodder" }.sumOf { it.amount }.toFloat()
    val medical = expenses.filter { it.category == "Medical" }.sumOf { it.amount }.toFloat()
    val labour = expenses.filter { it.category == "Labour" }.sumOf { it.amount }.toFloat()
    val electricity = expenses.filter { it.category == "Electricity" }.sumOf { it.amount }.toFloat()
    val others = expenses.filter { it.category == "Others" }.sumOf { it.amount }.toFloat()

    val pieSlices = listOf(
        PieSlice(AppStrings.get("fodder"), fodder, Color(0xFF4CAF50)),
        PieSlice(AppStrings.get("medical"), medical, Color(0xFFF44336)),
        PieSlice(AppStrings.get("labour"), labour, Color(0xFF2196F3)),
        PieSlice(AppStrings.get("electricity"), electricity, Color(0xFFFF9800)),
        PieSlice(AppStrings.get("others"), others, Color(0xFF9C27B0))
    ).filter { it.value > 0f }

    // ✅ AI SUGGESTION LOGIC
    // Analyses actual farmer data and gives personalised advice
    val aiSuggestion = remember(incomeVal, expenseVal, fodder, medical, profitPerLitre, litresVal) {
        when {
            // No data yet
            incomeVal == 0.0 && expenseVal == 0.0 ->
                AppStrings.get("tip_start")

            // Critical: Expenses more than income
            expenseVal > incomeVal -> {
                val excess = expenseVal - incomeVal
                AppStrings.get("tip_fodder") +
                        " (Loss: ₹${"%.0f".format(excess)})"
            }

            // Fodder taking more than 65% of expenses
            expenseVal > 0 && (fodder / expenseVal) > 0.65f ->
                AppStrings.get("tip_fodder_high") +
                        " (${"%.0f".format(fodder / expenseVal * 100)}% ${AppStrings.get("fodder")})"

            // Medical costs high (more than 20%)
            expenseVal > 0 && (medical / expenseVal) > 0.20f ->
                AppStrings.get("tip_medical") +
                        " (${"%.0f".format(medical / expenseVal * 100)}% ${AppStrings.get("medical")})"

            // Labour cost high
            expenseVal > 0 && (labour / expenseVal) > 0.30f ->
                "👷 Labour cost is ${
                    "%.0f".format(labour / expenseVal * 100)
                }% of expenses. Consider family labour to reduce cost."

            // Low profit per litre
            litresVal > 0 && profitPerLitre < 2.0 ->
                AppStrings.get("tip_low_profit") +
                        " (₹${"%.2f".format(profitPerLitre)}/L)"

            // Profitable but could improve
            isProfit && profitPerLitre in 2.0..5.0 ->
                "📈 Good progress! Profit/Litre is ₹${"%.2f".format(profitPerLitre)}. " +
                        "Adding 1 more cow could double your income!"

            // Excellent performance
            isProfit && profitPerLitre > 5.0 ->
                AppStrings.get("tip_good") +
                        " ₹${"%.2f".format(profitPerLitre)}/L"

            else -> AppStrings.get("tip_good")
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (isProfit && incomeVal > 0) {
            ConfettiAnimation(modifier = Modifier.fillMaxSize())
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // ✅ WELCOME CARD — only on Dashboard
            if (username.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1B5E20)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = AppStrings.get("welcome_back"),
                            color = Color(0xFFB9F6CA),
                            fontSize = 13.sp
                        )
                        Text(
                            text = username,
                            color = Color.White,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = currentMonth,
                            color = Color(0xFFB9F6CA),
                            fontSize = 12.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // ✅ PROFIT/LOSS BANNER
            AnimatedContent(
                targetState = isProfit,
                transitionSpec = {
                    fadeIn(animationSpec = tween(500)) togetherWith
                            fadeOut(animationSpec = tween(500))
                },
                label = "profitBanner"
            ) { profit ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = if (profit) Color(0xFF2E7D32) else Color(0xFFC62828),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = AppStrings.get(if (profit) "profitable" else "loss"),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "₹${"%.2f".format(netProfit)}",
                            color = Color.White,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = currentMonth,
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ✅ STAT CARDS
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatCard(
                    Modifier.weight(1f),
                    "💰 ${AppStrings.get("income")}",
                    "₹${"%.0f".format(incomeVal)}",
                    Color(0xFF1B5E20)
                )
                StatCard(
                    Modifier.weight(1f),
                    "💸 ${AppStrings.get("expense")}",
                    "₹${"%.0f".format(expenseVal)}",
                    Color(0xFFC62828)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatCard(
                    Modifier.weight(1f),
                    "🥛 ${AppStrings.get("litres")}",
                    "${"%.1f".format(litresVal)}L",
                    Color(0xFF0D47A1)
                )
                StatCard(
                    Modifier.weight(1f),
                    "📈 ${AppStrings.get("profit_litre")}",
                    "₹${"%.2f".format(profitPerLitre)}",
                    if (profitPerLitre >= 0) Color(0xFF2E7D32) else Color.Red
                )
            }

            // ✅ PIE CHART
            if (pieSlices.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "📊 ${AppStrings.get("expense_breakdown")}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color(0xFF1B5E20)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        PieChartView(
                            slices = pieSlices,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ✅ AI SUGGESTION CARD
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = when {
                        expenseVal > incomeVal -> Color(0xFFFFEBEE)
                        isProfit -> Color(0xFFFFF8E1)
                        else -> Color(0xFFFFF3E0)
                    }
                ),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            when {
                                expenseVal > incomeVal -> "🤖⚠️"
                                isProfit -> "🤖✅"
                                else -> "🤖💡"
                            },
                            fontSize = 20.sp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "GenAI ${AppStrings.get("smart_tip")}",
                            fontWeight = FontWeight.Bold,
                            color = when {
                                expenseVal > incomeVal -> Color(0xFFC62828)
                                else -> Color(0xFFF57F17)
                            },
                            fontSize = 14.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    // Show expense breakdown context
                    if (expenseVal > 0) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            val total = expenseVal.toFloat()
                            listOf(
                                Triple("🌾", fodder, Color(0xFF4CAF50)),
                                Triple("💊", medical, Color(0xFFF44336)),
                                Triple("👷", labour, Color(0xFF2196F3)),
                                Triple("⚡", electricity, Color(0xFFFF9800)),
                                Triple("📦", others, Color(0xFF9C27B0))
                            ).filter { it.second > 0f }.forEach { (emoji, value, color) ->
                                Card(
                                    modifier = Modifier.weight(1f),
                                    colors = CardDefaults.cardColors(
                                        containerColor = color.copy(alpha = 0.1f)
                                    )
                                ) {
                                    Column(
                                        modifier = Modifier.padding(4.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(emoji, fontSize = 12.sp)
                                        Text(
                                            "${"%.0f".format(value / total * 100)}%",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = color
                                        )
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    Text(
                        text = aiSuggestion,
                        fontSize = 13.sp,
                        color = Color(0xFF5D4037),
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}