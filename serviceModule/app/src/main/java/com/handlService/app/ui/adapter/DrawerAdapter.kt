/*
 * @copyright : ToXSL Technologies Pvt. Ltd. < www.toxsl.com >
 * @author     : Shiv Charan Panjeta < shiv@toxsl.com >
 * All Rights Reserved.
 * Proprietary and confidential :  All information contained herein is, and remains
 * the property of ToXSL Technologies Pvt. Ltd. and its partners.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.handlService.app.ui.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import com.handlService.app.R
import com.handlService.app.model.DrawerData
import com.handlService.app.ui.activity.BaseActivity
import java.util.*


class DrawerAdapter(internal var activity: BaseActivity, resource: Int, var objects: ArrayList<DrawerData>) : ArrayAdapter<DrawerData>(activity, resource, objects) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        if (convertView == null) {
            convertView = newView(parent)
        }
        bindView(position, convertView)
        return convertView
    }

    private fun newView(parent: ViewGroup): View {
        return activity.layoutInflater.inflate(R.layout.adapter_drawer, parent, false)
    }

    private fun bindView(position: Int, convertView: View?) {
        val nameTV = convertView!!.findViewById<TextView>(R.id.nameTV) as AppCompatTextView
        val iconIV = convertView.findViewById<View>(R.id.iconIV) as AppCompatImageView
        val data = getItem(position)
        nameTV.text = data?.name
        iconIV.setImageResource(data!!.icon)
    }
}
