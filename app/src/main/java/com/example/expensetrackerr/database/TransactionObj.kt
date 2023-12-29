package com.example.expensetrackerr.database


import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class TransactionObj(
    val id: Long = -1,

    val userId: String = "",

    val amount:Double = 0.0,

    val category: String = "",

    val note: String= "",

    val withWhom: String = "",

    val date: String = ""
) : Parcelable


