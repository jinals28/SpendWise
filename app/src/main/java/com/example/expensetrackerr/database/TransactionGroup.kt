package com.example.expensetrackerr.database

data class TransactionGroup(
    var date: String,
    val transactions: List<Transaction>
)
