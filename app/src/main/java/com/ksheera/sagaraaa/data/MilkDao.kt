package com.ksheera.sagaraaa.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MilkDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMilkEntry(entry: MilkEntry)

    @Query("SELECT * FROM milk_entries ORDER BY date DESC")
    fun getAllMilkEntries(): Flow<List<MilkEntry>>

    @Query("SELECT * FROM milk_entries WHERE date LIKE :monthYear || '%' ORDER BY date DESC")
    fun getMilkEntriesByMonth(monthYear: String): Flow<List<MilkEntry>>

    @Query("SELECT SUM(totalIncome) FROM milk_entries WHERE date LIKE :monthYear || '%'")
    fun getTotalIncomeByMonth(monthYear: String): Flow<Double?>

    @Query("SELECT SUM(litres) FROM milk_entries WHERE date LIKE :monthYear || '%'")
    fun getTotalLitresByMonth(monthYear: String): Flow<Double?>

    @Delete
    suspend fun deleteMilkEntry(entry: MilkEntry)
}