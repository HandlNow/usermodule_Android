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
import com.handlService.app.databinding.AdapterReviewsBinding
import com.handlService.app.model.NotificationData
import com.handlService.app.ui.activity.BaseActivity
import com.handlService.app.ui.fragment.*
import java.util.ArrayList

class AdapterReviews(val baseActivity: BaseActivity, val fragment: InsightsFragment, val list: ArrayList<NotificationData>) : BaseAdapter() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val binding = DataBindingUtil.inflate<AdapterReviewsBinding>(LayoutInflater.from(parent.context), R.layout.adapter_reviews, parent, false)
        return BaseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val binding = holder.binding as AdapterReviewsBinding
        if (position + 1 == list.size) {
            onPageEnd()
        }
        val data = list[position]
        binding.ratingBarRB.rating = data.rating.toFloat()
        binding.titleTV.text = data.fullName
        binding.nameTV.text = data.title
    }

    override fun getItemCount(): Int {
        return list.size
    }
}