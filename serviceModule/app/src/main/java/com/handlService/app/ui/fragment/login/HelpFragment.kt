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
import androidx.databinding.DataBindingUtil
import com.handlService.app.R
import com.handlService.app.databinding.FragmentHelpBinding
import com.handlService.app.ui.activity.MainActivity
import com.handlService.app.ui.extensions.checkString
import com.handlService.app.ui.extensions.replaceFragment
import com.handlService.app.ui.fragment.BaseFragment
import com.handlService.app.ui.fragment.HomeFragment
import com.handlService.app.utils.ClickHandler
import com.handlService.app.utils.Const
import com.toxsl.restfulClient.api.Api3Params
import com.toxsl.restfulClient.api.extension.handleException
import org.json.JSONObject


class HelpFragment : BaseFragment(), ClickHandler {

    private var binding: FragmentHelpBinding? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (binding == null)
            binding = DataBindingUtil.inflate(
                    inflater, R.layout.fragment_help, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    private fun initUI() {
        binding!!.handleClick = this
        baseActivity!!.setToolbar(baseActivity!!.getString(R.string.help))
        binding!!.emailET.setText(baseActivity!!.getProfileData()!!.email)

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
            R.id.submitTV -> {
                if (validation()) {
                    hitHelpAPI()
                }
            }
        }
    }

    private fun validation(): Boolean {
        when {
            binding!!.emailET.checkString().isEmpty() -> showToast(getString(R.string.please_enter_email_))
            !baseActivity!!.isValidMail(binding!!.emailET.checkString()) -> showToast(getString(R.string.please_enter_valid_email_))
            binding!!.topicET.checkString().isEmpty() -> showToast(getString(R.string.please_enter_topic))
            binding!!.conemailET.checkString().isEmpty() -> showToast(getString(R.string.please_enter_desc))
            else -> return true
        }
        return false
    }

    private fun hitHelpAPI() {
        val param = Api3Params()
        param.put("Information[email]", binding!!.emailET.checkString())
        param.put("Information[topic]", binding!!.topicET.checkString())
        param.put("Information[description]", binding!!.conemailET.checkString())
        val call = api!!.apiHelpContactUs(param.getServerHashMap())
        restFullClient!!.sendRequest(call, this)

    }


    override fun onSyncSuccess(responseCode: Int, responseMessage: String, responseUrl: String, response: String?) {
        super.onSyncSuccess(responseCode, responseMessage, responseUrl, response)
        try {
            val jsonObject = JSONObject(response!!)
            if (responseUrl.contains(Const.API_CONTACT_US)) {
                if (responseCode == Const.STATUS_OK) {
                    showToastOne(jsonObject.optString("message"))
                }
                baseActivity!!.replaceFragment(HomeFragment())
            }
        } catch (e: Exception) {
            handleException(e)
        }
    }
}
