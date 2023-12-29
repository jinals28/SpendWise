package com.example.expensetrackerr.ui.home.adapter

import android.graphics.Color
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.expensetrackerr.database.Transaction
import com.example.expensetrackerr.databinding.ListItemTransactionBinding
import com.example.expensetrackerr.databinding.ListItemTransactionBinding.*
import com.example.expensetrackerr.ui.receipts.statAdapter.StatTransactionAdapter

class TransactionAdapter(private val listTransactions: List<Transaction>) :
    RecyclerView.Adapter<TransactionAdapter.ViewHolder>() {

    private var lastDateDisplayed: String = ""

    class ViewHolder(binding : ListItemTransactionBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val textNote: TextView = binding.textNote
        private val textCategory: TextView = binding.textCategory
        private val textAmount: TextView = binding.textAmount

        fun bind(transaction: Transaction) {
            textNote.text = transaction.note
            textCategory.text = transaction.category
            textAmount.text = transaction.amount.toString()
            if(textNote.text.isEmpty()) {
                textNote.visibility = View.GONE
                textCategory.textSize = 16F
                textCategory.setTextColor(Color.BLACK)
                textCategory.gravity = Gravity.CENTER_HORIZONTAL
            }
//            Log.d("Transaction Date", transaction.date)
//            Log.d("Equal", (transaction.date.contentEquals(lastDateDisplayed)).toString())
//            if(!transaction.date.contentEquals(lastDateDisplayed)){
//                textDate.visibility = View.GONE
//            }else {
//                textDate.visibility = View.VISIBLE
//                lastDateDisplayed = transaction.date
//                Log.d("Last Date", lastDateDisplayed)
//            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ListItemTransactionBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listTransactions[holder.adapterPosition])
    }

    override fun getItemCount(): Int {
        return listTransactions.size
    }

}