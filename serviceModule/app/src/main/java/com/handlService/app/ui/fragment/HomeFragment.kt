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
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.alamkanak.weekview.threetenabp.setDateFormatter
import com.applandeo.materialcalendarview.EventDay
import com.applandeo.materialcalendarview.listeners.OnCalendarPageChangeListener
import com.applandeo.materialcalendarview.listeners.OnDayClickListener
import com.google.gson.Gson
import com.handlService.app.R
import com.handlService.app.databinding.FragmentHomeBinding
import com.handlService.app.databinding.LayoutAvailaibleBinding
import com.handlService.app.model.AppointmentData
import com.handlService.app.ui.activity.MainActivity
import com.handlService.app.ui.adapter.MyCustomSimpleAdapter
import com.handlService.app.ui.extensions.loadFromUrl
import com.handlService.app.ui.extensions.replaceFragment
import com.handlService.app.ui.extensions.setColor
import com.handlService.app.ui.fragment.login.NotificationFragment
import com.handlService.app.utils.ClickHandler
import com.handlService.app.utils.Const
import com.toxsl.restfulClient.api.extension.handleException
import org.json.JSONObject
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class HomeFragment : BaseFragment(), ClickHandler {
    private var nextData: AppointmentData? = null
    private var onlineBinding: LayoutAvailaibleBinding? = null
    private var binding: FragmentHomeBinding? = null
    private var list: ArrayList<AppointmentData> = arrayListOf()
    private var calList: ArrayList<AppointmentData> = arrayListOf()
    private var isDate = false
    private var month = ""
    private var year = ""
    private var isDialog = false

    private val weekdayFormatter = DateTimeFormatter.ofPattern("EEE", Locale.getDefault())
    private val dateFormatter = DateTimeFormatter.ofPattern("MM/dd", Locale.getDefault())
    private var count = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (binding == null)
            binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_home, container, false
            )
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        baseActivity!!.setToolbar(
            baseActivity!!.getString(R.string.home),
            ContextCompat.getColor(baseActivity!!, R.color.screen_bg),
            false
        )
        initUI()

    }

    private fun initUI() {
        isDialog = false
        resetList()
        binding!!.handleClick = this
        binding!!.toolI.handleClick = this
        setDefaultClick()
        count = 0
        binding!!.monthViewTV.setBackgroundColor(
            ContextCompat.getColor(
                baseActivity!!,
                R.color.LightGrey
            )
        )
        // binding!!.calendarView.setOnDayClickListener(this)
        binding!!.calendarView.visibility = View.VISIBLE
        binding!!.weekView.setDateFormatter { date: LocalDate ->
            val weekdayLabel = weekdayFormatter.format(date)
            val dateLabel = dateFormatter.format(date)
            weekdayLabel + "\n" + dateLabel
        }
        binding!!.calendarView.setOnDayClickListener(object : OnDayClickListener {
            override fun onDayClick(eventDay: EventDay) {
                val clickedDayCalendar = eventDay.calendar
                getMonthListAPI(
                    month, year,
                    baseActivity!!.changeDateFormatFromDate(
                        clickedDayCalendar.time,
                        "yyyy-MM-dd"
                    )
                )

                baseActivity!!.replaceFragment(MyAppointmentFragment())

            }
        })
        binding!!.layoutRL.visibility = View.VISIBLE
        val mCalendar = Calendar.getInstance()
        binding!!.calendarView.setOnForwardPageChangeListener(object :
            OnCalendarPageChangeListener {
            override fun onChange() {
                resetList()
                isDate = false
                mCalendar.add(Calendar.MONTH, +1)
                month = baseActivity!!.changeDateFormatFromDate(mCalendar.time, "MM")
                year = baseActivity!!.changeDateFormatFromDate(mCalendar.time, "yyyy")
                getMonthListAPI(month, year, "")
            }

        })

        binding!!.calendarView.setOnPreviousPageChangeListener(object :
            OnCalendarPageChangeListener {
            override fun onChange() {
                resetList()
                isDate = false
                mCalendar.add(Calendar.MONTH, -1)
                month = baseActivity!!.changeDateFormatFromDate(mCalendar.time, "MM")
                year = baseActivity!!.changeDateFormatFromDate(mCalendar.time, "yyyy")
                getMonthListAPI(month, year, "")

            }

        })

        isDate = false
        month = baseActivity!!.changeDateFormatFromDate(mCalendar.time, "MM")
        year = baseActivity!!.changeDateFormatFromDate(mCalendar.time, "yyyy")
        getRequestListAPI(month, year)
        getMonthListAPI(month, year, "")

        binding!!.toolI.countTV.text = baseActivity!!.getProfileData()?.notificationCount.toString()
        getOFFON()
    }

    private fun getMonthListAPI(month: String, year: String, date: String) {
        val call = api!!.apiGetMonthlyList(month, year, date)
        restFullClient!!.sendRequest(call, this)
    }

    private fun resetList() {
        list.clear()
        calList.clear()
    }

    private fun getRequestDateWise(date: String) {
        val call = api!!.apiGetRequestDateWise(date)
        restFullClient!!.sendRequest(call, this)

    }


    private fun getRequestListAPI(month: String, year: String) {
        val call = api!!.apiGetAllRequest(0, month, year)
        restFullClient!!.sendRequest(call, this)
    }

    private fun setDefaultClick() {
        binding!!.dayViewTV.setBackgroundColor(
            ContextCompat.getColor(
                baseActivity!!,
                R.color.White
            )
        )
        binding!!.weekViewTV.setBackgroundColor(
            ContextCompat.getColor(
                baseActivity!!,
                R.color.White
            )
        )
        binding!!.monthViewTV.setBackgroundColor(
            ContextCompat.getColor(
                baseActivity!!,
                R.color.White
            )
        )
        binding!!.weekView.visibility = View.GONE
        binding!!.calendarView.visibility = View.GONE
    }

    private fun getOFFON() {
        val call = api!!.apiOFFON()
        restFullClient!!.sendRequest(call, this)
    }

    override fun onHandleClick(view: View) {
        when (view.id) {
            R.id.reqLL -> {
                if (list.size > 0) {
                    val bundle = Bundle()
                    bundle.putParcelable("data", list[count])
                    baseActivity!!.replaceFragment(BookingDetailFragment(), bundle)
                }
            }
            R.id.newLL -> {
                if (nextData != null) {
                    val bundle = Bundle()
                    bundle.putParcelable("data", nextData)
                    baseActivity!!.replaceFragment(BookingDetailFragment(), bundle)
                }
            }
            R.id.leftIV -> {
                if (count == 0) {
                    showToastOne(baseActivity!!.getString(R.string.no_more_req_found))
                } else if (count > 0) {
                    count -= 1
                    setRequestData(list[count])
                }
            }
            R.id.rightIV -> {
                if (count == list.size - 1) {
                    showToastOne(baseActivity!!.getString(R.string.no_more_req_found))
                } else if (count < list.size - 1) {
                    count += 1
                    setRequestData(list[count])
                }
            }
            R.id.notiIV -> {
                baseActivity!!.replaceFragment(NotificationFragment())
            }
            R.id.menuIV -> {
                (baseActivity as MainActivity).openDrawer()
            }
            R.id.statusIV -> {
                isDialog = true
                getOFFON()
            }

            R.id.dayViewTV -> {
                setDefaultClick()
                binding!!.dayViewTV.setBackgroundColor(
                    ContextCompat.getColor(
                        baseActivity!!,
                        R.color.LightGrey
                    )
                )
                binding!!.weekView.visibility = View.VISIBLE
                binding!!.weekView.numberOfVisibleDays = 1
            }
            R.id.weekViewTV -> {
                setDefaultClick()
                binding!!.weekViewTV.setBackgroundColor(
                    ContextCompat.getColor(
                        baseActivity!!,
                        R.color.LightGrey
                    )
                )
                binding!!.weekView.visibility = View.VISIBLE
                binding!!.weekView.numberOfVisibleDays = 7
            }

            R.id.monthViewTV -> {
                setDefaultClick()
                binding!!.monthViewTV.setBackgroundColor(
                    ContextCompat.getColor(
                        baseActivity!!,
                        R.color.LightGrey
                    )
                )
                binding!!.calendarView.visibility = View.VISIBLE

            }
        }
    }

    val events: MutableList<EventDay> = ArrayList()

    @SuppressLint("SimpleDateFormat")
    override fun onSyncSuccess(
        responseCode: Int,
        responseMessage: String,
        responseUrl: String,
        response: String?
    ) {
        super.onSyncSuccess(responseCode, responseMessage, responseUrl, response)
        try {
            val jsonobject = JSONObject(response!!)
            if (responseUrl.contains(Const.API_AVAIL_REQUEST_LIST)) {
                if (responseCode == Const.STATUS_OK) {
                    val jsonArray = jsonobject.getJSONArray(Const.LIST)
                    for (i in 0 until jsonArray.length()) {
                        val object1 = jsonArray.getJSONObject(i)
                        val data = Gson().fromJson<AppointmentData>(
                            object1.toString(),
                            AppointmentData::class.java
                        )
                        if (data.bookingSlots.isNotEmpty()) {
                            list.add(data)
                        }
                    }
                    binding!!.countTV.text = list.size.toString()

                    if (list.size > 0) {
                        binding!!.reqLL.visibility = View.VISIBLE
                        binding!!.leftIV.visibility = View.VISIBLE
                        binding!!.rightIV.visibility = View.VISIBLE
                        setRequestData(list[count])
                    } else {
                        binding!!.reqLL.visibility = View.GONE
                        binding!!.leftIV.visibility = View.GONE
                        binding!!.rightIV.visibility = View.GONE
                    }
                }

            } /*else if (responseUrl.contains(Const.API_AVAIL_DATE_REQUEST_LIST)) {
                if (responseCode == Const.STATUS_OK) {
                    val dateList: ArrayList<AppointmentData> = arrayListOf()
                    val jsonArray = jsonobject.getJSONArray(Const.LIST)
                    for (i in 0 until jsonArray.length()) {
                        val object1 = jsonArray.getJSONObject(i)
                        val data = Gson().fromJson<AppointmentData>(
                                object1.toString(),
                                AppointmentData::class.java
                        )
                        data.isRequest = Const.STATE_PENDING
                        dateList.add(data)
                    }
                    if (dateList.isNotEmpty()) {
                        val bundle = Bundle()
                        bundle.putParcelable("data", dateList[0])
                        baseActivity!!.replaceFragment(BookingDetailFragment(), bundle)
                    }

                }

            } */ else if (responseUrl.contains(Const.API_ONLINE_OFFLINE)) {
                if (responseCode == Const.STATUS_OK) {
                    val status = jsonobject.optBoolean("status")
                    if (jsonobject.has("detail")) {
                        nextData = Gson().fromJson<AppointmentData>(
                            jsonobject.getJSONObject("detail").toString(),
                            AppointmentData::class.java
                        )
                        var time = ""
                        if (status && nextData != null) {
                            time = nextData!!.bookingSlots[0].startTime
                        }
                        if (isDialog) {
                            setPopDialog(time, status)
                        } else {
                            setAppointData(nextData!!)

                        }
                        binding!!.newLL.visibility = View.VISIBLE
                        binding!!.noAppTV.text = ""
                        binding!!.noAppTV.visibility = View.GONE
                    } else {
                        binding!!.newLL.visibility = View.GONE
                        binding!!.noAppTV.visibility = View.VISIBLE
                        binding!!.noAppTV.text = baseActivity!!.getString(R.string.no_appoit)
                    }
                }

            } else if (responseUrl.contains(Const.API_AVAILABLE_LIST)) {
                if (responseCode == Const.STATUS_OK) {
                    val event: MutableList<EventDay> = ArrayList()
                    val jsonArray = jsonobject.getJSONArray(Const.LIST)
                    calList.clear()
                    /* if (adapter != null) {
                         adapter = null
                     }*/
                    for (i in 0 until jsonArray.length()) {
                        val object1 = jsonArray.getJSONObject(i)
                        val data = Gson().fromJson<AppointmentData>(
                            object1.toString(),
                            AppointmentData::class.java
                        )
                        val calendar = Calendar.getInstance()
                        val sdf = SimpleDateFormat(Const.DATE_FORMAT)
                        val date = baseActivity!!.changeDateFormatGmtToLocal(
                            data.startTime,
                            Const.DATE_FORMAT,
                            Const.DATE_FORMAT
                        )
                        calendar.time = sdf.parse(date)!!
                        calList.add(data)
                        if (data.isBooked) {
                            event.add(EventDay(calendar, R.drawable.circle_light_blue))
                        } else {
                            event.add(EventDay(calendar, R.drawable.green_dot))
                        }
                    }
                    binding!!.calendarView.setEvents(event)
                    if (calList.size > 0) {
                        setAdapter()
                    }
                }
            } else if (responseUrl.contains(Const.API_MONTHLY_BOOKING_LIST)) {
                if (responseCode == Const.STATUS_OK) {
                    val event: MutableList<EventDay> = ArrayList()
                    val jsonArray = jsonobject.getJSONArray(Const.LIST)
                    for (i in 0 until jsonArray.length()) {
                        val object1 = jsonArray.getJSONObject(i)
                        val data = Gson().fromJson<AppointmentData>(
                            object1.toString(),
                            AppointmentData::class.java
                        )
                        val calendar = Calendar.getInstance()
                        val sdf = SimpleDateFormat(Const.DATE_FORMAT)
                        val date = baseActivity!!.changeDateFormatGmtToLocal(
                            data.startTime,
                            Const.DATE_FORMAT,
                            Const.DATE_FORMAT
                        )
                        calendar.time = sdf.parse(date)!!
                        calList.add(data)
                        if (data.isBooked) {
                            event.add(EventDay(calendar, R.drawable.circle_light_blue))
                        } else {
                            event.add(EventDay(calendar, R.drawable.green_dot))
                        }
                    }
                    binding!!.calendarView.setEvents(event)
                    if (calList.size > 0) {
                        setAdapter()
                    }
                }

            }


        } catch (e: Exception) {
            handleException(e)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setAppointData(data: AppointmentData) {
        val reqBind = binding!!.newI
        reqBind.catNameTV.text =
            data.userService.subcategoryName + " ( ref:" + data.id.toString() + ")"

        reqBind.userIV.loadFromUrl(baseActivity!!, data.userDetail?.profileFile?:"")
        val date = baseActivity!!.changeDateFormatGmtToLocal(
            data.bookingSlots[0].startTime,
            Const.DATE_FORMAT,
            "EEEE - dd MMMM yyyy"
        )
        val time = baseActivity!!.changeDateFormatGmtToLocal(
            data.bookingSlots[0].startTime,
            Const.DATE_FORMAT,
            "hh:mm a"
        ) + " - " +
                baseActivity!!.changeDateFormatGmtToLocal(
                    data.bookingSlots[data.bookingSlots.size - 1].endTime,
                    Const.DATE_FORMAT,
                    "hh:mm a"
                )

        reqBind.addressTV.text = data.address
        reqBind.nameTV.text = data.userDetail?.fullName

        reqBind.dateTV.text = date
        reqBind.slotTV.text =
            time + " (" + (30 * data.bookingSlots.size).toString() + baseActivity!!.getString(R.string.minss)
    }

    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    private fun setRequestData(data: AppointmentData) {
        val reqBind = binding!!.reqI
        reqBind.catNameTV.text =
            data.userService.subcategoryName + " ( ref:" + data.id.toString() + ")"
        reqBind.userIV.loadFromUrl(baseActivity!!, data.userDetail?.profileFile?:"")
        val date = baseActivity!!.changeDateFormatGmtToLocal(
            data.bookingSlots[0].startTime,
            Const.DATE_FORMAT,
            "EEEE - dd MMMM yyyy"
        )
        val time = baseActivity!!.changeDateFormatGmtToLocal(
            data.bookingSlots[0].startTime,
            Const.DATE_FORMAT,
            "hh:mm a"
        ) + " - " +
                baseActivity!!.changeDateFormatGmtToLocal(
                    data.bookingSlots[data.bookingSlots.size - 1].endTime,
                    Const.DATE_FORMAT,
                    "hh:mm a"
                )
        val simpleDateFormat = SimpleDateFormat(Const.DATE_FORMAT)

        val createdOn = simpleDateFormat.parse(
            baseActivity!!.changeDateFormatGmtToLocal(
                data.createdOn,
                Const.DATE_FORMAT,
                Const.DATE_FORMAT
            )
        )

        reqBind.addressTV.text = data.address
        reqBind.nameTV.text = data.userDetail?.fullName
        reqBind.timeTV.text = baseActivity!!.getTIME(
            SimpleDateFormat(Const.DATE_FORMAT).parse(
                baseActivity!!.changeDateFormatGmtToLocal(
                    data.bookingSlots[0].startTime,
                    Const.DATE_FORMAT,
                    Const.DATE_FORMAT
                )
            )
        )
        reqBind.timeTV.setColor(baseActivity!!, R.color.Red)
        reqBind.dateTV.text = date
        reqBind.slotTV.text =
            time + " (" + (30 * data.bookingSlots.size).toString() + baseActivity!!.getString(R.string.minss)
    }


    private fun setAdapter() {
        val adapter = MyCustomSimpleAdapter(
            baseActivity!!,
            this,
            rangeChangeHandler = this::onRangeChanged
        )
        binding!!.weekView.adapter = adapter
        adapter!!.submitList(calList)

    }

    @SuppressLint("SimpleDateFormat")
    private fun onRangeChanged(startDate: LocalDate, endDate: LocalDate) {
        //hitAvalabilityListApi(startDate)
        val startMonth = baseActivity!!.changeDateFormatFromDate(
            SimpleDateFormat("yyyy-MM-dd").parse(startDate.toString()),
            "MM"
        )
        val startYear = baseActivity!!.changeDateFormatFromDate(
            SimpleDateFormat("yyyy-MM-dd").parse(startDate.toString()),
            "yyyy"
        )
        val endMonth = baseActivity!!.changeDateFormatFromDate(
            SimpleDateFormat("yyyy-MM-dd").parse(endDate.toString()),
            "MM"
        )
        val endYear = baseActivity!!.changeDateFormatFromDate(
            SimpleDateFormat("yyyy-MM-dd").parse(endDate.toString()),
            "yyyy"
        )
        if ("$startMonth-$startYear" == "$endMonth-$endYear") {
            if ("$startMonth-$startYear" != "$month-$year") {
                month = startMonth
                year = startYear
                calList.clear()
                getMonthListAPI(month, year, "")
            }
        } else {
            if ("$startMonth-$startYear" != "$month-$year") {
                month = startMonth
                year = startYear
                calList.clear()
                getMonthListAPI(month, year, "")
            } else if ("$endMonth-$endYear" != "$month-$year") {
                month = endMonth
                year = endYear
                calList.clear()
                getMonthListAPI(month, year, "")
            }
        }
    }

    private fun hitAvalabilityListApi(startDate: LocalDate) {
        val call = api!!.apiAvailabilityList(startDate.toString())
        restFullClient!!.sendRequest(call, this)
    }


    private fun setPopDialog(startTime: String, status: Boolean) {
        onlineBinding = DataBindingUtil.inflate(
            LayoutInflater.from(baseActivity),
            R.layout.layout_availaible,
            null,
            false
        )
        val mBuilder = AlertDialog.Builder(baseActivity!!, R.style.animateDialog)
        mBuilder.setView(onlineBinding!!.root)
        val dialog = mBuilder.create()
        dialog.setCancelable(false)
        dialog.show()
        onlineBinding!!.crossIV.setOnClickListener {
            isDialog = false
            dialog.dismiss()
        }

        dialog.window!!.setBackgroundDrawableResource(R.color.transparent)
        if (status) {
            val time = baseActivity!!.getTimefromDate(startTime)
            if (time.isNotEmpty()) {
                onlineBinding!!.availableCB.isChecked = true
                onlineBinding!!.hourTV.text = time
                onlineBinding!!.descTV.text =
                    Html.fromHtml(baseActivity!!.getString(R.string.your_appointment_is_in_7_hours) + time)
                onlineBinding!!.hourTV.visibility = View.VISIBLE
            } else {
                onlineBinding!!.offlineCB.isChecked = true
                onlineBinding!!.hourTV.visibility = View.GONE
                onlineBinding!!.descTV.text =
                    Html.fromHtml(baseActivity!!.getString(R.string.no_booking_today))

            }
        } else {
            onlineBinding!!.offlineCB.isChecked = true
            onlineBinding!!.hourTV.visibility = View.GONE
            onlineBinding!!.descTV.text =
                Html.fromHtml(baseActivity!!.getString(R.string.no_booking_today))
        }
    }

    fun onCalnderClick(data: AppointmentData) {
        val bundle = Bundle()
        bundle.putParcelable("data", data)
        if (data.isBooked) {
            baseActivity!!.replaceFragment(BookingDetailFragmentCalendar(), bundle)
        }
    }

    /*override fun onDayClick(eventDay: EventDay) {
            baseActivity!!.replaceFragment(MyAppointmentFragment())
    }
*/
}