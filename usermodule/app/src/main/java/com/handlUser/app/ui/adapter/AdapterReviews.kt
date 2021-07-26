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
import com.handlUser.app.databinding.AdapterReviewsBinding
import com.handlUser.app.model.NotificationData
import com.handlUser.app.ui.activity.BaseActivity
import com.handlUser.app.ui.fragment.*
import java.util.ArrayList

class AdapterReviews(val baseActivity: BaseActivity, val fragment: SummaryFragment, val list: ArrayList<NotificationData>, val fullName: String) : BaseAdapter() {


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
        binding.titleTV.text = fullName
        binding.nameTV.text = data.title
    }

    override fun getItemCount(): Int {
        return list.size
    }
}