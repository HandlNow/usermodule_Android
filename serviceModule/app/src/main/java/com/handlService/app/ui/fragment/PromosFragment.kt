/*
 * @copyright : ToXSL Technologies Pvt. Ltd. < www.toxsl.com >
 * @author     : Shiv Charan Panjeta < shiv@toxsl.com >
 * All Rights Reserved.
 * Proprietary and confidential :  All information contained herein is, and remains
 * the property of ToXSL Technologies Pvt. Ltd. and its partners.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.handlService.app.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.handlService.app.R
import com.handlService.app.databinding.FragmentPromosBinding
import com.handlService.app.ui.activity.MainActivity
import com.handlService.app.utils.ClickHandler


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
        baseActivity!!.setToolbar(title = getString(R.string.promos), screenBg = ContextCompat.getColor(baseActivity!!, R.color.White))
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.home_menu, menu)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.homeMB) {
            (baseActivity as MainActivity).openDrawer()
        }
        return super.onOptionsItemSelected(item)
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
                startActivity(Intent.createChooser(sendIntent, ""))
            }
        }
    }
}
