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
import androidx.fragment.app.Fragment
import com.handlUser.app.R
import com.handlUser.app.databinding.AdapterServiceMatresSofaCleaningBinding
import com.handlUser.app.model.ParentData
import com.handlUser.app.ui.activity.BaseActivity
import com.handlUser.app.ui.adapter.BaseAdapter
import com.handlUser.app.ui.adapter.BaseViewHolder
import com.handlUser.app.ui.extensions.setColor

class MattressCleaningAdapter(val baseActivity: BaseActivity, var list: List<ParentData>, val fragment: Fragment) : BaseAdapter() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val binding = DataBindingUtil.inflate<AdapterServiceMatresSofaCleaningBinding>(LayoutInflater.from(parent.context), R.layout.adapter_service_matres_sofa_cleaning, parent, false)
        return BaseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val binding = holder.binding as AdapterServiceMatresSofaCleaningBinding
        val data = list[position]
        binding.quesTV.text = data.title
        binding.ansCarCB.isChecked = data.isChecked

        binding.ansCarCB.setOnCheckedChangeListener { buttonView, isChecked ->
            list[position].isChecked = isChecked
            for (i in 0 until list[position].children.size) {
                list[position].children[i].isChecked = isChecked
            }
            notifyDataSetChanged()
        }

        if (position + 1 == list.size) {
            onPageEnd()
        }

        binding.commonRV.adapter = MattressItemAdapter(baseActivity, list[position].children,fragment,position)

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