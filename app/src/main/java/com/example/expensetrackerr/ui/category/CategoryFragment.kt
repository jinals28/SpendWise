package com.example.expensetrackerr.ui.category

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.expensetrackerr.R
import com.example.expensetrackerr.database.TransactionObj
import com.example.expensetrackerr.databinding.FragmentCategoryBinding
import com.example.expensetrackerr.ui.category.categoryAdapter.CategoryAdapter

class CategoryFragment : Fragment() {

    private var idReceived = -1L

    private val args : CategoryFragmentArgs by navArgs()

    private val categoryList = listOf(
        "Food And Beverages",
        "Electronics",
        "Clothing",
        "Transportation",
        "Debt",
        "Loan",
        "Income",
        "Expense",
    )

    private lateinit var binding : FragmentCategoryBinding

    companion object {
        fun newInstance() = CategoryFragment()
    }

    private lateinit var viewModel: CategoryViewModel

    private lateinit var categoryAdapter: CategoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentCategoryBinding.inflate(layoutInflater, container, false)


        return binding.root

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (arguments != null){
            idReceived = args.id
        }

        val categoryRV: RecyclerView = binding.categoryRV

        categoryAdapter = CategoryAdapter(categoryList)
        categoryRV.layoutManager = LinearLayoutManager(context)
        categoryRV.adapter = categoryAdapter

        categoryAdapter.setOnItemClickListener { category ->
            if (findNavController().currentDestination!!.id == R.id.categoryFragment) {
                val action = CategoryFragmentDirections.navigateCategoryToAddTransaction(
                    TransactionObj(id = idReceived), category, id = idReceived)
                findNavController().navigate(action)
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this)[CategoryViewModel::class.java]
        // TODO: Use the ViewModel
    }

}