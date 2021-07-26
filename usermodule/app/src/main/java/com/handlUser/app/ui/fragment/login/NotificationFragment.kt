/*
 * @copyright : ToXSL Technologies Pvt. Ltd. < www.toxsl.com >
 * @author     : Shiv Charan Panjeta < shiv@toxsl.com >
 * All Rights Reserved.
 * Proprietary and confidential :  All information contained herein is, and remains
 * the property of ToXSL Technologies Pvt. Ltd. and its partners.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.handlUser.app.ui.fragment.login

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.google.gson.Gson
import com.handlUser.app.R
import com.handlUser.app.databinding.FragmentNotificationsBinding
import com.handlUser.app.model.NotificationData
import com.handlUser.app.model.ProfileData
import com.handlUser.app.ui.adapter.BaseAdapter
import com.handlUser.app.ui.adapter.NotificationAdapter
import com.handlUser.app.ui.fragment.BaseFragment
import com.handlUser.app.utils.Const
import com.toxsl.restfulClient.api.Api3Params
import com.toxsl.restfulClient.api.extension.handleException
import org.json.JSONObject


class NotificationFragment : BaseFragment(), BaseAdapter.OnPageEndListener {

    private var binding: FragmentNotificationsBinding? = null
    private var adapter: NotificationAdapter? = null
    private var list: ArrayList<NotificationData> = arrayListOf()
    private var isSingle = false
    private var pageCount = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (binding == null)
            binding = DataBindingUtil.inflate(
                    inflater, R.layout.fragment_notifications, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    private fun initUI() {
        baseActivity!!.setToolbar(title = getString(R.string.notifications), screenBg = ContextCompat.getColor(baseActivity!!, R.color.White))
        resetList()
        apiHitNotification()
        binding!!.pullToRefresh.setOnRefreshListener {
            resetList()
            apiHitNotification()
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
            adapter = NotificationAdapter(baseActivity!!, list, this)
            binding!!.notificationRV.adapter = adapter
            adapter!!.setOnPageEndListener(this)
        } else {
            adapter!!.notifyDataSetChanged()
        }
    }

    private fun apiHitNotification() {
        if (!isSingle) {
            isSingle = true
            val call = api!!.apiGetNotification(pageCount)
            restFullClient!!.sendRequest(call, this)
        }
    }

    override fun onPageEnd(vararg itemData: Any) {
        when (itemData[0] as String) {
            Const.API_NOTIFICATION_LIST -> {
                apiHitNotification()
            }
        }
    }

    override fun onSyncSuccess(responseCode: Int, responseMessage: String, responseUrl: String, response: String?) {
        super.onSyncSuccess(responseCode, responseMessage, responseUrl, response)
        try {
            if (responseCode == Const.STATUS_OK) {
                val jsonobject = JSONObject(response!!)
                if (responseUrl.contains(Const.API_NOTIFICATION_LIST)) {
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
                            binding!!.noDataTV.visibility = View.GONE
                            setAdapter()
                        } else {
                            binding!!.noDataTV.visibility = View.VISIBLE

                        }


                    } else {
                        isSingle = false
                    }
                    hitCheckApi()
                } else if (responseUrl.contains(Const.API_CHECK)) {
                    val data = Gson().fromJson<ProfileData>(jsonobject.getJSONObject("detail").toString(), ProfileData::class.java)
                    baseActivity!!.setProfileData(data)
                    restFullClient!!.setLoginStatus(data.accessToken)
                    baseActivity!!.updateDrawer()

                }
            }
        } catch (e: Exception) {
            handleException(e)
        }
    }

    private fun hitCheckApi() {
        val params = Api3Params()
        params.put("DeviceDetail[device_token]", baseActivity!!.getDeviceToken())
        params.put("DeviceDetail[device_type]", Const.ANDROID)
        params.put("DeviceDetail[device_name]", Build.MODEL)
        val call = api!!.apiCheck(params.getServerHashMap())
        restFullClient?.sendRequest(call, this)

    }

}
