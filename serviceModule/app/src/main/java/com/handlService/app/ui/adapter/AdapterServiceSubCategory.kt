/*
 * @copyright : ToXSL Technologies Pvt. Ltd. < www.toxsl.com >
 * @author     : Shiv Charan Panjeta < shiv@toxsl.com >
 * All Rights Reserved.
 * Proprietary and confidential :  All information contained herein is, and remains
 * the property of ToXSL Technologies Pvt. Ltd. and its partners.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.handlService.app.ui.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.handlService.app.R
import com.handlService.app.databinding.AdapterSubcategoryBinding
import com.handlService.app.model.CategoryData
import com.handlService.app.model.SubCategory
import com.handlService.app.ui.activity.BaseActivity
import com.handlService.app.ui.extensions.replaceFragment
import com.handlService.app.ui.fragment.RegisterServiceFragment
import com.handlService.app.ui.fragment.ServiceHomeCleaningFragment
import com.handlService.app.ui.fragment.ServicePricesFragment
import com.handlService.app.utils.Const

class AdapterServiceSubCategory(val baseActivity: BaseActivity, val fragment: RegisterServiceFragment, val list: ArrayList<SubCategory>) : BaseAdapter() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val binding = DataBindingUtil.inflate<AdapterSubcategoryBinding>(LayoutInflater.from(parent.context), R.layout.adapter_subcategory, parent, false)
        return BaseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val binding = holder.binding as AdapterSubcategoryBinding
        val data = list[position]
        binding.titleTV.text = data.title
        binding.checkCB.isChecked = data.isCheck
        binding.root.setOnClickListener {
            binding.checkCB.isChecked =  !binding.checkCB.isChecked
            val bundle = Bundle()
            bundle.putParcelable("list_data", list[position])
            when (list[position].typeId) {
                Const.HOME_CLEANING -> {
                    bundle.putBoolean("isMyService",false)
                    baseActivity.replaceFragment(ServiceHomeCleaningFragment(), bundle)
                }
                else->{
                    baseActivity.replaceFragment(ServicePricesFragment(), bundle)

                }
            }
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }
}