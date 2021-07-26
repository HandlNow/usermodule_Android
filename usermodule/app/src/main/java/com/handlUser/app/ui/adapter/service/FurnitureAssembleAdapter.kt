/*
 * @copyright : ToXSL Technologies Pvt. Ltd. < www.toxsl.com >
 * @author     : Shiv Charan Panjeta < shiv@toxsl.com >
 * All Rights Reserved.
 * Proprietary and confidential :  All information contained herein is, and remains
 * the property of ToXSL Technologies Pvt. Ltd. and its partners.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.handlUser.app.ui.adapter.service

import android.os.SystemClock
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.handlUser.app.R
import com.handlUser.app.databinding.AdapterServiceFunitureBinding
import com.handlUser.app.databinding.AdapterServicePaintingBinding
import com.handlUser.app.model.ParentData
import com.handlUser.app.ui.activity.BaseActivity
import com.handlUser.app.ui.adapter.BaseAdapter
import com.handlUser.app.ui.adapter.BaseViewHolder
import com.handlUser.app.ui.extensions.setColor
import com.handlUser.app.utils.Const

class FurnitureAssembleAdapter(val baseActivity: BaseActivity, var list: List<ParentData>) : BaseAdapter() {
    private var mLastClickTime: Long = 0


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val binding = DataBindingUtil.inflate<AdapterServiceFunitureBinding>(LayoutInflater.from(parent.context), R.layout.adapter_service_funiture, parent, false)
        return BaseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val binding = holder.binding as AdapterServiceFunitureBinding
        val data = list[position]
        binding.quesTV.text = data.title
        binding.ansTV.text = data.value.toString()
        binding.ansCarCB.isChecked = data.isChecked
        binding.ansCarCB.setOnCheckedChangeListener { buttonView, isChecked ->
            list[position].isChecked = isChecked
            notifyDataSetChanged()
        }

        if (data.isChecked) {
            binding.quesTV.setColor(baseActivity, R.color.dark_blue)
            binding.ansTV.setColor(baseActivity, R.color.dark_blue)
            binding.howManyquesTV.setColor(baseActivity, R.color.dark_blue)
            binding.ansTV.background = ContextCompat.getDrawable(baseActivity, R.drawable.dark_blue_stroke)
            binding.ansTV.setOnClickListener {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return@setOnClickListener
                }
                mLastClickTime = SystemClock.elapsedRealtime()
                onItemClick(Const.ONE, position)
            }
        } else {
            binding.quesTV.setColor(baseActivity, R.color.text_grey)
            binding.howManyquesTV.setColor(baseActivity, R.color.grey)
            binding.ansTV.setColor(baseActivity, R.color.grey)
            binding.ansTV.background = ContextCompat.getDrawable(baseActivity, R.drawable.grey_stroke)
        }
        if (position + 1 == list.size) {
            onPageEnd()
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }
}