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
import com.handlService.app.databinding.FragmentRegisterServicesBinding
import com.handlService.app.model.CategoryData
import com.handlService.app.ui.adapter.AdapterServiceCategory
import com.handlService.app.ui.adapter.BaseAdapter
import com.handlService.app.utils.Const
import com.toxsl.restfulClient.api.extension.handleException
import org.json.JSONObject


class RegisterServiceFragment : BaseFragment(), BaseAdapter.OnPageEndListener {

    private var binding: FragmentRegisterServicesBinding? = null
    private var list: ArrayList<CategoryData> = arrayListOf()
    private var isSingle = false
    private var pageCount = 0
    private var adapter: AdapterServiceCategory? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (binding == null)
            binding = DataBindingUtil.inflate(
                    inflater, R.layout.fragment_register_services, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    private fun initUI() {
        baseActivity!!.setToolbar(baseActivity!!.getString(R.string.register_services))
        resetList()
        hitCategoryListAPI()
        binding!!.pullToRefresh.setOnRefreshListener {
            resetList()
            hitCategoryListAPI()
            binding!!.pullToRefresh.isRefreshing = false
        }

    }

    private fun resetList() {
        list.clear()
        adapter = null
        isSingle = false
        pageCount = 0
    }

    private fun setAdapter() {
        if (adapter == null) {
            adapter = AdapterServiceCategory(baseActivity!!, this, list)
            binding!!.cleaningRV.adapter = adapter
            adapter!!.setOnPageEndListener(this)
        } else {
            adapter!!.notifyDataSetChanged()
        }
    }

    private fun hitCategoryListAPI() {
        if (!isSingle) {
            isSingle = true
            val call = api!!.apiGetCategoryList(pageCount)
            restFullClient!!.sendRequest(call, this)
        }
    }

    override fun onPageEnd(vararg itemData: Any) {
        hitCategoryListAPI()
    }

    override fun onSyncSuccess(responseCode: Int, responseMessage: String, responseUrl: String, response: String?) {
        super.onSyncSuccess(responseCode, responseMessage, responseUrl, response)
        try {
            val jsonobject = JSONObject(response!!)
            if (responseUrl.contains(Const.API_CATEGORY_LIST)) {
                if (responseCode == Const.STATUS_OK) {
                    pageCount++
                    val totalId = jsonobject.getJSONObject(Const._META).getInt(Const.PAGE_COUNT)
                    isSingle = pageCount >= totalId
                    val jsonArray = jsonobject.getJSONArray(Const.LIST)
                    for (i in 0 until jsonArray.length()) {
                        val object1 = jsonArray.getJSONObject(i)
                        val data = Gson().fromJson<CategoryData>(object1.toString(), CategoryData::class.java)
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
            handleException(e)
        }
    }

}
