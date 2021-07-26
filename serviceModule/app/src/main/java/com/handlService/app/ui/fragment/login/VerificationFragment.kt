/*
 * @copyright : ToXSL Technologies Pvt. Ltd. < www.toxsl.com >
 * @author     : Shiv Charan Panjeta < shiv@toxsl.com >
 * All Rights Reserved.
 * Proprietary and confidential :  All information contained herein is, and remains
 * the property of ToXSL Technologies Pvt. Ltd. and its partners.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.handlService.app.ui.fragment.login

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.google.gson.Gson
import com.handlService.app.R
import com.handlService.app.databinding.FragmentVerificationBinding
import com.handlService.app.model.ProfileData
import com.handlService.app.ui.activity.BaseActivity
import com.handlService.app.ui.extensions.replaceFragment
import com.handlService.app.ui.extensions.replaceFragmentWithoutStackWithoutBundle
import com.handlService.app.ui.fragment.BaseFragment
import com.handlService.app.utils.ClickHandler
import com.handlService.app.utils.Const
import com.toxsl.restfulClient.api.Api3Params
import com.toxsl.restfulClient.api.extension.handleException
import org.json.JSONException
import org.json.JSONObject


class VerificationFragment : BaseFragment(), ClickHandler, BaseActivity.PermCallback {

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

    private fun initUI() {
        binding!!.handleClick = this
        binding!!.dotsI.dot3.setImageResource(R.drawable.circle_dark_blue)
        baseActivity!!.setToolbar(baseActivity!!.getString(R.string.welcom), ContextCompat.getColor(baseActivity!!, R.color.screen_bg))
        setHasOptionsMenu(true)

        binding!!.pullToRefresh.setOnRefreshListener {
            hitCheckApi()
            binding!!.pullToRefresh.isRefreshing = false
        }
    }

    private fun hitCheckApi() {
        val params = Api3Params()
        params.put("DeviceDetail[device_token]", baseActivity!!.getDeviceToken())
        params.put("DeviceDetail[device_type]", Const.ANDROID)
        params.put("DeviceDetail[device_name]", Build.MODEL)
        val call = api!!.apiCheck(params.getServerHashMap())
        restFullClient?.sendRequest(call, this)

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_logout, menu)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.logoutMB) {
            baseActivity!!.logOut()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        hitCheckApi()
    }

    private fun checkData() {

        val isProofVerify = baseActivity!!.getProfileData()!!.isProofVerify
        val isImageVerify = baseActivity!!.getProfileData()!!.isImageVerify

        if (isProofVerify == Const.IS_VERIFIED) {
            binding!!.cardTV.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.ic_tick, 0)
        } else if (isProofVerify == Const.IS_NOT_VERIFIED) {
            binding!!.cardTV.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.ic_next_page, 0)
        } else if (isProofVerify == Const.IS_VERIFIED_PENDING) {
            binding!!.cardTV.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.ic_timer_gry, 0)
        }

        if (isImageVerify == Const.IS_VERIFIED) {
            binding!!.photoTV.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.ic_tick, 0)
        } else if (isImageVerify == Const.IS_NOT_VERIFIED) {
            binding!!.photoTV.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.ic_next_page, 0)
        } else if (isImageVerify == Const.IS_VERIFIED_PENDING) {
            binding!!.photoTV.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.ic_timer_gry, 0)
        }

        if (isImageVerify == Const.IS_VERIFIED && isProofVerify == Const.IS_VERIFIED) {
            baseActivity!!.setToolbar(baseActivity!!.getString(R.string.hi) + baseActivity!!.getProfileData()!!.firstName + baseActivity!!.getString(R.string.welcome_), ContextCompat.getColor(baseActivity!!, R.color.screen_bg))
            binding!!.continueTV.visibility = View.VISIBLE
            binding!!.passwordTagTV.visibility = View.GONE
            binding!!.passwordTag2TV.text = baseActivity!!.getString(R.string.status_approved)
            binding!!.descTV.visibility = View.VISIBLE
            binding!!.titleTV.visibility = View.VISIBLE
            binding!!.descTV.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_good_to_go, 0, 0, 0)
        } else if (isImageVerify == Const.IS_VERIFIED_PENDING || isProofVerify == Const.IS_VERIFIED_PENDING) {
            baseActivity!!.setToolbar(baseActivity!!.getString(R.string.hi) + baseActivity!!.getProfileData()!!.firstName + baseActivity!!.getString(R.string.welcome_), ContextCompat.getColor(baseActivity!!, R.color.screen_bg))
            binding!!.logoIV.visibility = View.GONE
            binding!!.continueTV.visibility = View.GONE
            binding!!.passwordTagTV.visibility = View.GONE
            binding!!.passwordTag2TV.text = baseActivity!!.getString(R.string.status_wait_for_an_approval)
            binding!!.descTV.visibility = View.VISIBLE
            binding!!.titleTV.visibility = View.VISIBLE
            binding!!.descTV.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_documnents, 0, 0, 0)
            binding!!.descTV.text = baseActivity!!.getString(R.string.thankyou_for_submitting_your_documents)
            binding!!.titleTV.text = baseActivity!!.getString(R.string.please_wait_while_out_team_reviewa_your_information_and_approves_it)
        } else {
            binding!!.continueTV.visibility = View.GONE
            binding!!.passwordTag2TV.text = baseActivity!!.getString(R.string.status_click_on_the_arrow_to_submit)
            binding!!.passwordTagTV.visibility = View.VISIBLE
            binding!!.descTV.visibility = View.GONE
            binding!!.titleTV.visibility = View.GONE
        }
    }

    override fun onHandleClick(view: View) {
        when (view.id) {
            R.id.cardTV -> {
                if (baseActivity!!.getProfileData()!!.isProofVerify == Const.IS_VERIFIED) {
                    showToastOne(getString(R.string.image_Vreify))
                } else {
                    baseActivity!!.replaceFragment(ICardInstructionFragment())
                }
            }
            R.id.photoTV -> {

                if (baseActivity!!.getProfileData()!!.isImageVerify == Const.IS_VERIFIED) {
                    showToastOne(getString(R.string.image_Vreify))
                } else {
                    if (baseActivity!!.checkPermissions(arrayOf(android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE), Const.IMAGE_PERMISSION, this)) {
                        chooseImagePicker(Const.IMAGE_PERMISSION, false)
                    }
                }

            }
            R.id.continueTV -> {
                if (baseActivity!!.getProfileData()!!.isImageVerify == Const.IS_VERIFIED && baseActivity!!.getProfileData()!!.isProofVerify == Const.IS_VERIFIED) {
                    baseActivity!!.replaceFragmentWithoutStackWithoutBundle(VerifiedFragment())
                }
            }
        }
    }


    override fun permGranted(resultCode: Int) {
        chooseImagePicker(Const.IMAGE_PERMISSION, false)

    }

    override fun permDenied(resultCode: Int) {


    }

    override fun onImageSelected(uri: Uri?, requestCode: Int) {
        super.onImageSelected(uri, requestCode)
        if (requestCode == Const.IMAGE_PERMISSION) {
            val bundle = Bundle()
            bundle.putBoolean("is_card", false)
            bundle.putString("file", uri.toString())
            baseActivity!!.replaceFragment(ICardDetailFragment(), bundle)
        }
    }

    override fun onSyncSuccess(responseCode: Int, responseMessage: String, responseUrl: String, response: String?) {
        try {
            val respObject = JSONObject(response!!)
            if (responseUrl.contains(Const.API_CHECK)) {
                val data = Gson().fromJson<ProfileData>(respObject.getJSONObject("detail").toString(), ProfileData::class.java)
                baseActivity!!.setProfileData(data)
                restFullClient!!.setLoginStatus(data.accessToken)
                checkData()
            }

        } catch (e: JSONException) {
            handleException(e)
        }
    }

}
