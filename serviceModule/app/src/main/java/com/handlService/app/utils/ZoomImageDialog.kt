/*
 * @copyright : ToXSL Technologies Pvt. Ltd. < www.toxsl.com >
 * @author     : Shiv Charan Panjeta < shiv@toxsl.com >
 * All Rights Reserved.
 * Proprietary and confidential :  All information contained herein is, and remains
 * the property of ToXSL Technologies Pvt. Ltd. and its partners.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.handlService.app.utils

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.handlService.app.R
import com.handlService.app.databinding.DialogZoomImageFragmentBinding
import com.handlService.app.ui.activity.BaseActivity
import com.handlService.app.ui.extensions.loadFromUrl

class ZoomImageDialog(val url: ArrayList<String>, val pos: Int, val baseActivity: BaseActivity) : DialogFragment() {

    var binding: DialogZoomImageFragmentBinding? = null
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        isCancelable = false

        binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.dialog_zoom_image_fragment, null, false)
        return binding!!.root
    }

    override fun getTheme(): Int {
        return R.style.DialogTheme
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()

    }

    private fun initUI() {
        var count = pos
        setImage(url[count])
        setVisibility(count)

        binding!!.crossIV.setOnClickListener {
            dismiss()
        }

        binding!!.prevIV.setOnClickListener {
            if (count > 0) {
                count--
            }
            setImage(url[count])
            setVisibility(count)
        }
        binding!!.nextIV.setOnClickListener {
            if (count < url.size - 1) {
                count++
            }
            setImage(url[count])
            setVisibility(count)
        }

    }

    private fun setVisibility(count: Int) {
        setPreviousVisibility(count)
        setNextVisibility(count)

    }

    private fun setNextVisibility(count: Int) {
        if (count == url.size - 1) {
            binding!!.nextIV.visibility = View.GONE
        } else {
            binding!!.nextIV.visibility = View.VISIBLE
        }


    }

    private fun setPreviousVisibility(count: Int) {
        if (count == 0) {
            binding!!.prevIV.visibility = View.GONE
        } else {
            binding!!.prevIV.visibility = View.VISIBLE
        }


    }

    private fun setImage(file: String) {
        binding!!.imageIV.loadFromUrl(baseActivity, file)
    }
}