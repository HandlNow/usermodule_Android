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
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.handlUser.app.R
import com.handlUser.app.databinding.FragmentRebookBinding
import com.handlUser.app.ui.extensions.replaceFragment
import com.handlUser.app.utils.ClickHandler


class ReBookFragment : BaseFragment(), ClickHandler {

    private var binding: FragmentRebookBinding? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (binding == null)
            binding = DataBindingUtil.inflate(
                    inflater, R.layout.fragment_rebook, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    private fun initUI() {
        binding!!.handleClick = this
        baseActivity!!.setToolbar(title = getString(R.string.rebook), ContextCompat.getColor(baseActivity!!,R.color.White))
    }


    override fun onHandleClick(view: View) {
        when (view.id) {
            R.id.rebookTV -> {
                baseActivity!!.replaceFragment(ReBookCalendarFragment())
            }
            R.id.notrebookTV -> {
                baseActivity!!.onBackPressed()
            }
            R.id.bookanotherTV -> {

            }
        }
    }
}
