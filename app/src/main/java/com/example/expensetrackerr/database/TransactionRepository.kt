package com.example.expensetrackerr.database

import android.util.Log
import androidx.lifecycle.LiveData
import com.example.expensetrackerr.chart.ChartData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TransactionRepository(private val transactionDao: Dao) {


    suspend fun insertTransaction(transaction : Transaction) {
        withContext(Dispatchers.IO){
            transactionDao.insert(transaction)
        }

    }

    suspend fun updateTransaction(transaction: Transaction)  {

        withContext(Dispatchers.IO){
             transactionDao.update(transaction)
        }

    }

    suspend fun delete(transaction: Transaction){

        withContext(Dispatchers.IO){
            transactionDao.delete(transaction)

        }
    }

    suspend fun getTransactionForUser(userId: String): List<Transaction> {
        val recentTransactionList : List<Transaction>
        withContext(Dispatchers.IO){
            recentTransactionList = transactionDao.getRecentTransactionsForUser(userId)
        }
        return recentTransactionList
    }

    fun getTotalBalance(userId: String) : LiveData<Double> {
        return transactionDao.getTotalBalance(userId)
    }

    fun getTotalBalanceForCategory(userId: String, category: String) : LiveData<Double> {
        Log.d("Repo Balance",
            transactionDao.getTotalBalanceForCategory(userId, category).value.toString()
        )
        return transactionDao.getTotalBalanceForCategory(userId,category)
    }

    suspend fun getTransactionList(userId: String, TransactionDate : String) : List<Transaction> {
        val transactionList : List<Transaction>
        withContext(Dispatchers.IO){
            transactionList = transactionDao.getTransactionForUser(userId, TransactionDate)
        }
        return transactionList
    }

    suspend fun getCategoryTransactionList(userId: String, category :String, startTransactionDate: String, endTransactionDate : String) : List<Transaction> {
        val transactionList : List<Transaction>
        withContext(Dispatchers.IO){
            transactionList = transactionDao.getCategoryTransactionForUser(userId, category, startTransactionDate, endTransactionDate)
        }
        return transactionList
    }

    suspend fun getAllTransactionList(userId: String, startTransactionDate: String, endTransactionDate : String) : List<Transaction> {
        val transactionList : List<Transaction>
        withContext(Dispatchers.IO){
            transactionList = transactionDao.getAllTransactionForUser(userId, startTransactionDate, endTransactionDate)
        }
        return transactionList
    }

    suspend fun getMonthTransactionList(userId: String, monthStr: String): List<Transaction> {
        val transactionList : List<Transaction>
        withContext(Dispatchers.IO){
            transactionList = transactionDao.getMonthTransactionForUser(userId, monthStr)
        }
        return transactionList

    }

    suspend fun getTransactionDataList(userId: String): List<ChartData> {
        val transactionList : List<ChartData>
        withContext(Dispatchers.IO){
            transactionList = transactionDao.getTransactionDataList(userId)
        }
        return transactionList

    }

    suspend fun getChartDataForAllCategory(userId: String, startTransactionDate: String, endTransactionDate: String) :List<ChartData> {
        val chartDataList : List<ChartData>
        withContext(Dispatchers.IO){
            chartDataList = transactionDao.getChartDataForAllCategory(userId, startTransactionDate, endTransactionDate)
        }
        return chartDataList
    }

    suspend fun getChartDataForMonth(userId: String, startTransactionDate: String, endTransactionDate: String) :List<ChartData> {
        val chartDataList : List<ChartData>
        withContext(Dispatchers.IO){
            chartDataList = transactionDao.getChartDataListForMonth(userId, startTransactionDate, endTransactionDate)
        }
        return chartDataList
    }

    suspend fun getChartDataForYear(userId: String, startTransactionDate: String, endTransactionDate: String) :List<ChartData> {
        val chartDataList : List<ChartData>
        withContext(Dispatchers.IO){
            chartDataList = transactionDao.getChartDataListForYear(userId, startTransactionDate, endTransactionDate)
        }
        return chartDataList
    }

    suspend fun getChartDataForCategory(userId: String, category : String, startTransactionDate: String, endTransactionDate: String) :List<ChartData> {
        val chartDataList : List<ChartData>
        withContext(Dispatchers.IO){
            chartDataList = transactionDao.getChartDataForCategory(userId, category, startTransactionDate, endTransactionDate)
        }
        return chartDataList
    }

    suspend fun getChartDataForCategoryYear(userId: String, category : String, startTransactionDate: String, endTransactionDate: String) :List<ChartData> {
        val chartDataList : List<ChartData>
        withContext(Dispatchers.IO){
            chartDataList = transactionDao.getChartDataForCategoryYear(userId, category, startTransactionDate, endTransactionDate)
        }
        return chartDataList
    }

//    suspend fun getTransaction() : List<Transaction> {
//        return transactionDao.getTransaction()
//    }
//
//    suspend fun getRecentTransactionForUser(userId : String, limit : Int) : List<Transaction> {
//        return transactionDao.getRecentTransactionsForUser(userId, limit)
//    }
}