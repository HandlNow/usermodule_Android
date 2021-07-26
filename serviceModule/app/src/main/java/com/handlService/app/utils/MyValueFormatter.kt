/*
 * @copyright : ToXSL Technologies Pvt. Ltd. < www.toxsl.com >
 * @author     : Shiv Charan Panjeta < shiv@toxsl.com >
 * All Rights Reserved.
 * Proprietary and confidential :  All information contained herein is, and remains
 * the property of ToXSL Technologies Pvt. Ltd. and its partners.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.handlService.app.utils

import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.DecimalFormat

class MyValueFormatter : ValueFormatter() {
    private val format = DecimalFormat("###,##0.0")

    // override this for BarChart
    override fun getBarLabel(barEntry: BarEntry?): String {
        return "€ " + format.format(barEntry?.y)
    }

    // override this for custom formatting of XAxis or YAxis labels
    override fun getAxisLabel(value: Float, axis: AxisBase?): String {
        return format.format(value) + "&"
    }
    // ... override other methods for the other chart types
}