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
import com.handlUser.app.databinding.AdapterSlotsBinding
import com.handlUser.app.model.Avalibility
import com.handlUser.app.ui.activity.BaseActivity
import com.handlUser.app.ui.extensions.setbackground
import com.handlUser.app.ui.fragment.RequestServiceFragment
import com.handlUser.app.utils.Const

class AdapterSlots(val baseActivity: BaseActivity, val list: ArrayList<Avalibility>, val requestServiceFragment: RequestServiceFragment) : BaseAdapter() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val binding = DataBindingUtil.inflate<AdapterSlotsBinding>(LayoutInflater.from(parent.context), R.layout.adapter_slots, parent, false)
        return BaseViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val binding = holder.binding as AdapterSlotsBinding
        val data = list[position]
        if (data.visibility) {
            binding.slotTV.visibility = View.VISIBLE
        } else {
            binding.slotTV.visibility = View.GONE
        }
        binding.slotTV.text = baseActivity.changeDateFormatGmtToLocal(data.startTime, Const.DATE_FORMAT, "hh:mm a") + "-" + baseActivity.changeDateFormatGmtToLocal(data.endTime, Const.DATE_FORMAT, "hh:mm a")

        if (data.isChecked) {
            binding.slotTV.setbackground(baseActivity, R.color.LightGrey)
            if (binding.slotRV.visibility == View.GONE) {
                binding.slotRV.visibility = View.VISIBLE
            } else {
                binding.slotRV.visibility = View.GONE
                if (binding.slotRV.adapter != null) {
                    binding.slotRV.adapter = null
                }
            }
        } else {
            binding.slotTV.setbackground(baseActivity, R.color.screen_bg)
            binding.slotRV.visibility = View.GONE

        }
        binding.root.setOnClickListener {
            for (i in 0 until list.size) {
                if (list[i].isChecked) {
                    list[i].isChecked = false
                    for (j in 0 until list[i].availabilitySlots.size) {
                        if (list[i].availabilitySlots[j].isChecked) {
                            list[i].availabilitySlots[j].isChecked = false
                        }
                    }
                }
            }
            list[position].isChecked = true
            notifyDataSetChanged()
            binding.slotRV.adapter = TimingAdapter(baseActivity, data.availabilitySlots, requestServiceFragment)


        }

    }

    override fun getItemCount(): Int {
        return list.size
    }
}