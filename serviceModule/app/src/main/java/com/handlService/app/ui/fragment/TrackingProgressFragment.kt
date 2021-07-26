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
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.google.gson.Gson
import com.handlService.app.R
import com.handlService.app.databinding.FragmentTrackingProgressBinding
import com.handlService.app.model.AppointmentData
import com.handlService.app.model.AvailabilitySlot
import com.handlService.app.ui.activity.MainActivity
import com.handlService.app.ui.adapter.AdapterVerticalProgress
import com.handlService.app.utils.Const
import com.toxsl.restfulClient.api.extension.handleException
import org.json.JSONObject
import kotlin.collections.ArrayList


class TrackingProgressFragment : BaseFragment() {

    private val list: ArrayList<AvailabilitySlot> = arrayListOf()
    private var binding: FragmentTrackingProgressBinding? = null
    private var data: AppointmentData? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (binding == null)
            binding = DataBindingUtil.inflate(
                    inflater, R.layout.fragment_tracking_progress, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            data = arguments?.getParcelable<AppointmentData>("booking_data")
        }
    }

    private fun initUI() {
        baseActivity!!.setToolbar(getString(R.string.progress), ContextCompat.getColor(baseActivity!!, R.color.White), true)
        setHasOptionsMenu(true)
        hitgetTrackingAPI()
    }

    private fun hitgetTrackingAPI() {
        val call = api!!.apiGetTracking(data!!.id)
        restFullClient!!.sendRequest(call, this)
    }

    override fun onSyncSuccess(responseCode: Int, responseMessage: String, responseUrl: String, response: String?) {
        super.onSyncSuccess(responseCode, responseMessage, responseUrl, response)
        try {
            val jsonobject = JSONObject(response!!)
            if (responseUrl.contains(Const.API_TRACKING_DATA)) {
                if (responseCode == Const.STATUS_OK) {
                    val jsonArray = jsonobject.getJSONArray(Const.LIST)
                    for (i in 0 until jsonArray.length()) {
                        val object1 = jsonArray.getJSONObject(i)
                        val data = Gson().fromJson<AvailabilitySlot>(object1.toString(), AvailabilitySlot::class.java)
                        list.add(data)
                    }
                    binding!!.notificationRV.adapter = AdapterVerticalProgress(baseActivity!!, this,list)

                }

            }
        } catch (e: Exception) {
            handleException(e)
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

}
