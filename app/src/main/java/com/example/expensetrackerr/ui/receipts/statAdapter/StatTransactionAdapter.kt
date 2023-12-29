package com.example.expensetrackerr.ui.receipts.statAdapter

import android.graphics.Color
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.expensetrackerr.database.Transaction
import com.example.expensetrackerr.database.TransactionObj
import com.example.expensetrackerr.databinding.StatCategoryListItemBinding

class StatTransactionAdapter(private val listTransactions: List<Transaction>) :
    RecyclerView.Adapter<StatTransactionAdapter.ViewHolder>() {

    private var onItemClickListener : ((TransactionObj, Int) -> Unit)? = null


    inner class ViewHolder(binding : StatCategoryListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val textNote: TextView = binding.textNote
        private val textCategory: TextView = binding.textCategory
        private val textAmount: TextView = binding.textAmount

        init {
            binding.root.setOnClickListener{
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION){
                    val selectedTransaction = listTransactions[position]
                    val transactionObj = TransactionObj(
                        selectedTransaction.id,
                        selectedTransaction.userId,
                        selectedTransaction.amount,
                        selectedTransaction.category,
                        selectedTransaction.note,
                        selectedTransaction.withWhom,
                        selectedTransaction.date
                    )
                    onItemClickListener?.invoke(transactionObj, position)
                }

            }
        }


        fun bind(transaction: Transaction) {
            textNote.text = transaction.note
            textCategory.text = transaction.category
            textAmount.text = transaction.amount.toString()
            if(textNote.text.isEmpty()) {
                textNote.visibility = View.GONE
                textCategory.textSize = 16F
                textCategory.setTextColor(Color.WHITE)
                textCategory.gravity = Gravity.CENTER_HORIZONTAL
            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = StatCategoryListItemBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listTransactions[holder.adapterPosition])
    }

    override fun getItemCount(): Int {
        return listTransactions.size
    }

    fun setOnClickListener(listener : ((TransactionObj, Int) -> Unit)){
        onItemClickListener = listener
    }


}