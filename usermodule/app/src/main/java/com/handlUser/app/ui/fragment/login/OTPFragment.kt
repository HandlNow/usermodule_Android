/*
 * @copyright : ToXSL Technologies Pvt. Ltd. < www.toxsl.com >
 * @author     : Shiv Charan Panjeta < shiv@toxsl.com >
 * All Rights Reserved.
 * Proprietary and confidential :  All information contained herein is, and remains
 * the property of ToXSL Technologies Pvt. Ltd. and its partners.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.handlUser.app.ui.fragment.login

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.databinding.DataBindingUtil
import com.handlUser.app.R
import com.handlUser.app.databinding.FragmentOtpVerificationBinding
import com.handlUser.app.ui.extensions.checkString
import com.handlUser.app.ui.extensions.replaceFragmentWithoutStackWithoutBundle
import com.handlUser.app.ui.fragment.BaseFragment
import com.handlUser.app.ui.fragment.PaymentMethodListFragment
import com.handlUser.app.utils.ClickHandler
import com.handlUser.app.utils.Const
import com.handlUser.app.utils.TimeUtils
import com.toxsl.restfulClient.api.Api3Params
import kotlinx.android.synthetic.main.fragment_otp_verification.*
import org.json.JSONObject


class OTPFragment : BaseFragment(), ClickHandler {

    private var binding: FragmentOtpVerificationBinding? = null
    private var contact = ""
    private var otp: Int? = null
    private var countryCode = ""

    private var handler: Handler? = null
    private var runnable: Runnable? = null
    private var TOTAL_TIME_WAIT = 1 * 15

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (binding == null)
            binding = DataBindingUtil.inflate(
                    inflater, R.layout.fragment_otp_verification, container, false)
        return binding!!.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            contact = it.getString("contact", "")
            otp = it.getInt("otp", 0)
            countryCode = it.getString("code", "")
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    private fun initUI() {
        binding!!.handleClick = this
        baseActivity!!.setToolbar(baseActivity!!.getString(R.string.otp_))
        otpEditTextAutomation()
        showToastOne("Your Otp is " + otp)
    }

    private fun otpEditTextAutomation() {

        istET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(charSequence: CharSequence, start: Int, before: Int, count: Int) {
                if (charSequence.length == 1) {
                    secondET.requestFocus()
                }
            }


            override fun afterTextChanged(s: Editable) {

            }
        })

        secondET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

                if (charSequence.length == 1) {

                    thirdET.requestFocus()
                } else if (charSequence.isEmpty()) {
                    istET.requestFocus()

                    istET.setSelection(istET.length())
                }
            }


            override fun afterTextChanged(editable: Editable) {

            }
        })

        thirdET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

                if (charSequence.length == 1) {

                    forthET.requestFocus()
                } else if (charSequence.isEmpty()) {

                    secondET.requestFocus()
                    secondET.setSelection(secondET.length())
                }

            }

            override fun afterTextChanged(editable: Editable) {

            }
        })
        forthET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                if (charSequence.length == 1) {

                    val imm = baseActivity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(baseActivity!!.currentFocus?.windowToken, 0)


                } else if (charSequence.isEmpty()) {

                    thirdET.requestFocus()
                    thirdET.setSelection(thirdET.length())
                }
            }

            override fun afterTextChanged(editable: Editable) {

            }
        })
    }

    override fun onHandleClick(view: View) {
        when (view.id) {
            R.id.continueTV -> {
                if (isValidate()) {
                    apiHitOtp()
                }
            }
            R.id.resendTV -> {
                binding!!.istET.setText("")
                binding!!.secondET.setText("")
                binding!!.thirdET.setText("")
                binding!!.forthET.setText("")
                binding!!.istET.requestFocus()
                apiResendOtp()
                TOTAL_TIME_WAIT = 1 * 15
                timmerRun()
            }
        }
    }

    private fun isValidate(): Boolean {
        when {
            binding!!.istET.checkString().isBlank()
                    || binding!!.secondET.checkString().isBlank()
                    || binding!!.thirdET.checkString().isBlank()
                    || binding!!.forthET.checkString().isBlank() -> baseActivity!!.showToastOne(baseActivity!!.getString(R.string.Please_enter_complete_otp))
            else -> {
                return true
            }
        }
        return false
    }

    private fun apiHitOtp() {
        val param = Api3Params()
        param.put("User[contact_no]", contact)
        param.put("User[dial_code]", countryCode)
        param.put("User[otp]", binding!!.istET.checkString() + binding!!.secondET.checkString() + binding!!.thirdET.checkString() + binding!!.forthET.checkString())
        param.put("AccessToken[device_token]", baseActivity!!.getDeviceToken())
        param.put("AccessToken[device_type]", Const.ANDROID)
        param.put("AccessToken[device_name]", Build.MODEL)
        val call = api!!.apiVerifyOtp(param.getServerHashMap())
        restFullClient!!.sendRequest(call, this)
    }

    override fun onSyncSuccess(responseCode: Int, responseMessage: String, responseUrl: String, response: String?) {
        super.onSyncSuccess(responseCode, responseMessage, responseUrl, response)
        try {
            val jsonObjects = JSONObject(response!!)
            if (responseUrl.contains(Const.API_VERIFY_OTP)) {
                if (responseCode == Const.STATUS_OK) {
                    if (jsonObjects.has("error")) {
                        showToastOne(jsonObjects.optString("error"))
                    } else {
                        showToastOne(getString(R.string.login_sucess))
                        restFullClient!!.setLoginStatus(baseActivity!!.getProfileData()!!.accessToken)
                        if (baseActivity!!.getProfileData()!!.isUserAddress && baseActivity!!.getProfileData()!!.isUserPayment) {
                            baseActivity!!.gotoMainActivity()
                        } else if (baseActivity!!.getProfileData()!!.isUserAddress) {
                            baseActivity!!.replaceFragmentWithoutStackWithoutBundle(PaymentMethodAdd(), R.id.container)
                        } else {
                            baseActivity!!.replaceFragmentWithoutStackWithoutBundle(RegisterHouseFragment(), R.id.container)
                        }
                    }
                }
            } else if (responseUrl.contains(Const.API_RESEND_OTP)) {
                if (responseCode == Const.STATUS_OK) {
                    val otp = jsonObjects.getJSONObject("detail").optString("otp")
                    showToastOne("Your Otp is " + otp)
                }
            }
        } catch (e: Exception) {
            baseActivity!!.handleException(e)
        }
    }


    private fun apiResendOtp() {
        val param = Api3Params()
        param.put("User[contact_no]", contact)
        val call = api!!.apiOtpResend(param.getServerHashMap())
        restFullClient!!.sendRequest(call, this)
    }

    @SuppressLint("SetTextI18n")
    private fun timmerRun() {
        if (handler == null) {
            handler = Handler()
            runnable = Runnable {
                if (TOTAL_TIME_WAIT == 0) {
                    resendTV.text = getString(R.string.resend_code)
                    resendTV.isEnabled = true
                } else {
                    resendTV.text =getString(R.string.resend_code_in) .plus(" ") .plus(TimeUtils.formatSeconds(TOTAL_TIME_WAIT))
                    TOTAL_TIME_WAIT--
                    handler!!.postDelayed(runnable!!, 1000)
                    resendTV.isEnabled = false

                }
            }
            handler!!.post(runnable!!)
        } else {
            handler!!.removeCallbacks(runnable!!)
            handler = null
            timmerRun()
        }
    }

    override fun onResume() {
        baseActivity!!.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        super.onResume()
        timmerRun()
    }

    override fun onPause() {
        super.onPause()
        if (handler != null) {
            handler!!.removeCallbacks(runnable!!)
            handler = null
        }
    }

}
