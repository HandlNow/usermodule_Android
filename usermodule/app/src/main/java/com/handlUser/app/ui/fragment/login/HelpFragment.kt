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
import androidx.databinding.DataBindingUtil
import com.handlUser.app.R
import com.handlUser.app.databinding.FragmentHelpBinding
import com.handlUser.app.ui.extensions.checkString
import com.handlUser.app.ui.fragment.BaseFragment
import com.handlUser.app.utils.ClickHandler
import com.handlUser.app.utils.Const
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
                baseActivity!!.onBackPressed()
            }
        } catch (e: Exception) {
            handleException(e)
        }
    }

}
