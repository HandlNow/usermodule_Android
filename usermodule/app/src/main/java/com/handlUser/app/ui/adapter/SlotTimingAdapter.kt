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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.handlUser.app.R
import com.handlUser.app.databinding.AdapterSlotTimingBinding
import com.handlUser.app.model.Avalibility
import com.handlUser.app.ui.activity.BaseActivity
import com.handlUser.app.utils.Const

class SlotTimingAdapter(val baseActivity: BaseActivity, val list: ArrayList<Avalibility>) : BaseAdapter() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val binding = DataBindingUtil.inflate<AdapterSlotTimingBinding>(LayoutInflater.from(parent.context), R.layout.adapter_slot_timing, parent, false)
        return BaseViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val binding = holder.binding as AdapterSlotTimingBinding
        val data = list[position]
        if (data.visibility) {
            binding.timeOneTV.visibility = View.VISIBLE
        } else {
            binding.timeOneTV.visibility = View.GONE
        }
        binding.timeOneTV.text = baseActivity.changeDateFormatGmtToLocal(data.startTime, Const.DATE_FORMAT, "hh:mm a") + "-" + baseActivity.changeDateFormatGmtToLocal(data.endTime, Const.DATE_FORMAT, "hh:mm a")

    }

    override fun getItemCount(): Int {
        return list.size
    }
}