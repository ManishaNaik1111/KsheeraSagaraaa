package com.ksheera.sagaraaa.ui

import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Environment
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.ksheera.sagaraaa.AppStrings
import com.ksheera.sagaraaa.viewmodel.FarmViewModel
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun SummaryScreen(viewModel: FarmViewModel, username: String) {
    @Suppress("UNUSED_VARIABLE")
    val lang = AppStrings.currentLanguage
    val income by viewModel.monthlyIncome.collectAsState()
    val expense by viewModel.monthlyExpense.collectAsState()
    val litres by viewModel.monthlyLitres.collectAsState()
    val milkEntries by viewModel.monthlyMilkEntries.collectAsState()
    val expenses by viewModel.monthlyExpenses.collectAsState()
    val context = LocalContext.current


    val incomeVal = income ?: 0.0
    val expenseVal = expense ?: 0.0
    val litresVal = litres ?: 0.0
    val netProfit = incomeVal - expenseVal
    val profitPerLitre = if (litresVal > 0) netProfit / litresVal else 0.0

    val fodderTotal = expenses.filter { it.category == "Fodder" }.sumOf { it.amount }
    val medicalTotal = expenses.filter { it.category == "Medical" }.sumOf { it.amount }
    val labourTotal = expenses.filter { it.category == "Labour" }.sumOf { it.amount }
    val electricityTotal = expenses.filter { it.category == "Electricity" }.sumOf { it.amount }
    val othersTotal = expenses.filter { it.category == "Others" }.sumOf { it.amount }

    val currentMonth = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(Date())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            AppStrings.get("monthly_summary"),
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1B5E20)
        )
        Text(currentMonth, color = Color.Gray, fontSize = 13.sp)

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = if (netProfit >= 0) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                SummaryRow(
                    AppStrings.get("total_income"),
                    "₹${"%.2f".format(incomeVal)}",
                    Color(0xFF2E7D32)
                )
                SummaryRow(
                    AppStrings.get("total_expense"),
                    "₹${"%.2f".format(expenseVal)}",
                    Color(0xFFC62828)
                )
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                SummaryRow(
                    AppStrings.get("net_profit"),
                    "₹${"%.2f".format(netProfit)}",
                    if (netProfit >= 0) Color(0xFF2E7D32) else Color(0xFFC62828)
                )
                SummaryRow(
                    AppStrings.get("total_litres"),
                    "${"%.2f".format(litresVal)} L",
                    Color(0xFF0D47A1)
                )
                SummaryRow(
                    AppStrings.get("profit_per_litre"),
                    "₹${"%.2f".format(profitPerLitre)}",
                    if (profitPerLitre >= 0) Color(0xFF2E7D32) else Color(0xFFC62828)
                )
                SummaryRow(
                    AppStrings.get("entry_days"),
                    "${milkEntries.size}",
                    Color.Gray
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            "💸 Expense Breakdown",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                ExpenseRow("🌾 ${AppStrings.get("fodder")}", fodderTotal)
                ExpenseRow("💊 ${AppStrings.get("medical")}", medicalTotal)
                ExpenseRow("👷 ${AppStrings.get("labour")}", labourTotal)
                ExpenseRow("⚡ ${AppStrings.get("electricity")}", electricityTotal)
                ExpenseRow("📦 ${AppStrings.get("others")}", othersTotal)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // DOWNLOAD PDF - saves to device only
        Button(
            onClick = {
                generatePdf(
                    context = context,
                    username = username,
                    milkEntries = milkEntries,
                    expenses = expenses,
                    month = currentMonth,
                    shareAfter = false  // ← false = download only
                )
            },
            modifier = Modifier.fillMaxWidth().height(54.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1565C0))
        ) {
            Icon(Icons.Default.Download, null, tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text(AppStrings.get("download_pdf"), color = Color.White, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(10.dp))

        // SEPARATE Share via WhatsApp Button
        Button(
            onClick = {
                generatePdf(
                    context = context,
                    username = username,
                    milkEntries = milkEntries,
                    expenses = expenses,
                    month = currentMonth,
                    shareAfter = true
                )
            },
            modifier = Modifier.fillMaxWidth().height(54.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366))
        ) {
            Icon(Icons.Default.Share, contentDescription = null, tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                AppStrings.get("share_whatsapp"),
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
        }
    }
}

