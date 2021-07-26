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
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.alamkanak.weekview.threetenabp.setDateFormatter
import com.applandeo.materialcalendarview.EventDay
import com.applandeo.materialcalendarview.listeners.OnCalendarPageChangeListener
import com.applandeo.materialcalendarview.listeners.OnDayClickListener
import com.google.gson.Gson
import com.handlUser.app.R
import com.handlUser.app.databinding.DialogCancelBinding
import com.handlUser.app.databinding.DialogCancelRequestBinding
import com.handlUser.app.databinding.DialogNewRequestBinding
import com.handlUser.app.databinding.FragmentMyCalendarBinding
import com.handlUser.app.model.AppointmentData
import com.handlUser.app.ui.adapter.MyCustomSimpleAdapter
import com.handlUser.app.ui.extensions.loadFromUrl
import com.handlUser.app.ui.extensions.replaceFragment
import com.handlUser.app.utils.ClickHandler
import com.handlUser.app.utils.Const
import com.toxsl.restfulClient.api.extension.handleException
import org.json.JSONObject
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class MyCalendarFragment : BaseFragment(), ClickHandler {

    private var cancelBinding: DialogCancelBinding? = null
    private var binding: FragmentMyCalendarBinding? = null
    private var list: ArrayList<AppointmentData> = arrayListOf()
    private val weekdayFormatter = DateTimeFormatter.ofPattern("EEE", Locale.getDefault())
    private val dateFormatter = DateTimeFormatter.ofPattern("MM/dd", Locale.getDefault())
    private var month = ""
    private var year = ""


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (binding == null)
            binding = DataBindingUtil.inflate(
                    inflater, R.layout.fragment_my_calendar, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    private fun resetList() {
        list.clear()
    }

    private fun initUI() {
        resetList()
        val mCalendar = Calendar.getInstance()
        binding!!.handleClick = this
        baseActivity!!.setToolbar(title = getString(R.string.my_calendar), ContextCompat.getColor(baseActivity!!, R.color.White))

        setDefaultClick()
        binding!!.dayViewTV.setBackgroundColor(ContextCompat.getColor(baseActivity!!, R.color.LightGrey))
        binding!!.weekView.visibility = View.VISIBLE
        binding!!.weekView.numberOfVisibleDays = 1
        binding!!.weekView.setDateFormatter { date: LocalDate ->
            val weekdayLabel = weekdayFormatter.format(date)
            val dateLabel = dateFormatter.format(date)
            weekdayLabel + "\n" + dateLabel
        }
        binding!!.weekView.showFirstDayOfWeekFirst = true

        binding!!.calendarView.setOnDayClickListener(object : OnDayClickListener {
            override fun onDayClick(eventDay: EventDay) {
                val clickedDayCalendar = eventDay.calendar
                getBookingListAPI(baseActivity!!.changeDateFormatFromDate(
                        clickedDayCalendar.time,
                        "yyyy-MM-dd"))
                // showCancelDialog()
            }
        })


        binding!!.calendarView.setOnForwardPageChangeListener(object : OnCalendarPageChangeListener {
            override fun onChange() {
                resetList()
                mCalendar.add(Calendar.MONTH, +1)
                month = baseActivity!!.changeDateFormatFromDate(mCalendar.time, "MM")
                year = baseActivity!!.changeDateFormatFromDate(mCalendar.time, "yyyy")
                getRequestListAPI(month, year)

            }

        })
        binding!!.calendarView.setOnPreviousPageChangeListener(object : OnCalendarPageChangeListener {
            override fun onChange() {
                resetList()
                mCalendar.add(Calendar.MONTH, -1)
                month = baseActivity!!.changeDateFormatFromDate(mCalendar.time, "MM")
                year = baseActivity!!.changeDateFormatFromDate(mCalendar.time, "yyyy")
                getRequestListAPI(month, year)

            }

        })
        month = baseActivity!!.changeDateFormatFromDate(mCalendar.time, "MM")
        year = baseActivity!!.changeDateFormatFromDate(mCalendar.time, "yyyy")
        val day = baseActivity!!.changeDateFormatFromDate(mCalendar.time, "dd-MM-yyyy")
        getRequestListAPI(month, year)
        hitAvalabilityListApi(day)

    }

    private fun getRequestListAPI(month: String, year: String) {
        val call = api!!.apiGetMonthlyList(month, year)
        restFullClient!!.sendRequest(call, this)
    }

    private fun getBookingListAPI(date: String) {
        val call = api!!.apiGetDayList(date!!)
        restFullClient!!.sendRequest(call, this)
    }


    private fun showCancelDialog() {

        val mBuilder = AlertDialog.Builder(baseActivity!!, R.style.animateDialog)
        cancelBinding = DataBindingUtil.inflate(LayoutInflater.from(baseActivity), R.layout.dialog_cancel, null, false)
        mBuilder.setView(cancelBinding!!.root)
        val alertDialog = mBuilder.create()
        alertDialog.show()

        cancelBinding!!.yesTV.setOnClickListener {
            alertDialog.dismiss()
            baseActivity!!.gotoMainActivity()
        }

        cancelBinding!!.noTV.setOnClickListener {
            alertDialog.dismiss()
        }
        cancelBinding!!.rescheduleTV.setOnClickListener {
            alertDialog.dismiss()
            baseActivity!!.replaceFragment(ReBookFragment())
        }

    }

    private fun setDefaultClick() {
        binding!!.dayViewTV.setBackgroundColor(ContextCompat.getColor(baseActivity!!, R.color.White))
        binding!!.weekViewTV.setBackgroundColor(ContextCompat.getColor(baseActivity!!, R.color.White))
        binding!!.monthViewTV.setBackgroundColor(ContextCompat.getColor(baseActivity!!, R.color.White))
        binding!!.weekView.visibility = View.GONE
        binding!!.calendarView.visibility = View.GONE

    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onHandleClick(view: View) {
        when (view.id) {
            R.id.dayViewTV -> {
                setDefaultClick()
                binding!!.dayViewTV.setBackgroundColor(ContextCompat.getColor(baseActivity!!, R.color.LightGrey))
                binding!!.weekView.visibility = View.VISIBLE
                binding!!.weekView.numberOfVisibleDays = 1

            }
            R.id.weekViewTV -> {
                setDefaultClick()
                binding!!.weekViewTV.setBackgroundColor(ContextCompat.getColor(baseActivity!!, R.color.LightGrey))
                binding!!.weekView.visibility = View.VISIBLE
                binding!!.weekView.numberOfVisibleDays = 7

            }

            R.id.monthViewTV -> {
                setDefaultClick()
                binding!!.monthViewTV.setBackgroundColor(ContextCompat.getColor(baseActivity!!, R.color.LightGrey))
                binding!!.calendarView.visibility = View.VISIBLE

            }

        }
    }


    @SuppressLint("SimpleDateFormat")
    override fun onSyncSuccess(responseCode: Int, responseMessage: String, responseUrl: String, response: String?) {
        super.onSyncSuccess(responseCode, responseMessage, responseUrl, response)
        try {
            val jsonobject = JSONObject(response!!)
            if (responseCode == Const.STATUS_OK) {
                if (responseUrl.contains(Const.API_MONTHLY_BOOKING_LIST)) {
                    val event: MutableList<EventDay> = ArrayList()
                    if (responseCode == Const.STATUS_OK) {
                        val jsonArray = jsonobject.getJSONArray(Const.LIST)
                        for (i in 0 until jsonArray.length()) {
                            val object1 = jsonArray.getJSONObject(i)
                            val data = Gson().fromJson<AppointmentData>(object1.toString(), AppointmentData::class.java)
                            val calendar = Calendar.getInstance()
                            val sdf = SimpleDateFormat(Const.DATE_FORMAT)
                            val date = baseActivity!!.changeDateFormatGmtToLocal(data.bookingSlots[0].startTime, Const.DATE_FORMAT, Const.DATE_FORMAT)
                            calendar.time = sdf.parse(date)!!
                            event.add(EventDay(calendar, R.drawable.circle_light_blue))
                            list.add(data)
                        }
                        binding!!.calendarView.setEvents(event)
                        if (list.size > 0) {
                            setAdapter()
                        }

                    }

                } else if (responseUrl.contains(Const.API_CALENDAR_LIST)) {
                    val event: MutableList<EventDay> = ArrayList()
                    val jsonArray = jsonobject.getJSONArray(Const.LIST)
                    list.clear()
                    for (i in 0 until jsonArray.length()) {
                        val object1 = jsonArray.getJSONObject(i)
                        val data = Gson().fromJson<AppointmentData>(object1.toString(), AppointmentData::class.java)
                        val calendar = Calendar.getInstance()
                        val sdf = SimpleDateFormat(Const.DATE_FORMAT)
                        val date = baseActivity!!.changeDateFormatGmtToLocal(data.bookingSlots[0].startTime, Const.DATE_FORMAT, Const.DATE_FORMAT)
                        calendar.time = sdf.parse(date)!!
                        list.add(data)
                        event.add(EventDay(calendar, R.drawable.circle_light_blue))
                    }
                    binding!!.calendarView.setEvents(event)
                    if (list.size > 0) {
                        setAdapter()

                    }
                } else if (responseUrl.contains(Const.API_BOOKING_AVAILABILITY_LIST)) {

                       if(jsonobject.getBoolean("is_booked")){
                           showCancelDialog()
                       }else{
                           showToast("You dont have any booking")
                       }

                }
            }
        } catch (e: Exception) {
            handleException(e)
        }
    }

    private fun setAdapter() {
        val adapter = MyCustomSimpleAdapter(baseActivity!!, this, rangeChangeHandler = this::onRangeChanged)
        binding!!.weekView.adapter = adapter
        adapter.submitList(list)
    }

    private fun hitAvalabilityListApi(startDate: String) {
        val call = api!!.apiAvailabilityList(startDate)
        restFullClient!!.sendRequest(call, this)
    }


    @SuppressLint("SimpleDateFormat")
    private fun onRangeChanged(startDate: LocalDate, endDate: LocalDate) {
        val startMonth = baseActivity!!.changeDateFormatFromDate(SimpleDateFormat("yyyy-MM-dd").parse(startDate.toString()), "MM")
        val startYear = baseActivity!!.changeDateFormatFromDate(SimpleDateFormat("yyyy-MM-dd").parse(startDate.toString()), "yyyy")
        val endMonth = baseActivity!!.changeDateFormatFromDate(SimpleDateFormat("yyyy-MM-dd").parse(endDate.toString()), "MM")
        val endYear = baseActivity!!.changeDateFormatFromDate(SimpleDateFormat("yyyy-MM-dd").parse(endDate.toString()), "yyyy")
        resetList()
        if (startMonth != month) {
            month = startMonth
            year = startYear
            getRequestListAPI(month, year)
        }
        // hitAvalabilityListApi(startDate.toString())
        getRequestListAPI(month, year)

    }

    fun onCalenderClick(data: AppointmentData) {

        when (data.stateId) {
            Const.STATE_PENDING -> {
                setRequestDialog(data)
            }
            Const.STATE_EDIT -> {
                val bundle = Bundle()
                bundle.putParcelable("booking_data", data)
                baseActivity!!.replaceFragment(RespondFragment(), bundle)
            }
            Const.STATE_END_WORK -> {
                if (data.isRate && data.paymentStatus == 0) {
                    val bundle = Bundle()
                    bundle.putParcelable("booking_data", data)
                    baseActivity!!.replaceFragment(PaymentMethodListFragment(), bundle)
                } else {
                    val bundle = Bundle()
                    bundle.putParcelable("booking_data", data)
                    baseActivity!!.replaceFragment(OrderSummaryFragment(), bundle)
                }
            }
            Const.STATE_REJECT, Const.STATE_CANCEL -> {
                cancelClick(data)
            }
            else -> {
                val bundle = Bundle()
                bundle.putParcelable("booking_data", data)
                baseActivity!!.replaceFragment(MapTrackingFragment(), bundle)
            }
        }


    }

    private var cancelReqBinding: DialogCancelRequestBinding? = null

    fun cancelClick(data: AppointmentData) {
        val mBuilder = AlertDialog.Builder(baseActivity!!, R.style.animateDialog)
        cancelReqBinding = DataBindingUtil.inflate(LayoutInflater.from(baseActivity), R.layout.dialog_cancel_request, null, false)
        mBuilder.setView(cancelReqBinding!!.root)
        val alertDialogd = mBuilder.create()
        alertDialogd.show()
        alertDialogd.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        cancelReqBinding!!.data = data
        cancelReqBinding!!.profileIV.loadFromUrl(baseActivity!!, data.provider.profileFile)
        val date = baseActivity!!.changeDateFormatGmtToLocal(data.bookingSlots[0].startTime, Const.DATE_FORMAT, "dd MMM")
        cancelReqBinding!!.dateValueTV.text = date
        cancelReqBinding!!.startTimeTV.text = baseActivity!!.changeDateFormatGmtToLocal(data.bookingSlots[0].startTime, Const.DATE_FORMAT, "hh:mm a")
        cancelReqBinding!!.endTimeTV.text = baseActivity!!.changeDateFormatGmtToLocal(data.bookingSlots[data.bookingSlots.size - 1].endTime, Const.DATE_FORMAT, "hh:mm a")
        cancelReqBinding!!.closeIV.setOnClickListener {
            alertDialogd.dismiss()
        }

    }

    private var dBinding: DialogNewRequestBinding? = null

    private fun setRequestDialog(data: AppointmentData) {
        val mBuilder = AlertDialog.Builder(baseActivity!!, R.style.animateDialog)
        dBinding = DataBindingUtil.inflate(LayoutInflater.from(baseActivity), R.layout.dialog_new_request, null, false)
        mBuilder.setView(dBinding!!.root)
        val alertDialogd = mBuilder.create()
        alertDialogd.show()
        alertDialogd.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dBinding!!.data = data
        dBinding!!.profileIV.loadFromUrl(baseActivity!!, data.provider.profileFile)
        val dates = baseActivity!!.changeDateFormatGmtToLocal(data.bookingSlots[0].startTime, Const.DATE_FORMAT, "dd MMM")
        dBinding!!.dateValueTV.text = dates
        dBinding!!.startTimeTV.text = baseActivity!!.changeDateFormatGmtToLocal(data.bookingSlots[0].startTime, Const.DATE_FORMAT, "hh:mm a")
        dBinding!!.endTimeTV.text = baseActivity!!.changeDateFormatGmtToLocal(data.bookingSlots[data.bookingSlots.size - 1].endTime, Const.DATE_FORMAT, "hh:mm a")
        dBinding!!.closeIV.setOnClickListener {
            alertDialogd.dismiss()
        }
        dBinding!!.seelocationTV.setOnClickListener {
            alertDialogd.dismiss()
            val bundle = Bundle()
            bundle.putParcelable("booking_data", data)
            baseActivity!!.replaceFragment(MapTrackingFragment(), bundle)
        }
    }

}
