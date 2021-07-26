/*
 * @copyright : ToXSL Technologies Pvt. Ltd. < www.toxsl.com >
 * @author     : Shiv Charan Panjeta < shiv@toxsl.com >
 * All Rights Reserved.
 * Proprietary and confidential :  All information contained herein is, and remains
 * the property of ToXSL Technologies Pvt. Ltd. and its partners.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.handlUser.app.ui.adapter.service

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.handlUser.app.R
import com.handlUser.app.databinding.AdapterServiceCarwashBinding
import com.handlUser.app.databinding.AdapterServicePaintingBinding
import com.handlUser.app.databinding.AdapterServicePestControlBinding
import com.handlUser.app.model.ParentData
import com.handlUser.app.ui.activity.BaseActivity
import com.handlUser.app.ui.adapter.BaseAdapter
import com.handlUser.app.ui.adapter.BaseViewHolder
import com.handlUser.app.ui.extensions.setColor
import com.handlUser.app.utils.Const

class PestControlAdapter(val baseActivity: BaseActivity, var list: List<ParentData>) : BaseAdapter() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val binding = DataBindingUtil.inflate<AdapterServicePestControlBinding>(LayoutInflater.from(parent.context), R.layout.adapter_service_pest_control, parent, false)
        return BaseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val binding = holder.binding as AdapterServicePestControlBinding
        val data = list[position]
        binding.quesTV.text = data.title
        binding.ansCarCB.isChecked = data.isChecked
        binding.ansCarCB.setOnCheckedChangeListener { buttonView, isChecked ->
            list[position].isChecked = isChecked
            notifyDataSetChanged()
        }

        if (position + 1 == list.size) {
            onPageEnd()
        }

        if (data.isChecked) {
            binding.quesTV.setColor(baseActivity, R.color.dark_blue)
        } else {
            binding.quesTV.setColor(baseActivity, R.color.text_grey)

        }

    }

    override fun getItemCount(): Int {
        return list.size
    }
}