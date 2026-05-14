package com.ksheera.sagaraaa.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ksheera.sagaraaa.AppStrings
import com.ksheera.sagaraaa.data.ExpenseEntry
import com.ksheera.sagaraaa.data.MilkEntry
import com.ksheera.sagaraaa.viewmodel.FarmViewModel

@Composable
fun CowAnalysisScreen(viewModel: FarmViewModel) {
    @Suppress("UNUSED_VARIABLE")
    val lang = AppStrings.currentLanguage
    val cows by viewModel.allCows.collectAsState()
    val allMilkEntries by viewModel.allMilkEntries.collectAsState()
    val allExpenses by viewModel.allExpenses.collectAsState()

    data class CowStats(
        val id: Int,
        val name: String,
        val breed: String,
        val milkEntries: List<MilkEntry>,
        val expenses: List<ExpenseEntry>,
        val totalLitres: Double,
        val totalIncome: Double,
        val fodderCost: Double,
        val medicalCost: Double,
        val labourCost: Double,
        val electricityCost: Double,
        val otherCost: Double,
        val totalExpense: Double,
        val netProfit: Double,
        val profitPerLitre: Double,
        val avgFat: Double
    )

    val cowStatsList = if (cows.isEmpty()) emptyList() else {
        cows.map { cow ->
            val entries = if (cows.size == 1) allMilkEntries
            else allMilkEntries.filter { it.cowId == cow.id || it.cowId == -1 }

            val cowExpenses = if (cows.size == 1) allExpenses
            else {
                val cowSpecific = allExpenses.filter { it.cowId == cow.id }
                val shared = allExpenses.filter { it.cowId == -1 || it.cowId == 0 }
                val sharedPerCow = shared.map {
                    it.copy(amount = it.amount / cows.size)
                }
                cowSpecific + sharedPerCow
            }

            val totalLitres = entries.sumOf { it.litres }
            val totalIncome = entries.sumOf { it.totalIncome }
            val fodder = cowExpenses.filter { it.category == "Fodder" }.sumOf { it.amount }
            val medical = cowExpenses.filter { it.category == "Medical" }.sumOf { it.amount }
            val labour = cowExpenses.filter { it.category == "Labour" }.sumOf { it.amount }
            val electricity = cowExpenses.filter { it.category == "Electricity" }.sumOf { it.amount }
            val others = cowExpenses.filter { it.category == "Others" }.sumOf { it.amount }
            val totalExpense = fodder + medical + labour + electricity + others
            val netProfit = totalIncome - totalExpense
            val profitPerLitre = if (totalLitres > 0) netProfit / totalLitres else 0.0
            val avgFat = if (entries.isNotEmpty()) entries.map { it.fatPercentage }.average() else 0.0

            CowStats(
                id = cow.id,
                name = cow.name,
                breed = cow.breed,
                milkEntries = entries,
                expenses = cowExpenses,
                totalLitres = totalLitres,
                totalIncome = totalIncome,
                fodderCost = fodder,
                medicalCost = medical,
                labourCost = labour,
                electricityCost = electricity,
                otherCost = others,
                totalExpense = totalExpense,
                netProfit = netProfit,
                profitPerLitre = profitPerLitre,
                avgFat = avgFat
            )
        }.sortedByDescending { it.netProfit }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            AppStrings.get("cow_analysis"),
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1B5E20)
        )
        Text(
            "Individual cow profitability & expense breakdown",
            fontSize = 13.sp,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(12.dp))

        if (cows.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1))
            ) {
                Column(
                    modifier = Modifier.padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("🐄", fontSize = 48.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "No cows added yet!",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        "Go to 'My Cows' tab and add your cows to see individual analysis.",
                        color = Color.Gray,
                        fontSize = 13.sp
                    )
                }
            }
            return
        }

        // Top performer banner
        if (cowStatsList.isNotEmpty()) {
            val best = cowStatsList.first()
            val worst = cowStatsList.last()

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF2E7D32))
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("🏆 Best", color = Color.White, fontSize = 11.sp)
                        Text(
                            best.name,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            "₹${"%.0f".format(best.netProfit)}",
                            color = Color(0xFFB9F6CA),
                            fontSize = 12.sp
                        )
                    }
                }
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(
                        containerColor = if (worst.netProfit < 0) Color(0xFFC62828)
                        else Color(0xFF388E3C)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            if (worst.netProfit < 0) "⚠️ Loss" else "✅ Good",
                            color = Color.White,
                            fontSize = 11.sp
                        )
                        Text(
                            worst.name,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            "₹${"%.0f".format(worst.netProfit)}",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 12.sp
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(cowStatsList) { stats ->
                val isProfit = stats.netProfit >= 0
                val shouldSell = stats.netProfit < 0 && stats.totalLitres < 10

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {

                        // Header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("🐄", fontSize = 24.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        stats.name,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp,
                                        color = Color(0xFF1B5E20)
                                    )
                                    if (stats.breed.isNotEmpty()) {
                                        Text(
                                            stats.breed,
                                            fontSize = 12.sp,
                                            color = Color.Gray
                                        )
                                    }
                                }
                            }
                            Box(
                                modifier = Modifier
                                    .background(
                                        color = if (isProfit) Color(0xFF2E7D32) else Color(0xFFC62828),
                                        shape = RoundedCornerShape(20.dp)
                                    )
                                    .padding(horizontal = 12.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    if (isProfit) "✅ PROFIT" else "❌ LOSS",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Income vs Expense bar
                        Text("Income vs Expense", fontSize = 12.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(modifier = Modifier.fillMaxWidth()) {
                            if (stats.totalIncome > 0 || stats.totalExpense > 0) {
                                val total = maxOf(stats.totalIncome, stats.totalExpense)
                                val incomeRatio = if (total > 0) (stats.totalIncome / total).toFloat() else 0f
                                val expenseRatio = if (total > 0) (stats.totalExpense / total).toFloat() else 0f
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(modifier = Modifier.fillMaxWidth().height(8.dp)) {
                                        Box(
                                            modifier = Modifier
                                                .weight(incomeRatio.coerceAtLeast(0.01f))
                                                .fillMaxHeight()
                                                .background(
                                                    Color(0xFF4CAF50),
                                                    RoundedCornerShape(4.dp)
                                                )
                                        )
                                        Spacer(modifier = Modifier.width(2.dp))
                                        Box(
                                            modifier = Modifier
                                                .weight(expenseRatio.coerceAtLeast(0.01f))
                                                .fillMaxHeight()
                                                .background(
                                                    Color(0xFFE53935),
                                                    RoundedCornerShape(4.dp)
                                                )
                                        )
                                    }
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            "Income ₹${"%.0f".format(stats.totalIncome)}",
                                            fontSize = 10.sp,
                                            color = Color(0xFF4CAF50)
                                        )
                                        Text(
                                            "Expense ₹${"%.0f".format(stats.totalExpense)}",
                                            fontSize = 10.sp,
                                            color = Color(0xFFE53935)
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(8.dp))

                        // Key metrics grid
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            CowMetric("🥛 Litres", "${"%.1f".format(stats.totalLitres)}L")
                            CowMetric("📊 Avg Fat", "${"%.1f".format(stats.avgFat)}%")
                            CowMetric("📅 Days", "${stats.milkEntries.size}")
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            CowMetric(
                                "💰 Income",
                                "₹${"%.0f".format(stats.totalIncome)}",
                                Color(0xFF2E7D32)
                            )
                            CowMetric(
                                "💸 Expense",
                                "₹${"%.0f".format(stats.totalExpense)}",
                                Color(0xFFE53935)
                            )
                            CowMetric(
                                "📈 Net",
                                "₹${"%.0f".format(stats.netProfit)}",
                                if (isProfit) Color(0xFF2E7D32) else Color(0xFFE53935)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(8.dp))

                        // Expense breakdown for this cow
                        Text(
                            "Expense Breakdown:",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1B5E20)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            CowExpenseItem("🌾 Fodder", stats.fodderCost)
                            CowExpenseItem("💊 Medical", stats.medicalCost)
                            CowExpenseItem("👷 Labour", stats.labourCost)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            CowExpenseItem("⚡ Electricity", stats.electricityCost)
                            CowExpenseItem("📦 Others", stats.otherCost)
                            CowMetric(
                                "📈 ₹/Litre",
                                "₹${"%.2f".format(stats.profitPerLitre)}",
                                if (stats.profitPerLitre >= 0) Color(0xFF2E7D32) else Color(0xFFE53935)
                            )
                        }

                        // Sell/Keep recommendation
                        Spacer(modifier = Modifier.height(8.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = if (shouldSell) Color(0xFFFFCDD2)
                                else if (isProfit) Color(0xFFE8F5E9)
                                else Color(0xFFFFF9C4)
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    if (shouldSell) "🔴" else if (isProfit) "🟢" else "🟡",
                                    fontSize = 18.sp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        if (shouldSell) "⚠️ Recommendation: Consider Selling"
                                        else if (isProfit) "✅ ${AppStrings.get("keep_advice")}"
                                        else "⚠️ Monitor Performance",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = if (shouldSell) Color(0xFFB71C1C)
                                        else if (isProfit) Color(0xFF1B5E20)
                                        else Color(0xFFF57F17)
                                    )
                                    Text(
                                        if (shouldSell)
                                            "This cow produces low milk and is running at a loss. Selling may be a better financial decision."
                                        else if (isProfit)
                                            "This cow is profitable! Keep maintaining good feed and health care."
                                        else
                                            "This cow's expenses exceed income. Try reducing fodder costs or increasing milking frequency.",
                                        fontSize = 11.sp,
                                        color = Color.DarkGray
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CowMetric(label: String, value: String, color: Color = Color.DarkGray) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, fontSize = 11.sp, color = Color.Gray)
        Text(value, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = color)
    }
}

@Composable
fun CowExpenseItem(label: String, amount: Double) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, fontSize = 10.sp, color = Color.Gray)
        Text(
            "₹${"%.0f".format(amount)}",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFE53935)
        )
    }
}