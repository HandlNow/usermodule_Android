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
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.handlUser.app.R
import com.handlUser.app.databinding.FragmentAddUserDetailBinding
import com.handlUser.app.model.ProfileData
import com.handlUser.app.ui.extensions.loadFromUrl
import com.handlUser.app.ui.extensions.replaceFragment
import com.handlUser.app.ui.fragment.BaseFragment
import com.handlUser.app.ui.fragment.MySavedHomesFragment
import com.handlUser.app.ui.fragment.PaymentMethodListFragment
import com.handlUser.app.utils.ClickHandler
import com.handlUser.app.utils.Const
import com.toxsl.restfulClient.api.Api3MultipartByte
import com.toxsl.restfulClient.api.extension.handleException
import org.json.JSONObject
import java.io.File
import java.io.FileNotFoundException


class AddUserDetailFragment : BaseFragment(), ClickHandler {

    private var binding: FragmentAddUserDetailBinding? = null
    private var profileImage: File? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (binding == null)
            binding = DataBindingUtil.inflate(
                    inflater, R.layout.fragment_add_user_detail, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    private fun initUI() {
        binding!!.handleClick = this
        binding!!.dotsI.dot5.setImageResource(R.drawable.circle_dark_blue)
        baseActivity!!.setToolbar(title = getString(R.string.add_profile_picture))
        setProfileData()
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_logout, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logoutMB -> {
                baseActivity!!.logOut()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    @SuppressLint("SetTextI18n")
    private fun setProfileData() {
        val data = baseActivity!!.getProfileData()!!
        binding!!.profileData = data
        if (data.profileFile != null) {
            binding!!.profilePicCIV.loadFromUrl(baseActivity!!, data.profileFile!!)
        }

    }

    override fun onHandleClick(view: View) {
        when (view.id) {
            R.id.doneTV -> {
                if (baseActivity!!.getProfileData()!!.profileFile.isNullOrEmpty()) {
                    when (profileImage) {
                        null -> {
                            showToastOne(resources.getString(R.string.add_image))
                        }
                        else -> {
                            updateProfile()
                        }
                    }
                } else {
                    baseActivity!!.gotoMainActivity()
                }

            }
            R.id.paymentTV -> baseActivity!!.replaceFragment(PaymentMethodListFragment())
            R.id.myHomesTV -> baseActivity!!.replaceFragment(MySavedHomesFragment())
            R.id.profilePicCIV -> {
                if (baseActivity!!.checkPermissions(arrayOf(android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE), Const.IMAGE_PERMISSION, this)) {
                    chooseImagePicker(Const.IMAGE_PERMISSION, false)
                }
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
                    .placeholder(R.drawable.profie_img)
                    .into(binding!!.profilePicCIV)

        }
    }

    override fun permGranted(resultCode: Int) {
        chooseImagePicker(Const.IMAGE_PERMISSION, false)
    }

    private fun updateProfile() {
        val param = Api3MultipartByte()
        param.put("User[first_name]", binding!!.firstNameTV.text.toString())
        if (profileImage != null) {
            try {
                param.put("User[profile_file]", profileImage)
            } catch (e: FileNotFoundException) {
                handleException(e)
            }
        }
        param.put("User[last_name]", binding!!.lastNameTv.text.toString())
        param.put("User[address]", baseActivity!!.getProfileData()!!.address!!)
        param.put("User[city]", baseActivity!!.getProfileData()!!.city!!)
        param.put("User[latitude]", baseActivity!!.getProfileData()!!.latitude!!)
        param.put("User[longitude]", baseActivity!!.getProfileData()!!.longitude!!)
        param.put("User[zipcode]", baseActivity!!.getProfileData()!!.zipcode!!)
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
                        showToastOne(baseActivity!!.getString(R.string.profile_pis_update_succes))
                        val data = Gson().fromJson<ProfileData>(jsonObjects.getJSONObject("detail").toString(), ProfileData::class.java)
                        baseActivity!!.setProfileData(data)
                        baseActivity!!.gotoMainActivity()
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
