package com.example.expensetrackerr.ui.receipts.statAdapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemLongClickListener
import android.widget.TextView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnItemTouchListener
import androidx.recyclerview.widget.RecyclerView.SimpleOnItemTouchListener
import com.example.expensetrackerr.database.Transaction
import com.example.expensetrackerr.database.TransactionGroup
import com.example.expensetrackerr.database.TransactionObj
import com.example.expensetrackerr.databinding.StatCategoryListBinding

class StatParentAdapter(private val recentTransaction: List<TransactionGroup>) : RecyclerView.Adapter<StatParentAdapter.ViewHolder>() {


    private var onItemClickChildListener : ((TransactionObj, Int) -> Unit)? = null


    inner class ViewHolder(binding: StatCategoryListBinding) : RecyclerView.ViewHolder(binding.root) {

        private val textDate : TextView = binding.textDate
        private val childRecyclerView: RecyclerView = binding.childRv

        fun bind(transactionGroup: TransactionGroup) {

            textDate.text = transactionGroup.date

            val childAdapter = StatTransactionAdapter(transactionGroup.transactions)

            childRecyclerView.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = childAdapter
            }

            childAdapter.setOnClickListener(onItemClickChildListener!!)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = StatCategoryListBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return recentTransaction.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(recentTransaction[holder.adapterPosition])
    }

    fun setOnClickListenerForChild(listener : ((TransactionObj, Int) -> Unit)){
        onItemClickChildListener = listener
    }

}