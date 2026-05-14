package com.ksheera.sagaraaa.repository

import com.ksheera.sagaraaa.data.*
import kotlinx.coroutines.flow.Flow

class FarmRepository(
    private val milkDao: MilkDao,
    private val expenseDao: ExpenseDao,
    private val cowDao: CowDao
) {
    fun getAllMilkEntries(): Flow<List<MilkEntry>> = milkDao.getAllMilkEntries()
    fun getMilkByMonth(monthYear: String): Flow<List<MilkEntry>> = milkDao.getMilkEntriesByMonth(monthYear)
    fun getTotalIncomeByMonth(monthYear: String): Flow<Double?> = milkDao.getTotalIncomeByMonth(monthYear)
    fun getTotalLitresByMonth(monthYear: String): Flow<Double?> = milkDao.getTotalLitresByMonth(monthYear)
    suspend fun insertMilkEntry(entry: MilkEntry) = milkDao.insertMilkEntry(entry)
    suspend fun deleteMilkEntry(entry: MilkEntry) = milkDao.deleteMilkEntry(entry)

    fun getAllExpenses(): Flow<List<ExpenseEntry>> = expenseDao.getAllExpenses()
    fun getExpensesByMonth(monthYear: String): Flow<List<ExpenseEntry>> = expenseDao.getExpensesByMonth(monthYear)
    fun getTotalExpenseByMonth(monthYear: String): Flow<Double?> = expenseDao.getTotalExpenseByMonth(monthYear)
    fun getExpenseByCategory(monthYear: String, category: String): Flow<Double?> = expenseDao.getExpenseByCategory(monthYear, category)
    suspend fun insertExpense(entry: ExpenseEntry) = expenseDao.insertExpense(entry)
    suspend fun deleteExpense(entry: ExpenseEntry) = expenseDao.deleteExpense(entry)

    fun getAllCows(): Flow<List<CowEntry>> = cowDao.getAllCows()
    fun getActiveCows(): Flow<List<CowEntry>> = cowDao.getActiveCows()
    suspend fun insertCow(cow: CowEntry) = cowDao.insertCow(cow)
    suspend fun deleteCow(cow: CowEntry) = cowDao.deleteCow(cow)
}