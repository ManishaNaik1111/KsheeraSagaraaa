package com.ksheera.sagaraaa.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.ksheera.sagaraaa.AppStrings
import com.ksheera.sagaraaa.data.CowEntry
import com.ksheera.sagaraaa.viewmodel.FarmViewModel

@Composable
fun CowScreen(viewModel: FarmViewModel) {
    @Suppress("UNUSED_VARIABLE")
    val lang = AppStrings.currentLanguage
    var cowName by remember { mutableStateOf("") }
    var cowBreed by remember { mutableStateOf("") }
    val cows by viewModel.allCows.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            AppStrings.get("cow_mgmt"),
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1B5E20)
        )
        Spacer(modifier = Modifier.height(12.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                OutlinedTextField(
                    value = cowName,
                    onValueChange = { cowName = it },
                    label = { Text(AppStrings.get("cow_name")) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = cowBreed,
                    onValueChange = { cowBreed = it },
                    label = { Text(AppStrings.get("breed")) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = {
                        if (cowName.isNotEmpty()) {
                            viewModel.insertCow(
                                CowEntry(name = cowName, breed = cowBreed)
                            )
                            cowName = ""; cowBreed = ""
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                ) {
                    Icon(Icons.Default.Add, null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(AppStrings.get("add_cow"), color = Color.White)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "${AppStrings.get("my_cows")} (${cows.size})",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(cows) { cow ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F8E9)),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🐄", fontSize = 28.sp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    cow.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = Color(0xFF1B5E20)
                                )
                                if (cow.breed.isNotEmpty()) {
                                    Text(
                                        "${AppStrings.get("breed")}: ${cow.breed}",
                                        fontSize = 13.sp,
                                        color = Color.Gray
                                    )
                                }
                                Text(
                                    if (cow.isActive) "✅ Active" else "❌ Inactive",
                                    fontSize = 12.sp,
                                    color = if (cow.isActive) Color(0xFF2E7D32) else Color.Red
                                )
                            }
                        }
                        IconButton(onClick = { viewModel.deleteCow(cow) }) {
                            Icon(Icons.Default.Delete, "Delete", tint = Color.Red)
                        }
                    }
                }
            }
        }
    }
}