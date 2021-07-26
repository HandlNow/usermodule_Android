/*
 * @copyright : ToXSL Technologies Pvt. Ltd. < www.toxsl.com >
 * @author     : Shiv Charan Panjeta < shiv@toxsl.com >
 * All Rights Reserved.
 * Proprietary and confidential :  All information contained herein is, and remains
 * the property of ToXSL Technologies Pvt. Ltd. and its partners.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.handlService.app.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import com.google.gson.Gson
import com.handlService.app.R
import com.handlService.app.databinding.*
import com.handlService.app.model.AppointmentData
import com.handlService.app.ui.activity.MainActivity
import com.handlService.app.ui.adapter.AdapterAppointment
import com.handlService.app.ui.adapter.BaseAdapter
import com.handlService.app.ui.extensions.replaceFragment
import com.handlService.app.utils.Const
import com.toxsl.restfulClient.api.extension.handleException
import org.json.JSONObject
import kotlin.collections.ArrayList


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

    @SuppressLint("ClickableViewAccessibility")
    private fun initUI() {
        resetList()
        baseActivity!!.setToolbar(title = getString(R.string.my_appointments))
        setHasOptionsMenu(true)

        binding!!.checkTV.setOnClickListener {
            baseActivity!!.replaceFragment(MyCalendarFragment())
        }
        binding!!.earningTV.setOnClickListener {
            baseActivity!!.replaceFragment(InsightsFragment())
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

    override fun onPageEnd(vararg itemData: Any) {
        hitListAPI()
    }

    override fun onSyncSuccess(responseCode: Int, responseMessage: String, responseUrl: String, response: String?) {
        super.onSyncSuccess(responseCode, responseMessage, responseUrl, response)
        try {
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

        } catch (e: Exception) {
            handleException(e)
        }
    }


}
