package com.ksheera.sagaraaa.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
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

@Composable
fun FinancialLiteracyScreen(viewModel: FarmViewModel) {
    @Suppress("UNUSED_VARIABLE")
    val lang = AppStrings.currentLanguage

    val income by viewModel.monthlyIncome.collectAsState()
    val expense by viewModel.monthlyExpense.collectAsState()
    val litres by viewModel.monthlyLitres.collectAsState()

    val incomeVal = income ?: 0.0
    val expenseVal = expense ?: 0.0
    val litresVal = litres ?: 0.0
    val netProfit = incomeVal - expenseVal
    val profitMargin = if (incomeVal > 0) (netProfit / incomeVal) * 100 else 0.0

    val score = when {
        profitMargin >= 30 -> 5
        profitMargin >= 20 -> 4
        profitMargin >= 10 -> 3
        profitMargin >= 0 -> 2
        else -> 1
    }

    // Tips with translations
    val tips = listOf(
        Triple("🌾", AppStrings.get("tip_title_1"), AppStrings.get("tip_desc_1")),
        Triple("📊", AppStrings.get("tip_title_2"), AppStrings.get("tip_desc_2")),
        Triple("🐄", AppStrings.get("tip_title_3"), AppStrings.get("tip_desc_3")),
        Triple("💊", AppStrings.get("tip_title_4"), AppStrings.get("tip_desc_4")),
        Triple("🥛", AppStrings.get("tip_title_5"), AppStrings.get("tip_desc_5")),
        Triple("🏦", AppStrings.get("tip_title_6"), AppStrings.get("tip_desc_6")),
        Triple("📱", AppStrings.get("tip_title_7"), AppStrings.get("tip_desc_7")),
        Triple("📈", AppStrings.get("tip_title_8"), AppStrings.get("tip_desc_8"))
    )

    val decisions = listOf(
        Pair(AppStrings.get("decision_1_if"), AppStrings.get("decision_1_then")),
        Pair(AppStrings.get("decision_2_if"), AppStrings.get("decision_2_then")),
        Pair(AppStrings.get("decision_3_if"), AppStrings.get("decision_3_then")),
        Pair(AppStrings.get("decision_4_if"), AppStrings.get("decision_4_then")),
        Pair(AppStrings.get("decision_5_if"), AppStrings.get("decision_5_then"))
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            "📚 ${AppStrings.get("financial_literacy")}",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1B5E20)
        )
        Text(
            AppStrings.get("literacy_subtitle"),
            fontSize = 13.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Input Cost vs Gross Income
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    AppStrings.get("input_vs_gross"),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color(0xFF0D47A1)
                )
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .background(Color(0xFFFFEBEE), RoundedCornerShape(8.dp))
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "📥 ${AppStrings.get("input_cost")}",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                        Text(
                            "₹${"%.2f".format(expenseVal)}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color(0xFFC62828)
                        )
                        Text(
                            AppStrings.get("what_you_spend"),
                            fontSize = 10.sp,
                            color = Color.Gray
                        )
                    }
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .background(Color(0xFFE8F5E9), RoundedCornerShape(8.dp))
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "📤 ${AppStrings.get("gross_income")}",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                        Text(
                            "₹${"%.2f".format(incomeVal)}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color(0xFF2E7D32)
                        )
                        Text(
                            AppStrings.get("what_you_earn"),
                            fontSize = 10.sp,
                            color = Color.Gray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            if (netProfit >= 0) Color(0xFF2E7D32) else Color(0xFFC62828),
                            RoundedCornerShape(8.dp)
                        )
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            AppStrings.get("net_profit_formula"),
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 11.sp
                        )
                        Text(
                            "₹${"%.2f".format(netProfit)}",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp
                        )
                        Text(
                            "${AppStrings.get("profit_margin")}: ${"%.1f".format(profitMargin)}%",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Entrepreneurship Tips
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1)),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "🚀 ${AppStrings.get("entrepreneurship")}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color(0xFFF57F17)
                )
                Text(
                    AppStrings.get("entrepreneurship_subtitle"),
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(12.dp))

                tips.forEach { (emoji, title, description) ->
                    ExpandableTip(emoji = emoji, title = title, description = description)
                    Spacer(modifier = Modifier.height(6.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Data-Driven Decisions
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "🎯 ${AppStrings.get("data_driven")}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color(0xFF1B5E20)
                )
                Spacer(modifier = Modifier.height(8.dp))

                decisions.forEach { (condition, action) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .background(Color.White, RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    ) {
                        Column {
                            Text(
                                "${AppStrings.get("if_label")} $condition",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0D47A1)
                            )
                            Text(
                                "${AppStrings.get("then_label")} $action",
                                fontSize = 12.sp,
                                color = Color(0xFF1B5E20)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Business Health Score
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = when (score) {
                    5 -> Color(0xFF1B5E20)
                    4 -> Color(0xFF2E7D32)
                    3 -> Color(0xFFF57F17)
                    2 -> Color(0xFFE65100)
                    else -> Color(0xFFC62828)
                }
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    AppStrings.get("business_health"),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "${"⭐".repeat(score)}${"☆".repeat(5 - score)}",
                    fontSize = 28.sp
                )
                Text(
                    when (score) {
                        5 -> AppStrings.get("excellent")
                        4 -> AppStrings.get("very_good")
                        3 -> AppStrings.get("good")
                        2 -> AppStrings.get("fair")
                        else -> AppStrings.get("needs_attention")
                    },
                    color = Color.White,
                    fontSize = 13.sp
                )
            }
        }
    }
}

@Composable
fun ExpandableTip(emoji: String, title: String, description: String) {
    var expanded by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(emoji, fontSize = 22.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color(0xFF1B5E20)
                    )
                }
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        if (expanded) Icons.Default.KeyboardArrowUp
                        else Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = Color(0xFF1B5E20)
                    )
                }
            }
            if (expanded) {
                HorizontalDivider(color = Color(0xFFE8F5E9))
                Text(
                    description,
                    fontSize = 13.sp,
                    color = Color.DarkGray,
                    modifier = Modifier.padding(
                        start = 16.dp, end = 16.dp, bottom = 12.dp, top = 8.dp
                    )
                )
            }
        }
    }
}