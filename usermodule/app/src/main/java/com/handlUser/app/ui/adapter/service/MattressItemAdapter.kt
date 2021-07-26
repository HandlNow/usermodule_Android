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
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.handlUser.app.R
import com.handlUser.app.databinding.AdapterServiceAcCleaningBinding
import com.handlUser.app.model.ChildrenData
import com.handlUser.app.ui.activity.BaseActivity
import com.handlUser.app.ui.adapter.BaseAdapter
import com.handlUser.app.ui.adapter.BaseViewHolder
import com.handlUser.app.ui.extensions.setColor
import com.handlUser.app.ui.fragment.services.ServiceGardenFragment
import com.handlUser.app.ui.fragment.services.ServiceMCSCleaningFragment
import com.handlUser.app.utils.Const

class MattressItemAdapter(val baseActivity: BaseActivity, var list: ArrayList<ChildrenData>, val fragment: Fragment, val checkPosition: Int) : BaseAdapter() {
    private var mLastClickTime: Long = 0


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val binding = DataBindingUtil.inflate<AdapterServiceAcCleaningBinding>(LayoutInflater.from(parent.context), R.layout.adapter_service_ac_cleaning, parent, false)
        return BaseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val binding = holder.binding as AdapterServiceAcCleaningBinding
        val data = list[position]
        binding.questTV.text = data.title
        binding.ansTV.text = data.value.toString()

        if (position + 1 == list.size) {
            onPageEnd()
        }

        if (position != 0) {
            binding.howManyTV.visibility = View.GONE
        }
        if (data.isChecked) {
            binding.questTV.setColor(baseActivity, R.color.dark_blue)
            binding.ansTV.setColor(baseActivity, R.color.dark_blue)
            binding.howManyTV.setColor(baseActivity, R.color.dark_blue)
            binding.ansTV.background = ContextCompat.getDrawable(baseActivity, R.drawable.dark_blue_stroke)
            binding.ansTV.setOnClickListener {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return@setOnClickListener
                }
                mLastClickTime = SystemClock.elapsedRealtime()
                if (fragment is ServiceMCSCleaningFragment) {
                    fragment.showDialog(position, checkPosition)

                } else if (fragment is ServiceGardenFragment) {
                    fragment.showDialog(position, checkPosition)

                }
            }


        } else {
            binding.questTV.setColor(baseActivity, R.color.text_grey)
            binding.howManyTV.setColor(baseActivity, R.color.grey)
            binding.ansTV.setColor(baseActivity, R.color.grey)
            binding.ansTV.background = ContextCompat.getDrawable(baseActivity, R.drawable.grey_stroke)
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }
}