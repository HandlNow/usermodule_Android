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
import com.handlUser.app.databinding.FragmentForgotPasswordBinding
import com.handlUser.app.ui.extensions.checkString
import com.handlUser.app.ui.fragment.BaseFragment
import com.handlUser.app.utils.ClickHandler
import com.handlUser.app.utils.Const
import com.toxsl.restfulClient.api.Api3Params
import org.json.JSONObject


class ForgotPasswordFragment : BaseFragment(), ClickHandler {
    private var binding: FragmentForgotPasswordBinding? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (binding == null)
            binding = DataBindingUtil.inflate(inflater, R.layout.fragment_forgot_password, container, false)
        return binding!!.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    private fun initUI() {
        binding!!.handleClick = this
        baseActivity!!.setToolbar(title = getString(R.string.forgot_your_password))
    }


    override fun onHandleClick(view: View) {
        when (view.id) {
            R.id.sendIV -> {
                if (validate()) {
                    apiHitForgot()
                }
            }
        }
    }

    private fun validate(): Boolean {
        when {
            binding!!.emailET.checkString().isEmpty() -> showToast(getString(R.string.please_enter_email_))
            !baseActivity!!.isValidMail(binding!!.emailET.checkString()) -> showToast(getString(R.string.please_enter_valid_email_))
            else -> return true
        }
        return false
    }

    private fun apiHitForgot() {
        val param = Api3Params()
        param.put("User[email]", binding!!.emailET.checkString())
        val call = api!!.apiForgot(param.getServerHashMap())
        restFullClient!!.sendRequest(call, this)

    }

    override fun onSyncSuccess(responseCode: Int, responseMessage: String, responseUrl: String, response: String?) {
        super.onSyncSuccess(responseCode, responseMessage, responseUrl, response)
        try {
            val jsonObject = JSONObject(response!!)
            if (responseCode == Const.STATUS_OK) {
                binding!!.emailET.text?.clear()
                showToast(jsonObject.getString("message"))
            }
        } catch (e: Exception) {
            baseActivity!!.handleException(e)
        }
    }
}
