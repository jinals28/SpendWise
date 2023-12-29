package com.example.expensetrackerr.ui.receipts

import android.app.AlertDialog
import android.graphics.Color
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.expensetrackerr.R
import com.example.expensetrackerr.chart.DateValueFormatter
import com.example.expensetrackerr.chart.ChartData
import com.example.expensetrackerr.databinding.FragmentReceiptsBinding
import com.example.expensetrackerr.ui.receipts.statAdapter.StatCategoryAdapter
import com.example.expensetrackerr.ui.receipts.statAdapter.StatParentAdapter
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class ReceiptsFragment : Fragment(), StatCategoryAdapter.OnItemClickListener{

    private var _binding: FragmentReceiptsBinding? = null

    private val binding get() = _binding!!

    private val user = FirebaseAuth.getInstance().currentUser

    val userId = user!!.uid

   private var startDate : String = ""

   private var endDate : String = ""

   private lateinit var viewModel : ReceiptsViewModel

    private lateinit var receiptsBarChart : BarChart


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentReceiptsBinding.inflate(layoutInflater, container, false)


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(
            this,
            ReceiptsViewModelFactory(requireActivity().application)
        )[ReceiptsViewModel::class.java]

        val txtTotalExpense = binding.txtStatTotalExpense

        val categoryListRV: RecyclerView = binding.categoryRV
        val transactionListRV: RecyclerView = binding.statRecyclerView

        val txtCategory : TextView = binding.txtStatCategory

        val btnTime : ImageButton = binding.btnCustomTimeLine


        val btnSortByTime : ImageButton = binding.btnSortByTime

        val txtTime : TextView = binding.txtStatTime

        receiptsBarChart = binding.receiptsBarChart

        startDate = viewModel.startDate.value!!

        endDate = viewModel.endDate.value!!

        viewModel.startDate.observe(viewLifecycleOwner){
            startDate = it
        }

        viewModel.endDate.observe(viewLifecycleOwner){
            endDate = it
        }

        categoryListRV.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        transactionListRV.layoutManager = LinearLayoutManager(requireContext())

        val defaultSortList : List<Sorted> = viewModel.sortedList.value!!

        val categoryAdapter = StatCategoryAdapter(requireContext(),defaultSortList,this)

        viewModel.getRecentTransactionForUser(userId, endDate)

        categoryListRV.adapter = categoryAdapter

        viewModel.sortedList.observe(viewLifecycleOwner){

            val sortListAdapter = StatCategoryAdapter(requireContext(),it,this)

            categoryListRV.adapter = sortListAdapter

        }

        viewModel.transactionList.observe(viewLifecycleOwner) { transactionList ->
            transactionList.forEach {
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val date : Date? = dateFormat.parse(it.date)

                val format = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                it.date = format.format(date!!)
            }
            val adapter = StatParentAdapter(transactionList)
            transactionListRV.adapter = adapter
            Log.d("ET TransactionList", transactionList.toString())
            viewModel.getTotalBalanceForCategory(transactionList)

            adapter.setOnClickListenerForChild { transactionObj, position ->
                val builder = AlertDialog.Builder(requireContext())
                builder.setTitle("Modify Transaction")
                    .setMessage("Are you sure you want to modify this transaction?")
                    .setPositiveButton("Edit") { _, _ ->
                        // User clicked Yes, so delete the transaction
                        if (findNavController().currentDestination!!.id == R.id.navigation_receipts){
                            val action = ReceiptsFragmentDirections.actionNavigationReceiptsToAddTransactionFragment(transactionObj)
                            findNavController().navigate(action)
                        }
                    }
                    .setNegativeButton("Delete") { _, _ ->
                        viewModel.delete(transactionObj)
                    }
                    .create()
                    .show()
                adapter.notifyItemRemoved(position)
            }


        }

        viewModel.totalCategoryBalance.observe(viewLifecycleOwner){

            txtTotalExpense.text = it.toString()
        }

        viewModel.receiptsChartDataList.observe(viewLifecycleOwner){
            Log.d("chart Data List", it.toString())
            showReceiptsBarChart(it)
        }

        viewModel.isCategory.observe(viewLifecycleOwner){

             if (it) {
                 txtCategory.text = "All Transaction"
                 viewModel.getAllTransactionForUser(userId, startDate, endDate)
             }else {
                 when(viewModel.customTime.value){
                     "Week" ->{
                         txtCategory.text = "Today"
                         viewModel.getRecentTransactionForUser(userId, endDate)
                     }
                     "Month" -> {
                         txtCategory.text = "Today"
                         viewModel.getRecentTransactionForUser(userId, endDate)
                     }
                     "Year" -> {
                         val calendar = Calendar.getInstance()
                         val monthFormat = SimpleDateFormat("MM-yyyy", Locale.getDefault())
                         val month = monthFormat.format(calendar.time)
                         txtCategory.text = month
                         viewModel.getTransactionForYear(userId, month)
                     }
                 }
             }
        }

        viewModel.customTime.observe(viewLifecycleOwner){
            val calendar = Calendar.getInstance()
            val monthFormat = SimpleDateFormat("MM-yyyy", Locale.getDefault())
            val month = monthFormat.format(calendar.time)
            when(it){
                "Week" ->{
                    txtCategory.text = "Today"
                    txtTime.text = month
                    viewModel.getRecentTransactionForUser(userId, endDate)
                }
                "Month" -> {
                    txtCategory.text = "Today"
                    txtTime.text = month
                    viewModel.getRecentTransactionForUser(userId, endDate)
                }
                "Year" -> {
                    val calendar = Calendar.getInstance()
                    val monthFormat = SimpleDateFormat("MM-yyyy", Locale.getDefault())
                    val month = monthFormat.format(calendar.time)
                    txtCategory.text = month
                    txtTime.text = calendar.get(Calendar.YEAR).toString()
                    viewModel.getTransactionForYear(userId, month)
                }
            }
        }

        btnSortByTime.setOnClickListener { view ->

            val popUpMenu : PopupMenu = PopupMenu(requireContext(), view)

            popUpMenu.menuInflater.inflate(R.menu.sort_by_time_menu, popUpMenu.menu)
            val mMenuItemClickListener = PopupMenu.OnMenuItemClickListener {  item ->
                when(item.itemId){

                    R.id.sortByTime -> {
                        onTimeSelected()
                        return@OnMenuItemClickListener true
                    }
                    R.id.sortByCategory -> {
                        onCategorySelected()
                        return@OnMenuItemClickListener true
                    }

                    else -> {
                        return@OnMenuItemClickListener true
                    }
                }
            }
            popUpMenu.setOnMenuItemClickListener(mMenuItemClickListener)
            popUpMenu.show()
        }

        btnTime.setOnClickListener {view ->

            val popupMenu : PopupMenu = PopupMenu(requireContext(), view)

            popupMenu.menuInflater.inflate(R.menu.time_period, popupMenu.menu)
            val mMenuTimeItemClickListener = PopupMenu.OnMenuItemClickListener { item ->

            when(item.itemId) {
                R.id.pastWeek ->{
                    onWeekSelected()
                    return@OnMenuItemClickListener true
                }
                R.id.thisMonth -> {
                    onMonthSelected()
                    return@OnMenuItemClickListener true
                }
                R.id.thisYear -> {
                    onYearSelected()
                    return@OnMenuItemClickListener true
                }
                else ->{
                    return@OnMenuItemClickListener true
                }
            }
            }
            popupMenu.setOnMenuItemClickListener(mMenuTimeItemClickListener)
            popupMenu.show()
        }


    }

    private fun showReceiptsBarChart(chartDataList : List<ChartData>){

        receiptsBarChart.description.isEnabled = false

        val entries = ArrayList<BarEntry>()

        val labels = mutableListOf<String>()

        val highlightColor = ContextCompat.getColor(requireContext(), R.color.theme_purple_dark)
        val normalColor = ContextCompat.getColor(requireContext(), R.color.theme_orange)

        chartDataList.forEachIndexed { index, chartData ->

            entries.add(BarEntry(index.toFloat(), chartData.totalAmount.unaryMinus().toFloat()))

            labels.add(chartData.label)
        }

        val leftAxis = receiptsBarChart.axisLeft
        leftAxis.isEnabled = false
        leftAxis.setDrawGridLines(false)

        val rightAxis = receiptsBarChart.axisRight
        rightAxis.isEnabled = false

        val dataSet = BarDataSet(entries, "")
        dataSet.setDrawValues(false)
        dataSet.valueTextColor = Color.BLACK

        val xAxisFormatter = DateValueFormatter(labels)

        val xAxis = receiptsBarChart.xAxis
        xAxis.valueFormatter = xAxisFormatter
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.textColor = Color.BLACK
        xAxis.isGranularityEnabled = true
        xAxis.setDrawGridLines(false)

        receiptsBarChart.setDrawValueAboveBar(false)

        dataSet.color = normalColor
        dataSet.highLightColor = highlightColor

        val data = BarData(dataSet)

        receiptsBarChart.data = data

        receiptsBarChart.invalidate()

    }

    private fun onWeekSelected(){
        viewModel.onWeekSelected()
    }

    private fun onCategorySelected() {
        viewModel.onCategorySelected()
    }

    private fun onTimeSelected() {
        viewModel.onTimeSelected()
    }

    override fun onItemClick(position: Int, adapter: StatCategoryAdapter, v: View, previousPosition : Int) {

        val viewModel: ReceiptsViewModel = ViewModelProvider(
            this,
            ReceiptsViewModelFactory(requireActivity().application)
        )[ReceiptsViewModel::class.java]

        val categoryListRV: RecyclerView = binding.categoryRV
        val txtCategory : TextView = binding.txtStatCategory

        val currentList : List<Sorted> = viewModel.sortedList.value!!

        val clickedCategory : Sorted = currentList[position]
        val prevClickedCategory : Sorted = currentList[previousPosition]

        prevClickedCategory.isSelected = false
        clickedCategory.isSelected = true

        categoryListRV.getChildAt(categoryListRV.indexOfChild(v)).findViewById<TextView>(R.id.txtCategory).apply {
            setBackgroundResource(R.drawable.bg_selectedcategory_tab)
            setTextColor(Color.WHITE)

        }

        val category = clickedCategory.name
        txtCategory.text = category
        Log.d("Receipts Category Name", category)
        viewModel.getSelectedTabList(userId, category)

        adapter.selectedCategoryPosition = currentList.indexOf(clickedCategory)
        adapter.notifyItemChanged(previousPosition)

    }

    private fun onMonthSelected(){

        viewModel.onMonthSelected()
    }

    private fun onYearSelected(){
        viewModel.onYearSelected()

    }

}

data class Sorted(val name : String, var isSelected : Boolean = false)
