/*
 * @copyright : ToXSL Technologies Pvt. Ltd. < www.toxsl.com >
 * @author     : Shiv Charan Panjeta < shiv@toxsl.com >
 * All Rights Reserved.
 * Proprietary and confidential :  All information contained herein is, and remains
 * the property of ToXSL Technologies Pvt. Ltd. and its partners.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.handlService.app.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.google.gson.Gson
import com.handlService.app.R
import com.handlService.app.databinding.FragmentServiceHomeCleaningBinding
import com.handlService.app.model.MyServiceData
import com.handlService.app.model.ServiceData
import com.handlService.app.model.SubCategory
import com.handlService.app.ui.adapter.BaseAdapter
import com.handlService.app.ui.adapter.HomeCleaningAdapter
import com.handlService.app.ui.extensions.checkString
import com.handlService.app.ui.extensions.replaceFragment
import com.handlService.app.utils.ClickHandler
import com.handlService.app.utils.Const
import com.toxsl.restfulClient.api.Api3Params
import org.json.JSONArray
import org.json.JSONObject


class ServiceHomeCleaningFragment : BaseFragment(), ClickHandler, BaseAdapter.OnPageEndListener {

    private var binding: FragmentServiceHomeCleaningBinding? = null
    private var data: SubCategory? = null
    private var subData: MyServiceData? = null
    private var isSingle = false
    private var pageCount = 0
    private var type = false
    private var adapter: HomeCleaningAdapter? = null
    private var list: ArrayList<ServiceData> = arrayListOf()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (binding == null)
            binding = DataBindingUtil.inflate(
                    inflater, R.layout.fragment_service_home_cleaning, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }


    private fun initUI() {
        binding!!.handleClick = this
        baseActivity!!.setToolbar(getString(R.string.home_cleaning)+baseActivity!!.getString(R.string._sub_services))

        resetList()
        hitGetDataAPI()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            type=arguments?.getBoolean("isMyService")!!
            if (type){
                subData = arguments?.getParcelable<MyServiceData>("service_data")
            }else {
                data = arguments?.getParcelable<SubCategory>("list_data")
            }
        }
    }

    private fun resetList() {
        pageCount = 0
        isSingle = false
        list.clear()
        adapter = null
    }

    private fun hitGetDataAPI() {
        if (!isSingle) {
            isSingle = true
            var id =0
            if (type){
                id=subData!!.subCategoryType
            }else {
               id= data!!.typeId
            }
            val call = api!!.apiGetSubCategoryList(id, pageCount)
            restFullClient!!.sendRequest(call, this)
        }
    }


    private fun setAdapter() {
        if (adapter == null) {
            adapter = HomeCleaningAdapter(baseActivity!!, list[0].parent)
            binding!!.commonRV.adapter = adapter
            adapter!!.setOnPageEndListener(this)
        } else {
            adapter!!.notifyDataSetChanged()
        }

    }

    override fun onSyncSuccess(responseCode: Int, responseMessage: String, responseUrl: String, response: String?) {
        super.onSyncSuccess(responseCode, responseMessage, responseUrl, response)
        try {
            val jsonobject = JSONObject(response!!)
            if (responseUrl.contains(Const.API_PROVIDER_SUB_CATEGORY_LIST)) {
                if (responseCode == Const.STATUS_OK) {
                    pageCount++
                    val totalId = jsonobject.getJSONObject(Const._META).getInt(Const.PAGE_COUNT)
                    isSingle = pageCount >= totalId
                    val jsonArray = jsonobject.getJSONArray(Const.LIST)
                    for (i in 0 until jsonArray.length()) {
                        val object1 = jsonArray.getJSONObject(i)
                        val data = Gson().fromJson<ServiceData>(object1.toString(), ServiceData::class.java)
                        for (k in 0 until data.parent.size) {
                            if (data.parent[k].isCheck == Const.ONE) {
                                data.parent[k].isChecked = true
                            }
                        }
                        list.add(data)
                    }
                    if (list.size > 0) {
                        setAdapter()
                    }

                } else {
                    isSingle = false
                }
            }
        } catch (e: Exception) {
            baseActivity!!.handleException(e)
        }
    }

    override fun onHandleClick(view: View) {
        when (view.id) {
            R.id.nextTV -> {
                var isValid = false
                for (data in list[0].parent) {
                    if (data.isChecked) {
                        isValid = true
                        break
                    }
                }
                if (isValid) {
                    hitAddServiceAPI()
                } else {
                    showToastOne(baseActivity!!.getString(R.string.select_data_to_proceed))
                }
            }
        }
    }

    override fun onPageEnd(vararg itemData: Any) {
        hitGetDataAPI()
    }

    private fun hitAddServiceAPI() {
        val bundle = requireArguments()
        bundle.putString("desc", binding!!.otherET.checkString())
        bundle.putString("source", getJsonArray().toString())
        bundle.putBoolean("isMyService", true)

        baseActivity!!.replaceFragment(ServicePricesFragment(), bundle)
    }

    private fun getJsonArray(): JSONArray {
        val array = JSONArray()
        for (data in list[0].parent) {
            if (data.isChecked) {
                val jsonObject = JSONObject()
                jsonObject.put("parent_id", data.id)
                array.put(jsonObject)
            }
        }
        return array
    }

}
