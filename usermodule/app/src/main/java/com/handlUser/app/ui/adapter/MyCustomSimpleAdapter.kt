/*
 * @copyright : ToXSL Technologies Pvt. Ltd. < www.toxsl.com >
 * @author     : Shiv Charan Panjeta < shiv@toxsl.com >
 * All Rights Reserved.
 * Proprietary and confidential :  All information contained herein is, and remains
 * the property of ToXSL Technologies Pvt. Ltd. and its partners.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.handlUser.app.ui.adapter

import android.graphics.Color
import com.alamkanak.weekview.WeekViewEntity
import com.alamkanak.weekview.threetenabp.WeekViewPagingAdapterThreeTenAbp
import com.handlUser.app.model.AppointmentData
import com.handlUser.app.ui.activity.BaseActivity
import com.handlUser.app.ui.fragment.MyCalendarFragment
import com.handlUser.app.utils.Const
import org.threeten.bp.LocalDate
import java.text.SimpleDateFormat
import java.util.*

class MyCustomSimpleAdapter(
        val baseActivity: BaseActivity, val fragment: MyCalendarFragment, val rangeChangeHandler: (startDate: LocalDate, endDate: LocalDate) -> Unit,
) : WeekViewPagingAdapterThreeTenAbp<AppointmentData>() {

    private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    override fun onCreateEntity(item: AppointmentData): WeekViewEntity {
        val id = item.id
        val start = checkNotNull(timeFormat.parse(baseActivity.changeDateFormatGmtToLocal(item.bookingSlots[0].startTime, Const.DATE_FORMAT, "HH:mm")))
        val end = checkNotNull(timeFormat.parse(baseActivity.changeDateFormatGmtToLocal(item.bookingSlots[item.bookingSlots.size - 1].endTime, Const.DATE_FORMAT, "HH:mm")))

        val now = Calendar.getInstance()

        val startTime = now.clone() as Calendar
        startTime.timeInMillis = start.time
        startTime.set(Calendar.YEAR, now.get(Calendar.YEAR))
        startTime.set(Calendar.MONTH, now.get(Calendar.MONTH))
        startTime.set(Calendar.DAY_OF_MONTH, baseActivity.changeDateFormatGmtToLocal(item.bookingSlots[0].startTime, Const.DATE_FORMAT, "dd").toInt())

        val endTime = startTime.clone() as Calendar
        endTime.timeInMillis = end.time
        endTime.set(Calendar.YEAR, startTime.get(Calendar.YEAR))
        endTime.set(Calendar.MONTH, startTime.get(Calendar.MONTH))
        endTime.set(Calendar.DAY_OF_MONTH, startTime.get(Calendar.DAY_OF_MONTH))
        var color:Int

        if (item.isBooked) {
            color = Color.parseColor("#E9967A")
        } else {
            color = Color.parseColor("#59c8ae")
        }
        val style = WeekViewEntity.Style.Builder()
                .setBackgroundColor(color)
                .build()

        return WeekViewEntity.Event.Builder(item)
                .setId(id.toLong())
                .setTitle(item.provider.fullName + item.userService.categoryName + "\n" +item.address)
                .setStartTime(startTime)
                .setEndTime(endTime)
                .setAllDay(false)
                .setStyle(style)
                .build()
    }

    override fun onEventClick(data: AppointmentData) {
        fragment.onCalenderClick(data)

    }

    override fun onRangeChanged(firstVisibleDate: LocalDate, lastVisibleDate: LocalDate) {
        rangeChangeHandler(firstVisibleDate, lastVisibleDate)
    }
}