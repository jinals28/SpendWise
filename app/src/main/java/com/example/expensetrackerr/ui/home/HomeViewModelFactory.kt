package com.example.expensetrackerr.ui.home

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.lang.IllegalArgumentException

class HomeViewModelFactory(private val application: Application, private val userId : String ) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)){
            return HomeViewModel(application, userId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}