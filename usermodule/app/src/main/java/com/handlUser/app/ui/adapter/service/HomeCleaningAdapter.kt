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
import com.handlUser.app.databinding.AdapterServiceHomeCleaningBinding
import com.handlUser.app.model.ParentData
import com.handlUser.app.ui.activity.BaseActivity
import com.handlUser.app.ui.adapter.BaseAdapter
import com.handlUser.app.ui.adapter.BaseViewHolder
import com.handlUser.app.ui.extensions.setColor
import com.handlUser.app.ui.extensions.setbackground
import com.handlUser.app.ui.fragment.services.ServiceHomeCleaningFragment

class HomeCleaningAdapter(
    val baseActivity: BaseActivity,
    var list: List<ParentData>,
  val  fragment: ServiceHomeCleaningFragment
) : BaseAdapter() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val binding = DataBindingUtil.inflate<AdapterServiceHomeCleaningBinding>(LayoutInflater.from(parent.context), R.layout.adapter_service_home_cleaning, parent, false)
        return BaseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val binding = holder.binding as AdapterServiceHomeCleaningBinding
        val data = list[position]
        binding.quesTV.text = data.title

        binding.root.setOnClickListener {

            fragment.onItemCLick(position)
        }

        if (position + 1 == list.size) {
            onPageEnd()
        }

        if (data.isChecked) {
            binding.quesTV.setColor(baseActivity, R.color.White)
            binding.quesTV.setbackground(baseActivity, R.color.light_blue)
        } else {
            binding.quesTV.setColor(baseActivity, R.color.sub_sentence_color)
            binding.quesTV.setbackground(baseActivity, R.color.LightGrey)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}