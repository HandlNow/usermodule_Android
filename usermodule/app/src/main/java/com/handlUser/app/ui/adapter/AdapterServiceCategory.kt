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
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.handlUser.app.R
import com.handlUser.app.databinding.AdapterServiceCategoryBinding
import com.handlUser.app.model.CategoryData
import com.handlUser.app.ui.activity.BaseActivity

class AdapterServiceCategory(val baseActivity: BaseActivity, val list: ArrayList<CategoryData>) : BaseAdapter() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val binding = DataBindingUtil.inflate<AdapterServiceCategoryBinding>(LayoutInflater.from(parent.context), R.layout.adapter_service_category, parent, false)
        return BaseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val binding = holder.binding as AdapterServiceCategoryBinding
        binding.data = list[position]
        binding.adapter = AdapterServiceHome(baseActivity, list[position].subCategories)

        if (position + 1 == list.size) {
            onPageEnd()
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}