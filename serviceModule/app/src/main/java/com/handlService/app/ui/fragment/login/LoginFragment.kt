/*
 * @copyright : ToXSL Technologies Pvt. Ltd. < www.toxsl.com >
 * @author     : Shiv Charan Panjeta < shiv@toxsl.com >
 * All Rights Reserved.
 * Proprietary and confidential :  All information contained herein is, and remains
 * the property of ToXSL Technologies Pvt. Ltd. and its partners.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.handlService.app.ui.fragment.login

import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.google.gson.Gson
import com.handlService.app.R
import com.handlService.app.databinding.FragmentLoginBinding
import com.handlService.app.model.ProfileData
import com.handlService.app.ui.extensions.*
import com.handlService.app.ui.fragment.BaseFragment
import com.handlService.app.utils.ClickHandler
import com.handlService.app.utils.Const
import com.toxsl.restfulClient.api.Api3Params
import org.json.JSONObject


class LoginFragment : BaseFragment(), ClickHandler {
    private var isShow: Boolean = false
    private var binding: FragmentLoginBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (binding == null)
            binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_login, container, false
            )
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    private fun initUI() {
        binding!!.handleClick = this
        baseActivity!!.setToolbar(baseActivity!!.getString(R.string.log_in))
        getSharedPref()
        binding!!.passET.inputType =
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        binding!!.passET.setSelection(binding!!.passET.length())
        binding!!.showHideIV.setImageResource(R.drawable.ic_baseline_visibility_off_24)
        binding!!.passET.typeface = binding!!.emailET.typeface
    }

    private fun setSharedPreference() {
        store!!.saveString(Const.USERNAME, binding!!.emailET.checkString())
        store!!.saveString(Const.PASSWORD, binding!!.passET.checkString())
    }

    private fun getSharedPref() {
        val user = store!!.getString(Const.USERNAME, "")
        val pass = store!!.getString(Const.PASSWORD, "")

        binding!!.emailET.setText(user)
        binding!!.passET.setText(pass)

        binding!!.checkCB.isChecked = user!! != ""
    }


    override fun onHandleClick(view: View) {
        baseActivity!!.hideSoftKeyboard()
        when (view.id) {
            R.id.loginTV -> {
                if (validation()) {
                    if (binding!!.checkCB.isChecked) {
                        setSharedPreference()
                    }
                    apiLoginHit()
                }
            }
            R.id.forgotTV -> baseActivity!!.replaceFragment(ForgotPasswordFragment())
            R.id.checkCB -> {
                when {
                    binding!!.checkCB.isChecked -> setSharedPreference()
                    else -> cleanPref()
                }
            }
            R.id.showHideIV -> {
                if (isShow) {
                    binding!!.passET.inputType =
                        InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                    binding!!.passET.setSelection(binding!!.passET.length())
                    binding!!.showHideIV.setImageResource(R.drawable.ic_baseline_visibility_off_24)
                } else {
                    binding!!.passET.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                    binding!!.passET.setSelection(binding!!.passET.length())
                    binding!!.showHideIV.setImageResource(R.drawable.ic_baseline_visibility_24)
                }
                isShow = !isShow
                binding!!.passET.typeface = binding!!.emailET.typeface
            }
        }
    }

    private fun cleanPref() {
        store!!.cleanKeyPref(Const.USERNAME)
        store!!.cleanKeyPref(Const.PASSWORD)
    }

    private fun validation(): Boolean {
        when {
            binding!!.emailET.isBlank() -> showToast(getString(R.string.please_enter_email_))
            !baseActivity!!.isValidMail(binding!!.emailET.checkString()) -> showToast(getString(R.string.please_enter_valid_email_))
            binding!!.passET.isBlank() -> showToast(getString(R.string.please_enter_password))
            binding!!.passET.getLength() < Const.EIGHT -> showToast(getString(R.string.please_enter_valid_password))
            else -> return true
        }
        return false
    }

    private fun apiLoginHit() {
        val param = Api3Params()
        param.put("LoginForm[username]", binding!!.emailET.checkString())
        param.put("LoginForm[password]", binding!!.passET.checkString())
        param.put("LoginForm[device_token]", baseActivity!!.getDeviceToken())
        param.put("LoginForm[device_type]", Const.ANDROID)
        param.put("LoginForm[device_name]", Build.MODEL)
        val call = api!!.apiLogin(param.getServerHashMap())
        restFullClient!!.sendRequest(call, this)
    }

    override fun onSyncSuccess(
        responseCode: Int,
        responseMessage: String,
        responseUrl: String,
        response: String?
    ) {
        super.onSyncSuccess(responseCode, responseMessage, responseUrl, response)
        try {
            val jsonObjects = JSONObject(response!!)
            if (responseUrl.contains(Const.API_LOGIN)) {
                if (responseCode == Const.STATUS_OK) {
                    val data = Gson().fromJson<ProfileData>(
                        jsonObjects.getJSONObject("detail").toString(),
                        ProfileData::class.java
                    )
                    if (data.stateId != 3) {
                        if (data.otpVerified == 0) {
                            val bundle = Bundle()
                            bundle.putString("contact", data.contactNo)
                            bundle.putInt(
                                "otp",
                                data.otp
                            )
                            baseActivity!!.replaceFragmentWithoutStack(OTPFragment(), bundle)

                        } else {
                            showToastOne(getString(R.string.login_sucess))
                            baseActivity!!.setProfileData(data)
                            restFullClient!!.setLoginStatus(data.accessToken)
                            baseActivity!!.gotoMainActivity()

                        }
                    } else {
                        showToastOne(baseActivity!!.getString(R.string.deactive_contact_support))
                    }
                }
            }
        } catch (e: Exception) {
            baseActivity!!.handleException(e)
        }
    }
}



