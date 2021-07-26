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
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.handlUser.app.R
import com.handlUser.app.databinding.DialogRebookBinding
import com.handlUser.app.databinding.FragmentRebookCalendarBinding
import com.handlUser.app.utils.ClickHandler


class ReBookCalendarFragment : BaseFragment(), ClickHandler {

    private var cancelBinding: DialogRebookBinding? = null
    private var binding: FragmentRebookCalendarBinding? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (binding == null)
            binding = DataBindingUtil.inflate(
                    inflater, R.layout.fragment_rebook_calendar, container, false)
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
                displayDialog()
            }

        }
    }

    private fun displayDialog() {
        val mBuilder = AlertDialog.Builder(baseActivity!!, R.style.animateDialog)
        cancelBinding = DataBindingUtil.inflate(LayoutInflater.from(baseActivity), R.layout.dialog_rebook, null, false)
        mBuilder.setView(cancelBinding!!.root)
        val alertDialog = mBuilder.create()
        alertDialog.show()


    }




}
