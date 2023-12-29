package com.example.expensetrackerr.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.expensetrackerr.chart.ChartData



@Dao
interface Dao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(transaction : Transaction)

    @Update
    fun update(transaction: Transaction)

    @Delete
    fun delete(transaction: Transaction)

    @Query("SELECT * FROM Transactions where userId = :userId ORDER BY date DESC LIMIT 3")
    fun getRecentTransactionsForUser(userId : String) : List<Transaction>

    @Query("SELECT * FROM Transactions where userId = :userId AND date = :TransactionDate ORDER BY id DESC")
    fun getTransactionForUser(userId: String, TransactionDate : String) : List<Transaction>

    @Query("SELECT * FROM Transactions where userId = :userId AND category = :category AND date BETWEEN :startTransactionDate AND :endTransactionDate ORDER BY id DESC")
    fun getCategoryTransactionForUser(userId: String, category : String, startTransactionDate : String, endTransactionDate : String) : List<Transaction>

    @Query("SELECT SUM(amount) FROM Transactions where userId = :userId")
    fun getTotalBalance(userId : String) : LiveData<Double>

    @Query("SELECT SUM(amount) FROM Transactions where userId = :userId AND category = :category")
    fun getTotalBalanceForCategory(userId: String, category : String) : LiveData<Double>

    @Query("SELECT * FROM Transactions where userId = :userId AND date BETWEEN :startTransactionDate AND :endTransactionDate ORDER BY date DESC")
    fun getAllTransactionForUser(userId: String, startTransactionDate : String, endTransactionDate : String) : List<Transaction>

    @Query("SELECT * FROM Transactions where userId = :userId AND strftime('%Y-%m', date) = :monthStr ORDER BY id DESC")
    fun getMonthTransactionForUser(userId: String, monthStr: String): List<Transaction>

    @Query("SELECT date as label , SUM(amount) as totalAmount FROM Transactions WHERE userId = :userId AND category <> 'Income' AND date >= date('now', '-6 days')Group BY date ORDER BY date ")
    fun getTransactionDataList(userId: String) : List<ChartData>


    @Query("SELECT category as label, SUM(amount) as totalAmount FROM Transactions WHERE userId = :userId AND category <> 'Income' AND date BETWEEN :startTransactionDate AND :endTransactionDate GROUP BY category")
    fun getChartDataForAllCategory(userId: String, startTransactionDate: String, endTransactionDate: String) : List<ChartData>

    @Query("SELECT date as label , SUM(amount) as totalAmount FROM Transactions WHERE userId = :userId AND category <> 'Income' AND date BETWEEN :startTransactionDate AND :endTransactionDate Group BY date ORDER BY date ")
    fun getChartDataListForMonth(userId: String, startTransactionDate: String, endTransactionDate: String) : List<ChartData>

    @Query("SELECT strftime('%m-%Y', date) as label , SUM(amount) as totalAmount FROM Transactions WHERE userId = :userId AND category <> 'Income' AND date BETWEEN :startTransactionDate AND :endTransactionDate Group BY strftime('%Y-%m', date) ORDER BY date ASC ")
    fun getChartDataListForYear(userId: String, startTransactionDate: String, endTransactionDate: String) : List<ChartData>

    @Query("SELECT strftime('%d/%m', date) as label, SUM(amount) as totalAmount FROM Transactions where userId = :userId AND category = :category AND date BETWEEN :startTransactionDate AND :endTransactionDate Group BY date ORDER BY date")
    fun getChartDataForCategory(userId: String, category : String, startTransactionDate : String, endTransactionDate : String) : List<ChartData>

    @Query("SELECT strftime('%m/%Y', date) as label, SUM(amount) as totalAmount FROM Transactions where userId = :userId AND category = :category AND date BETWEEN :startTransactionDate AND :endTransactionDate Group BY strftime('%m-%Y', date) ORDER BY strftime('%m-%Y', date)")
    fun getChartDataForCategoryYear(userId: String, category : String, startTransactionDate : String, endTransactionDate : String) : List<ChartData>
}