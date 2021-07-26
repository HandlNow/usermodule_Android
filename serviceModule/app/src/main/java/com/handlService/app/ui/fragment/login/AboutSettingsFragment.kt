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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.google.gson.Gson
import com.handlService.app.R
import com.handlService.app.databinding.FragmentAboutSettingBinding
import com.handlService.app.model.ProfileData
import com.handlService.app.ui.extensions.checkString
import com.handlService.app.ui.fragment.BaseFragment
import com.handlService.app.utils.ClickHandler
import com.handlService.app.utils.Const
import com.toxsl.restfulClient.api.Api3MultipartByte
import com.toxsl.restfulClient.api.Api3Params
import com.toxsl.restfulClient.api.extension.handleException
import org.json.JSONException
import org.json.JSONObject


class AboutSettingsFragment : BaseFragment(), ClickHandler {

    private var binding: FragmentAboutSettingBinding? = null
    private var isEdit = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (binding == null)
            binding = DataBindingUtil.inflate(
                    inflater, R.layout.fragment_about_setting, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    private fun initUI() {
        binding!!.handleClick = this
        baseActivity!!.setToolbar(getString(R.string.my_busi_seting), ContextCompat.getColor(baseActivity!!, R.color.White))
        binding!!.aboutET.setText(baseActivity!!.getProfileData()!!.aboutMe)

        binding!!.pullToRefresh.setOnRefreshListener {
            hitcheckAPI()
            binding!!.pullToRefresh.isRefreshing = false
        }
    }

    private fun hitcheckAPI() {
        val params = Api3Params()
        params.put("DeviceDetail[device_token]", baseActivity!!.getDeviceToken())
        params.put("DeviceDetail[device_type]", Const.ANDROID)
        params.put("DeviceDetail[device_name]", Build.MODEL)
        val call = api!!.apiCheck(params.getServerHashMap())
        restFullClient?.sendRequest(call, this)
    }

    private fun setEditUI() {
        isEdit = !isEdit
        binding!!.aboutET.isFocusable = isEdit
        binding!!.aboutET.isLongClickable = isEdit
        binding!!.aboutET.isFocusableInTouchMode = isEdit
        if (isEdit) {
            binding!!.saveBT.visibility = View.VISIBLE
            binding!!.editIV.visibility = View.GONE
        } else {
            binding!!.editIV.visibility = View.VISIBLE
            binding!!.saveBT.visibility = View.GONE
        }
    }

    private fun hitUpdateApi() {
        val param = Api3MultipartByte()
        param.put("User[about_me]", binding!!.aboutET.checkString())
        val call = api!!.apiUpdateProfile(param.getRequestBody())
        restFullClient!!.sendRequest(call, this)
    }

    override fun onHandleClick(view: View) {
        when (view.id) {

            R.id.editIV, R.id.saveBT -> {
                if (isEdit) {
                    if (binding!!.aboutET.checkString().isNotEmpty()) {
                        hitUpdateApi()
                    } else {
                        showToastOne(getString(R.string.please_write_something))
                    }
                } else {
                    setEditUI()
                }
            }
        }
    }

    override fun onSyncSuccess(responseCode: Int, responseMessage: String, responseUrl: String, response: String?) {
        super.onSyncSuccess(responseCode, responseMessage, responseUrl, response)
        try {
            val jsonObjects = JSONObject(response!!)
            if (responseUrl.contains(Const.API_UPDATE_PROFILE)) {
                if (responseCode == Const.STATUS_OK) {
                    try {
                        showToastOne(baseActivity!!.getString(R.string.profile_update_succes))
                        val data = Gson().fromJson<ProfileData>(jsonObjects.getJSONObject("detail").toString(), ProfileData::class.java)
                        baseActivity!!.setProfileData(data)
                        baseActivity!!.updateDrawer()
                        setEditUI()
                    } catch (e: Exception) {
                        baseActivity!!.handleException(e)
                    }

                }

            } else if (responseUrl.contains(Const.API_CHECK)) {
                val data = Gson().fromJson<ProfileData>(jsonObjects.getJSONObject("detail").toString(), ProfileData::class.java)
                baseActivity!!.setProfileData(data)
                restFullClient!!.setLoginStatus(data.accessToken)
                binding!!.aboutET.setText(baseActivity!!.getProfileData()!!.aboutMe)
            }


        } catch (e: Exception) {
            baseActivity!!.handleException(e)
        }
    }

}
