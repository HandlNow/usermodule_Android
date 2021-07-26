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
import com.handlUser.app.databinding.AdapterDialogNumberBinding
import com.handlUser.app.ui.activity.BaseActivity
import com.handlUser.app.utils.Const

class AdapterDialogNumber(val baseActivity: BaseActivity, val list: ArrayList<String>) : BaseAdapter() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val binding = DataBindingUtil.inflate<AdapterDialogNumberBinding>(LayoutInflater.from(parent.context), R.layout.adapter_dialog_number, parent, false)
        return BaseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val binding = holder.binding as AdapterDialogNumberBinding
        binding.nameTV.text = list[position]
        binding.nameTV.setOnClickListener {
            onItemClick(Const.TWO, list[position])
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }
}