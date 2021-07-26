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
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.handlUser.app.R
import com.handlUser.app.databinding.AdapterVerticalprogressBinding
import com.handlUser.app.model.AvailabilitySlot
import com.handlUser.app.ui.activity.BaseActivity
import com.handlUser.app.ui.fragment.TrackingProgressFragment
import com.handlUser.app.utils.Const

class AdapterVerticalProgress(val baseActivity: BaseActivity, val fragment: TrackingProgressFragment, val list: ArrayList<AvailabilitySlot>) : BaseAdapter() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val binding = DataBindingUtil.inflate<AdapterVerticalprogressBinding>(LayoutInflater.from(parent.context), R.layout.adapter_verticalprogress, parent, false)
        return BaseViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val binding = holder.binding as AdapterVerticalprogressBinding
        val data = list[position]
        val date = baseActivity.changeDateFormatGmtToLocal(data.createdOn, Const.DATE_FORMAT, "hh:mm a")
        binding.rightTV.text = date
        when (data.stateId) {
            Const.STATE_PENDING -> {
                binding.statusTV.text = baseActivity.getString(R.string.booking) + baseActivity.getString(R.string.pending)
            }
            Const.STATE_ACCEPT -> {
                binding.statusTV.text = baseActivity.getString(R.string.booking) + baseActivity.getString(R.string.accepted)
            }
            Const.STATE_REJECT -> {
                binding.statusTV.text = baseActivity.getString(R.string.booking) + baseActivity.getString(R.string.rejected)
            }
            Const.STATE_CANCEL -> {
                binding.statusTV.text = baseActivity.getString(R.string.booking) + baseActivity.getString(R.string.cancelled)
            }
            Const.STATE_START -> {
                binding.statusTV.text = baseActivity.getString(R.string.booking) + baseActivity.getString(R.string.moving)
            }
            Const.STATE_STOP -> {
                binding.statusTV.text = baseActivity.getString(R.string.booking) + baseActivity.getString(R.string.reached)
            }
            Const.STATE_START_WORK -> {
                binding.statusTV.text = baseActivity.getString(R.string.booking) + baseActivity.getString(R.string.working)
            }
            Const.STATE_END_WORK -> {
                binding.statusTV.text = baseActivity.getString(R.string.booking) + baseActivity.getString(R.string.completed)
            }
        }
        if (position == list.size - 2) {
            binding.circleIV.background = ContextCompat.getDrawable(baseActivity, R.drawable.light_blue_rounded_button)
            binding.viewV.setBackgroundColor(ContextCompat.getColor(baseActivity, R.color.light_blue))
        } else if (position == list.size - 1) {
            binding.circleIV.background = ContextCompat.getDrawable(baseActivity, R.drawable.light_blue_rounded_button)
            binding.viewV.setBackgroundColor(ContextCompat.getColor(baseActivity, R.color.grey))
        } else {
            binding.circleIV.background = ContextCompat.getDrawable(baseActivity, R.drawable.progress_rounded_button)
            binding.viewV.setBackgroundColor(ContextCompat.getColor(baseActivity, R.color.grey))
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }
}
