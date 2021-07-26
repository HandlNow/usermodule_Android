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
import android.view.*
import androidx.databinding.DataBindingUtil
import com.google.gson.Gson
import com.handlService.app.R
import com.handlService.app.databinding.FragmentChatListBinding
import com.handlService.app.model.AppointmentData
import com.handlService.app.model.ProfileData
import com.handlService.app.ui.activity.MainActivity
import com.handlService.app.ui.adapter.AdapterChatList
import com.handlService.app.ui.adapter.BaseAdapter
import com.handlService.app.utils.Const
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
        setHasOptionsMenu(true)
        resetList()
        hitChatListAPI()

        binding!!.pullToRefresh.setOnRefreshListener {
            resetList()
            hitChatListAPI()
            binding!!.pullToRefresh.isRefreshing = false
        }

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.home_menu, menu)

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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.homeMB) {
            (baseActivity as MainActivity).openDrawer()
        }
        return super.onOptionsItemSelected(item)
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
                    val totalId = jsonobject.getJSONObject(Const._META).getInt(Const.PAGE_COUNT)
                    isSingle = pageCount >= totalId
                    val jsonArray = jsonobject.getJSONArray(Const.LIST)
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
