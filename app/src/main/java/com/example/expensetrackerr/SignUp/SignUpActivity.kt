package com.example.expensetrackerr.SignUp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import com.example.expensetrackerr.MainActivity
import com.example.expensetrackerr.R
import com.example.expensetrackerr.databinding.ActivitySignUpBinding
import com.firebase.ui.auth.AuthMethodPickerLayout
import com.firebase.ui.auth.AuthUI
import com.google.android.gms.auth.api.Auth
import com.google.firebase.auth.FirebaseAuth


class SignUpActivity : AppCompatActivity() {

    companion object {
        const val RC_SIGN_IN = 1
    }

    private lateinit var signUpBinding: ActivitySignUpBinding

    private lateinit var signUpViewModel: SignUpViewModel

    val providers : List<AuthUI.IdpConfig> = listOf(

    // below is the line for adding
    // email and password authentication.
//        AuthUI.IdpConfig.EmailBuilder().build(),
//
//        // below line is used for adding google
//        // authentication builder in our app.
        AuthUI.IdpConfig.GoogleBuilder().build(),
//        AuthUI.IdpConfig.PhoneBuilder().setDefaultCountryIso("IN").build(),
        AuthUI.IdpConfig.EmailBuilder().setRequireName(true).build()
    )



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        signUpBinding = ActivitySignUpBinding.inflate(layoutInflater)

        setContentView(signUpBinding.root)

        signUpViewModel = ViewModelProvider(this).get(SignUpViewModel::class.java)

        val customLayout : AuthMethodPickerLayout = AuthMethodPickerLayout
            .Builder(R.layout.activity_sign_up)
            .setEmailButtonId(R.id.btnEmail)
            .setGoogleButtonId(R.id.btnGoogle)
            .build()


        signUpViewModel.authenticationState.observe(this) { state ->
            when (state) {
                is SignUpViewModel.AuthenticationState.Authenticated -> {
                    val user = state.user
                    Log.d("SignUp", user.phoneNumber.toString())
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                }

                is SignUpViewModel.AuthenticationState.Unauthenticated -> {
                    startActivityForResult(
                        AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setIsSmartLockEnabled(false)
                            .setAvailableProviders(providers)
                            .setTheme(R.style.Theme_ExpenseTrackerr)
                            .setAuthMethodPickerLayout(customLayout)
                            .build(),
                        RC_SIGN_IN
                    )
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        signUpViewModel.checkAuthenticationState()
    }


}