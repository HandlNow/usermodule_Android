/*
 * @copyright : ToXSL Technologies Pvt. Ltd. < www.toxsl.com >
 * @author     : Shiv Charan Panjeta < shiv@toxsl.com >
 * All Rights Reserved.
 * Proprietary and confidential :  All information contained herein is, and remains
 * the property of ToXSL Technologies Pvt. Ltd. and its partners.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.handlUser.app.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.handlUser.app.R
import com.handlUser.app.databinding.FragmentPromosBinding
import com.handlUser.app.utils.ClickHandler


class PromosFragment : BaseFragment(), ClickHandler {

    private var binding: FragmentPromosBinding? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (binding == null)
            binding = DataBindingUtil.inflate(
                    inflater, R.layout.fragment_promos, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    private fun initUI() {
        binding!!.handleClick = this
        baseActivity!!.setToolbar(title = getString(R.string.promos), ContextCompat.getColor(baseActivity!!, R.color.screen_bg))
    }


    override fun onHandleClick(view: View) {
        when (view.id) {
            R.id.shareTV -> {
                val sendIntent: Intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, baseActivity!!.getString(R.string.your_friend) + baseActivity!!.getProfileData()!!.fullName + baseActivity!!.getString(R.string.invited_you)
                            + baseActivity!!.getString(R.string.app_name) + baseActivity!!.getString(R.string.use_code)
                            + baseActivity!!.getProfileData()!!.referralCode + baseActivity!!.getString(R.string.promo_condition))
                    type = "text/plain"
                }
                startActivity(Intent.createChooser(sendIntent, getString(R.string.share_link)))
            }
        }
    }
}