fun generatePdf(
    context: Context,
    username: String,
    milkEntries: List<com.ksheera.sagaraaa.data.MilkEntry>,
    expenses: List<com.ksheera.sagaraaa.data.ExpenseEntry>,
    month: String,
    shareAfter: Boolean
) {
    try {
        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 1000, 1).create()
        val page = document.startPage(pageInfo)
        val canvas = page.canvas
        val paint = Paint()

        var y = 50f

        // Header
        paint.textSize = 22f
        paint.isFakeBoldText = true
        canvas.drawText("KsheeraSagara - Monthly Report", 50f, y, paint)
        y += 28f
        paint.textSize = 13f
        paint.isFakeBoldText = false
        canvas.drawText("Farmer: $username", 50f, y, paint); y += 18f
        canvas.drawText("Month: $month", 50f, y, paint); y += 18f
        canvas.drawText(
            "Generated: ${SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault()).format(Date())}",
            50f, y, paint
        ); y += 24f
        canvas.drawLine(50f, y, 545f, y, paint); y += 20f

        // Income Summary
        val totalIncome = milkEntries.sumOf { it.totalIncome }
        val totalLitres = milkEntries.sumOf { it.litres }
        val avgFat = if (milkEntries.isNotEmpty())
            milkEntries.map { it.fatPercentage }.average() else 0.0

        paint.textSize = 15f; paint.isFakeBoldText = true
        canvas.drawText("INCOME SUMMARY", 50f, y, paint); y += 20f
        paint.textSize = 13f; paint.isFakeBoldText = false
        canvas.drawText("Total Litres Sold: ${"%.2f".format(totalLitres)} L", 50f, y, paint); y += 18f
        canvas.drawText("Average Fat %: ${"%.2f".format(avgFat)}%", 50f, y, paint); y += 18f
        canvas.drawText("Total Income: Rs. ${"%.2f".format(totalIncome)}", 50f, y, paint); y += 18f
        canvas.drawText("Milk Entry Days: ${milkEntries.size}", 50f, y, paint); y += 26f

        // Expense Summary
        val fodder = expenses.filter { it.category == "Fodder" }.sumOf { it.amount }
        val medical = expenses.filter { it.category == "Medical" }.sumOf { it.amount }
        val labour = expenses.filter { it.category == "Labour" }.sumOf { it.amount }
        val electricity = expenses.filter { it.category == "Electricity" }.sumOf { it.amount }
        val others = expenses.filter { it.category == "Others" }.sumOf { it.amount }
        val totalExpense = fodder + medical + labour + electricity + others
        val netProfit = totalIncome - totalExpense
        val profitPerLitre = if (totalLitres > 0) netProfit / totalLitres else 0.0

        paint.textSize = 15f; paint.isFakeBoldText = true
        canvas.drawText("EXPENSE BREAKDOWN", 50f, y, paint); y += 20f
        paint.textSize = 13f; paint.isFakeBoldText = false

        fun expPercent(amount: Double) =
            if (totalExpense > 0) " (${(amount / totalExpense * 100).toInt()}%)" else ""

        canvas.drawText("Fodder: Rs.${"%.2f".format(fodder)}${expPercent(fodder)}", 50f, y, paint); y += 18f
        canvas.drawText("Medical: Rs.${"%.2f".format(medical)}${expPercent(medical)}", 50f, y, paint); y += 18f
        canvas.drawText("Labour: Rs.${"%.2f".format(labour)}${expPercent(labour)}", 50f, y, paint); y += 18f
        canvas.drawText("Electricity: Rs.${"%.2f".format(electricity)}${expPercent(electricity)}", 50f, y, paint); y += 18f
        canvas.drawText("Others: Rs.${"%.2f".format(others)}${expPercent(others)}", 50f, y, paint); y += 18f
        canvas.drawText("Total Expense: Rs.${"%.2f".format(totalExpense)}", 50f, y, paint); y += 26f

        // Net Profit
        canvas.drawLine(50f, y, 545f, y, paint); y += 20f
        paint.textSize = 18f; paint.isFakeBoldText = true
        canvas.drawText("NET PROFIT: Rs.${"%.2f".format(netProfit)}", 50f, y, paint); y += 24f
        paint.textSize = 14f
        canvas.drawText("Profit per Litre: Rs.${"%.2f".format(profitPerLitre)}", 50f, y, paint); y += 20f
        paint.textSize = 13f; paint.isFakeBoldText = false
        canvas.drawText(
            if (netProfit >= 0) "Status: PROFITABLE MONTH! ✓"
            else "Status: LOSS - Review expenses!", 50f, y, paint
        ); y += 30f

        // Expense % Analysis
        canvas.drawLine(50f, y, 545f, y, paint); y += 20f
        paint.textSize = 15f; paint.isFakeBoldText = true
        canvas.drawText("EXPENSE ANALYSIS", 50f, y, paint); y += 20f
        paint.textSize = 13f; paint.isFakeBoldText = false
        if (totalExpense > 0) {
            val biggestCategory = mapOf(
                "Fodder" to fodder, "Medical" to medical,
                "Labour" to labour, "Electricity" to electricity, "Others" to others
            ).maxByOrNull { it.value }
            canvas.drawText(
                "Biggest Cost: ${biggestCategory?.key} (${(biggestCategory?.value?.div(totalExpense)?.times(100))?.toInt()}%)",
                50f, y, paint
            ); y += 18f
            canvas.drawText("Input Cost Ratio: ${(totalExpense / totalIncome * 100).toInt()}% of income", 50f, y, paint); y += 26f
        }

        // Footer
        canvas.drawLine(50f, y, 545f, y, paint); y += 20f
        paint.textSize = 11f
        canvas.drawText("Generated by KsheeraSagara App | Dairy Profit Calculator", 50f, y, paint)

        document.finishPage(page)

        val fileName = "KsheeraSagara_${month.replace(" ", "_")}.pdf"
        val file = File(
            context.getExternalFilesDir(android.os.Environment.DIRECTORY_DOCUMENTS),
            fileName
        )
        document.writeTo(java.io.FileOutputStream(file))
        document.close()

        val uri = androidx.core.content.FileProvider.getUriForFile(
            context, "${context.packageName}.provider", file
        )

        if (shareAfter) {
            val intent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(android.content.Intent.EXTRA_STREAM, uri)
                putExtra(
                    android.content.Intent.EXTRA_TEXT,
                    "KsheeraSagara Monthly Report - $month\nNet: Rs.${"%.2f".format(netProfit)}"
                )
                addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
                setPackage("com.whatsapp")
            }
            try {
                context.startActivity(intent)
            } catch (e: Exception) {
                val fallback = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                    type = "application/pdf"
                    putExtra(android.content.Intent.EXTRA_STREAM, uri)
                    addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                context.startActivity(android.content.Intent.createChooser(fallback, "Share via..."))
            }
        } else {
            val intent = android.content.Intent(android.content.Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/pdf")
                addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            try {
                context.startActivity(intent)
            } catch (e: Exception) {
                android.widget.Toast.makeText(
                    context,
                    "PDF saved to Documents folder!",
                    android.widget.Toast.LENGTH_LONG
                ).show()
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
        android.widget.Toast.makeText(
            context,
            "Error generating PDF: ${e.message}",
            android.widget.Toast.LENGTH_LONG
        ).show()
    }
}

@Composable
fun SummaryRow(label: String, value: String, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, fontSize = 14.sp, color = Color.DarkGray)
        Text(value, fontWeight = FontWeight.Bold, color = color, fontSize = 14.sp)
    }
}

@Composable
fun ExpenseRow(label: String, amount: Double) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 14.sp)
        Text(
            "₹${"%.2f".format(amount)}",
            fontWeight = FontWeight.Bold,
            color = Color(0xFFC62828),
            fontSize = 14.sp
        )
    }
}