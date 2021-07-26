/*
 * @copyright : ToXSL Technologies Pvt. Ltd. < www.toxsl.com >
 * @author     : Shiv Charan Panjeta < shiv@toxsl.com >
 * All Rights Reserved.
 * Proprietary and confidential :  All information contained herein is, and remains
 * the property of ToXSL Technologies Pvt. Ltd. and its partners.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.handlService.app.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.handlService.app.R
import com.handlService.app.databinding.AdapterSubscriptionBinding
import com.handlService.app.model.PlanData
import com.handlService.app.ui.activity.BaseActivity
import com.handlService.app.ui.fragment.login.SubscriptionFragment

class AdapterSubscription(val baseActivity: BaseActivity, val fragment: SubscriptionFragment, var arrayList: ArrayList<PlanData>) : BaseAdapter() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val binding = DataBindingUtil.inflate<AdapterSubscriptionBinding>(LayoutInflater.from(parent.context), R.layout.adapter_subscription, parent, false)
        return BaseViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val binding = holder.binding as AdapterSubscriptionBinding
        when (position) {
            0 -> {
                binding.textTV.text = baseActivity.getString(R.string.free_trial)
                binding.paymentTypeTV.text = "-"
                binding.descTV.text = "-"
                binding.noFeeTV.text = baseActivity.getString(R.string.no_fee_handl)
                binding.chargesTV.text = baseActivity.getString(R.string.charges_sub)
            }
            1 -> {
                binding.textTV.text = baseActivity.getString(R.string.basic_trial)
                binding.paymentTypeTV.text = "-"
                binding.descTV.text = "-"
                binding.noFeeTV.text = baseActivity.getString(R.string.fee_handl)
                binding.chargesTV.text = baseActivity.getString(R.string.charges_sub)
            }
            else -> {
                binding.textTV.text = baseActivity.getString(R.string.monthly_trial)
                binding.paymentTypeTV.text = baseActivity.getString(R.string.cash_payments)
                binding.descTV.text = baseActivity.getString(R.string.search_priorty)
                binding.noFeeTV.text = baseActivity.getString(R.string.no_fee_handl)
                binding.chargesTV.text = baseActivity.getString(R.string.charges_sub_cash)
            }
        }

        if (arrayList[position].isBuy) {
            binding.linearLL.background = ContextCompat.getDrawable(baseActivity, R.drawable.white_blue_stroke)
        } else {
            binding.linearLL.background = ContextCompat.getDrawable(baseActivity, R.drawable.white_rect_button)
        }
        binding.root.setOnClickListener {
            if (arrayList[position].isBuy) {
                baseActivity.showToastOne(baseActivity.getString(R.string.plan_already_purchased))
            } else {
                onItemClick(position)
            }
        }

    }

    override fun getItemCount(): Int {
        return arrayList.size
    }
}