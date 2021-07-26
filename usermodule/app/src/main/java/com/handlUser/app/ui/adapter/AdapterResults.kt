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
import com.handlUser.app.databinding.AdapterResultsBinding
import com.handlUser.app.model.LangData
import com.handlUser.app.model.ProviderData
import com.handlUser.app.ui.activity.BaseActivity
import com.handlUser.app.ui.extensions.loadFromUrl
import com.handlUser.app.ui.extensions.makeScrollableInsideScrollView
import com.handlUser.app.utils.Const


class AdapterResults(val baseActivity: BaseActivity, val list: ArrayList<ProviderData>) :
    BaseAdapter() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val binding = DataBindingUtil.inflate<AdapterResultsBinding>(
            LayoutInflater.from(parent.context),
            R.layout.adapter_results,
            parent,
            false
        )
        return BaseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val binding = holder.binding as AdapterResultsBinding
        val data = list[position]
        binding.providerData = data
        binding.ratingTV.text = data.rating.toString()

        binding.userIV.loadFromUrl(baseActivity, data.profileFile)
        if (data.isTranspotation == Const.TYPE_ON) {
            baseActivity.setTextViewDrawableColor(binding.dateTV, R.color.dark_blue)
        } else {
            baseActivity.setTextViewDrawableColor(binding.dateTV, R.color.carColor)
        }

        if (data.avalibility.isNotEmpty()) {
            binding.slotsRV.adapter = SlotTimingAdapter(baseActivity, data.avalibility)
        }
        if (data.selecteLanguage.isNotEmpty()) {
            binding.nameTV.text = getMultiId(data.selecteLanguage)
            binding.nameTV.makeScrollableInsideScrollView()
        }


        binding.root.setOnClickListener {
            onItemClick(position)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    private fun getMultiId(colorList: ArrayList<LangData>): String {
        var id = ""
        for (data in colorList) {
            id = id + data.languageTitle + ","
        }
        return when {
            id.isNotEmpty() -> id.substring(0, id.length - 1)
            else -> id
        }
    }


}