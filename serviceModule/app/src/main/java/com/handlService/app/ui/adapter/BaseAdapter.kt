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


open class BaseAdapter : androidx.recyclerview.widget.RecyclerView.Adapter<BaseViewHolder>(), View.OnClickListener {
    private var onItemClickListener: OnItemClickListener? = null
    private var onPageEndListener: OnPageEndListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return BaseViewHolder(null)
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {

    }

    override fun getItemCount(): Int {
        return 0
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

    fun setOnPageEndListener(onPageEndListener: OnPageEndListener) {
        this.onPageEndListener = onPageEndListener
    }

    fun onItemClick(vararg itemData: Any) {
        if (onItemClickListener != null) {
            onItemClickListener!!.onItemClick(*itemData)
        }
    }

    fun onPageEnd(vararg itemData: Any) {
        if (onPageEndListener != null) {
            onPageEndListener!!.onPageEnd(*itemData)
        }
    }

    override fun onClick(v: View) {

    }

    interface OnItemClickListener {
        fun onItemClick(vararg itemData: Any)
    }

    interface OnPageEndListener {
        fun onPageEnd(vararg itemData: Any)
    }


}


