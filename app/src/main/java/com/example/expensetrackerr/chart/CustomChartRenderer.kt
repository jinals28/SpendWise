package com.example.expensetrackerr.chart

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import com.github.mikephil.charting.animation.ChartAnimator
import com.github.mikephil.charting.interfaces.dataprovider.BarDataProvider
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.renderer.BarChartRenderer
import com.github.mikephil.charting.utils.MPPointF
import com.github.mikephil.charting.utils.Utils
import com.github.mikephil.charting.utils.ViewPortHandler

class CustomChartRenderer(chart: BarDataProvider?,
                          animator: ChartAnimator?,
                          viewPortHandler: ViewPortHandler?
) : BarChartRenderer(chart, animator, viewPortHandler) {

    override fun drawDataSet(c: Canvas?, dataSet: IBarDataSet?, index: Int) {

//        val trans = mChart.getTransformer(dataSet!!.axisDependency)
//
//        val paint = Paint()
//
//        paint.style = Paint.Style.FILL
//        paint.color = dataSet.color
//
//        val buffer = mBarBuffers[index]
//
//        buffer.setPhases(mAnimator.phaseX, mAnimator.phaseY)
//
//        val roundedBarRect = RectF()
//
//        val barWidthHalf = mChart.barData.barWidth / 2.0f
//
//        var left : Float
//        var right : Float
//        var top : Float
//        var bottom : Float
//
//        val iconsOffset = MPPointF.getInstance(dataSet.iconsOffset)
//
//        iconsOffset.x = Utils.convertDpToPixel(iconsOffset.x)
//
//        for (j in 0 until buffer.buffer.size / 4) {
//
//            left = buffer.buffer[j * 4]
//            right = buffer.buffer[j * 4 + 2]
//            top = buffer.buffer[j * 4 + 1]
//            bottom = buffer.buffer[j * 4 + 3]
//
//            val cornerRadius = 20f
//
//            val path = Path()
//            path.addRoundRect(
//                RectF(left, top, right, bottom),
//                cornerRadius,
//                cornerRadius,
//                Path.Direction.CW
//
//            )
//
//            c!!.drawPath(path, paint)
//
//        }
//
//        MPPointF.recycleInstance(iconsOffset)

        for (j in 0 until mBarBuffers[index].size()){
            if (j % 4== 0 ){
                c!!.drawRoundRect(
                    mBarBuffers[index].buffer[j],
                    mBarBuffers[index].buffer[j + 1],
                    mBarBuffers[index].buffer[j + 2],
                    mBarBuffers[index].buffer[j + 3],
                    12f,
                    12f,
                    mRenderPaint
                )
            }
        }
    }
}