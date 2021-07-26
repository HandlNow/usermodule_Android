/*
 * @copyright : ToXSL Technologies Pvt. Ltd. < www.toxsl.com >
 * @author     : Shiv Charan Panjeta < shiv@toxsl.com >
 * All Rights Reserved.
 * Proprietary and confidential :  All information contained herein is, and remains
 * the property of ToXSL Technologies Pvt. Ltd. and its partners.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.handlUser.app.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.handlUser.app.R
import com.handlUser.app.databinding.AdapterAddressTypeBinding
import com.handlUser.app.model.AddressData
import com.handlUser.app.ui.activity.BaseActivity

class AdapterNicknameHome(val baseActivity: BaseActivity, val list: ArrayList<AddressData>) : BaseAdapter() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val binding = DataBindingUtil.inflate<AdapterAddressTypeBinding>(LayoutInflater.from(parent.context), R.layout.adapter_address_type, parent, false)
        return BaseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val binding = holder.binding as AdapterAddressTypeBinding
        val data = list[position]
        if (data.isSelected) {
            binding.homeET.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_home, 0, 0, 0)
        } else {
            binding.homeET.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_home_grey, 0, 0, 0)
        }
        binding.homeET.text = data.title
        binding.root.setOnClickListener {
            for (i in list) {
                i.isSelected = false
            }
            list[position].isSelected = true
            notifyDataSetChanged()
            onItemClick(position)

        }

    }

    override fun getItemCount(): Int {
        return list.size
    }
}