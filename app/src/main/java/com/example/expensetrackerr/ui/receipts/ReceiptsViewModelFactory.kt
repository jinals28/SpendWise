package com.example.expensetrackerr.ui.receipts

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.expensetrackerr.ui.home.HomeViewModel
import java.lang.IllegalArgumentException

class ReceiptsViewModelFactory(private val application: Application) :
    ViewModelProvider.AndroidViewModelFactory(application) {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ReceiptsViewModel::class.java))
            return ReceiptsViewModel(application) as T
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}