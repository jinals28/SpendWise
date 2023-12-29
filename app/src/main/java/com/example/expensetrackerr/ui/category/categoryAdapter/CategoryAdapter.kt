package com.example.expensetrackerr.ui.category.categoryAdapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.expensetrackerr.databinding.CategoryListBinding

class CategoryAdapter(private val categoryList : List<String>) : RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {

    private var onItemClickListener: ((String) -> Unit)? = null

    inner class ViewHolder(binding: CategoryListBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION){
                    val selectedCategory = categoryList[position]
                    onItemClickListener?.invoke(selectedCategory)
                }
            }
        }
        private val category : TextView = binding.textCategoryName

        fun bind(categoryName : String){
            category.text = categoryName
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
       val layoutInflater = LayoutInflater.from(parent.context)
        val binding = CategoryListBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
       return categoryList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(categoryList[holder.adapterPosition])
    }

    fun setOnItemClickListener(listener : (String) -> Unit){
        onItemClickListener = listener
    }
}