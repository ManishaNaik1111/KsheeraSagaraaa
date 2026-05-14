package com.ksheera.sagaraaa.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CowDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCow(cow: CowEntry)

    @Query("SELECT * FROM cow_entries WHERE isActive = 1")
    fun getActiveCows(): Flow<List<CowEntry>>

    @Query("SELECT * FROM cow_entries")
    fun getAllCows(): Flow<List<CowEntry>>

    @Delete
    suspend fun deleteCow(cow: CowEntry)
}