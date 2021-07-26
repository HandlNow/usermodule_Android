/*
 * @copyright : ToXSL Technologies Pvt. Ltd. < www.toxsl.com >
 * @author     : Shiv Charan Panjeta < shiv@toxsl.com >
 * All Rights Reserved.
 * Proprietary and confidential :  All information contained herein is, and remains
 * the property of ToXSL Technologies Pvt. Ltd. and its partners.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.handlUser.app.ui.fragment.services

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.google.gson.Gson
import com.handlUser.app.R
import com.handlUser.app.databinding.FragmentServiceExtrasBinding
import com.handlUser.app.model.CategoryData
import com.handlUser.app.ui.adapter.BaseAdapter
import com.handlUser.app.ui.adapter.service.ExtraRequirementAdapter
import com.handlUser.app.ui.extensions.checkString
import com.handlUser.app.ui.extensions.replaceFragment
import com.handlUser.app.ui.fragment.BaseFragment
import com.handlUser.app.ui.fragment.PickUpDateTimeFragment
import com.handlUser.app.utils.ClickHandler
import com.handlUser.app.utils.Const
import com.toxsl.restfulClient.api.Api3Params
import org.json.JSONObject


class ServiceExtraRequireFragment : BaseFragment(), ClickHandler {

    private var binding: FragmentServiceExtrasBinding? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (binding == null)
            binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_service_extras, container, false
            )
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    private fun initUI() {
        binding!!.handleClick = this
        baseActivity!!.setToolbar(getString(R.string.extra_require))

    }


    override fun onSyncSuccess(
        responseCode: Int,
        responseMessage: String,
        responseUrl: String,
        response: String?
    ) {
        super.onSyncSuccess(responseCode, responseMessage, responseUrl, response)
        try {
            val jsonobject = JSONObject(response!!)

            if (responseUrl.contains(Const.API_PROVIDER_ADD_REQUIREMENT)) {
                if (responseCode == Const.STATUS_OK) {
                    val bundle = arguments
                    bundle!!.putInt("extra_id", jsonobject.getJSONObject("detail").optInt("id"))
                    if (binding!!.checkCB.isChecked) {
                        bundle.putInt("requirements", Const.ONE)
                    } else {
                        bundle.putInt("requirements", Const.ZERO)
                    }

                    baseActivity!!.replaceFragment(PickUpDateTimeFragment(), bundle)
                }
            }


        } catch (e: Exception) {
            baseActivity!!.handleException(e)
        }
    }


    override fun onHandleClick(view: View) {
        when (view.id) {
            R.id.nextTV -> {
                hitAddServiceAPI()
            }
        }
    }

    private fun hitAddServiceAPI() {
        val param = Api3Params()
        param.put("UserRequirement[user_service_id]", arguments?.getInt("service_id", 0) as Int)
        param.put("UserRequirement[type_id]", Const.EXTRA_REQUIREMENT)
        param.put("UserRequirement[title]", binding!!.question2ET.checkString())
        if (binding!!.checkCB.isChecked) {
            param.put("requirements", Const.ONE)
        } else {
            param.put("requirements", Const.ZERO)
        }
        val call = api!!.apiAddRequirement(param.getServerHashMap())
        restFullClient!!.sendRequest(call, this)
    }
}
