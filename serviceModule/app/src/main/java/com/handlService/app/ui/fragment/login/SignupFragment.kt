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
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.google.gson.Gson
import com.handlService.app.R
import com.handlService.app.databinding.FragmentSignupBinding
import com.handlService.app.model.ProfileData
import com.handlService.app.ui.extensions.*
import com.handlService.app.ui.fragment.BaseFragment
import com.handlService.app.utils.ClickHandler
import com.handlService.app.utils.Const
import com.toxsl.restfulClient.api.Api3Params
import org.json.JSONObject


class SignupFragment : BaseFragment(), ClickHandler {

    private var isShow: Boolean = false
    private var isPassShow: Boolean = false
    private var binding: FragmentSignupBinding? = null
    private var countryCode = ""


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (binding == null)
            binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_signup, container, false
            )
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    private fun initUI() {
        baseActivity!!.setToolbar(getString(R.string.signup))
        binding!!.handleClick = this
        binding!!.dotsI.dot1.setImageResource(R.drawable.circle_dark_blue)
        binding!!.codeET.resetToDefaultCountry()
        binding!!.codeET.setOnCountryChangeListener {
            countryCode = binding!!.codeET.selectedCountryCode
            setPhoneNumberLength(binding!!.phoneET, countryCode)
        }
        countryCode = binding!!.codeET.selectedCountryCode

        setPhoneNumberLength(binding!!.phoneET, countryCode)
    }

    override fun onHandleClick(view: View) {
        when (view.id) {
            R.id.signupTV -> {
                if (validation()) {
                    val param = Api3Params()
                    param.put("User[first_name]", binding!!.firstNameET.checkString())
                    param.put("User[last_name]", binding!!.lastNameET.checkString())
                    param.put("User[email]", binding!!.emailET.checkString())
                    param.put("User[password]", binding!!.passET.checkString())
                    param.put("User[contact_no]", binding!!.phoneET.checkString())
                    param.put("User[referral_code]", binding!!.referalET.checkString())
                    param.put("User[country_code]", "+" + countryCode)
                    val call = api!!.apiSignUp(param.getServerHashMap())
                    restFullClient!!.sendRequest(call, this)

                }
            }
            R.id.agreeTV -> {
                val bundle = Bundle()
                bundle.putInt("type", Const.TERMS)
                baseActivity!!.replaceFragment(LegalFragment(), bundle)
            }
            R.id.passshowHideIV -> {
                if (isShow) {
                    binding!!.passET.inputType =
                        InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                    binding!!.passshowHideIV.setImageResource(R.drawable.ic_baseline_visibility_off_24)
                } else {
                    binding!!.passET.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                    binding!!.passshowHideIV.setImageResource(R.drawable.ic_baseline_visibility_24)
                }
                binding!!.passET.setSelection(binding!!.passET.length())
                isShow = !isShow
                binding!!.passET.typeface = binding!!.emailET.typeface
            }
            R.id.conPasshowHideIV -> {
                if (isPassShow) {
                    binding!!.conPassET.inputType =
                        InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                    binding!!.conPasshowHideIV.setImageResource(R.drawable.ic_baseline_visibility_off_24)
                } else {
                    binding!!.conPassET.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                    binding!!.conPasshowHideIV.setImageResource(R.drawable.ic_baseline_visibility_24)
                }
                binding!!.conPassET.setSelection(binding!!.conPassET.length())

                isPassShow = !isPassShow
                binding!!.conPassET.typeface = binding!!.emailET.typeface
            }

        }
    }

    private fun validation(): Boolean {
        when {
            binding!!.firstNameET.isBlank() -> showToast(getString(R.string.please_enter_first_name))
            binding!!.lastNameET.isBlank() -> showToast(getString(R.string.please_enter_last_name))
            binding!!.emailET.isBlank() -> showToast(getString(R.string.please_enter_email_))
            !baseActivity!!.isValidMail(binding!!.emailET.checkString()) -> showToast(getString(R.string.please_enter_valid_email_))
            binding!!.phoneET.isBlank() -> showToastOne(getString(R.string.please_enter_mobile_number))
            binding!!.phoneET.checkString().length < binding!!.phoneET.maxLength!! -> showToast(
                getString(R.string.please_enter_valid_mobile_number)
            )
            binding!!.passET.isBlank() -> showToast(getString(R.string.please_enter_password))
            binding!!.passET.checkString().length < Const.EIGHT -> showToast(getString(R.string.please_enter_valid_password))
            binding!!.conPassET.isBlank() -> showToast(getString(R.string.please_enter_confirm_password))
            binding!!.passET.checkString() != binding!!.conPassET.checkString() -> showToast(
                getString(R.string.password_does_match)
            )
            !binding!!.termCB.isChecked -> showToast(getString(R.string.please_accept_term_cnd))
            else -> return true
        }
        return false
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
            if (responseUrl.contains(Const.API_SIGN_UP)) {
                if (responseCode == Const.STATUS_OK) {
                    if (!jsonObjects.has("detail")) {
                        if (jsonObjects.handleMessageForServer("full_name").isNotEmpty()) {
                            showToastOne(jsonObjects.handleMessageForServer("full_name"))
                        }
                    } else {
                        val data = Gson().fromJson<ProfileData>(
                            jsonObjects.getJSONObject("detail").toString(), ProfileData::class.java
                        )
                        baseActivity!!.setProfileData(data)
                        val bundle = Bundle()
                        bundle.putString("contact", data.contactNo)
                        bundle.putInt("otp", data.otp)
                        bundle.putString("code", countryCode)
                        baseActivity!!.replaceFragmentWithoutStack(OTPFragment(), bundle)

                    }
                }
            }
        } catch (e: Exception) {
            baseActivity!!.handleException(e)
        }
    }

}
