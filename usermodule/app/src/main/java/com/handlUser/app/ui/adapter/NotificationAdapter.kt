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
import com.handlUser.app.databinding.AdapterNotificationBinding
import com.handlUser.app.model.NotificationData
import com.handlUser.app.ui.activity.BaseActivity
import com.handlUser.app.ui.fragment.login.NotificationFragment
import com.handlUser.app.utils.Const

class NotificationAdapter(val baseActivity: BaseActivity, var list: ArrayList<NotificationData>, val notificationFragment: NotificationFragment) : BaseAdapter() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val binding = DataBindingUtil.inflate<AdapterNotificationBinding>(LayoutInflater.from(parent.context), R.layout.adapter_notification, parent, false)
        return BaseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val binding = holder.binding as AdapterNotificationBinding

        val data = list[position]

        if (position + 1 == list.size) {
            onPageEnd(Const.API_NOTIFICATION_LIST)
        }
        val titleArray  = data.modelType.split("\\")
        binding.titleTV.text = titleArray[titleArray.size-1]
        binding.textTV.text = data.title
        val date = baseActivity.changeDateFormatGmtToLocal(data.createdOn, Const.DATE_FORMAT, "dd MMM, yyyy hh:mm a")
        binding.readmoreTV.text = date

    }

    override fun getItemCount(): Int {
        return list.size
    }
}