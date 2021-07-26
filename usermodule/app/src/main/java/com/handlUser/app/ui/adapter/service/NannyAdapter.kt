/*
 * @copyright : ToXSL Technologies Pvt. Ltd. < www.toxsl.com >
 * @author     : Shiv Charan Panjeta < shiv@toxsl.com >
 * All Rights Reserved.
 * Proprietary and confidential :  All information contained herein is, and remains
 * the property of ToXSL Technologies Pvt. Ltd. and its partners.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.handlUser.app.ui.adapter.service

import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.handlUser.app.R
import com.handlUser.app.databinding.AdapterServiceNannyBinding
import com.handlUser.app.model.ParentData
import com.handlUser.app.ui.activity.BaseActivity
import com.handlUser.app.ui.adapter.BaseAdapter
import com.handlUser.app.ui.adapter.BaseViewHolder
import com.handlUser.app.ui.fragment.services.ServiceNannyFragment
import com.handlUser.app.utils.Const

class NannyAdapter(val baseActivity: BaseActivity, var list: List<ParentData>, val fragment: Fragment) : BaseAdapter() {
    private var mLastClickTime: Long = 0


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val binding = DataBindingUtil.inflate<AdapterServiceNannyBinding>(LayoutInflater.from(parent.context), R.layout.adapter_service_nanny, parent, false)
        return BaseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val binding = holder.binding as AdapterServiceNannyBinding
        val data = list[position]
        binding.quesTV.text = data.title
        binding.ansTV.text = data.value.toString()
        binding.ansTV.setOnClickListener {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return@setOnClickListener
            }
            mLastClickTime = SystemClock.elapsedRealtime()
            onItemClick(Const.ONE, position)
        }
        if (position + 1 == list.size) {
            onPageEnd()
        }
        if (position == 0) {
            if (fragment is ServiceNannyFragment) {
                binding.ageTV.text = baseActivity.getString(R.string.what_age)
            } else {
                binding.ageTV.text = baseActivity.getString(R.string.what_grade)

            }
        } else {
            binding.ageTV.visibility = View.GONE

        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}