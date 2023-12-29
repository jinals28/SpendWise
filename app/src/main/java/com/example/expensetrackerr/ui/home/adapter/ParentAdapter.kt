package com.example.expensetrackerr.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.expensetrackerr.database.TransactionGroup
import com.example.expensetrackerr.databinding.ListDateItemBinding
import com.example.expensetrackerr.ui.receipts.statAdapter.StatTransactionAdapter

class ParentAdapter(private val recentTransaction: List<TransactionGroup>) : RecyclerView.Adapter<ParentAdapter.ViewHolder>() {

    class ViewHolder(binding: ListDateItemBinding) : RecyclerView.ViewHolder(binding.root) {

        private val textDate : TextView = binding.textDate
        private val childRecyclerView: RecyclerView = binding.childRv

        fun bind(transactionGroup: TransactionGroup) {

            textDate.text = transactionGroup.date

            val childAdapter = TransactionAdapter(transactionGroup.transactions)

            childRecyclerView.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = childAdapter
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ListDateItemBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return recentTransaction.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(recentTransaction[holder.adapterPosition])
    }
}