package com.ksheera.sagaraaa.ui

import android.app.DatePickerDialog
import android.content.Context
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
import com.ksheera.sagaraaa.NotificationHelper
import com.ksheera.sagaraaa.data.MilkEntry
import com.ksheera.sagaraaa.viewmodel.FarmViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IncomeScreen(viewModel: FarmViewModel) {
    @Suppress("UNUSED_VARIABLE")
    val lang = AppStrings.currentLanguage

    val context = LocalContext.current
    var litres by remember { mutableStateOf("") }
    var fat by remember { mutableStateOf("") }
    var snf by remember { mutableStateOf("") }
    var rate by remember { mutableStateOf("") }
    var selectedCowId by remember { mutableStateOf(-1) }
    var cowExpanded by remember { mutableStateOf(false) }
    var selectedDate by remember {
        mutableStateOf(
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        )
    }
    var showDatePicker by remember { mutableStateOf(false) }

    val entries by viewModel.allMilkEntries.collectAsState()
    val cows by viewModel.allCows.collectAsState()
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    // Date picker
    if (showDatePicker) {
        val cal = Calendar.getInstance()
        try {
            cal.time = sdf.parse(selectedDate) ?: Date()
        } catch (e: Exception) { }

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
            datePicker.maxDate = System.currentTimeMillis() // Can't select future dates
            show()
        }
    }

    // Check if selected date already has entry
    val dateAlreadyHasEntry = entries.any { it.date == selectedDate }
    val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    val isMissedDay = selectedDate != today

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            AppStrings.get("income_log"),
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1B5E20)
        )

        // Missed day banner
        if (isMissedDay) {
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0))
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("📅", fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            AppStrings.get("adding_past_entry"),
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = Color(0xFFE65100)
                        )
                        Text(
                            "${AppStrings.get("entry_for_date")}: $selectedDate",
                            fontSize = 12.sp,
                            color = Color(0xFFE65100)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {

                // Date selector
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
                                tint = Color(0xFF1B5E20)
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showDatePicker = true },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = if (isMissedDay) Color(0xFFE65100)
                        else Color(0xFF1B5E20),
                        unfocusedBorderColor = if (isMissedDay) Color(0xFFFF9800)
                        else Color(0xFF4CAF50)
                    )
                )

                if (dateAlreadyHasEntry) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "⚠️ ${AppStrings.get("entry_exists_date")}",
                        fontSize = 12.sp,
                        color = Color(0xFFF57F17)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Cow selector
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

                OutlinedTextField(
                    value = litres,
                    onValueChange = { litres = it },
                    label = { Text("🥛 ${AppStrings.get("litres_sold")}") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = fat,
                        onValueChange = { fat = it },
                        label = { Text(AppStrings.get("fat_percent")) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = snf,
                        onValueChange = { snf = it },
                        label = { Text(AppStrings.get("snf_percent")) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = rate,
                    onValueChange = { rate = it },
                    label = { Text(AppStrings.get("rate_per_litre")) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                val litresVal = litres.toDoubleOrNull() ?: 0.0
                val fatVal = fat.toDoubleOrNull() ?: 0.0
                val rateVal = rate.toDoubleOrNull() ?: 0.0
                val total = litresVal * rateVal

                if (total > 0) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF2E7D32))
                    ) {
                        Text(
                            "${AppStrings.get("total_colon")}: ₹${"%.2f".format(total)}",
                            modifier = Modifier.padding(
                                horizontal = 16.dp, vertical = 8.dp
                            ),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = {
                        if (litres.isNotEmpty() && fat.isNotEmpty() && rate.isNotEmpty()) {
                            val entry = MilkEntry(
                                date = selectedDate,
                                litres = litresVal,
                                fatPercentage = fatVal,
                                ratePerLitre = rateVal,
                                totalIncome = total,
                                cowId = selectedCowId
                            )
                            viewModel.insertMilkEntry(entry)
                            // Update last entry date for notifications
                            NotificationHelper.updateLastEntryDate(context)
                            litres = ""; fat = ""; snf = ""; rate = ""
                            // Reset to today after saving
                            selectedDate = SimpleDateFormat(
                                "yyyy-MM-dd", Locale.getDefault()
                            ).format(Date())
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isMissedDay) Color(0xFFE65100)
                        else Color(0xFF2E7D32)
                    )
                ) {
                    Text(
                        if (isMissedDay) "📅 ${AppStrings.get("add_past_entry")}"
                        else AppStrings.get("add_income"),
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Missing days warning
        val missedDays = getMissedDays(entries.map { it.date })
        if (missedDays.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        "📅 ${AppStrings.get("missed_entries_title")}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color(0xFFE65100)
                    )
                    Text(
                        "${AppStrings.get("missed_entries_desc")}: ${missedDays.take(5).joinToString(", ")}",
                        fontSize = 12.sp,
                        color = Color(0xFFE65100)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    // Quick tap to fill missed days
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        missedDays.take(4).forEach { missedDate ->
                            OutlinedButton(
                                onClick = { selectedDate = missedDate },
                                modifier = Modifier.weight(1f),
                                contentPadding = PaddingValues(4.dp)
                            ) {
                                Text(
                                    missedDate.substring(8), // Show only day number
                                    fontSize = 11.sp,
                                    color = Color(0xFFE65100)
                                )
                            }
                        }
                    }
                    if (missedDays.size > 4) {
                        Text(
                            "+${missedDays.size - 4} ${AppStrings.get("more_missed")}",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        Text(
            AppStrings.get("recent_entries"),
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            items(entries.sortedByDescending { it.date }) { entry ->
                val cowName = cows.find { it.id == entry.cowId }?.name
                    ?: AppStrings.get("all_cows")
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F8E9))
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("🐄", fontSize = 14.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    cowName,
                                    fontSize = 12.sp,
                                    color = Color(0xFF1B5E20),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Text(entry.date, fontSize = 11.sp, color = Color.Gray)
                            Text(
                                "${entry.litres}L | ${AppStrings.get("fat_percent")}: ${entry.fatPercentage}%",
                                fontSize = 13.sp
                            )
                            Text(
                                "₹${"%.2f".format(entry.totalIncome)}",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2E7D32),
                                fontSize = 15.sp
                            )
                        }
                        IconButton(onClick = { viewModel.deleteMilkEntry(entry) }) {
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

// Calculate missed days in current month
fun getMissedDays(entryDates: List<String>): List<String> {
    val result = mutableListOf<String>()
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val today = Calendar.getInstance()
    val cal = Calendar.getInstance().apply {
        set(Calendar.DAY_OF_MONTH, 1) // Start of month
    }
    while (cal.before(today)) {
        val dateStr = sdf.format(cal.time)
        if (!entryDates.contains(dateStr)) {
            result.add(dateStr)
        }
        cal.add(Calendar.DAY_OF_MONTH, 1)
    }
    return result.takeLast(7) // Show last 7 missed days only
}