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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.google.gson.Gson
import com.handlService.app.R
import com.handlService.app.databinding.FragmentEditProfileBinding
import com.handlService.app.model.ProfileData
import com.handlService.app.ui.extensions.checkString
import com.handlService.app.ui.fragment.BaseFragment
import com.handlService.app.utils.ClickHandler
import com.handlService.app.utils.Const
import com.toxsl.restfulClient.api.Api3MultipartByte
import org.json.JSONObject


class EditProfileFragment : BaseFragment(), ClickHandler {

    private var binding: FragmentEditProfileBinding? = null
    private var isEdit = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (binding == null)
            binding = DataBindingUtil.inflate(
                    inflater, R.layout.fragment_edit_profile, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    private fun initUI() {
        binding!!.handleClick = this
        baseActivity!!.setToolbar(getString(R.string.edit_profile), ContextCompat.getColor(baseActivity!!, R.color.White))
        binding!!.profileData = baseActivity!!.getProfileData()!!
    }


    private fun hitUpdateApi() {
        val param = Api3MultipartByte()
        param.put("User[first_name]", binding!!.firstNameET.checkString())
        param.put("User[last_name]", binding!!.lastNameET.checkString())
        val call = api!!.apiUpdateProfile(param.getRequestBody())
        restFullClient!!.sendRequest(call, this)
    }

    override fun onHandleClick(view: View) {
        when (view.id) {

            R.id.updateTV -> {
                if (validation()) {
                    hitUpdateApi()
                }
            }

        }
    }

    private fun validation(): Boolean {
        when {
            binding!!.firstNameET.checkString().isEmpty() -> showToast(getString(R.string.please_enter_first_name))
            binding!!.lastNameET.checkString().isEmpty() -> showToast(getString(R.string.please_enter_last_name))
            else -> return true
        }
        return false
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
                        baseActivity!!.onBackPressed()
                    } catch (e: Exception) {
                        baseActivity!!.handleException(e)
                    }

                }

            }

        } catch (e: Exception) {
            baseActivity!!.handleException(e)
        }
    }

}
