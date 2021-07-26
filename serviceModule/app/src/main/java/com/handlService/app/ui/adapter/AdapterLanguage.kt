/*
 * @copyright : ToXSL Technologies Pvt. Ltd. < www.toxsl.com >
 * @author     : Shiv Charan Panjeta < shiv@toxsl.com >
 * All Rights Reserved.
 * Proprietary and confidential :  All information contained herein is, and remains
 * the property of ToXSL Technologies Pvt. Ltd. and its partners.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.handlService.app.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.handlService.app.R
import com.handlService.app.databinding.AdapterLanugageBinding
import com.handlService.app.databinding.AdapterVerticalprogressBinding
import com.handlService.app.model.NotificationData
import com.handlService.app.ui.activity.BaseActivity

class AdapterLanguage(val baseActivity: BaseActivity, val list: ArrayList<NotificationData>) : BaseAdapter() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val binding = DataBindingUtil.inflate<AdapterLanugageBinding>(LayoutInflater.from(parent.context), R.layout.adapter_lanugage, parent, false)
        return BaseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val binding = holder.binding as AdapterLanugageBinding
        val data = list[position]
        if (position + 1 == list.size) {
            onPageEnd()
        }

        binding.locationTV.text = data.title
        binding.closeIV.setOnClickListener {
            onItemClick(position)
        }


    }

    override fun getItemCount(): Int {
        return list.size
    }
}