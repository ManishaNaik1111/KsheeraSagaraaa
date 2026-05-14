package com.ksheera.sagaraaa.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val username: String,
    val password: String,
    val phone: String = "",
    val farmName: String = "",
    val language: String = "en"
)