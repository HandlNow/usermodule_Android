/*
 * @copyright : ToXSL Technologies Pvt. Ltd. < www.toxsl.com >
 * @author     : Shiv Charan Panjeta < shiv@toxsl.com >
 * All Rights Reserved.
 * Proprietary and confidential :  All information contained herein is, and remains
 * the property of ToXSL Technologies Pvt. Ltd. and its partners.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.handlService.app.ui.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import com.google.gson.Gson
import com.handlService.app.BuildConfig
import com.handlService.app.R
import com.handlService.app.databinding.FragmentPromosBinding
import com.handlService.app.databinding.FragmentWorkDetailBinding
import com.handlService.app.model.ImageFile
import com.handlService.app.model.MyServiceData
import com.handlService.app.model.NotificationData
import com.handlService.app.model.ProfileData
import com.handlService.app.ui.activity.BaseActivity
import com.handlService.app.ui.activity.LoginSignUpActivity
import com.handlService.app.ui.activity.MainActivity
import com.handlService.app.ui.adapter.ImageAdapter
import com.handlService.app.ui.extensions.checkString
import com.handlService.app.ui.fragment.login.MyBusinessSettingsFragment
import com.handlService.app.ui.fragment.login.VerifiedFragment
import com.handlService.app.utils.ClickHandler
import com.handlService.app.utils.Const
import com.toxsl.restfulClient.api.Api3MultipartByte
import com.toxsl.restfulClient.api.Api3Params
import org.json.JSONObject
import toxsl.imagebottompicker.ImageBottomPicker
import java.io.File
import java.io.FileNotFoundException
import java.util.ArrayList


class WorkDetailFragment : BaseFragment(), ClickHandler, BaseActivity.PermCallback, ImageBottomPicker.OnMultiImageSelectedListener {

    private var binding: FragmentWorkDetailBinding? = null
    private var imageFileList: ArrayList<ImageFile> = arrayListOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (binding == null)
            binding = DataBindingUtil.inflate(
                    inflater, R.layout.fragment_work_detail, container, false)
        return binding!!.root
    }

    private var data: MyServiceData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            data = arguments?.getParcelable("data")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    private fun initUI() {
        binding!!.handleClick = this
        baseActivity!!.setToolbar(title = data!!.subCategoryName, screenBg = ContextCompat.getColor(baseActivity!!, R.color.White))
        setHasOptionsMenu(true)
        getImages()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.home_menu, menu)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.homeMB) {
            (baseActivity as MainActivity).openDrawer()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onHandleClick(view: View) {
        when (view.id) {
            R.id.saveBT -> {
                hitSaveAPI()
            }
            R.id.clickTV, R.id.photosIV -> {
                if (imageFileList.size < 6) {
                    openCamera()
                } else {
                    showToastOne(getString(R.string.please_delete_images))
                }
            }

        }
    }

    private fun openCamera() {
        if (baseActivity!!.checkPermissions(arrayOf(android.Manifest.permission.CAMERA, android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE), Const.IMAGE_CODE, this)) {
            val bottomPicker = ImageBottomPicker.Builder(baseActivity!!, Const.IMAGE_CODE).setOnMultiImageSelectedListener(this).setSelectMaxCount(6 - imageFileList.size).showCameraTile(true).showGalleryTile(false).showRemoved(false).create()
            bottomPicker.show(baseActivity?.supportFragmentManager)
        }

    }

    private fun hitSaveAPI() {
        val params = Api3MultipartByte()
        val imageStringList = imageFileList.filter { !it.isURL }
        if (imageStringList.isEmpty() && binding!!.aboutET.checkString().isEmpty()) {
            showToastOne(getString(R.string.please_add_data))
        } else {
            for (i in imageStringList.indices) {
                try {
                    params.put("File[image_file][$i]", File(imageStringList[i].image))
                } catch (e: FileNotFoundException) {
                    if (BuildConfig.DEBUG) {
                        e.printStackTrace()
                    }
                }
            }
            params.put("ProviderCategory[description]", binding!!.aboutET.checkString())
            val call = api!!.apiAddWorkDetail(params.getRequestBody(), data!!.id)
            restFullClient!!.sendRequest(call, this)
        }

    }

    override fun permGranted(resultCode: Int) {
        if (resultCode == Const.IMAGE_CODE) {
            openCamera()
        }
    }

    override fun permDenied(resultCode: Int) {

    }

    override fun onImagesSelected(uriList: ArrayList<Uri>?, requestCode: Int) {
        for (i in 0 until uriList!!.size) {
            val data = ImageFile(uriList[i].path.toString(), "", "", false, 0)
            imageFileList.add(data)
        }
        binding!!.photosRV.adapter = ImageAdapter(baseActivity!!, imageFileList, this)

    }

    private var position = 0

    fun removeImage(pos: Int) {
        position = pos
        val call = api!!.apiDeleteWorkImage(imageFileList[position].id)
        restFullClient!!.sendRequest(call, this)
    }

    private fun getImages() {
        val call = api!!.apiGetWorkImage(data!!.id)
        restFullClient!!.sendRequest(call, this)

    }

    override fun onSyncSuccess(responseCode: Int, responseMessage: String, responseUrl: String, response: String?) {
        super.onSyncSuccess(responseCode, responseMessage, responseUrl, response)
        try {
            val jsonobject = JSONObject(response!!)
            if (responseUrl.contains(Const.API_ADD_WORK_DETAIL)) {
                if (responseCode == Const.STATUS_OK) {
                    showToastOne(jsonobject.optString("message"))
                    if (baseActivity is LoginSignUpActivity) {
                        requireActivity().supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                        requireActivity().supportFragmentManager
                                .beginTransaction()
                                .replace(R.id.container, VerifiedFragment())
                                .commit()
                    } else {
                        if (baseActivity!!.getProfileData()!!.isLocation && baseActivity!!.getProfileData()!!.isService && baseActivity!!.getProfileData()!!.isSubscription && baseActivity!!.getProfileData()!!.isAvailable && baseActivity!!.getProfileData()!!.isLanguage) {
                            requireActivity().supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                            requireActivity().supportFragmentManager
                                    .beginTransaction()
                                    .replace(R.id.container, MyBusinessSettingsFragment())
                                    .commit()
                        } else {
                            requireActivity().supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                            requireActivity().supportFragmentManager
                                    .beginTransaction()
                                    .replace(R.id.container, VerifiedFragment())
                                    .commit()
                        }
                    }

                }

            } else if (responseUrl.contains(Const.API_DELETE_WORK_DETAIL_IMAGE)) {
                if (responseCode == Const.STATUS_OK) {
                    imageFileList.removeAt(position)
                    binding!!.photosRV.adapter = ImageAdapter(baseActivity!!, imageFileList, this)
                }
            } else if (responseUrl.contains(Const.API_GET_WORK_DETAIL_IMAGE)) {
                if (responseCode == Const.STATUS_OK) {
                    val jsonArray = jsonobject.getJSONArray(Const.LIST)
                    for (i in 0 until jsonArray.length()) {
                        val object1 = jsonArray.getJSONObject(i)
                        val data = Gson().fromJson<ImageFile>(object1.toString(), ImageFile::class.java)
                        data.isURL = true
                        imageFileList.add(data)
                        binding!!.aboutET.setText(data.description)
                    }
                    binding!!.photosRV.adapter = ImageAdapter(baseActivity!!, imageFileList, this)
                }
            }

        } catch (e: Exception) {
            baseActivity!!.handleException(e)
        }
    }


}
