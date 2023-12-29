package com.example.expensetrackerr.database

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "Transactions")
data class Transaction(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val userId: String,

    val amount:Double,

    val category: String,

    val note: String,

    val withWhom: String,

    val date: String
)


