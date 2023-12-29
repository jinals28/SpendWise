package com.example.expensetrackerr.ui.home

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem.OnMenuItemClickListener
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.expensetrackerr.MainActivity
import com.example.expensetrackerr.R
import com.example.expensetrackerr.SignUp.SignUpActivity
import com.example.expensetrackerr.chart.CustomChartRenderer
import com.example.expensetrackerr.chart.DateValueFormatter
import com.example.expensetrackerr.chart.ChartData
import com.example.expensetrackerr.databinding.FragmentHomeBinding
import com.example.expensetrackerr.ui.home.adapter.ParentAdapter
import com.firebase.ui.auth.AuthUI
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.google.firebase.auth.FirebaseAuth
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.collections.ArrayList

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var barChart : BarChart

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val user = FirebaseAuth.getInstance().currentUser
        val userId = user?.uid ?: ""
        Log.d("UserId",userId)
        val homeViewModel =
            ViewModelProvider(this, HomeViewModelFactory(requireActivity().application, userId))[HomeViewModel::class.java]


        val recyclerView : RecyclerView = binding.parentRecyclerView

        val txtName: TextView = binding.textName

        val txtTotalBalance : TextView = binding.totalAmountTextView

        val btnMore : ImageButton = binding.moreOptions

        barChart = binding.homeBarChart


        btnMore.setOnClickListener {

            val popUpMenu = PopupMenu(requireContext(), it)

            popUpMenu.menuInflater.inflate(R.menu.more_options_menu, popUpMenu.menu)

            val mMenuItemClickListener = PopupMenu.OnMenuItemClickListener { item ->

            when(item.itemId){
                R.id.signOutBtn -> {
                    onSignOut()
                    return@OnMenuItemClickListener true
                }
                else ->
                    return@OnMenuItemClickListener true
            }

            }

            popUpMenu.setOnMenuItemClickListener(mMenuItemClickListener)

            popUpMenu.show()

        }

        homeViewModel.checkAuthenticationState()
        homeViewModel.name.observe(viewLifecycleOwner) {
                txtName.text = it
        }

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        homeViewModel.totalBalance.observe(viewLifecycleOwner) { totalBalance ->
            txtTotalBalance.text = String.format("%.2f", totalBalance)

        }

        homeViewModel.getRecentTransactionForUser(userId)
        homeViewModel.allTransactions.observe(viewLifecycleOwner) { transactions ->
            Log.d("Adapter", "Change Observed $transactions")
            if(transactions != null){
                transactions.forEach {

                    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    val date : Date? = dateFormat.parse(it.date)

                    val format = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                    it.date = format.format(date!!)
                }
                val rvAdapter = ParentAdapter(transactions)
                recyclerView.adapter = rvAdapter
            }

        }

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener(){

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if ( dy > 0 ){
                    // Scrolling down, hide BottomAppBar
                    (requireActivity() as MainActivity).hideBottomAppBar()
                } else if(dy < 0){
                    (requireActivity() as MainActivity).showBottomAppBar()
                }
            }
        })
        
        homeViewModel.transactionDataList.observe(viewLifecycleOwner){
            Log.d("Transaction Data List", it.toString())
            showBarChart(it)
            
        }

    }

    private fun showBarChart(chartDataList: List<ChartData>) {

        barChart.description.isEnabled = false

        val entries = ArrayList<BarEntry>()

        val labels = mutableListOf<String>()

        val highlightColor = ContextCompat.getColor(requireContext(), R.color.theme_orange)

        val normaLColor = ContextCompat.getColor(requireContext(), R.color.theme_purple)

        chartDataList.forEachIndexed{ index, chartData ->
            entries.add(BarEntry(index.toFloat(),chartData.totalAmount.unaryMinus().toFloat()))

            if(index % 2 == 0){
                labels.add(chartData.label)
            }else {
                labels.add("")
            }
        }


        val leftAxis = barChart.axisLeft
        leftAxis.isEnabled = false
        leftAxis.setDrawGridLines(false)

        val rightAxis = barChart.axisRight
        rightAxis.isEnabled = false

        val dataSet = BarDataSet(entries,"")
        dataSet.setDrawValues(false)
        dataSet.valueTextColor = Color.WHITE

        val xAxisFormatter = DateValueFormatter(labels)

        val xAxis = barChart.xAxis
        xAxis.valueFormatter = xAxisFormatter
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.textColor = Color.WHITE
        xAxis.setDrawGridLines(false)

        barChart.setDrawValueAboveBar(false)

        dataSet.color = normaLColor
        dataSet.highLightColor = highlightColor

        val data = BarData(dataSet)

        barChart.data = data

        barChart.invalidate()

    }


    private fun onSignOut() {
        AuthUI.getInstance()
            .signOut(requireContext())
            .addOnCompleteListener {
                if (it.isComplete) {
                    Toast.makeText(requireContext(), "User Signed out", Toast.LENGTH_SHORT).show()
                    val intent = Intent(activity, SignUpActivity::class.java)
                    startActivity(intent)
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}