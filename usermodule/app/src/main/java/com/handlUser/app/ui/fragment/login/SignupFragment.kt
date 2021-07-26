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
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.google.gson.Gson
import com.handlUser.app.R
import com.handlUser.app.databinding.FragmentSignupBinding
import com.handlUser.app.model.ProfileData
import com.handlUser.app.ui.extensions.checkString
import com.handlUser.app.ui.extensions.handleMessageForServer
import com.handlUser.app.ui.extensions.replaceFragment
import com.handlUser.app.ui.extensions.replaceFragmentWithoutStack
import com.handlUser.app.ui.fragment.BaseFragment
import com.handlUser.app.utils.ClickHandler
import com.handlUser.app.utils.Const
import com.toxsl.restfulClient.api.Api3Params
import org.json.JSONObject


class SignupFragment : BaseFragment(), ClickHandler {

    private var binding: FragmentSignupBinding? = null
    private var isShow: Boolean = false
    private var isPassShow: Boolean = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (binding == null)
            binding = DataBindingUtil.inflate(
                    inflater, R.layout.fragment_signup, container, false)
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

    }


    override fun onHandleClick(view: View) {
        when (view.id) {
            R.id.signupTV -> {
                if (validation()) {
                    apiSignupHit()

                }
            }
            R.id.agreeTV -> {
                val bundle = Bundle()
                bundle.putInt("type", Const.TERMS)
                baseActivity!!.replaceFragment(LegalFragment(), bundle)
            }
            R.id.passshowHideIV -> {
                if (isShow) {
                    binding!!.passET.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
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
                    binding!!.conPassET.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
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
            binding!!.firstNameET.checkString().isEmpty() -> showToast(getString(R.string.please_enter_first_name))
            binding!!.lastNameET.checkString().isEmpty() -> showToast(getString(R.string.please_enter_last_name))
            binding!!.emailET.checkString().isEmpty() -> showToast(getString(R.string.please_enter_email_))
            !baseActivity!!.isValidMail(binding!!.emailET.checkString()) -> showToast(getString(R.string.please_enter_valid_email_))
            binding!!.passET.checkString().isEmpty() -> showToast(getString(R.string.please_enter_password))
            binding!!.passET.checkString().length < Const.EIGHT -> showToast(getString(R.string.please_enter_valid_password))
            binding!!.conPassET.text.toString().isEmpty() -> showToast(getString(R.string.please_enter_confirm_password))
            binding!!.passET.text.toString() != binding!!.conPassET.text.toString() -> showToast(getString(R.string.password_does_match))
            !binding!!.termCB.isChecked -> showToast(getString(R.string.please_accept_term_cnd))
            else -> return true
        }
        return false
    }

    private fun apiSignupHit() {
        val param = Api3Params()
        param.put("User[first_name]", binding!!.firstNameET.checkString())
        param.put("User[last_name]", binding!!.lastNameET.checkString())
        param.put("User[email]", binding!!.emailET.checkString())
        val call = api!!.apiSignUpCheck(param.getServerHashMap())
        restFullClient!!.sendRequest(call, this)
    }

    override fun onSyncSuccess(responseCode: Int, responseMessage: String, responseUrl: String, response: String?) {
        super.onSyncSuccess(responseCode, responseMessage, responseUrl, response)
        try {
            if (responseUrl.contains(Const.API_SIGN_UP_CHECK)) {
                if (responseCode == Const.STATUS_OK) {
                    val bundle = Bundle()
                    val profileData = ProfileData()
                    profileData.firstName = binding!!.firstNameET.checkString()
                    profileData.lastName = binding!!.lastNameET.checkString()
                    profileData.email = binding!!.emailET.checkString()
                    profileData.pass = binding!!.passET.checkString()
                    profileData.referralCode = binding!!.referalET.checkString()
                    bundle.putParcelable("data", profileData)
                    baseActivity!!.replaceFragment(VerificationFragment(), bundle)
                }
            }
        } catch (e: Exception) {
            baseActivity!!.handleException(e)
        }
    }

}
