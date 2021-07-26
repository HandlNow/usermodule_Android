/*
 * @copyright : ToXSL Technologies Pvt. Ltd. < www.toxsl.com >
 * @author     : Shiv Charan Panjeta < shiv@toxsl.com >
 * All Rights Reserved.
 * Proprietary and confidential :  All information contained herein is, and remains
 * the property of ToXSL Technologies Pvt. Ltd. and its partners.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.handlUser.app.ui.fragment

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import com.google.gson.Gson
import com.handlUser.app.R
import com.handlUser.app.databinding.FragmentChatListBinding
import com.handlUser.app.model.AppointmentData
import com.handlUser.app.model.ProfileData
import com.handlUser.app.ui.activity.MainActivity
import com.handlUser.app.ui.adapter.AdapterChatList
import com.handlUser.app.ui.adapter.BaseAdapter
import com.handlUser.app.utils.Const
import com.toxsl.restfulClient.api.extension.handleException
import org.json.JSONObject


class ChatListFragment : BaseFragment(), BaseAdapter.OnPageEndListener {
    private var binding: FragmentChatListBinding? = null
    private var list: ArrayList<AppointmentData> = arrayListOf()
    private var adapter: AdapterChatList? = null

    private var isSingle = false
    private var pageCount = 0
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (binding == null)
            binding = DataBindingUtil.inflate(
                    inflater, R.layout.fragment_chat_list, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    private fun initUI() {
        baseActivity!!.setToolbar(getString(R.string.messages))
        resetList()
        hitChatListAPI()
        binding!!.pullToRefresh.setOnRefreshListener {
            resetList()
            hitChatListAPI()
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
            adapter = AdapterChatList(baseActivity!!, list)
            binding!!.chatListRV.adapter = adapter
            adapter!!.setOnPageEndListener(this)
        } else {
            adapter!!.notifyDataSetChanged()
        }
    }


    private fun hitChatListAPI() {
        if (!isSingle) {
            isSingle = true
            val call = api!!.apiGetInboxMessages(pageCount)
            restFullClient!!.sendRequest(call, this)
        }
    }

    override fun onSyncSuccess(responseCode: Int, responseMessage: String, responseUrl: String, response: String?) {
        super.onSyncSuccess(responseCode, responseMessage, responseUrl, response)
        try {
            val jsonobject = JSONObject(response!!)
            if (responseUrl.contains(Const.API_INBOX_LIST)) {
                if (responseCode == Const.STATUS_OK) {
                    pageCount++
                    val metaObject = jsonobject.getJSONObject("_meta")
                    val totalId = metaObject.getInt("pageCount")
                    isSingle = pageCount >= totalId
                    val jsonArray = jsonobject.getJSONArray("list")
                    for (i in 0 until jsonArray.length()) {
                        val object1 = jsonArray.getJSONObject(i)
                        val data = Gson().fromJson<AppointmentData>(object1.toString(), AppointmentData::class.java)
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

    override fun onPageEnd(vararg itemData: Any) {
        hitChatListAPI()

    }


}
