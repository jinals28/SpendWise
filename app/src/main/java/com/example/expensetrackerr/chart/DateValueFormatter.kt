package com.example.expensetrackerr.chart

import android.util.Log
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DateValueFormatter(private val chartDataList : List<String>) : IndexAxisValueFormatter() {

    override fun getAxisLabel(value: Float, axis: AxisBase?): String {
        val parsedFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        val containsDate = chartDataList.any {

            try {
                parsedFormat.isLenient = false
                parsedFormat.parse(it)
                true
            }catch (e : Exception){
                false
            }
        }

        val index = Math.round(value)

        if (index >=0 && index < chartDataList.size){
            Log.d("Chart Data List Size", index.toString())
            return if(containsDate){
                if (index % 2 == 0){
                    val date = chartDataList[index]

                    val parsedDate = parsedFormat.parse(date)
                    val getFormat = SimpleDateFormat("dd/MM", Locale.getDefault())
                    val labelDate = getFormat.format(parsedDate)
                    labelDate
                }else{
                    ""
                }
            }else{
                Log.d("chart Data Index", chartDataList[index])
                chartDataList[index]
            }

        }

        return ""

    }
}