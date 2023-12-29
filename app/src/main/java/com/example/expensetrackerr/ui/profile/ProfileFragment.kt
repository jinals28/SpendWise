package com.example.expensetrackerr.ui.profile

import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.expensetrackerr.R
import com.example.expensetrackerr.SignUp.SignUpActivity
import com.example.expensetrackerr.databinding.FragmentProfileBinding
import com.firebase.ui.auth.AuthUI

class ProfileFragment : Fragment() {

    companion object {
        fun newInstance() = ProfileFragment()
    }

    private var _binding: FragmentProfileBinding? = null

    private val binding get() = _binding!!

    private lateinit var profileViewModel: ProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        profileViewModel = ViewModelProvider(this)[ProfileViewModel::class.java]

        _binding = FragmentProfileBinding.inflate(layoutInflater, container, false)
        val root = binding.root

        val btnSignOut = binding.btnSignOut
        btnSignOut.setOnClickListener(){

        }
        return root

    }

}