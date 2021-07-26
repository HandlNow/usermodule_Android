/*
 * @copyright : ToXSL Technologies Pvt. Ltd. < www.toxsl.com >
 * @author     : Shiv Charan Panjeta < shiv@toxsl.com >
 * All Rights Reserved.
 * Proprietary and confidential :  All information contained herein is, and remains
 * the property of ToXSL Technologies Pvt. Ltd. and its partners.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.handlUser.app.ui.fragment

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import com.google.gson.Gson
import com.handlUser.app.R
import com.handlUser.app.databinding.*
import com.handlUser.app.model.AppointmentData
import com.handlUser.app.ui.adapter.AdapterAppointment
import com.handlUser.app.ui.adapter.BaseAdapter
import com.handlUser.app.ui.extensions.loadFromUrl
import com.handlUser.app.ui.extensions.replaceFragment
import com.handlUser.app.utils.Const
import com.toxsl.restfulClient.api.extension.handleException
import org.json.JSONObject


class MyAppointmentFragment : BaseFragment(), BaseAdapter.OnPageEndListener {

    private var binding: FragmentMyAppointmentBinding? = null

    private var adapter: AdapterAppointment? = null
    private var list: ArrayList<AppointmentData> = arrayListOf()
    private var isSingle = false
    private var pageCount = 0
    private var type = 0


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_my_appointment, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    private fun initUI() {
        resetList()
        baseActivity!!.setToolbar(title = getString(R.string.my_appointments))
        binding!!.checkTV.setOnClickListener {
            baseActivity!!.replaceFragment(MyCalendarFragment())
        }
        type = Const.ZERO
        hitListAPI()
        binding!!.savedItemTV.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                resetList()
                type = Const.ZERO
                hitListAPI()
            }
            false
        }

        binding!!.broughtItemTV.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                resetList()
                type = Const.ONE
                hitListAPI()
            }
            false
        }
        binding!!.pullToRefresh.setOnRefreshListener {
            resetList()
            hitListAPI()
            binding!!.pullToRefresh.isRefreshing = false
        }

    }

    private fun hitListAPI() {
        if (!isSingle) {
            isSingle = true
            val call = api!!.apiGetBookingList(pageCount, type.toString())
            restFullClient!!.sendRequest(call, this)
        }
    }

    private fun resetList() {
        list.clear()
        adapter = null
        isSingle = false
        pageCount = 0

    }


    private fun setUpcomingAdapter() {
        if (adapter == null) {
            adapter = AdapterAppointment(baseActivity!!, this, list)
            binding!!.upcomingRV.adapter = adapter
            adapter!!.setOnPageEndListener(this)
        } else {
            adapter!!.notifyDataSetChanged()
        }
    }




    override fun onPageEnd(vararg itemData: Any) {
        hitListAPI()

    }

    override fun onSyncSuccess(responseCode: Int, responseMessage: String, responseUrl: String, response: String?) {
        super.onSyncSuccess(responseCode, responseMessage, responseUrl, response)
        try {
            if (responseCode == Const.STATUS_OK) {
                val jsonobject = JSONObject(response!!)
                if (responseUrl.contains(Const.API_BOOKING_LIST)) {
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
                            binding!!.noDataTV.visibility = View.GONE
                            binding!!.upcomingRV.visibility = View.VISIBLE
                            setUpcomingAdapter()
                        } else {
                            binding!!.noDataTV.visibility = View.VISIBLE
                            binding!!.upcomingRV.visibility = View.GONE
                        }

                    } else {
                        isSingle = false
                    }
                }
            }
        } catch (e: Exception) {
            handleException(e)
        }
    }


}
