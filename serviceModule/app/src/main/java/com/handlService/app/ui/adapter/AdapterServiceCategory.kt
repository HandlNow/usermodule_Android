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
import com.handlService.app.databinding.AdapterServiceCategoryBinding
import com.handlService.app.model.CategoryData
import com.handlService.app.ui.activity.BaseActivity
import com.handlService.app.ui.fragment.RegisterServiceFragment

class AdapterServiceCategory(val baseActivity: BaseActivity, val fragment: RegisterServiceFragment, val list: ArrayList<CategoryData>) : BaseAdapter() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val binding = DataBindingUtil.inflate<AdapterServiceCategoryBinding>(LayoutInflater.from(parent.context), R.layout.adapter_service_category, parent, false)
        return BaseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val binding = holder.binding as AdapterServiceCategoryBinding
        val data = list[position]
        binding.titleTV.text = data.title
        binding.subRV.adapter = AdapterServiceSubCategory(baseActivity, fragment, data.subCategories)

        if (position + 1 == list.size) {
            onPageEnd()
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}