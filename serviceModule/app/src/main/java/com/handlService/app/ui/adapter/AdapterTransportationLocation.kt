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
import com.handlService.app.databinding.AdapterTransportationLocationBinding
import com.handlService.app.model.UserAddressListModel
import com.handlService.app.ui.activity.BaseActivity

class AdapterTransportationLocation(val baseActivity: BaseActivity, val list: ArrayList<UserAddressListModel>) : BaseAdapter() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val binding = DataBindingUtil.inflate<AdapterTransportationLocationBinding>(LayoutInflater.from(parent.context), R.layout.adapter_transportation_location, parent, false)
        return BaseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val binding = holder.binding as AdapterTransportationLocationBinding
        val data = list[position]

        binding.textTV.text = data.city+" + " + data.km + " km"
        binding.textTV.isSelected = true
        if (position + 1 == list.size) {
            onPageEnd()
        }
        binding.crossIV.setOnClickListener {
            onItemClick(position)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}