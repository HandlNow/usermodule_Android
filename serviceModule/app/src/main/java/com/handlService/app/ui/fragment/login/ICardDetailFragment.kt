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
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.google.gson.Gson
import com.handlService.app.R
import com.handlService.app.databinding.FragmentIcardDetailBinding
import com.handlService.app.model.ProfileData
import com.handlService.app.ui.extensions.loadFromLocal
import com.handlService.app.ui.fragment.BaseFragment
import com.handlService.app.utils.ClickHandler
import com.handlService.app.utils.Const
import com.toxsl.restfulClient.api.Api3MultipartByte
import com.toxsl.restfulClient.api.extension.handleException
import org.json.JSONObject
import java.io.File
import java.io.FileNotFoundException


class ICardDetailFragment : BaseFragment(), ClickHandler {

    private var binding: FragmentIcardDetailBinding? = null
    private var profileImage: File? = null
    private var fileString: String = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (binding == null)
            binding = DataBindingUtil.inflate(
                    inflater, R.layout.fragment_icard_detail, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            fileString = arguments?.getString("file", "") as String
            profileImage = File(Uri.parse(fileString).path!!)
        }
    }

    private fun initUI() {
        binding!!.handleClick = this
        baseActivity!!.setToolbar(baseActivity!!.getString(R.string.welcom), ContextCompat.getColor(baseActivity!!, R.color.screen_bg), true)
        if (arguments?.getBoolean("is_card") as Boolean) {
            binding!!.passwordTagTV.setImageResource(R.mipmap.ic_img22)
            binding!!.noteTV.text = baseActivity!!.getString(R.string.make_sure_the_photo_is_readable_and_all_four_corners_are_visible)
        } else {
            binding!!.passwordTagTV.setImageResource(R.drawable.profie_img)
            binding!!.noteTV.text = Html.fromHtml(baseActivity!!.getString(R.string.make_sure_the_photo_is_readable))

        }
        binding!!.passwordTagTV.loadFromLocal(baseActivity!!, profileImage.toString())
    }


    override fun onHandleClick(view: View) {
        when (view.id) {
            R.id.submitTV -> {
                hitPhotoAPI()
            }
            R.id.retakeTV -> {
                baseActivity!!.onBackPressed()
            }

        }
    }

    private fun hitPhotoAPI() {
        val param = Api3MultipartByte()
        if (profileImage != null) {
            try {
                if (arguments?.getBoolean("is_card") as Boolean) {
                    param.put("User[id_proof]", profileImage)
                } else {
                    param.put("User[profile_file]", profileImage)
                }
            } catch (e: FileNotFoundException) {
                handleException(e)
            }
        }

        val call = api!!.apiupdateDocumentation(param.getRequestBody())
        restFullClient!!.sendRequest(call, this)


    }


    override fun onSyncSuccess(responseCode: Int, responseMessage: String, responseUrl: String, response: String?) {
        super.onSyncSuccess(responseCode, responseMessage, responseUrl, response)
        try {
            val jsonObjects = JSONObject(response!!)
            if (responseUrl.contains(Const.API_DOCUMENT)) {
                if (responseCode == Const.STATUS_OK) {
                    showToastOne(jsonObjects.optString(Const.MESSAGE))
                    val data = Gson().fromJson<ProfileData>(jsonObjects.getJSONObject("detail").toString(), ProfileData::class.java)
                    baseActivity!!.setProfileData(data)
                    baseActivity!!.gotoMainActivity()
                }
            }

        } catch (e: Exception) {
            baseActivity!!.handleException(e)
        }
    }

}
