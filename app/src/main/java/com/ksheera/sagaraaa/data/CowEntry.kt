package com.ksheera.sagaraaa.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cow_entries")
data class CowEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val breed: String = "",
    val isActive: Boolean = true
)