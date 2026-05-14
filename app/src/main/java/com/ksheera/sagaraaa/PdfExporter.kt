package com.ksheera.sagaraaa

import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Environment
import androidx.core.content.FileProvider
import com.ksheera.sagaraaa.data.ExpenseEntry
import com.ksheera.sagaraaa.data.MilkEntry
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

object PdfExporter {

    fun generateAndSharePdf(
        context: Context,
        username: String,
        milkEntries: List<MilkEntry>,
        expenses: List<ExpenseEntry>,
        month: String
    ) {
        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = document.startPage(pageInfo)
        val canvas = page.canvas
        val paint = Paint()

        var y = 50f

        // Header
        paint.textSize = 22f
        paint.isFakeBoldText = true
        canvas.drawText("KsheeraSagara - Monthly Report", 50f, y, paint)
        y += 30f

        paint.textSize = 14f
        paint.isFakeBoldText = false
        canvas.drawText("Farmer: $username", 50f, y, paint)
        y += 20f
        canvas.drawText("Month: $month", 50f, y, paint)
        y += 20f
        canvas.drawText(
            "Generated: ${SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date())}",
            50f, y, paint
        )
        y += 30f

        // Line
        paint.strokeWidth = 1f
        canvas.drawLine(50f, y, 545f, y, paint)
        y += 20f

        // Income Summary
        val totalIncome = milkEntries.sumOf { it.totalIncome }
        val totalLitres = milkEntries.sumOf { it.litres }
        paint.textSize = 16f
        paint.isFakeBoldText = true
        canvas.drawText("INCOME SUMMARY", 50f, y, paint)
        y += 22f
        paint.textSize = 13f
        paint.isFakeBoldText = false
        canvas.drawText("Total Litres: ${"%.2f".format(totalLitres)} L", 50f, y, paint)
        y += 18f
        canvas.drawText("Total Income: Rs.${"%.2f".format(totalIncome)}", 50f, y, paint)
        y += 28f

        // Expense Summary
        val totalExpense = expenses.sumOf { it.amount }
        val fodder = expenses.filter { it.category == "Fodder" }.sumOf { it.amount }
        val medical = expenses.filter { it.category == "Medical" }.sumOf { it.amount }
        val labour = expenses.filter { it.category == "Labour" }.sumOf { it.amount }
        val electricity = expenses.filter { it.category == "Electricity" }.sumOf { it.amount }
        val others = expenses.filter { it.category == "Others" }.sumOf { it.amount }

        paint.textSize = 16f
        paint.isFakeBoldText = true
        canvas.drawText("EXPENSE BREAKDOWN", 50f, y, paint)
        y += 22f
        paint.textSize = 13f
        paint.isFakeBoldText = false
        canvas.drawText("Fodder: Rs.${"%.2f".format(fodder)}", 50f, y, paint)
        y += 18f
        canvas.drawText("Medical: Rs.${"%.2f".format(medical)}", 50f, y, paint)
        y += 18f
        canvas.drawText("Labour: Rs.${"%.2f".format(labour)}", 50f, y, paint)
        y += 18f
        canvas.drawText("Electricity: Rs.${"%.2f".format(electricity)}", 50f, y, paint)
        y += 18f
        canvas.drawText("Others: Rs.${"%.2f".format(others)}", 50f, y, paint)
        y += 18f
        canvas.drawText("Total Expense: Rs.${"%.2f".format(totalExpense)}", 50f, y, paint)
        y += 28f

        // Net Profit
        canvas.drawLine(50f, y, 545f, y, paint)
        y += 20f
        val netProfit = totalIncome - totalExpense
        val profitPerLitre = if (totalLitres > 0) netProfit / totalLitres else 0.0
        paint.textSize = 18f
        paint.isFakeBoldText = true
        canvas.drawText(
            "NET PROFIT: Rs.${"%.2f".format(netProfit)}",
            50f, y, paint
        )
        y += 24f
        paint.textSize = 14f
        canvas.drawText(
            "Profit per Litre: Rs.${"%.2f".format(profitPerLitre)}",
            50f, y, paint
        )
        y += 24f
        paint.textSize = 13f
        paint.isFakeBoldText = false
        canvas.drawText(
            if (netProfit >= 0) "Status: PROFITABLE MONTH!" else "Status: LOSS - Review expenses!",
            50f, y, paint
        )

        document.finishPage(page)

        // Save file
        val fileName = "KsheeraSagara_${month.replace(" ", "_")}.pdf"
        val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName)
        document.writeTo(FileOutputStream(file))
        document.close()

        // Share via WhatsApp or any app
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            file
        )
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_TEXT, "KsheeraSagara Monthly Report - $month")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(shareIntent, "Share Report via..."))
    }
}