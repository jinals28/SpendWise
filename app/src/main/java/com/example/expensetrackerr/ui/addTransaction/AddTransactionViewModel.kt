package com.example.expensetrackerr.ui.addTransaction

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.expensetrackerr.R
import com.example.expensetrackerr.database.Database
import com.example.expensetrackerr.database.Transaction
import com.example.expensetrackerr.database.TransactionRepository
import com.example.expensetrackerr.utils.Event
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddTransactionViewModel(application: Application) : AndroidViewModel(application) {

    private val _transactionData = MutableLiveData<Transaction>()
    val transactionData : LiveData<Transaction> = _transactionData

    private val repository : TransactionRepository

    init {

        val transactionDao = Database.getDatabase(application).getDao()
        repository = TransactionRepository(transactionDao)

    }

    private val _selectedDate = MutableLiveData<String>()
    val selectedDate : LiveData<String>
        get() = _selectedDate

    private val _validationErrors = MutableLiveData<Event<Map<String, Int>>>()
    val validationErrors : LiveData<Event<Map<String, Int>>> = _validationErrors


    private val calender: Calendar = Calendar.getInstance()
    var selectedYear : Int = calender.get(Calendar.YEAR)
    var selectedMonth : Int = calender.get(Calendar.MONTH)
    var selectedDay : Int = calender.get(Calendar.DAY_OF_MONTH)

    fun setTransactionData(data : Transaction) {
        _transactionData.value = data
    }


    suspend fun insertTransaction(transaction: Transaction) {
        repository.insertTransaction(transaction)
    }

    suspend fun update(transaction: Transaction){
        repository.updateTransaction(transaction)
    }
    fun areFieldValid(amount : Double, category : String) : Boolean {
        return validateFields(amount, category)
    }


    private fun validateFields(amount : Double, category : String) : Boolean {
        val errors = mutableMapOf<String, Int>()


        if (category.isEmpty()) {
            errors.putIfAbsent("category", R.string.error_category_required)
        }else {
            errors.remove("category")
        }

        return if(errors.isEmpty()) {
            // Fields are valid, proceed with saving
            _validationErrors.value = Event(errors)
            true
        } else {
            _validationErrors.value = Event(errors)
            false
        }

    }


    fun onSelectedDate(year : Int, monthOfTheYear: Int, dayOfMonth : Int ) {
        // Handle date picker dialog and update the date field
        // For example:
        // date = selectedDateFormatted

        selectedYear = year
        selectedMonth = monthOfTheYear
        selectedDay = dayOfMonth

        val formattedDate = formatDate(year, monthOfTheYear, dayOfMonth)
        _selectedDate.value = formattedDate

    }


    private fun formatDate(year: Int, month: Int, day: Int): String {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.DAY_OF_MONTH, day)

        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        return dateFormat.format(calendar.time)

    }


}