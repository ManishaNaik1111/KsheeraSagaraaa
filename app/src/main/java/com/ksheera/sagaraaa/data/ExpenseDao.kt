package com.ksheera.sagaraaa.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(entry: ExpenseEntry)

    @Query("SELECT * FROM expense_entries ORDER BY date DESC")
    fun getAllExpenses(): Flow<List<ExpenseEntry>>

    @Query("SELECT * FROM expense_entries WHERE date LIKE :monthYear || '%' ORDER BY date DESC")
    fun getExpensesByMonth(monthYear: String): Flow<List<ExpenseEntry>>

    @Query("SELECT SUM(amount) FROM expense_entries WHERE date LIKE :monthYear || '%'")
    fun getTotalExpenseByMonth(monthYear: String): Flow<Double?>

    @Query("SELECT SUM(amount) FROM expense_entries WHERE date LIKE :monthYear || '%' AND category = :category")
    fun getExpenseByCategory(monthYear: String, category: String): Flow<Double?>

    @Delete
    suspend fun deleteExpense(entry: ExpenseEntry)
}