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
import androidx.recyclerview.widget.RecyclerView
import com.handlService.app.R
import com.handlService.app.databinding.AdapterFilterHomeDesignBinding
import com.handlService.app.model.ImageFile
import com.handlService.app.ui.activity.BaseActivity
import com.handlService.app.ui.extensions.loadFromLocal
import com.handlService.app.ui.extensions.loadFromUrl
import com.handlService.app.ui.fragment.WorkDetailFragment
import kotlinx.android.synthetic.main.adapter_filter_home_design.view.*
import java.util.*

class ImageAdapter(val baseActivity: BaseActivity, val uriList: ArrayList<ImageFile>, val toolDetailsFragment: WorkDetailFragment) : RecyclerView.Adapter<ImageAdapter.MyHolderView>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolderView {
        val binding: AdapterFilterHomeDesignBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.adapter_filter_home_design, parent, false)
        return MyHolderView(binding)
    }

    override fun onBindViewHolder(holder: MyHolderView, position: Int) {
        val binding = holder.itemView
        val data = uriList[position]
        if (data.isURL) {
            binding.imageIV.loadFromUrl(baseActivity, data.name)
        } else {
            binding.imageIV.loadFromLocal(baseActivity, data.image)
        }

        binding.crossIV.setOnClickListener {
            if (data.isURL) {
                toolDetailsFragment.removeImage(position)
            } else {
                uriList.removeAt(position)
                notifyDataSetChanged()
            }
        }
    }

    override fun getItemCount(): Int {
        return uriList.size
    }

    class MyHolderView(itemView: AdapterFilterHomeDesignBinding) : RecyclerView.ViewHolder(itemView.root)
}