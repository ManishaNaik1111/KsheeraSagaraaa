package com.ksheera.sagaraaa.ui

import android.app.DatePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ksheera.sagaraaa.AppStrings
import com.ksheera.sagaraaa.data.ExpenseEntry
import com.ksheera.sagaraaa.viewmodel.FarmViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseScreen(viewModel: FarmViewModel) {
    @Suppress("UNUSED_VARIABLE")
    val lang = AppStrings.currentLanguage

    val context = LocalContext.current
    val categoryKeys = listOf("fodder", "medical", "labour", "electricity", "others")
    val categoryEmojis = listOf("🌾", "💊", "👷", "⚡", "📦")
    val categoryEnglish = listOf("Fodder", "Medical", "Labour", "Electricity", "Others")

    var selectedCategoryIndex by remember { mutableStateOf(0) }
    var amount by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var categoryExpanded by remember { mutableStateOf(false) }
    var selectedCowId by remember { mutableStateOf(-1) }
    var cowExpanded by remember { mutableStateOf(false) }
    var selectedDate by remember {
        mutableStateOf(
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        )
    }
    var showDatePicker by remember { mutableStateOf(false) }

    val expenses by viewModel.allExpenses.collectAsState()
    val cows by viewModel.allCows.collectAsState()
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    val isMissedDay = selectedDate != today

    if (showDatePicker) {
        val cal = Calendar.getInstance()
        try { cal.time = sdf.parse(selectedDate) ?: Date() } catch (e: Exception) { }
        DatePickerDialog(
            context,
            { _, year, month, day ->
                selectedDate = String.format("%04d-%02d-%02d", year, month + 1, day)
                showDatePicker = false
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).apply {
            datePicker.maxDate = System.currentTimeMillis()
            show()
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            AppStrings.get("expense_log"),
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFB71C1C)
        )

        if (isMissedDay) {
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0))
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("📅", fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "${AppStrings.get("adding_past_entry")}: $selectedDate",
                        fontSize = 12.sp,
                        color = Color(0xFFE65100),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {

                // DATE PICKER
                OutlinedTextField(
                    value = selectedDate,
                    onValueChange = {},
                    label = { Text(AppStrings.get("select_date")) },
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { showDatePicker = true }) {
                            Icon(
                                Icons.Default.CalendarMonth,
                                contentDescription = "Pick date",
                                tint = Color(0xFFC62828)
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showDatePicker = true },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFC62828),
                        unfocusedBorderColor = if (isMissedDay) Color(0xFFFF9800)
                        else Color(0xFFE57373)
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                // COW SELECTOR
                if (cows.isNotEmpty()) {
                    ExposedDropdownMenuBox(
                        expanded = cowExpanded,
                        onExpandedChange = { cowExpanded = !cowExpanded }
                    ) {
                        OutlinedTextField(
                            value = if (selectedCowId == -1) AppStrings.get("all_cows")
                            else cows.find { it.id == selectedCowId }?.name
                                ?: AppStrings.get("select_cow"),
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("🐄 ${AppStrings.get("select_cow")}") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(cowExpanded)
                            },
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = cowExpanded,
                            onDismissRequest = { cowExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text(AppStrings.get("all_cows")) },
                                onClick = { selectedCowId = -1; cowExpanded = false }
                            )
                            cows.forEach { cow ->
                                DropdownMenuItem(
                                    text = { Text("🐄 ${cow.name} (${cow.breed})") },
                                    onClick = {
                                        selectedCowId = cow.id
                                        cowExpanded = false
                                    }
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // CATEGORY
                ExposedDropdownMenuBox(
                    expanded = categoryExpanded,
                    onExpandedChange = { categoryExpanded = !categoryExpanded }
                ) {
                    OutlinedTextField(
                        value = "${categoryEmojis[selectedCategoryIndex]} ${AppStrings.get(categoryKeys[selectedCategoryIndex])}",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(AppStrings.get("category")) },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(categoryExpanded)
                        },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = categoryExpanded,
                        onDismissRequest = { categoryExpanded = false }
                    ) {
                        categoryKeys.forEachIndexed { index, key ->
                            DropdownMenuItem(
                                text = {
                                    Text("${categoryEmojis[index]} ${AppStrings.get(key)}")
                                },
                                onClick = {
                                    selectedCategoryIndex = index
                                    categoryExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("${AppStrings.get("amount")} (₹)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text(AppStrings.get("note")) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        if (amount.isNotEmpty()) {
                            val entry = ExpenseEntry(
                                date = selectedDate,
                                category = categoryEnglish[selectedCategoryIndex],
                                amount = amount.toDoubleOrNull() ?: 0.0,
                                note = note,
                                cowId = selectedCowId
                            )
                            viewModel.insertExpense(entry)
                            amount = ""; note = ""
                            selectedDate = SimpleDateFormat(
                                "yyyy-MM-dd", Locale.getDefault()
                            ).format(Date())
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isMissedDay) Color(0xFFE65100)
                        else Color(0xFFC62828)
                    )
                ) {
                    Text(
                        if (isMissedDay) "📅 ${AppStrings.get("add_past_entry")}"
                        else AppStrings.get("add_expense"),
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            AppStrings.get("recent_expenses"),
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            items(expenses.sortedByDescending { it.date }) { expense ->
                val cowName = if (expense.cowId == -1 || expense.cowId == 0)
                    AppStrings.get("all_cows")
                else cows.find { it.id == expense.cowId }?.name
                    ?: AppStrings.get("all_cows")

                val catIndex = categoryEnglish.indexOf(expense.category)
                    .takeIf { it >= 0 } ?: 0

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1))
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(categoryEmojis[catIndex], fontSize = 16.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    AppStrings.get(categoryKeys[catIndex]),
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFF57F17),
                                    fontSize = 13.sp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "🐄 $cowName",
                                    fontSize = 11.sp,
                                    color = Color(0xFF1B5E20)
                                )
                            }
                            // DATE SHOWN HERE
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("📅", fontSize = 11.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    expense.date,
                                    fontSize = 11.sp,
                                    color = Color.Gray,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            if (expense.note.isNotEmpty()) {
                                Text(
                                    "📝 ${expense.note}",
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                            }
                            Text(
                                "₹${"%.2f".format(expense.amount)}",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFC62828),
                                fontSize = 15.sp
                            )
                        }
                        IconButton(onClick = { viewModel.deleteExpense(expense) }) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Delete",
                                tint = Color.Red
                            )
                        }
                    }
                }
            }
        }
    }
}