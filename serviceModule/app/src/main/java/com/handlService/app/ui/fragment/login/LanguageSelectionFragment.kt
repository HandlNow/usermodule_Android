/*
 * @copyright : ToXSL Technologies Pvt. Ltd. < www.toxsl.com >
 * @author     : Shiv Charan Panjeta < shiv@toxsl.com >
 * All Rights Reserved.
 * Proprietary and confidential :  All information contained herein is, and remains
 * the property of ToXSL Technologies Pvt. Ltd. and its partners.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.handlService.app.ui.fragment.login

import android.os.Bundle
import android.view.*
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.handlService.app.R
import com.handlService.app.databinding.FragmentLangSelectionBinding
import com.handlService.app.ui.extensions.replaceFragment
import com.handlService.app.ui.fragment.BaseFragment
import com.handlService.app.utils.ClickHandler


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
        baseActivity!!.setToolbar("",  ContextCompat.getColor(baseActivity!!, R.color.White),false)
        binding!!.handleClick = this

    }

    override fun onHandleClick(view: View) {
        when (view.id) {
            R.id.otherLangTV, R.id.engLangTV -> baseActivity!!.replaceFragment(SelectionFragment())
        }
    }
}
