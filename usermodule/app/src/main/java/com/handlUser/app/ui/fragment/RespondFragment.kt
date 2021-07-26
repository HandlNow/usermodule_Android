/*
 * @copyright : ToXSL Technologies Pvt. Ltd. < www.toxsl.com >
 * @author     : Shiv Charan Panjeta < shiv@toxsl.com >
 * All Rights Reserved.
 * Proprietary and confidential :  All information contained herein is, and remains
 * the property of ToXSL Technologies Pvt. Ltd. and its partners.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.handlUser.app.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import com.google.gson.Gson
import com.handlUser.app.R
import com.handlUser.app.databinding.FragmentRespondBinding
import com.handlUser.app.model.AppointmentData
import com.handlUser.app.model.AvailabilitySlot
import com.handlUser.app.model.ProviderData
import com.handlUser.app.ui.activity.MainActivity
import com.handlUser.app.ui.adapter.AdapterRespondSlot
import com.handlUser.app.ui.extensions.replaceFragmentWithoutStackWithoutBundle
import com.handlUser.app.utils.ClickHandler
import com.handlUser.app.utils.Const
import com.toxsl.restfulClient.api.Api3Params
import org.json.JSONObject


class RespondFragment : BaseFragment(), ClickHandler {

    private var proData: ProviderData? = null
    private var binding: FragmentRespondBinding? = null

    private var data: AppointmentData? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (binding == null)
            binding = DataBindingUtil.inflate(
                    inflater, R.layout.fragment_respond, container, false)
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

    @SuppressLint("SetTextI18n")
    private fun initUI() {
        binding!!.handleClick = this
        baseActivity!!.setToolbar(title = getString(R.string.respond))
        setHasOptionsMenu(true)
        binding!!.descTV.text = data!!.provider.firstName + getString(R.string.new_time)
        if (!data!!.bookingSlots.isNullOrEmpty()) {
            binding!!.monthTV.text = baseActivity!!.changeDateFormatGmtToLocal(data!!.bookingSlots[0].startTime, Const.DATE_FORMAT, "MMMM")
            binding!!.dayTV.text = baseActivity!!.changeDateFormatGmtToLocal(data!!.bookingSlots[0].startTime, Const.DATE_FORMAT, "dd (EEE)")
            binding!!.timeTV.text = baseActivity!!.changeDateFormatGmtToLocal(data!!.bookingSlots[0].startTime, Const.DATE_FORMAT, "hh:mm a")
        }
        hitRespondAPI()
    }

    private fun hitRespondAPI() {
        val call = api!!.apiEditResponse(data!!.id)
        restFullClient!!.sendRequest(call, this)
    }


    override fun onHandleClick(view: View) {
        when (view.id) {
            R.id.confirmTV -> {
                var allWeek: List<AvailabilitySlot> = listOf()
                val list = proData!!.avalibility[0].availabilitySlots
                allWeek = list.filter { it.isChecked }

                if (allWeek.isNotEmpty()) {
                    hitConfirmRespondAPI(allWeek)
                } else {
                    showToastOne(getString(R.string.please_select_time_slot))
                }
            }
            R.id.availTimeTV -> {
                binding!!.timeRV.visibility = View.VISIBLE
            }
            R.id.bookanotherTV -> {
                val call = api!!.apiChangeState(data!!.id, Const.STATE_REJECT, "")
                restFullClient!!.sendRequest(call, this)
            }
        }
    }

    private fun setAdapter(data: ProviderData) {
        binding!!.timeRV.adapter = AdapterRespondSlot(baseActivity!!, data.avalibility[0].availabilitySlots)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_drawer, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menuIV -> {
                (baseActivity as MainActivity).openDrawer()

            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSyncSuccess(responseCode: Int, responseMessage: String, responseUrl: String, response: String?) {
        super.onSyncSuccess(responseCode, responseMessage, responseUrl, response)
        try {
            val jsonobject = JSONObject(response!!)
            if (responseUrl.contains(Const.API_EDIT_RESPONSE)) {
                if (responseCode == Const.STATUS_OK) {
                    proData = Gson().fromJson<ProviderData>(jsonobject.getJSONObject("detail").toString(), ProviderData::class.java)
                    binding!!.availMonthTV.text = baseActivity!!.changeDateFormatGmtToLocal(proData!!.avalibility[0].startTime, Const.DATE_FORMAT, "MMMM")
                    binding!!.availDayTV.text = baseActivity!!.changeDateFormatGmtToLocal(proData!!.avalibility[0].startTime, Const.DATE_FORMAT, "dd (EEE)")
                    binding!!.availTimeTV.text = "--"
                    setAdapter(proData!!)
                }
            } else if (responseUrl.contains(Const.API_AVAIL_CHANGE_STATE) ||
                    responseUrl.contains(Const.API_CONFIRM_EDIT_RESPOND)) {
                if (responseCode == Const.STATUS_OK) {
                    baseActivity!!.replaceFragmentWithoutStackWithoutBundle(HomeFragment())
                }
            }

        } catch (e: Exception) {
            baseActivity!!.handleException(e)
        }
    }

    private fun hitConfirmRespondAPI(allWeek: List<AvailabilitySlot>) {
        val params = Api3Params()
        params.put("BookingSlot[booking_id]", data!!.id)
        params.put("BookingSlot[provider_id]", data!!.providerId)
        params.put("slot_id", getMultiId(allWeek))
        val call = api!!.apiConfirmRespond(params.getServerHashMap(), data!!.id)
        restFullClient!!.sendRequest(call, this)
    }

    private fun getMultiId(allWeek: List<AvailabilitySlot>): String {

        var id = ""
        for (data in allWeek) {
            if (data.isChecked) {
                id = String.format("%s,%d", id, data.id)
            }
        }

        return when {
            id.isNotEmpty() -> id.substring(1)
            else -> id
        }
    }

}
