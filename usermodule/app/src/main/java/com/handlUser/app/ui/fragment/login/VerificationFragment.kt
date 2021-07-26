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
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.google.gson.Gson
import com.handlUser.app.R
import com.handlUser.app.databinding.FragmentVerificationBinding
import com.handlUser.app.model.ProfileData
import com.handlUser.app.ui.extensions.*
import com.handlUser.app.ui.fragment.BaseFragment
import com.handlUser.app.utils.ClickHandler
import com.handlUser.app.utils.Const
import com.toxsl.restfulClient.api.Api3Params
import org.json.JSONObject


class VerificationFragment : BaseFragment(), ClickHandler {

    private var countryCode = ""
    private var profileData: ProfileData? = null
    private var binding: FragmentVerificationBinding? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (binding == null)
            binding = DataBindingUtil.inflate(
                    inflater, R.layout.fragment_verification, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            arguments.let {
                profileData = arguments?.getParcelable<ProfileData>("data")
            }
        }
    }

    private fun initUI() {
        binding!!.handleClick = this
        binding!!.dotsI.dot2.setImageResource(R.drawable.circle_dark_blue)
        baseActivity!!.setToolbar(title = getString(R.string.verification), screenBg = ContextCompat.getColor(baseActivity!!, R.color.White), showTitle = false)
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
            R.id.registerTV -> {
                if (validation()) {
                    apiSignupHit()
                }
            }
            R.id.termsTV -> {
                val bundle = Bundle()
                bundle.putInt("type", Const.TERMS)
                baseActivity!!.replaceFragment(LegalFragment(), bundle)
            }
            R.id.policyTV -> {
                val bundle = Bundle()
                bundle.putInt("type", Const.PRIVACY)
                baseActivity!!.replaceFragment(LegalFragment(), bundle)
            }
        }
    }

    private fun validation(): Boolean {
        when {
            binding!!.phoneET.text.toString().isEmpty() -> showToastOne(getString(R.string.please_enter_mobile_number))
            binding!!.phoneET.checkString().length < binding!!.phoneET.maxLength!! -> showToast(getString(R.string.please_enter_valid_mobile_number))
            else -> return true
        }
        return false
    }

    private fun apiSignupHit() {
        val param = Api3Params()
        param.put("User[first_name]", profileData!!.firstName!!)
        param.put("User[last_name]", profileData!!.lastName!!)
        param.put("User[email]", profileData!!.email!!)
        param.put("User[password]", profileData!!.pass!!)
        param.put("User[contact_no]", binding!!.phoneET.checkString())
        param.put("User[referral_code]", profileData!!.referralCode)
        param.put("User[country_code]", "+" + countryCode)
        param.put("User[zipcode]", baseActivity!!.getAddressData()?.zipcode!!)
        param.put("User[longitude]", baseActivity!!.getAddressData()?.longitude!!)
        param.put("User[latitude]", baseActivity!!.getAddressData()?.latitude!!)
        param.put("User[city]", baseActivity!!.getAddressData()?.city!!)
        param.put("User[address]", baseActivity!!.getAddressData()?.address!!)
        val call = api!!.apiSignUp(param.getServerHashMap())
        restFullClient!!.sendRequest(call, this)
    }

    override fun onSyncSuccess(responseCode: Int, responseMessage: String, responseUrl: String, response: String?) {
        super.onSyncSuccess(responseCode, responseMessage, responseUrl, response)
        try {
            val jsonObjects = JSONObject(response!!)
            if (responseUrl.contains(Const.API_SIGN_UP)) {
                if (responseCode == Const.STATUS_OK) {
                    if (!jsonObjects.has("detail")) {
                        if (jsonObjects.handleMessageForServer("full_name").isNotEmpty()) {
                            showToastOne(jsonObjects.handleMessageForServer("full_name"))
                            baseActivity!!.onBackPressed()
                        }
                    } else {
                        val data = Gson().fromJson<ProfileData>(jsonObjects.getJSONObject("detail").toString(), ProfileData::class.java)
                        baseActivity!!.setProfileData(data)
                        val bundle = Bundle()
                        bundle.putString("contact", data.contactNo)
                        bundle.putInt("otp", data.otp!!)
                        bundle.putString("code", countryCode)
                        baseActivity!!.replaceFragmentWithoutStack(OTPFragment(), R.id.container, bundle)
                    }
                }
            }
        } catch (e: Exception) {
            baseActivity!!.handleException(e)
        }
    }

}
