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
import com.handlUser.app.R
import com.handlUser.app.databinding.AdapterServiceFunitureBinding
import com.handlUser.app.databinding.AdapterServicePaintingBinding
import com.handlUser.app.databinding.AdapterServicePetWsBinding
import com.handlUser.app.model.ParentData
import com.handlUser.app.ui.activity.BaseActivity
import com.handlUser.app.ui.adapter.BaseAdapter
import com.handlUser.app.ui.adapter.BaseViewHolder
import com.handlUser.app.ui.extensions.setColor
import com.handlUser.app.utils.Const

class PetServiceAdapter(val baseActivity: BaseActivity, var list: List<ParentData>) : BaseAdapter() {
    private var mLastClickTime: Long = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val binding = DataBindingUtil.inflate<AdapterServicePetWsBinding>(LayoutInflater.from(parent.context), R.layout.adapter_service_pet_ws, parent, false)
        return BaseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val binding = holder.binding as AdapterServicePetWsBinding
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

        binding.ansCB.isChecked = data.isChecked
        binding.ansCB.setOnCheckedChangeListener { buttonView, isChecked ->
            list[position].isChecked = isChecked
            notifyDataSetChanged()
        }

        if (position != 0) {
            binding.howManyTV.visibility = View.GONE
            binding.whatPetTV.visibility = View.GONE
        }
        if (data.isChecked) {
            binding.petWalkingTV.setColor(baseActivity, R.color.dark_blue)
        } else {
            binding.petWalkingTV.setColor(baseActivity, R.color.text_grey)
        }

        if (position + 1 == list.size) {
            onPageEnd()
        }

        if (data.title == baseActivity.getString(R.string.pet_walking) || data.title == baseActivity.getString(R.string.pet_sitting)) {
            binding.petWalkingTV.text = data.title
            binding.quesTV.visibility = View.GONE
            binding.ansTV.visibility = View.GONE
            binding.questionTV.visibility = View.VISIBLE
            binding.petWalkingTV.visibility = View.VISIBLE
            binding.ansCB.visibility = View.VISIBLE
        }
        if (data.title == baseActivity.getString(R.string.pet_sitting)) {
            binding.questionTV.visibility = View.GONE
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }
}