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
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.handlUser.app.R
import com.handlUser.app.databinding.AdapterTimeBinding
import com.handlUser.app.model.AvailabilitySlot
import com.handlUser.app.ui.activity.BaseActivity
import com.handlUser.app.ui.extensions.setbackground
import com.handlUser.app.ui.extensions.showToastMain
import com.handlUser.app.ui.fragment.RequestServiceFragment
import com.handlUser.app.utils.Const


class TimingAdapter(val baseActivity: BaseActivity, val list: ArrayList<AvailabilitySlot>, val fragment: RequestServiceFragment) : BaseAdapter() {

    var bool = false
    var pos = -5
    var lastpos = -5
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val binding = DataBindingUtil.inflate<AdapterTimeBinding>(LayoutInflater.from(parent.context), R.layout.adapter_time, parent, false)
        return BaseViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val binding = holder.binding as AdapterTimeBinding
        val data = list[position]
        binding.slotTV.text = baseActivity.changeDateFormatGmtToLocal(data.startTime, Const.DATE_FORMAT, "hh:mm a") + "-" + baseActivity.changeDateFormatGmtToLocal(data.endTime, Const.DATE_FORMAT, "hh:mm a")
        binding.root.setOnClickListener {
            if (data.stateId == Const.ONE) {
                baseActivity.showToastOne(baseActivity.getString(R.string.slot_is_booked))
            } else {
                val dataList = list.filter { it.isChecked }
                when {
                    dataList.isEmpty() -> {
                        bool = false
                    }
                    else -> {
                        pos = list.indexOf(dataList[0])
                        lastpos = list.indexOf(dataList[dataList.size - 1])
                        bool = true
                    }
                }

                if (bool) {
                    if (list[position].isChecked) {
                        if (position - lastpos == 0 || position - lastpos == 1
                                || position - pos == 0 || position - pos == -1) {
                            list[position].isChecked = !list[position].isChecked
                        } else {
                            showToastMain(baseActivity, baseActivity.getString(R.string.slot_continuous_selection))
                        }
                    } else {
                        if (position - lastpos == 0 || position - lastpos == -1 || position - lastpos == 1
                                || position - pos == 0 || position - pos == -1 || position - pos == 1) {
                            list[position].isChecked = !list[position].isChecked
                        } else {
                            showToastMain(baseActivity, baseActivity.getString(R.string.slot_continuous_selection))
                        }
                    }

                } else {
                    list[position].isChecked = !list[position].isChecked
                }
                notifyDataSetChanged()
                fragment.onItemsetData()
            }
        }
        if (data.stateId == Const.ONE) {
            binding.slotTV.setbackground(baseActivity, R.color.chart_color)
            binding.slotTV.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_down_arrow_chart, 0)
        } else {
            if (data.isChecked) {
                binding.slotTV.setbackground(baseActivity, R.color.green_select)
                binding.slotTV.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_down_arrow_green, 0)
            } else {
                binding.slotTV.setbackground(baseActivity, R.color.screen_bg)
                binding.slotTV.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_down_arrow_white, 0)
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    private fun setTextViewDrawableColor(textView: AppCompatTextView, color: Int) {
        for (drawable in textView.compoundDrawables) {
            if (drawable != null) {
                drawable.colorFilter = PorterDuffColorFilter(ContextCompat.getColor(textView.context, color), PorterDuff.Mode.SRC_IN)
            }
        }
    }
}