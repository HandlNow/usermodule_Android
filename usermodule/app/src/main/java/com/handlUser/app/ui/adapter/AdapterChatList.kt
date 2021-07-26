/*
 * @copyright : ToXSL Technologies Pvt. Ltd. < www.toxsl.com >
 * @author     : Shiv Charan Panjeta < shiv@toxsl.com >
 * All Rights Reserved.
 * Proprietary and confidential :  All information contained herein is, and remains
 * the property of ToXSL Technologies Pvt. Ltd. and its partners.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.handlUser.app.ui.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.handlUser.app.R
import com.handlUser.app.databinding.AdapterChatListBinding
import com.handlUser.app.model.AppointmentData
import com.handlUser.app.model.ProfileData
import com.handlUser.app.ui.activity.BaseActivity
import com.handlUser.app.ui.extensions.loadFromUrl
import com.handlUser.app.ui.extensions.replaceFragment
import com.handlUser.app.ui.fragment.ChatFragment
import com.handlUser.app.utils.Const

class AdapterChatList(val baseActivity: BaseActivity, val list: ArrayList<AppointmentData>) : BaseAdapter() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val binding = DataBindingUtil.inflate<AdapterChatListBinding>(LayoutInflater.from(parent.context), R.layout.adapter_chat_list, parent, false)
        return BaseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val binding = holder.binding as AdapterChatListBinding
        val data = list[position]
        binding.userIV.loadFromUrl(baseActivity, data.provider.profileFile!!)
        binding.titleTV.text = baseActivity!!.getString(R.string.booking_id) + " " + data.id.toString() + "\n" + data.provider.fullName
        if (data.stateIdChat == Const.STATE_MESSAGE.toString()) {
            binding.textTV.text = data.lastMessge
        } else {
            binding.textTV.text = baseActivity!!.getString(R.string.media_received)

        }
        val date = baseActivity.changeDateFormatGmtToLocal(data.createdOn, Const.DATE_FORMAT, "hh:mm a")
        binding.timeTV.text = date
        binding.root.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("to_id", data.provider.id.toString())
            bundle.putString("model_id", data.id.toString())
            bundle.putString("to_name", data.provider.fullName)
            baseActivity.replaceFragment(ChatFragment(), bundle)
        }
        if (position + 1 == list.size) {
            onPageEnd()
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}