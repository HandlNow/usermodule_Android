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
import com.handlService.app.databinding.FragmentMyServicesBinding
import com.handlService.app.model.MyServiceData
import com.handlService.app.ui.adapter.AdapterMyServices
import com.handlService.app.ui.adapter.BaseAdapter
import com.handlService.app.ui.extensions.replaceFragment
import com.handlService.app.utils.ClickHandler
import com.handlService.app.utils.Const
import com.toxsl.restfulClient.api.extension.handleException
import org.json.JSONObject


class MyServicesFragment : BaseFragment(), ClickHandler, BaseAdapter.OnPageEndListener {

    private var binding: FragmentMyServicesBinding? = null
    private var list: ArrayList<MyServiceData> = arrayListOf()
    private var isSingle = false
    private var pageCount = 0
    private var adapter: AdapterMyServices? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (binding == null)
            binding = DataBindingUtil.inflate(
                    inflater, R.layout.fragment_my_services, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    private fun initUI() {
        binding!!.handleClick = this
        baseActivity!!.setToolbar(getString(R.string.my_services))
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
            adapter = AdapterMyServices(baseActivity!!, this, list)
            binding!!.serviceRV.adapter = adapter
            adapter!!.setOnPageEndListener(this)
        } else {
            adapter!!.notifyDataSetChanged()
        }
    }

    private fun hitCategoryListAPI() {
        if (!isSingle) {
            isSingle = true
            val call = api!!.apiGetMyCategoryList(pageCount)
            restFullClient!!.sendRequest(call, this)
        }
    }


    override fun onHandleClick(view: View) {

        when (view.id) {
            R.id.saveTV -> {
                if (list.size > 0) {
                    val data = baseActivity!!.getProfileData()
                    data!!.isService = true
                    baseActivity!!.setProfileData(data)
                    baseActivity!!.onBackPressed()
                } else {
                    showToastOne(getString(R.string.please_Add_Services))
                }
            }
            R.id.addTV -> {
                baseActivity!!.replaceFragment(RegisterServiceFragment())
            }
        }
    }

    override fun onPageEnd(vararg itemData: Any) {
        hitCategoryListAPI()


    }

    override fun onSyncSuccess(responseCode: Int, responseMessage: String, responseUrl: String, response: String?) {
        super.onSyncSuccess(responseCode, responseMessage, responseUrl, response)
        try {
            val jsonobject = JSONObject(response!!)
            if (responseUrl.contains(Const.API_MY_CATEGORY_LIST)) {
                if (responseCode == Const.STATUS_OK) {
                    pageCount++
                    val totalId = jsonobject.getJSONObject(Const._META).getInt(Const.PAGE_COUNT)
                    isSingle = pageCount >= totalId
                    val jsonArray = jsonobject.getJSONArray(Const.LIST)
                    for (i in 0 until jsonArray.length()) {
                        val object1 = jsonArray.getJSONObject(i)
                        val data = Gson().fromJson<MyServiceData>(object1.toString(), MyServiceData::class.java)
                        list.add(data)
                    }

                    if (list.size > 0) {
                        setAdapter()
                        binding!!.noDataTV.visibility = View.GONE
                    } else {
                        binding!!.noDataTV.visibility = View.VISIBLE
                    }

                } else {
                    isSingle = false
                }
            } else if (responseUrl.contains(Const.API_MY_CATEGORY_ON_OFF)) {
                if (responseCode == Const.STATUS_OK) {
                    list[disablePos].stateId = jsonobject.getJSONObject("detail").optInt("state_id")
                    adapter?.notifyItemChanged(disablePos)
                    if (list[disablePos].stateId != 0) {
                        showToastOne(getString(R.string.service_enabled))
                    } else {
                        showToastOne(getString(R.string.service_disabled))
                    }
                }
            }
        } catch (e: Exception) {
            handleException(e)
        }
    }

    var disablePos = 0
    fun hitDisableAbleAPI(pos: Int) {
        disablePos = pos
        val call = api!!.apiGetMyCategoryOnOff(list[pos].id)
        restFullClient!!.sendRequest(call, this)
    }

}
