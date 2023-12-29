package com.example.expensetrackerr.ui.home

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.expensetrackerr.chart.ChartData
import com.example.expensetrackerr.database.Database
import com.example.expensetrackerr.database.TransactionGroup
import com.example.expensetrackerr.database.TransactionRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale



class HomeViewModel(application: Application, userId: String) : AndroidViewModel(application) {

    private val repository : TransactionRepository

    private val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    private val _currentDate : MutableLiveData<String> = MutableLiveData<String>().apply {
        val cal = Calendar.getInstance()
        val date = format.format(cal.time)
        value = date
    }

   val currentDate : LiveData<String> = _currentDate

    private val _startDate : MutableLiveData<String> = MutableLiveData<String>().apply {
        val cal = Calendar.getInstance()
        cal.set(Calendar.DAY_OF_MONTH, 1)
        val date = format.format(cal.time)
        value = date
    }

    val startDate : LiveData<String> = _startDate

    private val _allTransactions = MutableLiveData<List<TransactionGroup>>().apply {
        value = emptyList()
    }

    val totalBalance : LiveData<Double>


    val allTransactions: LiveData<List<TransactionGroup>> = _allTransactions

    private val user = FirebaseAuth.getInstance().currentUser

    private val _transactionDataList : MutableLiveData<List<ChartData>> = MutableLiveData<List<ChartData>>().apply {
        value = emptyList()
    }

    val transactionDataList : LiveData<List<ChartData>> = _transactionDataList

    init {

        val transactionDao = Database.getDatabase(application).getDao()
        repository = TransactionRepository(transactionDao)
        totalBalance = repository.getTotalBalance(user!!.uid)
        getTransactionDataList(userId)

    }

    private val _name = MutableLiveData<String>().apply {
        value = user?.displayName ?: "User"
    }
    val name: LiveData<String> = _name

    fun checkAuthenticationState(){
        _name.value = user?.displayName ?: "User"
    }


    fun getRecentTransactionForUser(userId: String) {
        viewModelScope.launch {
            val recentTransaction = repository.getTransactionForUser(userId)
            val recent = recentTransaction.groupBy { transaction ->
                transaction.date
            }
                .map { (date, transaction) ->
                    TransactionGroup(date, transaction)

                }
            Log.d("Transaction Group", recent.toString())
            _allTransactions.value = recent

        }
    }

    fun getTransactionDataList(userId: String) {
        viewModelScope.launch {
            val transactionList = repository.getTransactionDataList(userId)
            _transactionDataList.value = transactionList
        }
    }


}