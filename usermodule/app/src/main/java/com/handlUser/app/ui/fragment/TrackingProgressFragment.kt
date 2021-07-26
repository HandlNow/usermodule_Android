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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.google.gson.Gson
import com.handlUser.app.R
import com.handlUser.app.databinding.DialogTerminateServiceBinding
import com.handlUser.app.databinding.FragmentTrackingProgressBinding
import com.handlUser.app.model.AppointmentData
import com.handlUser.app.model.AvailabilitySlot
import com.handlUser.app.ui.adapter.AdapterDialogNumber
import com.handlUser.app.ui.adapter.AdapterVerticalProgress
import com.handlUser.app.utils.ClickHandler
import com.handlUser.app.utils.Const
import com.toxsl.restfulClient.api.extension.handleException
import org.json.JSONObject


class TrackingProgressFragment : BaseFragment(), ClickHandler {

    private var dialogBinding: DialogTerminateServiceBinding? = null
    private var alertDialog: AlertDialog? = null
    private var binding: FragmentTrackingProgressBinding? = null
    private var data: AppointmentData? = null
    private val list: ArrayList<AvailabilitySlot> = arrayListOf()


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
        binding!!.handleClick = this
        baseActivity!!.setToolbar(getString(R.string.progress), ContextCompat.getColor(baseActivity!!, R.color.White), true)
        hitgetTrackingAPI()
    }


    private fun hitgetTrackingAPI() {
        val call = api!!.apiGetTracking(data!!.id)
        restFullClient!!.sendRequest(call, this)
    }

    override fun onSyncSuccess(responseCode: Int, responseMessage: String, responseUrl: String, response: String?) {
        super.onSyncSuccess(responseCode, responseMessage, responseUrl, response)
        try {
            if (responseCode == Const.STATUS_OK) {
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

                } else if (responseUrl.contains(Const.API_AVAIL_CHANGE_STATE)) {
                    if (responseCode == Const.STATUS_OK) {
                        showToastOne(jsonobject.optString("message"))
                        alertDialog!!.dismiss()
                    }

                }

            }
        } catch (e: Exception) {
            handleException(e)
        }
    }

    override fun onHandleClick(view: View) {

        when (view.id) {
            R.id.terminateTV -> {
                showDialog()
            }
        }
    }

    private fun showDialog() {

        val mBuilder = AlertDialog.Builder(baseActivity!!, R.style.animateDialog)
        dialogBinding = DataBindingUtil.inflate(LayoutInflater.from(baseActivity), R.layout.dialog_terminate_service, null, false)
        mBuilder.setView(dialogBinding!!.root)
        alertDialog = mBuilder.create()
        alertDialog!!.show()

        dialogBinding!!.yesTV.setOnClickListener {
            val call = api!!.apiChangeState(data!!.id, Const.STATE_TERMINATE, "")
            restFullClient!!.sendRequest(call, this)
        }

        dialogBinding!!.noTV.setOnClickListener {
            alertDialog!!.dismiss()
        }

    }
}
