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
import android.os.Bundle
import android.view.*
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.handlService.app.BuildConfig
import com.handlService.app.R
import com.handlService.app.databinding.FragmentPromosBinding
import com.handlService.app.databinding.FragmentServiceExtrasBinding
import com.handlService.app.model.MyServiceData
import com.handlService.app.ui.activity.MainActivity
import com.handlService.app.ui.adapter.ImageAdapter
import com.handlService.app.ui.extensions.checkString
import com.handlService.app.ui.extensions.replaceFragment
import com.handlService.app.utils.ClickHandler
import com.handlService.app.utils.Const
import com.toxsl.restfulClient.api.Api3Params
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileNotFoundException


class RequirementFragment : BaseFragment(), ClickHandler {

    private var data: MyServiceData? = null

    private var binding: FragmentServiceExtrasBinding? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (binding == null)
            binding = DataBindingUtil.inflate(
                    inflater, R.layout.fragment_service_extras, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    private fun initUI() {
        binding!!.handleClick = this
        baseActivity!!.setToolbar(title = data!!.subCategoryName + baseActivity!!.getString(R.string.specail_Require), screenBg = ContextCompat.getColor(baseActivity!!, R.color.White))
        setHasOptionsMenu(true)
        getRequirementAPI()
    }

    private fun getRequirementAPI() {
        val call = api!!.apiGetRequirement(data!!.categoryId.toInt(), data!!.subCategoryId)
        restFullClient!!.sendRequest(call, this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            data = arguments?.getParcelable("data")
        }
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
            R.id.nextTV -> {
                hitRequireAPI()
            }
        }
    }

    private fun hitRequireAPI() {
        val params = Api3Params()

        val check = if (binding!!.checkCB.isChecked) {
            Const.ONE
        } else {
            Const.ZERO
        }
        params.put("SpecialRequirement[category_id]", data!!.categoryId)
        params.put("SpecialRequirement[sub_category_id]", data!!.subCategoryId)
        params.put("SpecialRequirement[is_required]", check)
        params.put("SpecialRequirement[description]", binding!!.question2ET.checkString())

        val call = api!!.apiAddRequirement(params.getServerHashMap(),data!!.categoryId,data!!.subCategoryId)
        restFullClient!!.sendRequest(call, this)

    }

    override fun onSyncSuccess(responseCode: Int, responseMessage: String, responseUrl: String, response: String?) {
        super.onSyncSuccess(responseCode, responseMessage, responseUrl, response)
        try {
            val jsonobject = JSONObject(response!!)
            if (responseUrl.contains(Const.API_GET_EXTRA_REQUIREMENT)) {
                if (responseCode == Const.STATUS_OK) {
                    binding!!.question2ET.setText(jsonobject.getJSONObject("detail").optString("description"))
                    binding!!.checkCB.isChecked = jsonobject.getJSONObject("detail").optInt("is_required") == Const.ONE
                }
            } else if (responseUrl.contains(Const.API_ADD_EXTRA_REQUIREMENT)) {
                if (responseCode == Const.STATUS_OK) {
                    baseActivity!!.replaceFragment(WorkDetailFragment(), requireArguments())
                }
            }

        } catch (e: Exception) {
            baseActivity!!.handleException(e)
        }
    }

    override fun onSyncFailure(errorCode: Int, t: Throwable?, response: Response<String>?, call: Call<String>?, callBack: Callback<String>?) {
            if (errorCode == Const.STATUS) {
                return
            }
        }


}
