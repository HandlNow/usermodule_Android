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
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.handlUser.app.R
import com.handlUser.app.databinding.AdapterRespondSlotBinding
import com.handlUser.app.databinding.AdapterSlotsBinding
import com.handlUser.app.model.AvailabilitySlot
import com.handlUser.app.model.Avalibility
import com.handlUser.app.ui.activity.BaseActivity
import com.handlUser.app.ui.extensions.setbackground
import com.handlUser.app.ui.fragment.RequestServiceFragment
import com.handlUser.app.utils.Const

class AdapterRespondSlot(val baseActivity: BaseActivity, val list: ArrayList<AvailabilitySlot>) : BaseAdapter() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val binding = DataBindingUtil.inflate<AdapterRespondSlotBinding>(LayoutInflater.from(parent.context), R.layout.adapter_respond_slot, parent, false)
        return BaseViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val binding = holder.binding as AdapterRespondSlotBinding
        val data = list[position]
        binding.slotTV.text = baseActivity.changeDateFormatGmtToLocal(data.startTime, Const.DATE_FORMAT, "hh:mm a")
        binding.root.setOnClickListener {
            list[position].isChecked = !list[position].isChecked
            notifyDataSetChanged()
        }
        if (data.isChecked) {
            binding.slotTV.background = ContextCompat.getDrawable(baseActivity, R.drawable.black_stroke)
        } else {
            binding.slotTV.background = ContextCompat.getDrawable(baseActivity, R.color.screen_bg)
        }

        if (data.stateId == Const.ONE) {
            binding.slotTV.setbackground(baseActivity, R.color.chart_color)
            binding.slotTV.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_down_arrow_chart, 0)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}