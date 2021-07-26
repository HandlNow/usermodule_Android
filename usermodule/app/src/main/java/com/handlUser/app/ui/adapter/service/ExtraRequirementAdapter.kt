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
import androidx.databinding.DataBindingUtil
import com.handlUser.app.R
import com.handlUser.app.databinding.AdapterServiceExtrasBinding
import com.handlUser.app.model.CategoryData
import com.handlUser.app.ui.activity.BaseActivity
import com.handlUser.app.ui.adapter.BaseAdapter
import com.handlUser.app.ui.adapter.BaseViewHolder
import com.handlUser.app.ui.extensions.setColor
import com.handlUser.app.ui.extensions.setbackground

class ExtraRequirementAdapter(val baseActivity: BaseActivity, var list: ArrayList<CategoryData>) : BaseAdapter() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val binding = DataBindingUtil.inflate<AdapterServiceExtrasBinding>(LayoutInflater.from(parent.context), R.layout.adapter_service_extras, parent, false)
        return BaseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val binding = holder.binding as AdapterServiceExtrasBinding
        val data = list[position]
        binding.quesTV.text = data.title

        if (position + 1 == list.size) {
            onPageEnd()
        }
        binding.root.setOnClickListener {
            list[position].isChecked = !list[position].isChecked
            notifyDataSetChanged()
        }
        if (data.isChecked) {
            binding.quesTV.setColor(baseActivity, R.color.White)
            binding.quesTV.setbackground(baseActivity, R.drawable.dark_rounded_button)
        } else {
            binding.quesTV.setColor(baseActivity, R.color.dark_blue)
            binding.quesTV.setbackground(baseActivity, R.drawable.grey_rounded_button)
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }
}