package com.ksheera.sagaraaa.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expense_entries")
data class ExpenseEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val date: String,
    val category: String,
    val amount: Double,
    val note: String = "",
    val cowId: Int = 0
)