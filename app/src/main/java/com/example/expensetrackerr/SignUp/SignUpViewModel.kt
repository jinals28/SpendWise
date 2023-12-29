package com.example.expensetrackerr.SignUp

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.Auth
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class SignUpViewModel : ViewModel() {

    private val _authenticationState = MutableLiveData<AuthenticationState>()
    val authenticationState : LiveData<AuthenticationState> get() = _authenticationState

    fun checkAuthenticationState(){
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null){
            _authenticationState.value = AuthenticationState.Authenticated(user)
        }else {
            _authenticationState.value = AuthenticationState.Unauthenticated
        }
    }

    sealed class AuthenticationState {
        object Unauthenticated : AuthenticationState()
        data class Authenticated(val user: FirebaseUser) : AuthenticationState()
    }
}