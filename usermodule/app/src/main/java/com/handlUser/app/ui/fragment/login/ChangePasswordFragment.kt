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
import com.handlUser.app.databinding.FragmentChangePasswordBinding
import com.handlUser.app.ui.extensions.checkString
import com.handlUser.app.ui.fragment.BaseFragment
import com.handlUser.app.utils.ClickHandler
import com.handlUser.app.utils.Const
import com.toxsl.restfulClient.api.Api3Params
import org.json.JSONObject


class ChangePasswordFragment : BaseFragment(), ClickHandler {

    private var binding: FragmentChangePasswordBinding? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (binding == null)
            binding = DataBindingUtil.inflate(
                    inflater, R.layout.fragment_change_password, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    private fun initUI() {
        binding!!.handleClick = this
        baseActivity!!.setToolbar(title = getString(R.string.change_password))
    }


    override fun onHandleClick(view: View) {
        when (view.id) {
            R.id.saveTV -> {
                if (valid()) {
                    apiHitChange()
                }
            }
        }
    }

    private fun valid(): Boolean {
        when {
            binding!!.passET.text.toString().isEmpty() -> showToast(getString(R.string.please_enter_newpassword))
            binding!!.passET.checkString().length < Const.EIGHT -> showToast(getString(R.string.please_enter_valid_password))
            binding!!.conPassET.text.toString().isEmpty() -> showToast(getString(R.string.please_enter_confirm_password))
            binding!!.passET.text.toString() != binding!!.conPassET.text.toString() -> showToast(getString(R.string.password_does_match))
            else -> return true
        }
        return false
    }

    private fun apiHitChange() {
        val param = Api3Params()
        param.put("User[newPassword]", binding!!.passET.text.toString())
        val call = api!!.apiChangePassword(param.getServerHashMap())
        restFullClient!!.sendRequest(call, this)
    }

    override fun onSyncSuccess(responseCode: Int, responseMessage: String, responseUrl: String, response: String?) {
        super.onSyncSuccess(responseCode, responseMessage, responseUrl, response)
        try {
            val jsonObjects = JSONObject(response!!)
            if (responseUrl.contains(Const.API_CHANGE_PASSWORD)) {
                if (responseCode == Const.STATUS_OK) {
                    if (jsonObjects.has("message")) {
                        showToastOne(jsonObjects.getString("message"))
                    }
                    baseActivity!!.setProfileData(null)
                    restFullClient!!.setLoginStatus(null)
                    baseActivity!!.gotoLoginSignUpActivity()
                }
            }
        } catch (e: Exception) {
            baseActivity!!.handleException(e)
        }
    }

}
