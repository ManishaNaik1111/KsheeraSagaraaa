package com.ksheera.sagaraaa.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ksheera.sagaraaa.data.*
import com.ksheera.sagaraaa.repository.FarmRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class FarmViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val repository = FarmRepository(db.milkDao(), db.expenseDao(), db.cowDao())

    private val currentMonthYear: String
        get() {
            val sdf = SimpleDateFormat("yyyy-MM", Locale.getDefault())
            return sdf.format(Date())
        }

    val allMilkEntries = repository.getAllMilkEntries().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )
    val allExpenses = repository.getAllExpenses().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )
    val allCows = repository.getAllCows().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )
    val monthlyIncome = repository.getTotalIncomeByMonth(currentMonthYear).stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0
    )
    val monthlyExpense = repository.getTotalExpenseByMonth(currentMonthYear).stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0
    )
    val monthlyLitres = repository.getTotalLitresByMonth(currentMonthYear).stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0
    )
    val monthlyMilkEntries = repository.getMilkByMonth(currentMonthYear).stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )
    val monthlyExpenses = repository.getExpensesByMonth(currentMonthYear).stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    fun insertMilkEntry(entry: MilkEntry) = viewModelScope.launch {
        repository.insertMilkEntry(entry)
    }
    fun insertExpense(entry: ExpenseEntry) = viewModelScope.launch {
        repository.insertExpense(entry)
    }
    fun insertCow(cow: CowEntry) = viewModelScope.launch {
        repository.insertCow(cow)
    }
    fun deleteMilkEntry(entry: MilkEntry) = viewModelScope.launch {
        repository.deleteMilkEntry(entry)
    }
    fun deleteExpense(entry: ExpenseEntry) = viewModelScope.launch {
        repository.deleteExpense(entry)
    }
    fun deleteCow(cow: CowEntry) = viewModelScope.launch {
        repository.deleteCow(cow)
    }

    fun getMilkEntriesForCow(cowId: Int): List<MilkEntry> {
        return allMilkEntries.value.filter { it.cowId == cowId }
    }

    fun getExpensesForCow(cowId: Int): List<ExpenseEntry> {
        return allExpenses.value.filter { it.cowId == cowId }
    }
    // Add this function to FarmViewModel
    fun getMilkEntriesForMonth(monthYear: String): StateFlow<List<MilkEntry>> {
        return repository.getMilkByMonth(monthYear).stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
        )
    }
}