package com.example.expensetrackerr.ui.addTransaction

import android.app.DatePickerDialog
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.expensetrackerr.R
import com.example.expensetrackerr.database.Transaction
import com.example.expensetrackerr.databinding.FragmentAddTransactionBinding
import com.example.expensetrackerr.utils.EventObserver
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date
import java.util.Locale

class AddTransactionFragment : Fragment() {

   private lateinit var binding: FragmentAddTransactionBinding
   private val viewModel: AddTransactionViewModel by viewModels()

   private val args : AddTransactionFragmentArgs by navArgs()
    private val dateFormat = java.text.SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

    private val format = java.text.SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentAddTransactionBinding.inflate(inflater,container, false)



        return binding.root

    }

    private fun setupDatePicker() {

//        val currentDate = Calendar.getInstance()
//        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
//        val defaultDate = dateFormat.format(currentDate.time)
//        binding.editTextDate.setText(defaultDate)

        binding.editTextDate.setOnClickListener {
            showDatePickerDialog()
        }

    }

    private fun showDatePickerDialog() {
        val datePicker = DatePickerDialog(
            requireContext(),
            { _, year, monthOfYear, dayOfMonth ->
                viewModel.onSelectedDate(year, monthOfYear, dayOfMonth)
            },
            viewModel.selectedYear,
            viewModel.selectedMonth,
            viewModel.selectedDay
        )

        datePicker.datePicker.maxDate = System.currentTimeMillis() - 1000

        datePicker.show()

    }

    override fun onResume() {
        super.onResume()

        val editTextCategory : TextInputEditText = binding.editTextCategory
        val editTextAmount : TextInputEditText = binding.editTextAmount
        val editTextNote : TextInputEditText = binding.editTextNotes
        val editTextWithWhom : TextInputEditText = binding.editTextWithWhom
        val editTextDate : TextInputEditText = binding.editTextDate

        if( arguments != null){
            val category = args.category
            val amount = args.transactionObj.amount
            val tranCategory = args.transactionObj.category
            val note = args.transactionObj.note
            val withWhom = args.transactionObj.withWhom
            val userId = args.transactionObj.userId
            var date = args.transactionObj.date
            if (date != ""){
                val yearDate = format.parse(date)
                date = dateFormat.format(yearDate)
            }
            if (category == ""){
                editTextAmount.setText(amount.unaryMinus().toString())
                editTextCategory.setText(tranCategory)
                editTextNote.setText(note)
                editTextWithWhom.setText(withWhom)
                editTextDate.setText(date)
            }else{
                editTextCategory.setText(category)
            }

        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val editTextAmount : TextInputEditText = binding.editTextAmount
        val editTextCategory : TextInputEditText = binding.editTextCategory
        val editTextNote : TextInputEditText = binding.editTextNotes
        val editTextWithWhom : TextInputEditText = binding.editTextWithWhom
        val editTextDate : TextInputEditText = binding.editTextDate

        val amountInputLayout :TextInputLayout = binding.amountInputLayout
        val categoryInputLayout : TextInputLayout = binding.categoryInputLayout

        val btnSave : Button = binding.saveButton

        setupDatePicker()

        viewModel.selectedDate.observe(viewLifecycleOwner){
            binding.editTextDate.setText(it)
        }

        editTextCategory.setOnClickListener {
            if (arguments != null){
                val id = args.transactionObj.id
                if (id != -1L){
                    val action = AddTransactionFragmentDirections.navigateAddTransactionToCategory(id)
                    findNavController().navigate(action)
                }else{
                    val action = AddTransactionFragmentDirections.navigateAddTransactionToCategory()
                    findNavController().navigate(action)
                }
            }
        }

        btnSave.setOnClickListener{
            val category = editTextCategory.text.toString()
            val amount : Double = if (category != "Income"){
                editTextAmount.text.toString().toDouble().unaryMinus()
            }else{
                editTextAmount.text.toString().toDouble()
            }
            val note = editTextNote.text.toString()
            val withWhom = editTextWithWhom.text.toString()
            val date = editTextDate.text.toString()
            val userId = FirebaseAuth.getInstance().uid

            clearValidationForm()


            val dateObj : Date? = dateFormat.parse(date)


            val dateGiven = format.format(dateObj!!)


            if (viewModel.areFieldValid(amount, category)){

                if (arguments != null){
                    val id = args.transactionObj.id
                    if (id != -1L) {
                        val transaction = Transaction(
                            id = id,
                            userId = userId!!,
                            amount = amount,
                            category = category,
                            note = note,
                            withWhom = withWhom,
                            date = dateGiven
                        )
                        Log.d("Update Transaction", transaction.toString())
                        lifecycleScope.launch {
                            viewModel.update(transaction)
                        }

                    }else{
                        val transaction = Transaction(
                            userId = userId!!,
                            amount = amount,
                            category = category,
                            note = note,
                            withWhom = withWhom,
                            date = dateGiven
                        )

                        Log.d("Transaction", transaction.toString())

                        lifecycleScope.launch {
                            viewModel.insertTransaction(transaction)

                        }

                    }
                }

                findNavController().navigate(R.id.navigate_add_transaction_to_home)


            }
        }

        viewModel.validationErrors.observe(viewLifecycleOwner, EventObserver{ errors ->
            displayValidationErrors(errors)

        })

        editTextAmount.doAfterTextChanged {
            amountInputLayout.error = null

        }

        editTextCategory.doAfterTextChanged {
            categoryInputLayout.error = null

        }

    }

    private fun displayValidationErrors(errors: Map<String, Int>) {
        val amountInputLayout :TextInputLayout = binding.amountInputLayout
        val categoryInputLayout : TextInputLayout = binding.categoryInputLayout
        for ((fieldName, errorResId) in errors) {
            when (fieldName) {
                    "amount" -> amountInputLayout.error = getString(errorResId)
                    "category" -> categoryInputLayout.error = getString(errorResId)

                }
        }
    }

    private fun clearValidationForm() {
        val amountInputLayout :TextInputLayout = binding.amountInputLayout
        val categoryInputLayout : TextInputLayout = binding.categoryInputLayout

        amountInputLayout.error = null
        categoryInputLayout.error = null
    }


}