/*
 * @copyright : ToXSL Technologies Pvt. Ltd. < www.toxsl.com >
 * @author     : Shiv Charan Panjeta < shiv@toxsl.com >
 * All Rights Reserved.
 * Proprietary and confidential :  All information contained herein is, and remains
 * the property of ToXSL Technologies Pvt. Ltd. and its partners.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.handlService.app.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.handlService.app.R
import com.handlService.app.databinding.AdapterSlotsBinding
import com.handlService.app.model.SlotData
import com.handlService.app.ui.activity.BaseActivity
import com.handlService.app.ui.fragment.AvailabilitySlotsFragment
import com.handlService.app.ui.fragment.BookingDetailFragment
import com.handlService.app.ui.fragment.BookingDetailFragmentCalendar
import com.handlService.app.ui.fragment.HomeFragment
import com.prolificinteractive.materialcalendarview.CalendarDay
import java.text.SimpleDateFormat
import java.util.*

class AdapterAvailableSlots(val baseActivity: BaseActivity, val datas: ArrayList<SlotData>, val fragment: Fragment, val day: String?) : BaseAdapter() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val binding = DataBindingUtil.inflate<AdapterSlotsBinding>(LayoutInflater.from(parent.context), R.layout.adapter_slots, parent, false)
        return BaseViewHolder(binding)
    }

    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val binding = holder.binding as AdapterSlotsBinding
        val sdf = SimpleDateFormat("HH:mm:ss")
        val startTime = sdf.parse(baseActivity.getTimeFromDataFromGMT(datas[position].startTime))!!.time
        val endTime = sdf.parse(baseActivity.getTimeFromDataFromGMT(datas[position].endTime))!!.time
        val startTimeValue = SimpleDateFormat("hh:mm a").format(startTime)
        val endTimeValue = SimpleDateFormat("hh:mm a").format(endTime)
        binding.timeTV.text = "$startTimeValue - $endTimeValue"
        val today = CalendarDay.today().year.toString() + "-" + CalendarDay.today().month.toString() + "-" + CalendarDay.today().day.toString()
        if (baseActivity.getTimeFromDataFromGMT(datas[position].endTime) < baseActivity.changeDateFormatFromDate(Calendar.getInstance().time, "HH:mm")) {
            if (today != day!!) {
                binding.deleteIV.visibility = View.VISIBLE
                binding.silverV.visibility = View.VISIBLE
            } else {
                binding.deleteIV.visibility = View.INVISIBLE
                binding.silverV.visibility = View.INVISIBLE
            }
        } else {
            binding.deleteIV.visibility = View.VISIBLE
            binding.silverV.visibility = View.VISIBLE
        }
        if (fragment is AvailabilitySlotsFragment) {
            binding.deleteIV.setOnClickListener {
                fragment.onDelete(position)
            }
        } else {
            binding.deleteIV.visibility = View.INVISIBLE
            binding.silverV.visibility = View.INVISIBLE
            binding.root.setOnClickListener {
                if (fragment is BookingDetailFragment){
                    (fragment as BookingDetailFragment).onRootCLick(position)
                }else if (fragment is BookingDetailFragmentCalendar){
                    (fragment as BookingDetailFragmentCalendar).onRootCLick(position)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return datas.size
    }
}