/*
 * @copyright : ToXSL Technologies Pvt. Ltd. < www.toxsl.com >
 * @author     : Shiv Charan Panjeta < shiv@toxsl.com >
 * All Rights Reserved.
 * Proprietary and confidential :  All information contained herein is, and remains
 * the property of ToXSL Technologies Pvt. Ltd. and its partners.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.handlUser.app.ui.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.handlUser.app.R
import com.handlUser.app.databinding.AdapterSavedHomeListBinding
import com.handlUser.app.model.AddressData
import com.handlUser.app.ui.activity.BaseActivity
import com.handlUser.app.ui.extensions.replaceFragment
import com.handlUser.app.ui.fragment.EditRegisterHouseFragment

class AdapterSavedHomeList(val baseActivity: BaseActivity, val list: ArrayList<AddressData>) : BaseAdapter() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val binding = DataBindingUtil.inflate<AdapterSavedHomeListBinding>(LayoutInflater.from(parent.context), R.layout.adapter_saved_home_list, parent, false)
        return BaseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val binding = holder.binding as AdapterSavedHomeListBinding
        val data = list[position]
        binding.nameTV.text = data.title
        binding.numberTV.text = data.address

        binding.root.setOnClickListener {
            val bundle = Bundle()
            bundle.putParcelable("data", data)
            baseActivity.replaceFragment(EditRegisterHouseFragment(), bundle)

        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}