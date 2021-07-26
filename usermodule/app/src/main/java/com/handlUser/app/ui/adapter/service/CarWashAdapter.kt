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
import com.handlUser.app.databinding.AdapterServiceCarwashBinding
import com.handlUser.app.databinding.AdapterServicePaintingBinding
import com.handlUser.app.model.ParentData
import com.handlUser.app.ui.activity.BaseActivity
import com.handlUser.app.ui.adapter.BaseAdapter
import com.handlUser.app.ui.adapter.BaseViewHolder
import com.handlUser.app.ui.extensions.setColor
import com.handlUser.app.utils.Const

class CarWashAdapter(val baseActivity: BaseActivity, var list: List<ParentData>) : BaseAdapter() {
    private var mLastClickTime: Long = 0


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val binding = DataBindingUtil.inflate<AdapterServiceCarwashBinding>(LayoutInflater.from(parent.context), R.layout.adapter_service_carwash, parent, false)
        return BaseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val binding = holder.binding as AdapterServiceCarwashBinding
        val data = list[position]
        binding.internalCarTV.text = data.title
        binding.numberICarTV.text = data.children[0].title
        binding.ansTV.text = data.value.toString()

        binding.ansCarCB.isChecked = data.isChecked
        binding.ansCarCB.setOnCheckedChangeListener { buttonView, isChecked ->
            onItemClick(Const.THREE, position)
        }

        if (position + 1 == list.size) {
            onPageEnd()
        }

        if (data.isChecked) {
            binding.internalCarTV.setColor(baseActivity, R.color.dark_blue)
            binding.numberICarTV.setColor(baseActivity, R.color.dark_blue)
            binding.ansTV.setColor(baseActivity, R.color.dark_blue)
            binding.ansTV.background = ContextCompat.getDrawable(baseActivity, R.drawable.dark_blue_stroke)
            binding.ansTV.setOnClickListener {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return@setOnClickListener
                }
                mLastClickTime = SystemClock.elapsedRealtime()
                onItemClick(Const.ONE, position)
            }
        } else {
            binding.internalCarTV.setColor(baseActivity, R.color.text_grey)
            binding.numberICarTV.setColor(baseActivity, R.color.text_grey)
            binding.ansTV.setColor(baseActivity, R.color.grey)
            binding.ansTV.background = ContextCompat.getDrawable(baseActivity, R.drawable.grey_stroke)
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }
}