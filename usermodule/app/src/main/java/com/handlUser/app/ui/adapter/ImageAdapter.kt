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
import androidx.recyclerview.widget.RecyclerView
import com.handlUser.app.R
import com.handlUser.app.databinding.AdapterFilterHomeDesignBinding
import com.handlUser.app.model.ProviderFile
import com.handlUser.app.ui.activity.BaseActivity
import com.handlUser.app.ui.extensions.loadFromUrl
import com.handlUser.app.utils.ZoomImageDialog
import kotlinx.android.synthetic.main.adapter_filter_home_design.view.*
import java.util.*

class ImageAdapter(val baseActivity: BaseActivity, val uriList: ArrayList<ProviderFile>) : RecyclerView.Adapter<ImageAdapter.MyHolderView>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolderView {
        val binding: AdapterFilterHomeDesignBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.adapter_filter_home_design, parent, false)
        return MyHolderView(binding)
    }

    override fun onBindViewHolder(holder: MyHolderView, position: Int) {
        val binding = holder.itemView
        val data = uriList[position]
        binding.imageIV.loadFromUrl(baseActivity, data.key)
        binding.crossIV.visibility = View.GONE

        binding.imageIV.setOnClickListener {
            val array: ArrayList<String> = arrayListOf()
            array.add(data.key)
            showDialog(array, 0)
        }
    }

    override fun getItemCount(): Int {
        return uriList.size
    }

    class MyHolderView(itemView: AdapterFilterHomeDesignBinding) : RecyclerView.ViewHolder(itemView.root)

    fun showDialog(arrayList: ArrayList<String>, pos: Int) {
        val dialogFragment = ZoomImageDialog(arrayList, pos, baseActivity)
        dialogFragment.show(baseActivity.supportFragmentManager, "dialog")
    }

}