package com.ksheera.sagaraaa.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ksheera.sagaraaa.viewmodel.FarmViewModel
import java.text.SimpleDateFormat
import com.ksheera.sagaraaa.AppStrings
import java.util.*

@Composable
fun MilkCalendarScreen(viewModel: FarmViewModel) {
    @Suppress("UNUSED_VARIABLE")
    val lang = AppStrings.currentLanguage
    val allEntries by viewModel.allMilkEntries.collectAsState()

    var currentYear by remember { mutableStateOf(Calendar.getInstance().get(Calendar.YEAR)) }
    var currentMonth by remember { mutableStateOf(Calendar.getInstance().get(Calendar.MONTH)) }

    val monthNames = listOf(
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    )

    // Get days that have entries for current month
    val entryDates = remember(allEntries, currentMonth, currentYear) {
        allEntries.filter { entry ->
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            try {
                val date = sdf.parse(entry.date)
                val cal = Calendar.getInstance()
                cal.time = date!!
                cal.get(Calendar.MONTH) == currentMonth &&
                        cal.get(Calendar.YEAR) == currentYear
            } catch (e: Exception) { false }
        }.map { entry ->
            entry.date.substring(8, 10).toIntOrNull() ?: 0
        }.toSet()
    }

    // Get daily totals
    val dailyTotals = remember(allEntries, currentMonth, currentYear) {
        val map = mutableMapOf<Int, Double>()
        allEntries.filter { entry ->
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            try {
                val date = sdf.parse(entry.date)
                val cal = Calendar.getInstance()
                cal.time = date!!
                cal.get(Calendar.MONTH) == currentMonth &&
                        cal.get(Calendar.YEAR) == currentYear
            } catch (e: Exception) { false }
        }.forEach { entry ->
            val day = entry.date.substring(8, 10).toIntOrNull() ?: 0
            map[day] = (map[day] ?: 0.0) + entry.totalIncome
        }
        map
    }

    val calendar = Calendar.getInstance()
    calendar.set(currentYear, currentMonth, 1)
    val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    val firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1

    val monthlyTotal = dailyTotals.values.sum()
    val entryCount = entryDates.size

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        Text(
            AppStrings.get("milk_calendar"),
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1B5E20)
        )
        Text(
            AppStrings.get("track_entries"),
            fontSize = 13.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Month stats
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("📝 ${AppStrings.get("entry_days_count")}",
                    fontSize = 12.sp,
                    color = Color.Gray)
                    Text(
                        "$entryCount / $daysInMonth",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color(0xFF1B5E20)
                    )
                }
            }
            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text( "💰 ${AppStrings.get("month_income")}", fontSize = 12.sp, color = Color.Gray)
                    Text(
                        "₹${"%.0f".format(monthlyTotal)}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color(0xFF0D47A1)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Month navigation
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1B5E20)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    if (currentMonth == 0) {
                        currentMonth = 11; currentYear--
                    } else currentMonth--
                }) {
                    Icon(Icons.Default.ChevronLeft, contentDescription = null, tint = Color.White)
                }
                Text(
                    "${monthNames[currentMonth]} $currentYear",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                IconButton(onClick = {
                    if (currentMonth == 11) {
                        currentMonth = 0; currentYear++
                    } else currentMonth++
                }) {
                    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.White)
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Day headers
        val dayNames = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
        Row(modifier = Modifier.fillMaxWidth()) {
            dayNames.forEach { day ->
                Text(
                    text = day,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1B5E20)
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Calendar grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Empty cells for first week
            items(firstDayOfWeek) {
                Box(modifier = Modifier.size(40.dp))
            }

            // Day cells
            items(daysInMonth) { index ->
                val day = index + 1
                val hasEntry = day in entryDates
                val income = dailyTotals[day] ?: 0.0

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            color = when {
                                hasEntry -> Color(0xFF2E7D32)
                                else -> Color(0xFFF5F5F5)
                            },
                            shape = CircleShape
                        )
                        .border(
                            width = if (day == Calendar.getInstance().get(Calendar.DAY_OF_MONTH) &&
                                currentMonth == Calendar.getInstance().get(Calendar.MONTH) &&
                                currentYear == Calendar.getInstance().get(Calendar.YEAR)) 2.dp else 0.dp,
                            color = Color(0xFFFFC107),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$day",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (hasEntry) Color.White else Color.Gray
                        )
                        if (hasEntry && income > 0) {
                            Text(
                                text = "₹${"%.0f".format(income / 100)}",
                                fontSize = 7.sp,
                                color = Color(0xFFB9F6CA)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Legend
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .background(Color(0xFF2E7D32), CircleShape)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Entry Added", fontSize = 12.sp)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .background(Color(0xFFF5F5F5), CircleShape)
                        .border(1.dp, Color.LightGray, CircleShape)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("No Entry", fontSize = 12.sp, color = Color.Gray)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .background(Color.White, CircleShape)
                        .border(2.dp, Color(0xFFFFC107), CircleShape)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Today", fontSize = 12.sp)
            }
        }

        // Missing days warning
        val missingDays = daysInMonth - entryCount
        if (missingDays > 0 && currentMonth == Calendar.getInstance().get(Calendar.MONTH)) {
            Spacer(modifier = Modifier.height(12.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0))
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("⚠️", fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "You missed $missingDays day(s) this month! Add entries for accurate profit calculation.",
                        fontSize = 13.sp,
                        color = Color(0xFFE65100)
                    )
                }
            }
        }
    }
}