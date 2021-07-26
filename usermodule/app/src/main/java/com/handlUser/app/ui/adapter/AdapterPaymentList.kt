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
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.handlUser.app.R
import com.handlUser.app.databinding.AdapterPaymentListBinding
import com.handlUser.app.model.CardModel
import com.handlUser.app.ui.activity.BaseActivity
import com.handlUser.app.ui.fragment.PaymentMethodListFragment
import com.stripe.android.model.CardBrand

class AdapterPaymentList(val baseActivity: BaseActivity, val list: ArrayList<CardModel>, val isPayement: Boolean) : BaseAdapter() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val binding = DataBindingUtil.inflate<AdapterPaymentListBinding>(LayoutInflater.from(parent.context), R.layout.adapter_payment_list, parent, false)
        return BaseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val binding = holder.binding as AdapterPaymentListBinding
        val data = list[position]
        binding.numberTV.text = "Ending in " + data.last4
        binding.nameTV.text = data.brand
        binding.checkCB.isChecked = data.isSelected
        if (isPayement) {
            binding.checkCB.visibility = View.VISIBLE
        } else {
            binding.checkCB.visibility = View.GONE
        }

        if (data.brand == CardBrand.Visa.displayName) {
            binding.brandIV.setImageDrawable(ContextCompat.getDrawable(baseActivity, CardBrand.Visa.icon))
        } else if (data.brand == CardBrand.AmericanExpress.displayName) {
            binding.brandIV.setImageDrawable(ContextCompat.getDrawable(baseActivity, CardBrand.AmericanExpress.icon))
        } else if (data.brand == CardBrand.Discover.displayName) {
            binding.brandIV.setImageDrawable(ContextCompat.getDrawable(baseActivity, CardBrand.Discover.icon))
        } else if (data.brand == CardBrand.JCB.displayName) {
            binding.brandIV.setImageDrawable(ContextCompat.getDrawable(baseActivity, CardBrand.JCB.icon))
        } else if (data.brand == CardBrand.DinersClub.displayName) {
            binding.brandIV.setImageDrawable(ContextCompat.getDrawable(baseActivity, CardBrand.DinersClub.icon))
        } else if (data.brand == CardBrand.MasterCard.displayName) {
            binding.brandIV.setImageDrawable(ContextCompat.getDrawable(baseActivity, CardBrand.MasterCard.icon))
        } else if (data.brand == CardBrand.UnionPay.displayName) {
            binding.brandIV.setImageDrawable(ContextCompat.getDrawable(baseActivity, CardBrand.UnionPay.icon))
        } else {
            binding.brandIV.setImageDrawable(ContextCompat.getDrawable(baseActivity, CardBrand.Unknown.icon))
        }

        binding.root.setOnClickListener {
            for (i in list) {
                i.isSelected = false
            }
            list[position].isSelected = true
            notifyDataSetChanged()
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }
}