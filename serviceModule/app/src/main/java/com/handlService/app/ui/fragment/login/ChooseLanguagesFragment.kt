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
import android.view.*
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.google.gson.Gson
import com.handlService.app.R
import com.handlService.app.databinding.FragmentChooseLangBinding
import com.handlService.app.model.NotificationData
import com.handlService.app.ui.adapter.AdapterLanguageList
import com.handlService.app.ui.adapter.BaseAdapter
import com.handlService.app.ui.fragment.BaseFragment
import com.handlService.app.utils.Const
import com.toxsl.restfulClient.api.Api3Params
import com.toxsl.restfulClient.api.extension.handleException
import org.json.JSONObject


class ChooseLanguagesFragment : BaseFragment(), BaseAdapter.OnPageEndListener {

    private var binding: FragmentChooseLangBinding? = null
    private var list: ArrayList<NotificationData> = arrayListOf()
    private var isSingle = false
    private var pageCount = 0
    private var adapter: AdapterLanguageList? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (binding == null)
            binding = DataBindingUtil.inflate(
                    inflater, R.layout.fragment_choose_lang, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    private fun initUI() {
        baseActivity!!.setToolbar(title = getString(R.string.select_language), screenBg = ContextCompat.getColor(baseActivity!!, R.color.White))
        resetList()
        hitLanguageListAPI()

        binding!!.doneTV.setOnClickListener {
            val list = list.filter { it.isChecked }
            if (list.isNotEmpty()) {
                hitAddAPI()
            } else {
                showToastOne(baseActivity!!.getString(R.string.chhose_langugae))
            }
        }

        binding!!.pullToRefresh.setOnRefreshListener {
            resetList()
            hitLanguageListAPI()
            binding!!.pullToRefresh.isRefreshing = false
        }

    }

    private fun hitAddAPI() {
        val params = Api3Params()
        params.put("language", getMultiId(list))
        val call = api!!.apiAddLanguage(params.getServerHashMap())
        restFullClient!!.sendRequest(call, this)
    }

    private fun getMultiId(list: ArrayList<NotificationData>): String {

        var id = ""
        for (data in list) {
            if (data.isChecked) {
                id = String.format("%s,%d", id, data.id)
            }
        }

        return when {
            id.isNotEmpty() -> id.substring(1)
            else -> id
        }
    }

    private fun resetList() {
        isSingle = false
        pageCount = 0
        list.clear()
        adapter = null
    }


    private fun hitLanguageListAPI() {
        if (!isSingle) {
            isSingle = true
            val call = api!!.apiGetLanguageList(pageCount)
            restFullClient!!.sendRequest(call, this)
        }
    }


    private fun setAdapter() {
        if (adapter == null) {
            adapter = AdapterLanguageList(baseActivity!!, list)
            binding!!.notificationRV.adapter = adapter
            adapter!!.setOnPageEndListener(this)
        } else {
            adapter!!.notifyDataSetChanged()
        }
    }


    override fun onSyncSuccess(responseCode: Int, responseMessage: String, responseUrl: String, response: String?) {
        super.onSyncSuccess(responseCode, responseMessage, responseUrl, response)
        try {
            val jsonobject = JSONObject(response!!)
            if (responseUrl.contains(Const.API_LANGUAGE_LIST)) {
                if (responseCode == Const.STATUS_OK) {
                    pageCount++
                    val totalId = jsonobject.getJSONObject(Const._META).getInt(Const.PAGE_COUNT)
                    isSingle = pageCount >= totalId
                    val jsonArray = jsonobject.getJSONArray(Const.LIST)
                    for (i in 0 until jsonArray.length()) {
                        val object1 = jsonArray.getJSONObject(i)
                        val data = Gson().fromJson<NotificationData>(object1.toString(), NotificationData::class.java)
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
            } else if (responseUrl.contains(Const.API_ADD_LANGUAGE)) {
                if (responseCode == Const.STATUS_OK) {
                    baseActivity!!.onBackPressed()
                }
            }
        } catch (e: Exception) {
            handleException(e)
        }
    }

    override fun onPageEnd(vararg itemData: Any) {
        hitLanguageListAPI()
    }


}
