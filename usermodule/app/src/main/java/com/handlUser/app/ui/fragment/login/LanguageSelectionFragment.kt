/*
 * @copyright : ToXSL Technologies Pvt. Ltd. < www.toxsl.com >
 * @author     : Shiv Charan Panjeta < shiv@toxsl.com >
 * All Rights Reserved.
 * Proprietary and confidential :  All information contained herein is, and remains
 * the property of ToXSL Technologies Pvt. Ltd. and its partners.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.handlUser.app.ui.fragment.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.handlUser.app.R
import com.handlUser.app.databinding.DialogNumberBinding
import com.handlUser.app.databinding.FragmentLangSelectionBinding
import com.handlUser.app.databinding.FragmentNotificationsBinding
import com.handlUser.app.ui.adapter.AdapterAddressHouses
import com.handlUser.app.ui.adapter.AdapterDialogNumber
import com.handlUser.app.ui.extensions.createArrayList
import com.handlUser.app.ui.extensions.replaceFragment
import com.handlUser.app.ui.fragment.BaseFragment
import com.handlUser.app.utils.ClickHandler


class LanguageSelectionFragment : BaseFragment(), ClickHandler {


    private var binding: FragmentLangSelectionBinding? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (binding == null)
            binding = DataBindingUtil.inflate(
                    inflater, R.layout.fragment_lang_selection, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    private fun initUI() {
        baseActivity!!.setToolbar("", ContextCompat.getColor(baseActivity!!, R.color.White), false)
        binding!!.handleClick = this
    }



    override fun onHandleClick(view: View) {
        when (view.id) {
            R.id.otherLangTV, R.id.engLangTV -> baseActivity!!.replaceFragment(ShareLocationFragment())
        }
    }
}
