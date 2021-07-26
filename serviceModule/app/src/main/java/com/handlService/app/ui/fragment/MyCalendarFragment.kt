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
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Html
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.alamkanak.weekview.threetenabp.setDateFormatter
import com.applandeo.materialcalendarview.EventDay
import com.applandeo.materialcalendarview.listeners.OnCalendarPageChangeListener
import com.applandeo.materialcalendarview.listeners.OnDayClickListener
import com.google.gson.Gson
import com.handlService.app.R
import com.handlService.app.databinding.*
import com.handlService.app.model.AppointmentData
import com.handlService.app.model.SlotData
import com.handlService.app.ui.activity.MainActivity
import com.handlService.app.ui.adapter.MyCustomSimpleAdapter
import com.handlService.app.ui.extensions.loadFromUrl
import com.handlService.app.ui.extensions.replaceFragment
import com.handlService.app.utils.ClickHandler
import com.handlService.app.utils.Const
import com.toxsl.restfulClient.api.extension.handleException
import kotlinx.android.synthetic.main.fragment_my_calendar.*
import org.json.JSONObject
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class MyCalendarFragment : BaseFragment(), ClickHandler {

    private var binding: FragmentMyCalendarBinding? = null

    private val weekdayFormatter = DateTimeFormatter.ofPattern("EEE", Locale.getDefault())
    private val dateFormatter = DateTimeFormatter.ofPattern("MM/dd", Locale.getDefault())
    private var month = ""
    private var year = ""
    private var list: ArrayList<AppointmentData> = arrayListOf()

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
        baseActivity!!.setToolbar(title = getString(R.string.my_calendar))
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
                getRequestListAPI(month, year,
                        baseActivity!!.changeDateFormatFromDate(
                                clickedDayCalendar.time,
                                "yyyy-MM-dd"
                        )
                )

                baseActivity!!.replaceFragment(MyAppointmentFragment())

            }
        })
        binding!!.calendarView.setOnForwardPageChangeListener(object : OnCalendarPageChangeListener {
            override fun onChange() {
                resetList()

                mCalendar.add(Calendar.MONTH, +1)
                month = baseActivity!!.changeDateFormatFromDate(mCalendar.time, "MM")
                year = baseActivity!!.changeDateFormatFromDate(mCalendar.time, "yyyy")
                getRequestListAPI(month, year, "")

            }

        })
        binding!!.calendarView.setOnPreviousPageChangeListener(object : OnCalendarPageChangeListener {
            override fun onChange() {
                resetList()
                mCalendar.add(Calendar.MONTH, -1)
                month = baseActivity!!.changeDateFormatFromDate(mCalendar.time, "MM")
                year = baseActivity!!.changeDateFormatFromDate(mCalendar.time, "yyyy")
                getRequestListAPI(month, year, "")

            }

        })

        setHasOptionsMenu(true)
        offlineCB.isChecked = false
        availableCB.isChecked = true
        offlineCB.setOnCheckedChangeListener { compoundButton, b ->
            availableCB.isChecked = !b
        }
        availableCB.setOnCheckedChangeListener { compoundButton, b ->
            offlineCB.isChecked = !b
        }
        month = baseActivity!!.changeDateFormatFromDate(mCalendar.time, "MM")
        year = baseActivity!!.changeDateFormatFromDate(mCalendar.time, "yyyy")
        getRequestListAPI(month, year, "")

        getOFFON()
    }


    private fun getRequestListAPI(month: String, year: String, date: String) {
        val call = api!!.apiGetMonthlyList(month, year, date)
        restFullClient!!.sendRequest(call, this)
    }

    private fun getOFFON() {
        val call = api!!.apiOFFON()
        restFullClient!!.sendRequest(call, this)
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

    private fun setDefaultClick() {
        binding!!.dayViewTV.setBackgroundColor(ContextCompat.getColor(baseActivity!!, R.color.White))
        binding!!.weekViewTV.setBackgroundColor(ContextCompat.getColor(baseActivity!!, R.color.White))
        binding!!.monthViewTV.setBackgroundColor(ContextCompat.getColor(baseActivity!!, R.color.White))
        binding!!.weekView.visibility = View.GONE
        binding!!.calendarView.visibility = View.GONE

    }


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
            if (responseUrl.contains(Const.API_MONTHLY_BOOKING_LIST)) {
                list.clear()
                if (responseCode == Const.STATUS_OK) {
                    val event: MutableList<EventDay> = ArrayList()
                    val jsonArray = jsonobject.getJSONArray(Const.LIST)
                    for (i in 0 until jsonArray.length()) {
                        val object1 = jsonArray.getJSONObject(i)
                        val data = Gson().fromJson<AppointmentData>(object1.toString(), AppointmentData::class.java)
                        val calendar = Calendar.getInstance()
                        val sdf = SimpleDateFormat(Const.DATE_FORMAT)
                        val date = baseActivity!!.changeDateFormatGmtToLocal(data.startTime, Const.DATE_FORMAT, Const.DATE_FORMAT)
                        calendar.time = sdf.parse(date)!!
                        list.add(data)
                        if (data.isBooked) {
                            event.add(EventDay(calendar, R.drawable.circle_light_blue))
                        } else {
                            event.add(EventDay(calendar, R.drawable.green_dot))
                        }                    }
                    binding!!.calendarView.setEvents(event)
                    if (list.size > 0) {
                        setAdapter()
                    }
                }

            } else if (responseUrl.contains(Const.API_ONLINE_OFFLINE)) {
                if (responseCode == Const.STATUS_OK) {
                    val status = jsonobject.optBoolean("status")
                    val nextData = Gson().fromJson<AppointmentData>(jsonobject.getJSONObject("detail").toString(), AppointmentData::class.java)
                    var startTime = ""
                    if (status && nextData != null) {
                        startTime = nextData.bookingSlots[0].startTime
                    }
                    binding!!.availLL.visibility = View.VISIBLE
                    if (status) {
                        val simpleDateFormat = SimpleDateFormat(Const.DATE_FORMAT)

                        val date1 = simpleDateFormat.parse(baseActivity!!.changeDateFormatGmtToLocal(startTime, Const.DATE_FORMAT, Const.DATE_FORMAT))
                        val date2 = simpleDateFormat.parse(baseActivity!!.changeDateFormatFromDate(Calendar.getInstance().time, Const.DATE_FORMAT))

                        val difference: Long = date1!!.time - date2!!.time
                        val days = (difference / (1000 * 60 * 60 * 24)).toInt()
                        val hours = ((difference - 1000 * 60 * 60 * 24 * days) / (1000 * 60 * 60)).toInt()
                        val min = (difference - 1000 * 60 * 60 * 24 * days - 1000 * 60 * 60 * hours).toInt() / (1000 * 60)


                        var time = ""
                        if (days > 0) {
                            time = "$days${baseActivity!!.getString(R.string.days)}"
                        }
                        if (hours > 0) {
                            time = "$time$hours${baseActivity!!.getString(R.string.hours)}"
                        }
                        if (min > 0) {
                            time = "$time$min${baseActivity!!.getString(R.string.minutes)}"
                        }

                        if (time.isNotEmpty()) {
                            binding!!.availableCB.isChecked = true
                            binding!!.hourTV.text = time
                            binding!!.descTV.text = Html.fromHtml(baseActivity!!.getString(R.string.your_appointment_is_in_7_hours) + time)
                            binding!!.hourTV.visibility = View.VISIBLE
                        } else {
                            binding!!.offlineCB.isChecked = true
                            binding!!.hourTV.visibility = View.GONE
                            binding!!.descTV.text = Html.fromHtml(baseActivity!!.getString(R.string.no_booking_today))

                        }
                    } else {
                        binding!!.offlineCB.isChecked = true
                        binding!!.hourTV.visibility = View.GONE
                        binding!!.descTV.text = Html.fromHtml(baseActivity!!.getString(R.string.no_booking_today))
                    }
                }

            } else if (responseUrl.contains(Const.API_AVAIL_CHANGE_STATE)) {
                if (responseCode == Const.STATUS_OK) {
                    showToastOne(jsonobject.optString("message"))
                    if (alertDialogd != null) {
                        alertDialogd!!.dismiss()
                    }
                    if (alertDialog != null) {
                        alertDialog!!.dismiss()
                    }
                }

            } else if (responseUrl.contains(Const.API_AVAILABLE_LIST)) {
                if (responseCode == Const.STATUS_OK) {
                    val event: MutableList<EventDay> = ArrayList()
                    val jsonArray = jsonobject.getJSONArray(Const.LIST)
                    list.clear()
                    for (i in 0 until jsonArray.length()) {
                        val object1 = jsonArray.getJSONObject(i)
                        val data = Gson().fromJson<AppointmentData>(object1.toString(), AppointmentData::class.java)
                        val calendar = Calendar.getInstance()
                        val sdf = SimpleDateFormat(Const.DATE_FORMAT)
                        val date = baseActivity!!.changeDateFormatGmtToLocal(data.startTime, Const.DATE_FORMAT, Const.DATE_FORMAT)
                        calendar.time = sdf.parse(date)!!
                        list.add(data)
                        if (data.isBooked) {
                            event.add(EventDay(calendar, R.drawable.circle_light_blue))
                        } else {
                            event.add(EventDay(calendar, R.drawable.green_dot))
                        }
                    }

                    binding!!.calendarView.setEvents(event)
                    if (list.size > 0) {
                        setAdapter()
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

    @SuppressLint("SimpleDateFormat")
    private fun onRangeChanged(startDate: LocalDate, endDate: LocalDate) {

        log(startDate.toString())
        val startMonth = baseActivity!!.changeDateFormatFromDate(SimpleDateFormat("yyyy-MM-dd").parse(startDate.toString()), "MM")
        val startYear = baseActivity!!.changeDateFormatFromDate(SimpleDateFormat("yyyy-MM-dd").parse(startDate.toString()), "yyyy")
        val endMonth = baseActivity!!.changeDateFormatFromDate(SimpleDateFormat("yyyy-MM-dd").parse(endDate.toString()), "MM")
         val endYear = baseActivity!!.changeDateFormatFromDate(SimpleDateFormat("yyyy-MM-dd").parse(endDate.toString()), "yyyy")
        resetList()
        if (startMonth != month) {
            month = startMonth
            year = startYear
            getRequestListAPI(month, year, "")
        }
        //  hitAvalabilityListApi(startDate)
    }

    private fun hitAvalabilityListApi(startDate: LocalDate) {
        val call = api!!.apiAvailabilityList(startDate.toString())
        restFullClient!!.sendRequest(call, this)
    }


    fun onCalnderClick(data: AppointmentData) {
        when (data.stateId) {
            Const.STATE_PENDING -> {
                //setRequestDialog(data)
            }
            Const.STATE_ACCEPT -> {
              //  setAcceptDialog(data)
            }
            else -> {
                val bundle = Bundle()
                bundle.putParcelable("booking_data", data)
                baseActivity!!.replaceFragment(MapTrackingFragment(), bundle)
            }
        }


    }

    private var dialog: AlertDialog? = null
    private var dialogBinding: AddTimeDialogBinding? = null
    private var alertDialog: AlertDialog? = null
    private var cancelBinding: DialogCancelServiceBinding? = null
    private var cBinding: DailogAvailabilityBinding? = null
    private var eBinding: DialogEditRequestBinding? = null
    private var dBinding: DialogNewRequestBinding? = null
    private var cancelReqBinding: DialogCancelRequestBinding? = null
    private var alertDialogd: AlertDialog? = null

    private var type = 0
    private var dayList = ArrayList<SlotData>()

    private fun setAcceptDialog(data: AppointmentData) {
        val mBuilder = AlertDialog.Builder(baseActivity!!, R.style.animateDialog)
        cBinding = DataBindingUtil.inflate(LayoutInflater.from(baseActivity), R.layout.dailog_availability, null, false)
        mBuilder.setView(cBinding!!.root)
        alertDialog = mBuilder.create()
        alertDialog!!.show()
        alertDialog!!.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        cBinding!!.data = data
        cBinding!!.profileIV.loadFromUrl(baseActivity!!, data.createdBy.profileFile)
        val date = baseActivity!!.changeDateFormatGmtToLocal(data.startTime, Const.DATE_FORMAT, "dd MMM")
        cBinding!!.dateValueTV.text = date
        cBinding!!.startTimeTV.text = baseActivity!!.changeDateFormatGmtToLocal(data.startTime, Const.DATE_FORMAT, "hh:mm a")
        cBinding!!.endTimeTV.text = baseActivity!!.changeDateFormatGmtToLocal(data.endTime, Const.DATE_FORMAT, "hh:mm a")
        cBinding!!.closeIV.setOnClickListener {
            alertDialog!!.dismiss()
        }
        cBinding!!.seelocationTV.setOnClickListener {
            alertDialog!!.dismiss()
            val bundle = Bundle()
            bundle.putParcelable("booking_data", data)
            baseActivity!!.replaceFragment(MapTrackingFragment(), bundle)
        }
        cBinding!!.cancelTV.setOnClickListener {
            setCancelDialog(data)
        }

    }

    private fun setCancelDialog(data: AppointmentData) {
        val mBuilder = AlertDialog.Builder(baseActivity!!, R.style.animateDialog)
        cancelBinding = DataBindingUtil.inflate(LayoutInflater.from(baseActivity), R.layout.dialog_cancel_service, null, false)
        mBuilder.setView(cancelBinding!!.root)
        val alertDialogcd = mBuilder.create()
        alertDialogcd.show()
        alertDialogcd.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        cancelBinding!!.yesTV.setOnClickListener {
            alertDialogcd.dismiss()
            hitAcceptRejectAPI(data.id, Const.STATE_CANCEL)
        }

        cancelBinding!!.noTV.setOnClickListener {
            alertDialogcd.dismiss()
        }

    }

    fun cancelClick(position: Int) {
        val data = list[position]
        val mBuilder = AlertDialog.Builder(baseActivity!!, R.style.animateDialog)
        cancelReqBinding = DataBindingUtil.inflate(LayoutInflater.from(baseActivity), R.layout.dialog_cancel_request, null, false)
        mBuilder.setView(cancelReqBinding!!.root)
        val alertDialogd = mBuilder.create()
        alertDialogd.show()
        alertDialogd.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        cancelReqBinding!!.data = data
        cancelReqBinding!!.profileIV.loadFromUrl(baseActivity!!, data.createdBy.profileFile)
        val date = baseActivity!!.changeDateFormatGmtToLocal(data.bookingSlots[0].startTime, Const.DATE_FORMAT, "dd MMM")
        cancelReqBinding!!.dateValueTV.text = date
        cancelReqBinding!!.startTimeTV.text = baseActivity!!.changeDateFormatGmtToLocal(data.bookingSlots[0].startTime, Const.DATE_FORMAT, "hh:mm a")
        cancelReqBinding!!.endTimeTV.text = baseActivity!!.changeDateFormatGmtToLocal(data.bookingSlots[data.bookingSlots.size - 1].endTime, Const.DATE_FORMAT, "hh:mm a")
        cancelReqBinding!!.closeIV.setOnClickListener {
            alertDialogd.dismiss()
        }

    }

    private fun hitAcceptRejectAPI(id: Int, state: Int) {

        val call = api!!.apiChangeState(id, state, "")
        restFullClient!!.sendRequest(call, this)
    }

    private fun setRequestDialog(data: AppointmentData) {
        val mBuilder = AlertDialog.Builder(baseActivity!!, R.style.animateDialog)
        dBinding = DataBindingUtil.inflate(LayoutInflater.from(baseActivity), R.layout.dialog_new_request, null, false)
        mBuilder.setView(dBinding!!.root)
        alertDialogd = mBuilder.create()
        alertDialogd!!.show()
        alertDialogd!!.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dBinding!!.data = data
        dBinding!!.profileIV.loadFromUrl(baseActivity!!, data.createdBy.profileFile)
        val date = baseActivity!!.changeDateFormatGmtToLocal(data.startTime, Const.DATE_FORMAT, "dd MMMM yyyy")
        dBinding!!.dateValueTV.text = date
        dBinding!!.earnValueTV.text = baseActivity!!.getString(R.string.euro) + data.price
        dBinding!!.timeValueTV.text = baseActivity!!.changeDateFormatGmtToLocal(data.startTime, Const.DATE_FORMAT, "hh:mm a") + " - " +
                baseActivity!!.changeDateFormatGmtToLocal(data.endTime, Const.DATE_FORMAT, "hh:mm a")
        dBinding!!.closeIV.setOnClickListener {
            alertDialogd!!.dismiss()
        }
        dBinding!!.seelocationTV.setOnClickListener {
            alertDialogd!!.dismiss()
            val bundle = Bundle()
            bundle.putParcelable("booking_data", data)
            baseActivity!!.replaceFragment(MapTrackingFragment(), bundle)
        }

        dBinding!!.requestTV.setOnClickListener {
            alertDialogd!!.dismiss()
            rejectRequest(data)
        }

        dBinding!!.approveTV.setOnClickListener {
            alertDialogd!!.dismiss()
            hitAcceptRejectAPI(data.id, Const.STATE_ACCEPT)
        }


    }

    private fun rejectRequest(data: AppointmentData) {
        AlertDialog.Builder(baseActivity!!, R.style.animateDialog)
                .setMessage(baseActivity!!.getString(R.string.reject_sure))
                .setPositiveButton(getString(R.string.confirm)) { d, i ->
                    hitAcceptRejectAPI(data.id, Const.STATE_REJECT)
                    d.dismiss()
                }
                .setNegativeButton(getString(R.string.cancel)) { d, i ->
                    d.dismiss()
                }.show()

    }

}
