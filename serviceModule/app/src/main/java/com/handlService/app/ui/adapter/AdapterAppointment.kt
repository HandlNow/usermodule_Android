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
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.handlService.app.R
import com.handlService.app.databinding.AdapterAppointmentBinding
import com.handlService.app.model.AppointmentData
import com.handlService.app.ui.activity.BaseActivity
import com.handlService.app.ui.extensions.loadFromUrl
import com.handlService.app.ui.extensions.replaceFragment
import com.handlService.app.ui.extensions.setColor
import com.handlService.app.ui.fragment.BookingDetailFragment
import com.handlService.app.ui.fragment.MyAppointmentFragment
import com.handlService.app.ui.fragment.OrderSummaryFragment
import com.handlService.app.utils.Const
import java.text.SimpleDateFormat

class AdapterAppointment(val baseActivity: BaseActivity, val fragment: MyAppointmentFragment, val list: ArrayList<AppointmentData>) : BaseAdapter() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val binding = DataBindingUtil.inflate<AdapterAppointmentBinding>(LayoutInflater.from(parent.context), R.layout.adapter_appointment, parent, false)
        return BaseViewHolder(binding)
    }

    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val reqBind = holder.binding as AdapterAppointmentBinding
        val data = list[position]
        reqBind.catNameTV.text = data.userService.subcategoryName + " ( ref:" + data.id.toString() + ")"
        reqBind.userIV.loadFromUrl(baseActivity, data.userDetail?.profileFile?:"")
        if (!data.bookingSlots.isNullOrEmpty()) {
            val date = baseActivity.changeDateFormatGmtToLocal(data.bookingSlots[0].startTime, Const.DATE_FORMAT, "EEEE - dd MMMM yyyy")
            val time = baseActivity.changeDateFormatGmtToLocal(data.bookingSlots[0].startTime, Const.DATE_FORMAT, "hh:mm a") + " - " +
                    baseActivity.changeDateFormatGmtToLocal(data.bookingSlots[data.bookingSlots.size - 1].endTime, Const.DATE_FORMAT, "hh:mm a")

            reqBind.addressTV.text = data.address
            reqBind.nameTV.text = data.userDetail?.fullName

            reqBind.dateTV.text = date
            reqBind.slotTV.text = time + " (" + (30 * data!!.bookingSlots.size).toString() + baseActivity.getString(R.string.minss)
        }
        reqBind.root.setOnClickListener {
            if (data.stateId == Const.STATE_END_WORK) {
                val bundle = Bundle()
                bundle.putParcelable("booking_data", data)
                baseActivity.replaceFragment(OrderSummaryFragment(), bundle)
            } else {
                val bundle = Bundle()
                bundle.putParcelable("data", data)
                baseActivity.replaceFragment(BookingDetailFragment(), bundle)
            }
        }
        if (position + 1 == list.size) {
            onPageEnd()
        }

        when (data.stateId) {
            Const.STATE_PENDING -> {
                reqBind.view.background = ContextCompat.getDrawable(baseActivity, R.color.Red)
                reqBind.timeTV.text = baseActivity.getTIME(SimpleDateFormat(Const.DATE_FORMAT).parse(baseActivity.changeDateFormatGmtToLocal(data.bookingSlots[0].startTime, Const.DATE_FORMAT, Const.DATE_FORMAT)))
                reqBind.timeTV.setColor(baseActivity, R.color.Red)
            }
            Const.STATE_CANCEL, Const.STATE_REJECT -> {
                reqBind.view.background = ContextCompat.getDrawable(baseActivity, R.color.White)
                reqBind.timeTV.text = ""
            }
            Const.STATE_END_WORK-> {
                reqBind.view.background = ContextCompat.getDrawable(baseActivity, R.color.White)
                reqBind.timeTV.text =  baseActivity.getString(R.string.completed)
                reqBind.timeTV.setColor(baseActivity, R.color.Green)
            }
            else -> {
                reqBind.view.background = ContextCompat.getDrawable(baseActivity, R.color.yellow)
                reqBind.timeTV.text = baseActivity.getString(R.string.confirmed_task)
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}