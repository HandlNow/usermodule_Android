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
import com.handlService.app.databinding.AdapterMyServicesBinding
import com.handlService.app.model.MyServiceData
import com.handlService.app.ui.activity.BaseActivity
import com.handlService.app.ui.extensions.replaceFragment
import com.handlService.app.ui.fragment.MyServicesFragment
import com.handlService.app.ui.fragment.ServiceHomeCleaningFragment
import com.handlService.app.ui.fragment.ServicePricesFragment
import com.handlService.app.utils.Const

class AdapterMyServices(val baseActivity: BaseActivity, val fragment: MyServicesFragment, val list: ArrayList<MyServiceData>) : BaseAdapter() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val binding = DataBindingUtil.inflate<AdapterMyServicesBinding>(LayoutInflater.from(parent.context), R.layout.adapter_my_services, parent, false)
        return BaseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val binding = holder.binding as AdapterMyServicesBinding
        val data = list[position]
        binding.textTV.text = data.subCategoryName
        binding.amountTV.text = baseActivity.getString(R.string.euro) + data.price + "/h"

        binding.checkCB.isChecked = data.stateId != 0
        binding.root.setOnClickListener {
            val bundle = Bundle()
            when (list[position].subCategoryType) {
                Const.HOME_CLEANING -> {
                    bundle.putBoolean("isMyService", true)
                    bundle.putParcelable("service_data", list[position])
                    baseActivity.replaceFragment(ServiceHomeCleaningFragment(), bundle)
                }
                else -> {
                    bundle.putBoolean("isMyService", false)
                    bundle.putParcelable("service_data", list[position])
                    baseActivity.replaceFragment(ServicePricesFragment(), bundle)
                }

            }
        }
        binding.checkCB.setOnClickListener {
            fragment.hitDisableAbleAPI(position)
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }
}