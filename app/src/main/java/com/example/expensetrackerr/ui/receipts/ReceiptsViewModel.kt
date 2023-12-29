package com.example.expensetrackerr.ui.receipts

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.expensetrackerr.chart.ChartData
import com.example.expensetrackerr.database.Database
import com.example.expensetrackerr.database.Transaction
import com.example.expensetrackerr.database.TransactionGroup
import com.example.expensetrackerr.database.TransactionObj
import com.example.expensetrackerr.database.TransactionRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class ReceiptsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TransactionRepository

    private val _receiptsChartDataList : MutableLiveData<List<ChartData>> = MutableLiveData<List<ChartData>>().apply {
        value = emptyList()
    }

    val receiptsChartDataList : LiveData<List<ChartData>> = _receiptsChartDataList

    private val _transactionList: MutableLiveData<List<TransactionGroup>> =
        MutableLiveData<List<TransactionGroup>>().apply {
            value = emptyList()
        }

    val transactionList: LiveData<List<TransactionGroup>> = _transactionList

    private val calendar: Calendar = Calendar.getInstance()
    private val year : Int = calendar.get(Calendar.YEAR)
    private val month = calendar.get(Calendar.MONTH)
    private val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val currentDate = dateFormat.format(calendar.time)

    private val format = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

    private val _isCategory : MutableLiveData<Boolean> = MutableLiveData<Boolean>().apply {
        value = false
    }

    val userId = FirebaseAuth.getInstance().currentUser!!.uid

    val isCategory : LiveData<Boolean> = _isCategory

    private val _customTime : MutableLiveData<String> = MutableLiveData<String>().apply {
        value = "Week"
    }

    val customTime : LiveData<String> = _customTime

    private val categoryList = listOf(
        Sorted("All Transaction", true),
        Sorted("Food And Beverages"),
        Sorted("Electronics"),
        Sorted("Clothing"),
        Sorted("Transportation"),
        Sorted("Debt"),
        Sorted("Loan"),
        Sorted("Income"),
        Sorted("Expense")
    )

    private var timePeriodList : List<Sorted> = getWeekList()

    private val _sortedList: MutableLiveData<List<Sorted>> =
        MutableLiveData<List<Sorted>>().apply {
            value = timePeriodList
        }

    val sortedList: LiveData<List<Sorted>> = _sortedList

    private val _totalExpense : MutableLiveData<Double> = MutableLiveData<Double>().apply{
        value = 0.0
    }

    val totalCategoryBalance : LiveData<Double> = _totalExpense

    private val user = FirebaseAuth.getInstance().currentUser


    private val _startDate : MutableLiveData<String> = MutableLiveData<String>().apply {
        val startCalendar = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month)
            val day = dayOfMonth
            set(Calendar.DAY_OF_MONTH, day - 7)
        }
        val startDate = dateFormat.format(startCalendar.time)
        value = startDate
    }

    val startDate : LiveData<String> = _startDate

    private val _endDate : MutableLiveData<String> = MutableLiveData<String>().apply {

        value = currentDate
    }

    val endDate : LiveData<String> = _endDate

    init {

        val transactionDao = Database.getDatabase(application).getDao()
        repository = TransactionRepository(transactionDao)
        getReceiptsChartDataForWeek(userId)

    }

    private fun getReceiptsChartDataForWeek(userId: String){
        viewModelScope.launch {
            val chartDataList = repository.getTransactionDataList(userId)
            _receiptsChartDataList.value = chartDataList
        }
    }

    private fun getWeekList(): List<Sorted> {

        val currentdate = Date()
        val calendar = Calendar.getInstance()
        calendar.time = currentdate

        val weekList : MutableList<Sorted> = mutableListOf()

        for(i in 0 downTo -6) {
            val date = calendar.time
            val dateString = format.format(date)
            calendar.add(Calendar.DAY_OF_MONTH, -1)
            Log.d("date"+ i.toString(), dateString)
            val sorted : Sorted = when (i) {
                0 -> {
                    Sorted("Today" , true)
                }
                -1 -> {
                    Sorted("Yesterday")
                }
                else -> {
                    Sorted(dateString)
                }
            }
            weekList.add(sorted)
        }
        return weekList

    }

    fun onTimeSelected() {

        _isCategory.value = false
        _sortedList.value = timePeriodList
        when (customTime.value) {
            "Week" -> getReceiptsChartDataForWeek(userId)
            "Month" -> getChartDataForMonth(userId, startDate.value!!, endDate.value!!)
            "Year" -> getChartDataForYear(userId, startDate.value!!, endDate.value!!)
        }

    }

    fun onCategorySelected() {

        _isCategory.value = true
        _sortedList.value = categoryList
        getChartDataForAllCategory(userId, startDate.value!!, endDate.value!!)
    }

    private fun getChartDataForAllCategory(userId: String, startDate: String, endDate : String) {

        viewModelScope.launch {
            val chartDataList = repository.getChartDataForAllCategory(userId, startDate, endDate)
            _receiptsChartDataList.value = chartDataList
        }

    }

    fun onDaySelected(startDate : String, endDate : String){
        _endDate.value = endDate
        _startDate.value = endDate

    }

    fun delete(transactionObj : TransactionObj){
        viewModelScope.launch {
            val transaction = Transaction(
                id = transactionObj.id,
                userId = transactionObj.userId,
                amount = transactionObj.amount,
                category = transactionObj.category,
                note = transactionObj.note,
                withWhom = transactionObj.withWhom,
                date = transactionObj.date
            )
            repository.delete(transaction)
        }
    }

    fun onMonthSelected() {
        _customTime.value = "Month"
       _startDate.apply {
           val calendar = Calendar.getInstance()
           calendar.set(Calendar.YEAR, year)
           calendar.set(Calendar.MONTH, month)
           calendar.set(Calendar.DAY_OF_MONTH, 1)
           val date = dateFormat.format(calendar.time)
           value = date
       }

        timePeriodList = getMonthList()
        _sortedList.value = timePeriodList
        getChartDataForMonth(userId, startDate.value!!, endDate.value!!)


    }

    private fun getChartDataForMonth(userId: String, startDate: String, endDate: String) {
        viewModelScope.launch {
            val chartDataListForMonth = repository.getChartDataForMonth(userId, startDate, endDate)
            _receiptsChartDataList.value = chartDataListForMonth
        }

    }

    private fun getMonthList(): List<Sorted> {

        val todayCalendar = Calendar.getInstance()
        todayCalendar.set(Calendar.YEAR, year)
        todayCalendar.set(Calendar.MONTH, month)
        val dayOfMonth = todayCalendar.get(Calendar.DAY_OF_MONTH)
        Log.d("Day Of MOnth", dayOfMonth.toString())
        val weekList : MutableList<Sorted> = mutableListOf()

        for(i in dayOfMonth downTo 1) {
            todayCalendar.set(Calendar.DAY_OF_MONTH, i)
            val date = format.format(todayCalendar.time)
            val sorted : Sorted = when (i) {
                dayOfMonth -> {

                    Sorted("Today" , true)

                }
                dayOfMonth - 1 -> {

                    Sorted("Yesterday")
                }
                else -> {
                    Sorted(date)

                }
            }

            weekList.add(sorted)
        }
        Log.d("Month list", weekList.toString())
        return weekList

    }

    fun onYearSelected(){
        _customTime.value = "Year"
        _startDate.apply {
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, 0)
            calendar.set(Calendar.DAY_OF_MONTH, 1)
            val date = dateFormat.format(calendar.time)
            value = date
        }

        timePeriodList = getYearList()
        _sortedList.value = timePeriodList
        getChartDataForYear(userId, startDate.value!!, endDate.value!!)
        }

    private fun getChartDataForYear(userId: String, startDate: String, endDate: String) {

        viewModelScope.launch{
            val chartDataList = repository.getChartDataForYear(userId, startDate, endDate)
            _receiptsChartDataList.value = chartDataList
        }
    }

    private fun getYearList(): List<Sorted> {

        val todayCalendar = Calendar.getInstance()
        todayCalendar.set(Calendar.YEAR, year)
        val month = todayCalendar.get(Calendar.MONTH)
        val yearList : MutableList<Sorted> = mutableListOf()

        val format = SimpleDateFormat("MM-yyyy", Locale.getDefault())
        for(i in month downTo 0) {
            todayCalendar.set(Calendar.MONTH, i)
            val date = format.format(todayCalendar.time)
            val sorted : Sorted = when (i) {
                month -> {

                    Sorted(date , true)

                }
                else -> {
                    Sorted(date)
                }
            }

            yearList.add(sorted)
        }
        Log.d("Month list", yearList.toString())
        return yearList
    }

    fun getRecentTransactionForUser(userId: String, TransactionDate :String) {
        viewModelScope.launch {

            val recentTransaction = repository.getTransactionList(userId, TransactionDate)
            val recent = recentTransaction.groupBy { transaction ->
                transaction.date
            }
                .map { (date, transaction) ->
                    TransactionGroup(date, transaction)

                }
            Log.d("Transaction Group Stat", recent.toString())
            _transactionList.value = recent
        }
    }

    private fun getCategoryTransactionForUser(userId: String, category : String, startTransactionDate : String, endTransactionDate : String) {
            viewModelScope.launch {
                val recentTransaction = repository.getCategoryTransactionList(userId, category, startTransactionDate, endTransactionDate)
                val recent = recentTransaction.groupBy { transaction ->
                    transaction.date
                }
                    .map { (date, transaction) ->
                        TransactionGroup(date, transaction)

                    }
                Log.d("Transaction Group Stat", recent.toString())
                _transactionList.value = recent
                if (customTime.value != "Year"){
                    val chartDataList = repository.getChartDataForCategory(userId, category, startTransactionDate, endTransactionDate)
                    _receiptsChartDataList.value = chartDataList
                }else{
                    val chartDataList = repository.getChartDataForCategoryYear(userId, category, startTransactionDate, endTransactionDate)
                    _receiptsChartDataList.value = chartDataList
                }
            }
    }

    fun getTotalBalanceForCategory(transactionGroupList : List<TransactionGroup>) {
        var totalExpenseMonth : Double = 0.0
        transactionGroupList.forEach {
            it.transactions.forEach { transaction ->
                totalExpenseMonth = totalExpenseMonth.plus(transaction.amount)
            }
        }
        _totalExpense.value = totalExpenseMonth
    }

    fun getSelectedTabList(userId: String, category : String){

        if (_isCategory.value == true){
            if (category == "All Transaction"){
                getAllTransactionForUser(userId, _startDate.value!!, _endDate.value!!)
                getChartDataForAllCategory(userId, startDate.value!!, endDate.value!!)
            }else{
                getCategoryTransactionForUser(userId, category, _startDate.value!!, _endDate.value!!)
            }
        }else if (_customTime.value == "Year"){
            getTransactionForYear(userId, category)
        }else {
            getDateTransactionForUser(userId, category)
        }
    }

    fun getTransactionForYear(userId: String, category: String) {

        val monthFormat = SimpleDateFormat("MM-yyyy",Locale.getDefault())
        val month = monthFormat.parse(category)

        val yearFormat = SimpleDateFormat("yyyy-MM", Locale.getDefault())
        val monthStr = yearFormat.format(month!!)

        viewModelScope.launch {
            val transactionList = repository.getMonthTransactionList(userId, monthStr)
            val recent = transactionList.groupBy { transaction ->
                transaction.date
            }
                .map { (date, transaction) ->
                    TransactionGroup(date, transaction)

                }
            Log.d("Transaction Group Stat", recent.toString())
            _transactionList.value = recent
        }
    }

    fun getAllTransactionForUser(userId: String, startDate: String, endDate: String) {
        viewModelScope.launch {
            val recentTransaction = repository.getAllTransactionList(userId, startDate, endDate)
            val recent = recentTransaction.groupBy { transaction ->
                transaction.date
            }
                .map { (date, transaction) ->
                    TransactionGroup(date, transaction)

                }
            Log.d("Transaction Group Stat", recent.toString())
            _transactionList.value = recent
        }

    }

    private fun getDateTransactionForUser(userId: String, category: String) {
        val dOT: String = when (category) {
            "Today" -> {

                currentDate
            }
            "Yesterday" -> {
                val today = Calendar.getInstance()
                val dayOfMonth = today.get(Calendar.DAY_OF_MONTH)
                Log.d("Day Of month", dayOfMonth.toString())
                today.set(Calendar.DAY_OF_MONTH, dayOfMonth - 1)
                val yesterdayDate = dateFormat.format(today.time)
                Log.d("Yesterday", yesterdayDate)
                yesterdayDate
            }
            else -> {

                val date : Date? = format.parse(category)
                dateFormat.format(date!!)
            }
        }
        Log.d("Year", dOT)

        getRecentTransactionForUser(userId, dOT)

    }

    fun onWeekSelected() {
        _customTime.value = "Week"
        _startDate.apply {
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth - 6)
            val date = dateFormat.format(calendar.time)
            value = date
        }

        timePeriodList = getWeekList()
        _sortedList.value = timePeriodList
    }

}