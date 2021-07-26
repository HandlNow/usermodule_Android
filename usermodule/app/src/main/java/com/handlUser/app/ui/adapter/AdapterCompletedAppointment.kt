/*
 * @copyright : ToXSL Technologies Pvt. Ltd. < www.toxsl.com >
 * @author     : Shiv Charan Panjeta < shiv@toxsl.com >
 * All Rights Reserved.
 * Proprietary and confidential :  All information contained herein is, and remains
 * the property of ToXSL Technologies Pvt. Ltd. and its partners.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.handlUser.app.ui.adapter

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.handlUser.app.R
import com.handlUser.app.databinding.AdapterCompletedListBinding
import com.handlUser.app.model.AppointmentData
import com.handlUser.app.ui.activity.BaseActivity
import com.handlUser.app.ui.extensions.loadFromUrl
import com.handlUser.app.ui.extensions.replaceFragment
import com.handlUser.app.ui.fragment.MyAppointmentFragment
import com.handlUser.app.ui.fragment.OrderSummaryFragment
import com.handlUser.app.ui.fragment.PaymentMethodListFragment
import com.handlUser.app.utils.Const

class AdapterCompletedAppointment(val baseActivity: BaseActivity, val fragment: MyAppointmentFragment, val list: ArrayList<AppointmentData>) : BaseAdapter() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val binding = DataBindingUtil.inflate<AdapterCompletedListBinding>(LayoutInflater.from(parent.context), R.layout.adapter_completed_list, parent, false)
        return BaseViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val binding = holder.binding as AdapterCompletedListBinding
        val data = list[position]
        binding.root.setOnClickListener {

        }

        binding.data = data
        binding.userIV.loadFromUrl(baseActivity, data.provider.profileFile!!)
        val date = baseActivity.changeDateFormatGmtToLocal(data.bookingSlots[0].startTime, Const.DATE_FORMAT, "dd MMMM yyyy")

        binding.dateTV.text = date + "\u2022 " + baseActivity.getString(R.string.euro) + data.price

        if (position + 1 == list.size) {
            onPageEnd()
        }
        when (data.stateId) {
            Const.STATE_PENDING -> {
                binding.statusTV.text = baseActivity.getString(R.string.pending)
            }
            Const.STATE_ACCEPT -> {
                binding.statusTV.text = baseActivity.getString(R.string.accepted)
            }
            Const.STATE_REJECT -> {
                binding.statusTV.text = baseActivity.getString(R.string.rejected)
            }
            Const.STATE_CANCEL -> {
                binding.statusTV.text = baseActivity.getString(R.string.cancelled)
            }
            Const.STATE_START -> {
                binding.statusTV.text = baseActivity.getString(R.string.moving)
            }
            Const.STATE_STOP -> {
                binding.statusTV.text = baseActivity.getString(R.string.reached)
            }
            Const.STATE_START_WORK -> {
                binding.statusTV.text = baseActivity.getString(R.string.working)
            }
            Const.STATE_END_WORK -> {
                binding.statusTV.text = baseActivity.getString(R.string.completed)
            }
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }
}