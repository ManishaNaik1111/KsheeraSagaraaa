package com.ksheera.sagaraaa.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "milk_entries")
data class MilkEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val date: String,
    val litres: Double,
    val fatPercentage: Double,
    val ratePerLitre: Double,
    val totalIncome: Double,
    val cowId: Int = 0
)