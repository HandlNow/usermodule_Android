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
import androidx.databinding.DataBindingUtil
import com.handlUser.app.model.ChatData
import com.handlUser.app.R
import com.handlUser.app.databinding.AdapterChatBinding
import com.handlUser.app.ui.activity.BaseActivity
import com.handlUser.app.ui.extensions.loadFromUrl
import com.handlUser.app.ui.fragment.ChatFragment
import com.handlUser.app.utils.Const
import com.handlUser.app.utils.ZoomImageDialog

class ChatAdapter(val baseActivity: BaseActivity, var messageList: ArrayList<ChatData>, val fragment: ChatFragment) : BaseAdapter() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val binding = DataBindingUtil.inflate<AdapterChatBinding>(LayoutInflater.from(parent.context), R.layout.adapter_chat, parent, false)
        return BaseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val binding = holder.binding as AdapterChatBinding
        val data = messageList[position]
        val date = baseActivity.changeDateFormatGmtToLocal(data.createdOn, Const.DATE_FORMAT, "hh:mm a")
        if (data.createdById != baseActivity.getProfileData()!!.id) {
            binding.incomingCL.visibility = View.VISIBLE
            binding.outgoingCL.visibility = View.GONE
            binding.incomingTimeTV.text = date
            if (data.stateId == Const.STATE_MESSAGE) {
                binding.incomingMsgTV.text = data.message
                binding.incomingMsgTV.visibility = View.VISIBLE
                binding.incomingMsgIV.visibility = View.GONE
            } else {
                binding.incomingMsgTV.visibility = View.GONE
                binding.incomingMsgIV.visibility = View.VISIBLE
                binding.incomingMsgIV.loadFromUrl(baseActivity, data.message)

                binding.incomingMsgIV.setOnClickListener {
                    val array: ArrayList<String> = arrayListOf()
                    array.add(data.message)
                    showDialog(array, 0)
                }
            }

        } else {
            binding.incomingCL.visibility = View.GONE
            binding.outgoingCL.visibility = View.VISIBLE
            binding.outgoingCL.visibility = View.VISIBLE
            binding.outgoingTimeTV.text = date
            if (data.stateId == Const.STATE_MESSAGE) {
                binding.outgoingMsgTV.text = data.message
                binding.outgoingMsgTV.visibility = View.VISIBLE
                binding.outgoingMsgIV.visibility = View.GONE
            } else {
                binding.outgoingMsgTV.visibility = View.GONE
                binding.outgoingMsgIV.visibility = View.VISIBLE
                binding.outgoingMsgIV.loadFromUrl(baseActivity, data.message)
                binding.outgoingMsgIV.setOnClickListener {
                    val array: ArrayList<String> = arrayListOf()
                    array.add(data.message)
                    showDialog(array, 0)
                }

            }
        }

        if (messageList.size == position + 1) {
            fragment.getPreviousMessages()
        }

    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    fun showDialog(arrayList: ArrayList<String>, pos: Int) {
        val dialogFragment = ZoomImageDialog(arrayList, pos, baseActivity)
        dialogFragment.show(baseActivity.supportFragmentManager, "dialog")
    }

}