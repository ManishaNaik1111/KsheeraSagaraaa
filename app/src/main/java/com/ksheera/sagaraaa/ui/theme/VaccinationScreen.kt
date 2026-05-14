package com.ksheera.sagaraaa.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.ksheera.sagaraaa.viewmodel.FarmViewModel
import java.text.SimpleDateFormat
import com.ksheera.sagaraaa.AppStrings
import java.util.*

data class VaccinationRecord(
    val id: Int = 0,
    val cowName: String,
    val vaccineName: String,
    val dateGiven: String,
    val nextDueDate: String,
    val notes: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VaccinationScreen(viewModel: FarmViewModel) {

    @Suppress("UNUSED_VARIABLE")
    val lang = AppStrings.currentLanguage

    val cows by viewModel.allCows.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }

    var records by remember {
        mutableStateOf(
            listOf(
                VaccinationRecord(
                    1,
                    "General",
                    "FMD Vaccine",
                    "2026-02-01",
                    "2026-08-01",
                    "Foot & Mouth Disease"
                ),
                VaccinationRecord(
                    2,
                    "General",
                    "HS Vaccine",
                    "2026-01-15",
                    "2027-01-15",
                    "Haemorrhagic Septicaemia"
                ),
                VaccinationRecord(
                    3,
                    "General",
                    "BQ Vaccine",
                    "2026-03-01",
                    "2027-03-01",
                    "Black Quarter"
                )
            )
        )
    }

    var cowName by remember { mutableStateOf("") }
    var vaccineName by remember { mutableStateOf("") }
    var dateGiven by remember { mutableStateOf("") }
    var nextDue by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    var cowExpanded by remember { mutableStateOf(false) }

    val today =
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    if (showAddDialog) {

        Dialog(
            onDismissRequest = {
                showAddDialog = false
            }
        ) {

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {

                Column(
                    modifier = Modifier.padding(20.dp)
                ) {

                    Text(
                        AppStrings.get("add_record"),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color(0xFF1B5E20)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    if (cows.isNotEmpty()) {

                        ExposedDropdownMenuBox(
                            expanded = cowExpanded,
                            onExpandedChange = {
                                cowExpanded = !cowExpanded
                            }
                        ) {

                            OutlinedTextField(
                                value = if (cowName.isEmpty())
                                    AppStrings.get("cow_label")
                                else cowName,
                                onValueChange = {},
                                readOnly = true,
                                label = {
                                    Text(AppStrings.get("cow_label"))
                                },
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(
                                        cowExpanded
                                    )
                                },
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth()
                            )

                            ExposedDropdownMenu(
                                expanded = cowExpanded,
                                onDismissRequest = {
                                    cowExpanded = false
                                }
                            ) {

                                DropdownMenuItem(
                                    text = {
                                        Text("All Cows")
                                    },
                                    onClick = {
                                        cowName = "All Cows"
                                        cowExpanded = false
                                    }
                                )

                                cows.forEach { cow ->

                                    DropdownMenuItem(
                                        text = {
                                            Text("🐄 ${cow.name}")
                                        },
                                        onClick = {
                                            cowName = cow.name
                                            cowExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                    } else {

                        OutlinedTextField(
                            value = cowName,
                            onValueChange = {
                                cowName = it
                            },
                            label = {
                                Text(AppStrings.get("cow_label"))
                            },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    OutlinedTextField(
                        value = vaccineName,
                        onValueChange = {
                            vaccineName = it
                        },
                        label = {
                            Text(AppStrings.get("vaccine_name"))
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = dateGiven,
                        onValueChange = {
                            dateGiven = it
                        },
                        label = {
                            Text("Date Given (yyyy-MM-dd)")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        placeholder = {
                            Text(today)
                        }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = nextDue,
                        onValueChange = {
                            nextDue = it
                        },
                        label = {
                            Text("Next Due Date (yyyy-MM-dd)")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = notes,
                        onValueChange = {
                            notes = it
                        },
                        label = {
                            Text("Notes (optional)")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {

                        OutlinedButton(
                            onClick = {
                                showAddDialog = false
                            },
                            modifier = Modifier.weight(1f)
                        ) {

                            Text(AppStrings.get("cancel"))
                        }

                        Button(
                            onClick = {

                                if (
                                    vaccineName.isNotEmpty() &&
                                    nextDue.isNotEmpty()
                                ) {

                                    records =
                                        records + VaccinationRecord(
                                            id = records.size + 1,
                                            cowName =
                                                cowName.ifEmpty {
                                                    "General"
                                                },
                                            vaccineName = vaccineName,
                                            dateGiven =
                                                dateGiven.ifEmpty {
                                                    today
                                                },
                                            nextDueDate = nextDue,
                                            notes = notes
                                        )

                                    cowName = ""
                                    vaccineName = ""
                                    dateGiven = ""
                                    nextDue = ""
                                    notes = ""

                                    showAddDialog = false
                                }
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF1B5E20)
                            )
                        ) {

                            Text(
                                AppStrings.get("save"),
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement =
                Arrangement.SpaceBetween,
            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Column {

                Text(
                    AppStrings.get("vaccination_calendar"),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1B5E20)
                )

                Text(
                    AppStrings.get("track_health"),
                    fontSize = 13.sp,
                    color = Color.Gray
                )
            }

            FloatingActionButton(
                onClick = {
                    showAddDialog = true
                },
                containerColor = Color(0xFF1B5E20),
                modifier = Modifier.size(48.dp)
            ) {

                Icon(
                    Icons.Default.Add,
                    contentDescription = null,
                    tint = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFFFF3E0)
            )
        ) {

            Column(
                modifier = Modifier.padding(12.dp)
            ) {

                Text(
                    AppStrings.get("standard_vaccines"),
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Color(0xFFE65100)
                )

                Spacer(modifier = Modifier.height(6.dp))

                val vaccines = listOf(
                    "FMD" to "Every 6 months",
                    "HS" to "Every year",
                    "BQ" to "Every year",
                    "Brucellosis" to "Once (calves)",
                    "Theileriosis" to "Once (endemic areas)"
                )

                vaccines.forEach { (name, freq) ->

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement =
                            Arrangement.SpaceBetween
                    ) {

                        Text(
                            "• $name",
                            fontSize = 12.sp
                        )

                        Text(
                            freq,
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            AppStrings.get("your_records"),
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            verticalArrangement =
                Arrangement.spacedBy(8.dp)
        ) {

            items(records) { record ->

                val sdf =
                    SimpleDateFormat(
                        "yyyy-MM-dd",
                        Locale.getDefault()
                    )

                val todayCal =
                    Calendar.getInstance()

                val dueCal =
                    Calendar.getInstance()

                val isDue = try {

                    dueCal.time =
                        sdf.parse(record.nextDueDate)!!

                    dueCal.before(todayCal) ||
                            dueCal == todayCal

                } catch (e: Exception) {
                    false
                }

                val daysUntilDue = try {

                    val due =
                        sdf.parse(record.nextDueDate)!!

                    val diff =
                        due.time - Date().time

                    (diff / (1000 * 60 * 60 * 24))
                        .toInt()

                } catch (e: Exception) {
                    999
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor =
                            when {
                                isDue ->
                                    Color(0xFFFFEBEE)

                                daysUntilDue <= 30 ->
                                    Color(0xFFFFF8E1)

                                else ->
                                    Color(0xFFE8F5E9)
                            }
                    ),
                    elevation =
                        CardDefaults.cardElevation(2.dp)
                ) {

                    Column(
                        modifier =
                            Modifier.padding(14.dp)
                    ) {

                        Row(
                            modifier =
                                Modifier.fillMaxWidth(),
                            horizontalArrangement =
                                Arrangement.SpaceBetween,
                            verticalAlignment =
                                Alignment.CenterVertically
                        ) {

                            Row(
                                verticalAlignment =
                                    Alignment.CenterVertically
                            ) {

                                Text(
                                    "💉",
                                    fontSize = 20.sp
                                )

                                Spacer(
                                    modifier =
                                        Modifier.width(8.dp)
                                )

                                Column {

                                    Text(
                                        record.vaccineName,
                                        fontWeight =
                                            FontWeight.Bold,
                                        fontSize = 15.sp
                                    )

                                    Text(
                                        "🐄 ${record.cowName}",
                                        fontSize = 12.sp,
                                        color =
                                            Color(0xFF1B5E20)
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .background(
                                        color =
                                            when {
                                                isDue ->
                                                    Color(0xFFC62828)

                                                daysUntilDue <= 30 ->
                                                    Color(0xFFF57F17)

                                                else ->
                                                    Color(0xFF2E7D32)
                                            },
                                        shape =
                                            RoundedCornerShape(
                                                12.dp
                                            )
                                    )
                                    .padding(
                                        horizontal = 8.dp,
                                        vertical = 4.dp
                                    )
                            ) {

                                Text(
                                    when {
                                        isDue ->
                                            AppStrings.get("overdue")

                                        daysUntilDue <= 30 ->
                                            AppStrings.get("due_soon")

                                        else ->
                                            AppStrings.get("ok_status")
                                    },
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight =
                                        FontWeight.Bold
                                )
                            }
                        }

                        Spacer(
                            modifier =
                                Modifier.height(8.dp)
                        )

                        Row(
                            modifier =
                                Modifier.fillMaxWidth(),
                            horizontalArrangement =
                                Arrangement.SpaceBetween
                        ) {

                            Column {

                                Text(
                                    AppStrings.get("given"),
                                    fontSize = 11.sp,
                                    color = Color.Gray
                                )

                                Text(
                                    record.dateGiven,
                                    fontSize = 12.sp,
                                    fontWeight =
                                        FontWeight.Medium
                                )
                            }

                            Column(
                                horizontalAlignment =
                                    Alignment.End
                            ) {

                                Text(
                                    AppStrings.get("next_due_label"),
                                    fontSize = 11.sp,
                                    color = Color.Gray
                                )

                                Text(
                                    record.nextDueDate,
                                    fontSize = 12.sp,
                                    fontWeight =
                                        FontWeight.Bold,
                                    color =
                                        if (isDue)
                                            Color(0xFFC62828)
                                        else
                                            Color(0xFF1B5E20)
                                )
                            }
                        }

                        if (record.notes.isNotEmpty()) {

                            Spacer(
                                modifier =
                                    Modifier.height(4.dp)
                            )

                            Text(
                                "📝 ${record.notes}",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }

                        if (!isDue && daysUntilDue < 999) {

                            Spacer(
                                modifier =
                                    Modifier.height(4.dp)
                            )

                            Text(
                                if (daysUntilDue > 0)
                                    "Due in $daysUntilDue days"
                                else
                                    AppStrings.get("due_today"),
                                fontSize = 12.sp,
                                color =
                                    if (daysUntilDue <= 7)
                                        Color(0xFFC62828)
                                    else
                                        Color(0xFF1B5E20),
                                fontWeight =
                                    FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}