/*
 * @copyright : ToXSL Technologies Pvt. Ltd. < www.toxsl.com >
 * @author     : Shiv Charan Panjeta < shiv@toxsl.com >
 * All Rights Reserved.
 * Proprietary and confidential :  All information contained herein is, and remains
 * the property of ToXSL Technologies Pvt. Ltd. and its partners.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.handlService.app.ui.fragment.login

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.handlService.app.R
import com.handlService.app.databinding.FragmentBusinessSettingBinding
import com.handlService.app.model.ProfileData
import com.handlService.app.ui.activity.BaseActivity
import com.handlService.app.ui.activity.MainActivity
import com.handlService.app.ui.extensions.loadFromUrl
import com.handlService.app.ui.extensions.replaceFragment
import com.handlService.app.ui.fragment.*
import com.handlService.app.utils.ClickHandler
import com.handlService.app.utils.Const
import com.toxsl.restfulClient.api.Api3MultipartByte
import com.toxsl.restfulClient.api.extension.handleException
import org.json.JSONObject
import java.io.File
import java.io.FileNotFoundException


class MyBusinessSettingsFragment : BaseFragment(), ClickHandler, BaseActivity.PermCallback {

    private var profileImage: File? = null
    private var binding: FragmentBusinessSettingBinding? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (binding == null)
            binding = DataBindingUtil.inflate(
                    inflater, R.layout.fragment_business_setting, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    private fun initUI() {
        binding!!.handleClick = this
        baseActivity!!.setToolbar(getString(R.string.my_busi_seting))
        setHasOptionsMenu(true)
        setData()
    }

    @SuppressLint("SetTextI18n")
    private fun setData() {
        val data = baseActivity!!.getProfileData()!!
        binding!!.profilePicCIV.loadFromUrl(baseActivity!!, data.profileFile)
        binding!!.profileData = data

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.edit_menu, menu)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.homeMB -> {
                (baseActivity as MainActivity).openDrawer()
            }
            R.id.editIV -> {
                baseActivity!!.replaceFragment(EditProfileFragment())
            }
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onHandleClick(view: View) {
        when (view.id) {
            R.id.profilePicCIV -> {
                if (baseActivity!!.checkPermissions(arrayOf(android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE), Const.IMAGE_PERMISSION, this)) {
                    chooseImagePicker(Const.IMAGE_PERMISSION, false)
                }
            }
            R.id.businessTV -> {
                baseActivity!!.replaceFragment(AboutSettingsFragment())
            }
            R.id.locationTV -> {
                baseActivity!!.replaceFragment(RegisterHouseFragment())

            }
            R.id.myServiceTV -> {
                baseActivity!!.replaceFragment(MyServicesFragment())

            }
            R.id.availTV -> {
                baseActivity!!.replaceFragment(AvailabilitySlotsFragment())

            }
            R.id.subscTV -> {
                baseActivity!!.replaceFragment(SubscriptionFragment())
            }
            R.id.paymentMethodTV -> {
                baseActivity!!.replaceFragment(PaymentDetailsFragment())
            }
            R.id.changePassTV -> {
                baseActivity!!.replaceFragment(ChangePasswordFragment())
            }

            R.id.languageTV -> {
                baseActivity!!.replaceFragment(LanguagesFragment())
            }


        }
    }


    override fun onImageSelected(uri: Uri?, requestCode: Int) {
        super.onImageSelected(uri, requestCode)
        if (requestCode == Const.IMAGE_PERMISSION) {
            profileImage = File(uri!!.path!!)
            Glide.with(baseActivity!!)
                    .load(uri)
                    .centerCrop()
                    .placeholder(R.mipmap.ic_default)
                    .into(binding!!.profilePicCIV)
            hitUpdateApi()

        }
    }

    override fun permGranted(resultCode: Int) {
        chooseImagePicker(Const.IMAGE_PERMISSION, false)
    }

    override fun permDenied(resultCode: Int) {


    }

    private fun hitUpdateApi() {
        val param = Api3MultipartByte()
        if (profileImage != null) {
            try {
                param.put("User[profile_file]", profileImage)
            } catch (e: FileNotFoundException) {
                handleException(e)
            }
        }
        param.put("User[first_name]", binding!!.firstNameTV.text.toString())
        param.put("User[last_name]", binding!!.lastNameTV.text.toString())
        val call = api!!.apiUpdateProfile(param.getRequestBody())
        restFullClient!!.sendRequest(call, this)
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
