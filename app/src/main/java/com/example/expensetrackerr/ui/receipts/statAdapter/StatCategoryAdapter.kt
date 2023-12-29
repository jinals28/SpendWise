package com.example.expensetrackerr.ui.receipts.statAdapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.expensetrackerr.R
import com.example.expensetrackerr.databinding.StatCategoryRvItemBinding
import com.example.expensetrackerr.ui.receipts.Sorted

class StatCategoryAdapter(private val context : Context, private val categoryList : List<Sorted>,
                          private val listener : OnItemClickListener) : RecyclerView.Adapter<StatCategoryAdapter.ViewHolder>(){


    var selectedCategoryPosition : Int = 0


    inner class ViewHolder(binding : StatCategoryRvItemBinding) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {
        private val txtCategory : TextView = binding.txtCategory

        init {
            itemView.setOnClickListener(this)

            }

        fun onBind(position: Int ) {
            txtCategory.text = categoryList[position].name

        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION){
                listener.onItemClick(position, this@StatCategoryAdapter, itemView, selectedCategoryPosition)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = StatCategoryRvItemBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (categoryList[position].isSelected){
            holder.itemView.findViewById<TextView>(R.id.txtCategory).apply {
                setBackgroundResource(R.drawable.bg_selectedcategory_tab)
                val color : Int = resources.getColor(R.color.white)
                setTextColor(color)
            }
        }else {
            holder.itemView.findViewById<TextView>(R.id.txtCategory).apply {
                setBackgroundResource(R.drawable.bg_category_tab)
                val color : Int = resources.getColor(R.color.theme_dark_grey)
                setTextColor(color)
            }
        }
       holder.onBind(position)
    }

    interface OnItemClickListener{
        fun onItemClick(position: Int, adapter : StatCategoryAdapter, v : View, previousPosition : Int)
    }
}