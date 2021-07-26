/*
 * @copyright : ToXSL Technologies Pvt. Ltd. < www.toxsl.com >
 * @author     : Shiv Charan Panjeta < shiv@toxsl.com >
 * All Rights Reserved.
 * Proprietary and confidential :  All information contained herein is, and remains
 * the property of ToXSL Technologies Pvt. Ltd. and its partners.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.handlUser.app.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.handlUser.app.R
import com.handlUser.app.databinding.FragmentFilterBinding
import com.handlUser.app.databinding.FragmentPromosBinding
import com.handlUser.app.model.FilterData
import com.handlUser.app.utils.ClickHandler
import com.handlUser.app.utils.Const


class FilterFragment : BaseFragment(), ClickHandler {

    private var binding: FragmentFilterBinding? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (binding == null)
            binding = DataBindingUtil.inflate(
                    inflater, R.layout.fragment_filter, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    private fun initUI() {
        binding!!.handleClick = this
        baseActivity!!.setToolbar(title = getString(R.string.filter), ContextCompat.getColor(baseActivity!!, R.color.screen_bg))
        resetData()

    }

    private fun resetData() {

        binding!!.providerCB.isChecked = false
        binding!!.reviewCB.isChecked = false
        binding!!.nearestCB.isChecked = false
        binding!!.lthCB.isChecked = false
        binding!!.htlCB.isChecked = false
        baseActivity!!.store?.setInt("filter", 0)
    }

    override fun onHandleClick(view: View) {
        when (view.id) {
            R.id.providerCB, R.id.providerTV -> {
                binding!!.reviewCB.isChecked = false
                binding!!.nearestCB.isChecked = false
                binding!!.lthCB.isChecked = false
                binding!!.htlCB.isChecked = false
            }
            R.id.reviewCB, R.id.reviewTV -> {
                binding!!.providerCB.isChecked = false
                binding!!.nearestCB.isChecked = false
                binding!!.lthCB.isChecked = false
                binding!!.htlCB.isChecked = false
            }
            R.id.nearestCB, R.id.nearestTV -> {
                binding!!.providerCB.isChecked = false
                binding!!.reviewCB.isChecked = false
                binding!!.lthCB.isChecked = false
                binding!!.htlCB.isChecked = false
            }
            R.id.lthCB, R.id.lthTV -> {
                binding!!.providerCB.isChecked = false
                binding!!.reviewCB.isChecked = false
                binding!!.nearestCB.isChecked = false
                binding!!.htlCB.isChecked = false
            }
            R.id.htlCB, R.id.htlTV -> {
                binding!!.providerCB.isChecked = false
                binding!!.reviewCB.isChecked = false
                binding!!.nearestCB.isChecked = false
                binding!!.lthCB.isChecked = false
            }
            R.id.clearTV -> {
                resetData()
            }
            R.id.doneTV -> {
                var filter = 0
                when {
                    binding!!.nearestCB.isChecked -> {
                        filter = Const.NEAR_BY
                    }
                    binding!!.providerCB.isChecked -> {
                        filter = Const.MOST_POPULAR
                    }
                    binding!!.reviewCB.isChecked -> {
                        filter = Const.HIGHEST_REVIEW
                    }
                    binding!!.lthCB.isChecked -> {
                        filter = Const.PRICE_LOW_TO_HIGH
                    }
                    binding!!.htlCB.isChecked -> {
                        filter = Const.PRICE_HIGH_TO_LOW
                    }
                }
                baseActivity!!.store?.setInt("filter", filter)
                baseActivity!!.onBackPressed()
            }
        }
    }
}
